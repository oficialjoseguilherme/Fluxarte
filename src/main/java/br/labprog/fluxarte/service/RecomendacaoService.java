package br.labprog.fluxarte.service;

import br.labprog.fluxarte.dto.response.PrateleiraResponse;
import br.labprog.fluxarte.dto.response.RecomendacaoHomeResponse;
import br.labprog.fluxarte.dto.response.RecomendacaoResponse;
import br.labprog.fluxarte.mapper.RecomendacaoMapper;
import br.labprog.fluxarte.model.GeneroObra;
import br.labprog.fluxarte.model.HistoricoVisualizacao;
import br.labprog.fluxarte.model.ObraAudiovisual;
import br.labprog.fluxarte.model.Recomendacao;
import br.labprog.fluxarte.model.Usuario;
import br.labprog.fluxarte.model.enums.AlgoritmoRecomendacao;
import br.labprog.fluxarte.model.enums.ClassificacaoIndicativa;
import br.labprog.fluxarte.repository.RecomendacaoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Period;
import java.util.*;

@Service
@RequiredArgsConstructor
public class RecomendacaoService {

    private static final int MAX_RECOMENDACOES = 20;

    private final RecomendacaoRepository recomendacaoRepository;
    private final UsuarioService usuarioService;
    private final HistoricoVisualizacaoService historicoService;
    private final ObraAudiovisualService obraService;
    private final RecomendacaoMapper recomendacaoMapper;

    @Transactional
    public List<Recomendacao> gerarParaUsuario(UUID usuarioId) {
        Usuario usuario = usuarioService.buscarEntidadePorId(usuarioId);
        List<HistoricoVisualizacao> historico = historicoService.listarEntidadesPorUsuario(usuarioId);

        Map<Long, ObraAudiovisual> assistidas = new LinkedHashMap<>();
        for (HistoricoVisualizacao h : historico) {
            assistidas.putIfAbsent(h.getObra().getId(), h.getObra());
        }

        List<ObraAudiovisual> candidatas = obraService.listarExibiveis().stream()
                .filter(obra -> !assistidas.containsKey(obra.getId()))
                .filter(obra -> podeAssistir(usuario, obra))
                .toList();

        LocalDateTime agora = LocalDateTime.now();
        Map<Long, Recomendacao> melhores = new HashMap<>();

        porPreferenciaGenero(usuario, candidatas, melhores, agora);
        porHistorico(usuario, assistidas.values(), candidatas, melhores, agora);
        porSimilarDiretor(usuario, assistidas.values(), candidatas, melhores, agora);

        List<Recomendacao> resultado = melhores.values().stream()
                .sorted((a, b) -> Float.compare(b.getScore(), a.getScore()))
                .limit(MAX_RECOMENDACOES)
                .toList();

        recomendacaoRepository.deleteByUsuarioId(usuarioId);

        return recomendacaoRepository.saveAll(resultado);
    }

    @Transactional(readOnly = true)
    public List<Recomendacao> listarPorUsuario(UUID usuarioId) {
        return recomendacaoRepository.findByUsuarioIdOrderByScoreDesc(usuarioId);
    }

    @Transactional
    public List<Recomendacao> obterOuGerarRecomendacoes(UUID usuarioId) {
        List<Recomendacao> existentes = recomendacaoRepository.findByUsuarioIdOrderByScoreDesc(usuarioId);
        if (existentes.isEmpty()) {
            return gerarParaUsuario(usuarioId);
        }
        return existentes;
    }

    @Transactional
    public RecomendacaoHomeResponse obterHome(UUID usuarioId) {
        List<Recomendacao> recomendacoes = obterOuGerarRecomendacoes(usuarioId);
        if (recomendacoes.isEmpty()) {
            return new RecomendacaoHomeResponse(null, List.of());
        }

        RecomendacaoResponse destaque = recomendacaoMapper.toResponse(recomendacoes.get(0));

        List<RecomendacaoResponse> todosResponses = recomendacoes.stream()
                .map(recomendacaoMapper::toResponse)
                .toList();

        List<PrateleiraResponse> prateleiras = new ArrayList<>();

        prateleiras.add(new PrateleiraResponse(
                "em-alta-para-voce",
                "Em Sintonia com Você",
                todosResponses
        ));

        List<RecomendacaoResponse> porHistorico = recomendacoes.stream()
                .filter(r -> r.getAlgoritmo() == AlgoritmoRecomendacao.HISTORICO)
                .map(recomendacaoMapper::toResponse)
                .toList();
        if (!porHistorico.isEmpty()) {
            prateleiras.add(new PrateleiraResponse(
                    "porque-voce-assistiu",
                    "Porque Você Assistiu",
                    porHistorico
            ));
        }

        List<RecomendacaoResponse> porDiretor = recomendacoes.stream()
                .filter(r -> r.getAlgoritmo() == AlgoritmoRecomendacao.SIMILAR_DIRETOR)
                .map(recomendacaoMapper::toResponse)
                .toList();
        if (!porDiretor.isEmpty()) {
            prateleiras.add(new PrateleiraResponse(
                    "mesmo-diretor",
                    "Mais Obras dos Seus Diretores Favoritos",
                    porDiretor
            ));
        }

        return new RecomendacaoHomeResponse(destaque, prateleiras);
    }

    private void porPreferenciaGenero(Usuario usuario, List<ObraAudiovisual> candidatas,
                                      Map<Long, Recomendacao> melhores, LocalDateTime agora) {
        Set<GeneroObra> preferidos = usuario.getGenerosPreferidos();
        if (preferidos == null || preferidos.isEmpty()) {
            return;
        }

        for (ObraAudiovisual obra : candidatas) {
            long comuns = obra.getGeneros().stream().filter(preferidos::contains).count();
            registrar(melhores, usuario, obra, AlgoritmoRecomendacao.PREFERENCIA_GENERO,
                    (float) comuns / preferidos.size(), agora);
        }
    }

    private void porHistorico(Usuario usuario, Iterable<ObraAudiovisual> assistidas,
                              List<ObraAudiovisual> candidatas, Map<Long, Recomendacao> melhores,
                              LocalDateTime agora) {
        Map<GeneroObra, Integer> frequencia = new HashMap<>();
        int total = 0;
        for (ObraAudiovisual obra : assistidas) {
            for (GeneroObra genero : obra.getGeneros()) {
                frequencia.put(genero, frequencia.getOrDefault(genero, 0) + 1);
                total++;
            }
        }
        if (total == 0) {
            return;
        }

        for (ObraAudiovisual obra : candidatas) {
            int peso = 0;
            for (GeneroObra genero : obra.getGeneros()) {
                peso += frequencia.getOrDefault(genero, 0);
            }
            registrar(melhores, usuario, obra, AlgoritmoRecomendacao.HISTORICO, (float) peso / total, agora);
        }
    }

    private void porSimilarDiretor(Usuario usuario, Iterable<ObraAudiovisual> assistidas,
                                   List<ObraAudiovisual> candidatas, Map<Long, Recomendacao> melhores,
                                   LocalDateTime agora) {
        Set<String> diretores = new HashSet<>();
        for (ObraAudiovisual obra : assistidas) {
            if (obra.getDiretor() != null && !obra.getDiretor().isBlank()) {
                diretores.add(obra.getDiretor().trim().toLowerCase());
            }
        }

        for (ObraAudiovisual obra : candidatas) {
            if (obra.getDiretor() != null && diretores.contains(obra.getDiretor().trim().toLowerCase())) {
                registrar(melhores, usuario, obra, AlgoritmoRecomendacao.SIMILAR_DIRETOR, 1.0f, agora);
            }
        }
    }

    private void registrar(Map<Long, Recomendacao> melhores, Usuario usuario, ObraAudiovisual obra,
                           AlgoritmoRecomendacao algoritmo, float score, LocalDateTime agora) {
        if (score <= 0) {
            return;
        }

        Recomendacao atual = melhores.get(obra.getId());
        if (atual == null || score > atual.getScore()) {
            melhores.put(obra.getId(), Recomendacao.builder()
                    .usuario(usuario)
                    .obra(obra)
                    .algoritmo(algoritmo)
                    .score(score)
                    .geradaEm(agora)
                    .build());
        }
    }

    private boolean podeAssistir(Usuario usuario, ObraAudiovisual obra) {
        ClassificacaoIndicativa classificacao = obra.getClassificacaoIndicativa();

        if (classificacao.isAdulto() && !Boolean.TRUE.equals(usuario.getAceitaConteudoAdulto())) {
            return false;
        }
        if (usuario.getDataNascimento() != null) {
            int idade = Period.between(usuario.getDataNascimento(), LocalDate.now()).getYears();
            return idade >= classificacao.getIdadeMinima();
        }
        return true;
    }
}