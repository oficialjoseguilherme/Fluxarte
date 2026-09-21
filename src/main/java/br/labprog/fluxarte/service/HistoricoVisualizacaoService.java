package br.labprog.fluxarte.service;

import br.labprog.fluxarte.dto.request.HistoricoVisualizacaoRequest;
import br.labprog.fluxarte.dto.response.HistoricoVisualizacaoResponse;
import br.labprog.fluxarte.mapper.HistoricoVisualizacaoMapper;
import br.labprog.fluxarte.model.HistoricoVisualizacao;
import br.labprog.fluxarte.model.MidiaStreaming;
import br.labprog.fluxarte.model.Usuario;
import br.labprog.fluxarte.repository.HistoricoVisualizacaoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class HistoricoVisualizacaoService {

    private final HistoricoVisualizacaoRepository historicoRepository;
    private final UsuarioService usuarioService;
    private final MidiaStreamingService midiaService;
    private final HistoricoVisualizacaoMapper historicoMapper;

    @Transactional
    public HistoricoVisualizacaoResponse registrar(UUID usuarioId, HistoricoVisualizacaoRequest request) {
        if (request == null) {
            throw new IllegalArgumentException("Dados de historico sao obrigatorios");
        }
        Usuario usuario = usuarioService.buscarEntidadePorId(usuarioId);
        MidiaStreaming midia = midiaService.buscarEntidadePorId(request.midiaId());

        HistoricoVisualizacao historico = historicoMapper.toEntity(request);
        historico.setUsuario(usuario);
        historico.setObra(midia.getObra());
        historico.setMidia(midia);
        historico.setDataVisualizacao(LocalDateTime.now());

        return historicoMapper.toResponse(historicoRepository.save(historico));
    }

    @Transactional
    public HistoricoVisualizacao registrar(UUID usuarioId, Long midiaId,
                                           Integer tempoAssistidoSegundos, Boolean concluido) {
        if (tempoAssistidoSegundos != null && tempoAssistidoSegundos < 0) {
            throw new IllegalArgumentException("Tempo assistido nao pode ser negativo");
        }

        Usuario usuario = usuarioService.buscarEntidadePorId(usuarioId);
        MidiaStreaming midia = midiaService.buscarEntidadePorId(midiaId);

        HistoricoVisualizacaoRequest request = new HistoricoVisualizacaoRequest(
                midiaId, tempoAssistidoSegundos, concluido);
        HistoricoVisualizacao historico = historicoMapper.toEntity(request);
        historico.setUsuario(usuario);
        historico.setObra(midia.getObra());
        historico.setMidia(midia);
        historico.setDataVisualizacao(LocalDateTime.now());

        return historicoRepository.save(historico);
    }

    @Transactional(readOnly = true)
    public List<HistoricoVisualizacao> listarPorUsuario(UUID usuarioId) {
        return historicoRepository.findByUsuarioIdOrderByDataVisualizacaoDesc(usuarioId);
    }

    @Transactional(readOnly = true)
    public List<HistoricoVisualizacaoResponse> listarPorUsuarioResponse(UUID usuarioId) {
        return historicoRepository.findByUsuarioIdOrderByDataVisualizacaoDesc(usuarioId).stream()
                .map(historicoMapper::toResponse)
                .toList();
    }
}