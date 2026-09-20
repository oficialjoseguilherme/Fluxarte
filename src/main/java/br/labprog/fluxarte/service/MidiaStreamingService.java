package br.labprog.fluxarte.service;

import br.labprog.fluxarte.dto.request.MidiaStreamingRequest;
import br.labprog.fluxarte.dto.response.MidiaStreamingResponse;
import br.labprog.fluxarte.mapper.MidiaStreamingMapper;
import br.labprog.fluxarte.model.MidiaStreaming;
import br.labprog.fluxarte.model.ObraAudiovisual;
import br.labprog.fluxarte.model.enums.TipoMidia;
import br.labprog.fluxarte.model.enums.TipoPlayer;
import br.labprog.fluxarte.repository.MidiaStreamingRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.NoSuchElementException;

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
    public MidiaStreaming cadastrar(Long obraId, MidiaStreaming dados) {
        validar(dados);

        ObraAudiovisual obra = obraService.buscarEntidadePorId(obraId);

        dados.setObra(obra);
        dados.setAtualizadoEm(LocalDateTime.now());

        return midiaRepository.save(dados);
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

    @Transactional
    public MidiaStreaming atualizar(Long id, MidiaStreaming dados) {
        validar(dados);

        MidiaStreaming midia = buscarEntidadePorId(id);
        midia.setTipoMidia(dados.getTipoMidia());
        midia.setEduplayEmbedUrl(dados.getEduplayEmbedUrl());
        midia.setCdnStreamUrl(dados.getCdnStreamUrl());
        midia.setResolucao(dados.getResolucao());
        midia.setFormato(dados.getFormato());
        midia.setPlayerType(dados.getPlayerType());
        midia.setDuracaoSegundos(dados.getDuracaoSegundos());
        midia.setThumbsSpriteUrl(dados.getThumbsSpriteUrl());
        midia.setAtualizadoEm(LocalDateTime.now());

        return midiaRepository.save(midia);
    }

    @Transactional(readOnly = true)
    public MidiaStreamingResponse buscarPorId(Long id) {
        return midiaMapper.toResponse(buscarEntidadePorId(id));
    }

    @Transactional(readOnly = true)
    public MidiaStreaming buscarEntidadePorId(Long id) {
        return midiaRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Midia nao encontrada: " + id));
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

    private void validar(MidiaStreaming dados) {
        if (dados == null) {
            throw new IllegalArgumentException("Dados da midia sao obrigatorios");
        }
        if (dados.getTipoMidia() == null) {
            throw new IllegalArgumentException("Tipo da midia e obrigatorio");
        }
        if (dados.getDuracaoSegundos() != null && dados.getDuracaoSegundos() <= 0) {
            throw new IllegalArgumentException("Duracao da midia deve ser maior que zero");
        }
        if (dados.getPlayerType() == TipoPlayer.EMBED && isVazio(dados.getEduplayEmbedUrl())) {
            throw new IllegalArgumentException("Player EMBED exige a URL de embed do Eduplay");
        }
        if (dados.getPlayerType() == TipoPlayer.NATIVE && isVazio(dados.getCdnStreamUrl())) {
            throw new IllegalArgumentException("Player NATIVE exige a URL de stream da CDN");
        }
    }

    private boolean isVazio(String valor) {
        return valor == null || valor.isBlank();
    }
}