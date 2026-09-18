package br.labprog.fluxarte.service;

import br.labprog.fluxarte.model.GeneroObra;
import br.labprog.fluxarte.model.ObraAudiovisual;
import br.labprog.fluxarte.model.enums.NomeGenero;
import br.labprog.fluxarte.model.enums.StatusObra;
import br.labprog.fluxarte.repository.GeneroObraRepository;
import br.labprog.fluxarte.repository.ObraAudiovisualRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.NoSuchElementException;

@Service
@RequiredArgsConstructor
public class ObraAudiovisualService {

    private final ObraAudiovisualRepository obraRepository;
    private final GeneroObraRepository generoRepository;

    // a entidade nao gera criadoEm sozinha (sem @CreationTimestamp), entao o service define
    @Transactional
    public ObraAudiovisual cadastrar(ObraAudiovisual dados) {
        validar(dados);

        LocalDateTime agora = LocalDateTime.now();

        ObraAudiovisual obra = ObraAudiovisual.builder()
                .titulo(dados.getTitulo().trim())
                .tituloOriginal(dados.getTituloOriginal())
                .diretor(dados.getDiretor())
                .sinopse(dados.getSinopse())
                .anoProducao(dados.getAnoProducao())
                .idiomaOriginal(dados.getIdiomaOriginal())
                .posterUrl(dados.getPosterUrl())
                .bannerUrl(dados.getBannerUrl())
                .disponivel(dados.getDisponivel() != null ? dados.getDisponivel() : false)
                .status(dados.getStatus() != null ? dados.getStatus() : StatusObra.RASCUNHO)
                .dataInicioExibicao(dados.getDataInicioExibicao())
                .dataFimExibicao(dados.getDataFimExibicao())
                .classificacaoIndicativa(dados.getClassificacaoIndicativa())
                .criadoEm(agora)
                .atualizadoEm(agora)
                .build();

        return obraRepository.save(obra);
    }

    @Transactional
    public ObraAudiovisual atualizar(Long id, ObraAudiovisual dados) {
        validar(dados);

        ObraAudiovisual obra = buscarPorId(id);

        obra.setTitulo(dados.getTitulo().trim());
        obra.setTituloOriginal(dados.getTituloOriginal());
        obra.setDiretor(dados.getDiretor());
        obra.setSinopse(dados.getSinopse());
        obra.setAnoProducao(dados.getAnoProducao());
        obra.setIdiomaOriginal(dados.getIdiomaOriginal());
        obra.setPosterUrl(dados.getPosterUrl());
        obra.setBannerUrl(dados.getBannerUrl());
        obra.setDataInicioExibicao(dados.getDataInicioExibicao());
        obra.setDataFimExibicao(dados.getDataFimExibicao());
        obra.setClassificacaoIndicativa(dados.getClassificacaoIndicativa());
        obra.setAtualizadoEm(LocalDateTime.now());

        return obraRepository.save(obra);
    }

    @Transactional(readOnly = true)
    public ObraAudiovisual buscarPorId(Long id) {
        return obraRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Obra nao encontrada: " + id));
    }

    @Transactional(readOnly = true)
    public List<ObraAudiovisual> listarTodas() {
        return obraRepository.findAll();
    }

    @Transactional(readOnly = true)
    public List<ObraAudiovisual> buscarPorTitulo(String titulo) {
        return obraRepository.findByTituloContainingIgnoreCase(titulo);
    }

    // RF09
    @Transactional(readOnly = true)
    public List<ObraAudiovisual> buscarPorDiretor(String diretor) {
        return obraRepository.findByDiretorContainingIgnoreCase(diretor);
    }

    // RF13: catalogo visivel ao espectador
    @Transactional(readOnly = true)
    public List<ObraAudiovisual> listarExibiveis() {
        return obraRepository.findExibiveis(LocalDateTime.now(), StatusObra.ATIVO);
    }

    @Transactional
    public ObraAudiovisual alterarStatus(Long id, StatusObra novoStatus) {
        if (novoStatus == null) {
            throw new IllegalArgumentException("Status da obra e obrigatorio");
        }

        ObraAudiovisual obra = buscarPorId(id);
        obra.setStatus(novoStatus);
        obra.setAtualizadoEm(LocalDateTime.now());

        return obraRepository.save(obra);
    }

    @Transactional
    public ObraAudiovisual alterarDisponibilidade(Long id, boolean disponivel) {
        ObraAudiovisual obra = buscarPorId(id);
        obra.setDisponivel(disponivel);
        obra.setAtualizadoEm(LocalDateTime.now());

        return obraRepository.save(obra);
    }

    // Obra e o lado dono do N:N; se a linha do genero ainda nao existir na tabela, ela e criada
    @Transactional
    public ObraAudiovisual adicionarGenero(Long obraId, NomeGenero nome) {
        if (nome == null) {
            throw new IllegalArgumentException("Nome do genero e obrigatorio");
        }

        ObraAudiovisual obra = buscarPorId(obraId);
        GeneroObra genero = generoRepository.findByNome(nome)
                .orElseGet(() -> generoRepository.save(GeneroObra.builder().nome(nome).build()));

        obra.getGeneros().add(genero);
        obra.setAtualizadoEm(LocalDateTime.now());

        return obraRepository.save(obra);
    }

    @Transactional
    public ObraAudiovisual removerGenero(Long obraId, NomeGenero nome) {
        if (nome == null) {
            throw new IllegalArgumentException("Nome do genero e obrigatorio");
        }

        ObraAudiovisual obra = buscarPorId(obraId);
        obra.getGeneros().removeIf(genero -> genero.getNome() == nome);
        obra.setAtualizadoEm(LocalDateTime.now());

        return obraRepository.save(obra);
    }

    @Transactional
    public void excluir(Long id) {
        ObraAudiovisual obra = buscarPorId(id);
        obraRepository.delete(obra);
    }

    private void validar(ObraAudiovisual dados) {
        if (dados == null) {
            throw new IllegalArgumentException("Dados da obra sao obrigatorios");
        }
        if (dados.getTitulo() == null || dados.getTitulo().isBlank()) {
            throw new IllegalArgumentException("Titulo da obra e obrigatorio");
        }
        if (dados.getClassificacaoIndicativa() == null) {
            throw new IllegalArgumentException("Classificacao indicativa e obrigatoria");
        }
        // RF13: a janela de exibicao precisa ser coerente
        if (dados.getDataInicioExibicao() != null && dados.getDataFimExibicao() != null
                && dados.getDataFimExibicao().isBefore(dados.getDataInicioExibicao())) {
            throw new IllegalArgumentException("Data fim de exibicao nao pode ser anterior a data de inicio");
        }
    }
}