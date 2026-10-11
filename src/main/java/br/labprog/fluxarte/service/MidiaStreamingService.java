package br.labprog.fluxarte.service;

import br.labprog.fluxarte.dto.request.MidiaStreamingRequest;
import br.labprog.fluxarte.dto.response.MidiaStreamingResponse;
import br.labprog.fluxarte.mapper.MidiaStreamingMapper;
import br.labprog.fluxarte.model.MidiaStreaming;
import br.labprog.fluxarte.model.ObraAudiovisual;
import br.labprog.fluxarte.model.enums.TipoMidia;
import br.labprog.fluxarte.repository.MidiaStreamingRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import br.labprog.fluxarte.exceptions.RecursoNaoEncontradoException;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class MidiaStreamingService {

    private final MidiaStreamingRepository midiaRepository;
    private final ObraAudiovisualService obraService;
    private final MidiaStreamingMapper midiaMapper;

    @Transactional
    public MidiaStreamingResponse cadastrar(MidiaStreamingRequest request) {
        if (request == null) {
            throw new IllegalArgumentException("Dados da midia sao obrigatorios");
        }

        ObraAudiovisual obra = obraService.buscarEntidadePorId(request.obraId());

        MidiaStreaming midia = midiaMapper.toEntity(request);
        midia.setObra(obra);
        midia.setAtualizadoEm(LocalDateTime.now());

        MidiaStreaming salva = midiaRepository.save(midia);
        return midiaMapper.toResponse(salva);
    }

    @Transactional
    public MidiaStreamingResponse atualizar(Long id, MidiaStreamingRequest request) {
        if (request == null) {
            throw new IllegalArgumentException("Dados da midia sao obrigatorios");
        }

        MidiaStreaming midia = buscarEntidadePorId(id);
        midiaMapper.updateEntityFromRequest(request, midia);
        midia.setAtualizadoEm(LocalDateTime.now());

        MidiaStreaming salva = midiaRepository.save(midia);
        return midiaMapper.toResponse(salva);
    }

    @Transactional(readOnly = true)
    public MidiaStreamingResponse buscarPorId(Long id) {
        return midiaMapper.toResponse(buscarEntidadePorId(id));
    }

    @Transactional(readOnly = true)
    public MidiaStreaming buscarEntidadePorId(Long id) {
        return midiaRepository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Midia nao encontrada"));
    }

    @Transactional(readOnly = true)
    public List<MidiaStreamingResponse> listarPorObra(Long obraId) {
        return midiaRepository.findByObraId(obraId).stream()
                .map(midiaMapper::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<MidiaStreamingResponse> listarPorObraETipo(Long obraId, TipoMidia tipoMidia) {
        return midiaRepository.findByObraIdAndTipoMidia(obraId, tipoMidia).stream()
                .map(midiaMapper::toResponse)
                .toList();
    }

    @Transactional
    public void excluir(Long id) {
        MidiaStreaming midia = buscarEntidadePorId(id);
        midiaRepository.delete(midia);
    }
}