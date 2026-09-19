package br.labprog.fluxarte.service;

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

    // RF17: cada sessao de visualizacao gera uma linha nova (o historico nao e sobrescrito).
    // A obra vem da propria midia, para nao haver combinacao inconsistente obra/midia.
    @Transactional
    public HistoricoVisualizacao registrar(UUID usuarioId, Long midiaId,
                                           Integer tempoAssistidoSegundos, Boolean concluido) {
        if (tempoAssistidoSegundos != null && tempoAssistidoSegundos < 0) {
            throw new IllegalArgumentException("Tempo assistido nao pode ser negativo");
        }

        Usuario usuario = usuarioService.buscarEntidadePorId(usuarioId);
        MidiaStreaming midia = midiaService.buscarPorId(midiaId);

        HistoricoVisualizacao historico = HistoricoVisualizacao.builder()
                .usuario(usuario)
                .obra(midia.getObra())
                .midia(midia)
                .dataVisualizacao(LocalDateTime.now())
                .tempoAssistidoSegundos(tempoAssistidoSegundos)
                .concluido(concluido != null && concluido)
                .build();

        return historicoRepository.save(historico);
    }

    // mais recente primeiro
    @Transactional(readOnly = true)
    public List<HistoricoVisualizacao> listarPorUsuario(UUID usuarioId) {
        return historicoRepository.findByUsuarioIdOrderByDataVisualizacaoDesc(usuarioId);
    }
}