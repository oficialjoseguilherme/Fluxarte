package br.labprog.fluxarte.service;

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

    // RF18: existe no maximo um progresso por usuario e midia, entao salvar cria ou atualiza.
    // A obra vem da propria midia, para nao haver combinacao inconsistente obra/midia.
    @Transactional
    public ProgressoVisualizacao salvarProgresso(UUID usuarioId, Long midiaId, Integer posicaoSegundos) {
        if (posicaoSegundos == null || posicaoSegundos < 0) {
            throw new IllegalArgumentException("Posicao deve ser maior ou igual a zero");
        }

        Usuario usuario = usuarioService.buscarPorId(usuarioId);
        MidiaStreaming midia = midiaService.buscarPorId(midiaId);

        if (midia.getDuracaoSegundos() != null && posicaoSegundos > midia.getDuracaoSegundos()) {
            throw new IllegalArgumentException("Posicao nao pode ser maior que a duracao da midia");
        }

        ProgressoVisualizacao progresso = progressoRepository
                .findByUsuarioIdAndMidiaId(usuarioId, midiaId)
                .orElseGet(() -> ProgressoVisualizacao.builder()
                        .usuario(usuario)
                        .obra(midia.getObra())
                        .midia(midia)
                        .build());

        progresso.setPosicaoSegundos(posicaoSegundos);
        progresso.setAtualizadoEm(LocalDateTime.now());

        return progressoRepository.save(progresso);
    }

    // vazio significa que o usuario ainda nao assistiu, entao o player comeca do zero
    @Transactional(readOnly = true)
    public Optional<ProgressoVisualizacao> buscarProgresso(UUID usuarioId, Long midiaId) {
        return progressoRepository.findByUsuarioIdAndMidiaId(usuarioId, midiaId);
    }

    // lista para "continuar assistindo", do mais recente para o mais antigo
    @Transactional(readOnly = true)
    public List<ProgressoVisualizacao> listarPorUsuario(UUID usuarioId) {
        return progressoRepository.findByUsuarioIdOrderByAtualizadoEmDesc(usuarioId);
    }

    // sem erro se nao houver progresso: o resultado desejado (nao ter progresso) ja e verdadeiro
    @Transactional
    public void remover(UUID usuarioId, Long midiaId) {
        progressoRepository.findByUsuarioIdAndMidiaId(usuarioId, midiaId)
                .ifPresent(progressoRepository::delete);
    }
}