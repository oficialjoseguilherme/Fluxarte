package br.labprog.fluxarte.service;

import br.labprog.fluxarte.dto.request.ProgressoVisualizacaoRequest;
import br.labprog.fluxarte.dto.response.ProgressoVisualizacaoResponse;
import br.labprog.fluxarte.exceptions.RegraNegocioException;
import br.labprog.fluxarte.mapper.ProgressoVisualizacaoMapper;
import br.labprog.fluxarte.model.MidiaStreaming;
import br.labprog.fluxarte.model.ProgressoVisualizacao;
import br.labprog.fluxarte.model.Usuario;
import br.labprog.fluxarte.repository.ProgressoVisualizacaoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ProgressoVisualizacaoService {

    private final ProgressoVisualizacaoRepository progressoRepository;
    private final UsuarioService usuarioService;
    private final MidiaStreamingService midiaService;
    private final ProgressoVisualizacaoMapper progressoMapper;

    @Transactional
    public Optional<ProgressoVisualizacaoResponse> salvarProgresso(UUID usuarioId, ProgressoVisualizacaoRequest request) {
        if (request == null) {
            throw new RegraNegocioException("Dados de progresso sao obrigatorios");
        }

        MidiaStreaming midia = midiaService.buscarEntidadePorId(request.midiaId());

        if (midia.getDuracaoSegundos() != null && request.posicaoSegundos() > midia.getDuracaoSegundos()) {
            throw new RegraNegocioException("Posicao nao pode ser maior que a duracao da midia");
        }

        if (midia.getDuracaoSegundos() != null && request.posicaoSegundos().equals(midia.getDuracaoSegundos())) {
            remover(usuarioId, request.midiaId());
            return Optional.empty();
        }

        Usuario usuario = usuarioService.buscarEntidadePorId(usuarioId);
        ProgressoVisualizacao progresso = progressoRepository
                .findByUsuarioIdAndMidiaId(usuarioId, request.midiaId())
                .orElseGet(() -> ProgressoVisualizacao.builder()
                        .usuario(usuario)
                        .obra(midia.getObra())
                        .midia(midia)
                        .build());

        progresso.setPosicaoSegundos(request.posicaoSegundos());
        progresso.setAtualizadoEm(LocalDateTime.now());

        return Optional.of(progressoMapper.toResponse(progressoRepository.save(progresso)));
    }

    @Transactional(readOnly = true)
    public Optional<ProgressoVisualizacaoResponse> buscarProgresso(UUID usuarioId, Long midiaId) {
        return progressoRepository.findByUsuarioIdAndMidiaId(usuarioId, midiaId)
                .map(progressoMapper::toResponse);
    }

    @Transactional(readOnly = true)
    public List<ProgressoVisualizacaoResponse> listarPorUsuario(UUID usuarioId) {
        return progressoRepository.findByUsuarioIdOrderByAtualizadoEmDesc(usuarioId).stream()
                .map(progressoMapper::toResponse)
                .toList();
    }

    @Transactional
    public void remover(UUID usuarioId, Long midiaId) {
        progressoRepository.findByUsuarioIdAndMidiaId(usuarioId, midiaId)
                .ifPresent(progressoRepository::delete);
    }
}