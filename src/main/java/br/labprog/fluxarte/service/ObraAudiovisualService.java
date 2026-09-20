package br.labprog.fluxarte.service;

import br.labprog.fluxarte.dto.request.ObraAudiovisualRequest;
import br.labprog.fluxarte.dto.response.ObraAudiovisualResponse;
import br.labprog.fluxarte.mapper.ObraAudiovisualMapper;
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
    private final ObraAudiovisualMapper obraMapper;

    @Transactional
    public ObraAudiovisualResponse cadastrar(ObraAudiovisualRequest request) {
        if (request == null) {
            throw new IllegalArgumentException("Dados da obra sao obrigatorios");
        }

        LocalDateTime agora = LocalDateTime.now();
        ObraAudiovisual obra = obraMapper.toEntity(request);
        obra.setCriadoEm(agora);
        obra.setAtualizadoEm(agora);

        ObraAudiovisual salva = obraRepository.save(obra);
        return obraMapper.toResponse(salva);
    }

    @Transactional
    public ObraAudiovisual cadastrar(ObraAudiovisual dados) {
        validar(dados);

        LocalDateTime agora = LocalDateTime.now();
        dados.setTitulo(dados.getTitulo().trim());
        if (dados.getDisponivel() == null) {
            dados.setDisponivel(false);
        }
        if (dados.getStatus() == null) {
            dados.setStatus(StatusObra.RASCUNHO);
        }
        dados.setCriadoEm(agora);
        dados.setAtualizadoEm(agora);

        return obraRepository.save(dados);
    }

    @Transactional
    public ObraAudiovisualResponse atualizar(Long id, ObraAudiovisualRequest request) {
        if (request == null) {
            throw new IllegalArgumentException("Dados da obra sao obrigatorios");
        }

        ObraAudiovisual obra = buscarEntidadePorId(id);
        obraMapper.updateEntityFromRequest(request, obra);
        obra.setAtualizadoEm(LocalDateTime.now());

        ObraAudiovisual salva = obraRepository.save(obra);
        return obraMapper.toResponse(salva);
    }

    @Transactional
    public ObraAudiovisual atualizar(Long id, ObraAudiovisual dados) {
        validar(dados);

        ObraAudiovisual obra = buscarEntidadePorId(id);
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
    public ObraAudiovisualResponse buscarPorId(Long id) {
        return obraMapper.toResponse(buscarEntidadePorId(id));
    }

    @Transactional(readOnly = true)
    public ObraAudiovisual buscarEntidadePorId(Long id) {
        return obraRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Obra nao encontrada: " + id));
    }

    @Transactional(readOnly = true)
    public List<ObraAudiovisualResponse> listarTodas() {
        return obraRepository.findAll().stream()
                .map(obraMapper::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<ObraAudiovisualResponse> buscarPorTitulo(String titulo) {
        return obraRepository.findByTituloContainingIgnoreCase(titulo).stream()
                .map(obraMapper::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<ObraAudiovisualResponse> buscarPorDiretor(String diretor) {
        return obraRepository.findByDiretorContainingIgnoreCase(diretor).stream()
                .map(obraMapper::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<ObraAudiovisual> listarExibiveis() {
        return obraRepository.findExibiveis(LocalDateTime.now(), StatusObra.ATIVO);
    }

    @Transactional
    public ObraAudiovisual alterarStatus(Long id, StatusObra novoStatus) {
        if (novoStatus == null) {
            throw new IllegalArgumentException("Status da obra e obrigatorio");
        }

        ObraAudiovisual obra = buscarEntidadePorId(id);
        obra.setStatus(novoStatus);
        obra.setAtualizadoEm(LocalDateTime.now());

        return obraRepository.save(obra);
    }

    @Transactional
    public ObraAudiovisual alterarDisponibilidade(Long id, boolean disponivel) {
        ObraAudiovisual obra = buscarEntidadePorId(id);
        obra.setDisponivel(disponivel);
        obra.setAtualizadoEm(LocalDateTime.now());

        return obraRepository.save(obra);
    }

    @Transactional
    public ObraAudiovisual adicionarGenero(Long obraId, NomeGenero nome) {
        if (nome == null) {
            throw new IllegalArgumentException("Nome do genero e obrigatorio");
        }

        ObraAudiovisual obra = buscarEntidadePorId(obraId);
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

        ObraAudiovisual obra = buscarEntidadePorId(obraId);
        obra.getGeneros().removeIf(genero -> genero.getNome() == nome);
        obra.setAtualizadoEm(LocalDateTime.now());

        return obraRepository.save(obra);
    }

    @Transactional
    public void excluir(Long id) {
        ObraAudiovisual obra = buscarEntidadePorId(id);
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
        if (dados.getDataInicioExibicao() != null && dados.getDataFimExibicao() != null
                && dados.getDataFimExibicao().isBefore(dados.getDataInicioExibicao())) {
            throw new IllegalArgumentException("Data fim de exibicao nao pode ser anterior a data de inicio");
        }
    }
}