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

import br.labprog.fluxarte.exceptions.RecursoNaoEncontradoException;
import br.labprog.fluxarte.exceptions.RegraNegocioException;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ObraAudiovisualService {

    private final ObraAudiovisualRepository obraRepository;
    private final GeneroObraRepository generoRepository;
    private final ObraAudiovisualMapper obraMapper;

    @Transactional
    public ObraAudiovisualResponse cadastrar(ObraAudiovisualRequest request) {
        if (request == null) {
            throw new RegraNegocioException("Dados da obra sao obrigatorios");
        }

        LocalDateTime agora = LocalDateTime.now();
        ObraAudiovisual obra = obraMapper.toEntity(request);
        obra.setCriadoEm(agora);
        obra.setAtualizadoEm(agora);

        ObraAudiovisual salva = obraRepository.save(obra);
        return obraMapper.toResponse(salva);
    }

    @Transactional
    public ObraAudiovisualResponse atualizar(Long id, ObraAudiovisualRequest request) {
        if (request == null) {
            throw new RegraNegocioException("Dados da obra sao obrigatorios");
        }

        ObraAudiovisual obra = buscarEntidadePorId(id);
        obraMapper.updateEntityFromRequest(request, obra);
        obra.setAtualizadoEm(LocalDateTime.now());

        ObraAudiovisual salva = obraRepository.save(obra);
        return obraMapper.toResponse(salva);
    }

    @Transactional(readOnly = true)
    public ObraAudiovisualResponse buscarPorId(Long id) {
        return obraMapper.toResponse(buscarEntidadePorId(id));
    }

    @Transactional(readOnly = true)
    public ObraAudiovisual buscarEntidadePorId(Long id) {
        return obraRepository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Obra nao encontrada"));
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
    public ObraAudiovisualResponse alterarStatus(Long id, StatusObra novoStatus) {
        if (novoStatus == null) {
            throw new RegraNegocioException("Status da obra e obrigatorio");
        }

        ObraAudiovisual obra = buscarEntidadePorId(id);
        obra.setStatus(novoStatus);
        obra.setAtualizadoEm(LocalDateTime.now());

        return obraMapper.toResponse(obraRepository.save(obra));
    }

    @Transactional
    public ObraAudiovisualResponse alterarDisponibilidade(Long id, boolean disponivel) {
        ObraAudiovisual obra = buscarEntidadePorId(id);
        obra.setDisponivel(disponivel);
        obra.setAtualizadoEm(LocalDateTime.now());

        return obraMapper.toResponse(obraRepository.save(obra));
    }

    @Transactional
    public ObraAudiovisualResponse adicionarGenero(Long obraId, NomeGenero nome) {
        if (nome == null) {
            throw new RegraNegocioException("Nome do genero e obrigatorio");
        }

        ObraAudiovisual obra = buscarEntidadePorId(obraId);
        GeneroObra genero = buscarOuCriarGenero(nome);
        obra.getGeneros().add(genero);
        obra.setAtualizadoEm(LocalDateTime.now());

        return obraMapper.toResponse(obraRepository.save(obra));
    }

    private GeneroObra buscarOuCriarGenero(NomeGenero nome) {
        return generoRepository.findByNome(nome)
                .orElseGet(() -> generoRepository.save(GeneroObra.builder().nome(nome).build()));
    }

    @Transactional
    public ObraAudiovisualResponse removerGenero(Long obraId, NomeGenero nome) {
        if (nome == null) {
            throw new RegraNegocioException("Nome do genero e obrigatorio");
        }

        ObraAudiovisual obra = buscarEntidadePorId(obraId);
        obra.getGeneros().removeIf(genero -> genero.getNome() == nome);
        obra.setAtualizadoEm(LocalDateTime.now());

        return obraMapper.toResponse(obraRepository.save(obra));
    }

    @Transactional
    public void excluir(Long id) {
        ObraAudiovisual obra = buscarEntidadePorId(id);
        obraRepository.delete(obra);
    }
}