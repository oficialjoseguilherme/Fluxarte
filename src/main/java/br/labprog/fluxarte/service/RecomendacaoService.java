package br.labprog.fluxarte.service;

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
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class RecomendacaoService {

    private static final int MAX_RECOMENDACOES = 20;

    private final RecomendacaoRepository recomendacaoRepository;
    private final UsuarioService usuarioService;
    private final HistoricoVisualizacaoService historicoService;
    private final ObraAudiovisualService obraService;

    // Gera de novo as recomendacoes do usuario (RF17): as antigas sao substituidas.
    // Uma obra so aparece uma vez, com o algoritmo que deu o maior score para ela.
    @Transactional
    public List<Recomendacao> gerarParaUsuario(UUID usuarioId) {
        Usuario usuario = usuarioService.buscarEntidadePorId(usuarioId);
        List<HistoricoVisualizacao> historico = historicoService.listarPorUsuario(usuarioId);

        // obras ja assistidas, por id (sem repeticao)
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

    // PREFERENCIA_GENERO: fracao dos generos preferidos do usuario que a obra cobre
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

    // HISTORICO: peso dos generos da obra dentro dos generos que o usuario ja assistiu
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

    // SIMILAR_DIRETOR: obra do mesmo diretor de algo que o usuario ja assistiu
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

    // guarda so a melhor recomendacao de cada obra; score zero nao e recomendacao
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

    // respeita a preferencia de conteudo adulto e a idade minima da classificacao indicativa
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