package br.labprog.fluxarte.service;

import br.labprog.fluxarte.dto.request.AtividadeUsuarioRequest;
import br.labprog.fluxarte.dto.response.AtividadeUsuarioResponse;
import br.labprog.fluxarte.mapper.AtividadeUsuarioMapper;
import br.labprog.fluxarte.model.AtividadeUsuario;
import br.labprog.fluxarte.model.Usuario;
import br.labprog.fluxarte.model.enums.TipoAtividade;
import br.labprog.fluxarte.repository.AtividadeUsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AtividadeUsuarioService {

    private final AtividadeUsuarioRepository atividadeRepository;
    private final UsuarioService usuarioService;
    private final ObraAudiovisualService obraService;
    private final AtividadeUsuarioMapper atividadeMapper;

    @Transactional
    public AtividadeUsuarioResponse registrar(UUID usuarioId, AtividadeUsuarioRequest request) {
        if (request == null) {
            throw new IllegalArgumentException("Dados da atividade sao obrigatorios");
        }
        Usuario usuario = usuarioService.buscarEntidadePorId(usuarioId);

        if (request.tipoAtividade() != TipoAtividade.BUSCA && request.obraIdReferenciada() != null) {
            obraService.buscarEntidadePorId(request.obraIdReferenciada());
        }

        AtividadeUsuario atividade = atividadeMapper.toEntity(request);
        atividade.setUsuario(usuario);
        if (request.tipoAtividade() == TipoAtividade.BUSCA) {
            atividade.setTermoBusca(request.termoBusca() != null ? request.termoBusca().trim() : null);
            atividade.setObraIdReferenciada(null);
        } else {
            atividade.setTermoBusca(null);
            atividade.setObraIdReferenciada(request.obraIdReferenciada());
        }

        return atividadeMapper.toResponse(atividadeRepository.save(atividade));
    }

    @Transactional
    public AtividadeUsuario registrar(UUID usuarioId, TipoAtividade tipo, String termoBusca,
                                      Long obraIdReferenciada, String metadadosJson) {
        if (tipo == null) {
            throw new IllegalArgumentException("Tipo da atividade e obrigatorio");
        }

        Usuario usuario = usuarioService.buscarEntidadePorId(usuarioId);

        if (tipo == TipoAtividade.BUSCA) {
            if (termoBusca == null || termoBusca.isBlank()) {
                throw new IllegalArgumentException("Atividade de busca exige o termo pesquisado");
            }
        } else {
            if (obraIdReferenciada == null) {
                throw new IllegalArgumentException("Atividade " + tipo + " exige a obra referenciada");
            }
            obraService.buscarEntidadePorId(obraIdReferenciada);
        }

        AtividadeUsuarioRequest request = new AtividadeUsuarioRequest(
                tipo,
                tipo == TipoAtividade.BUSCA ? termoBusca.trim() : null,
                tipo == TipoAtividade.BUSCA ? null : obraIdReferenciada,
                metadadosJson
        );
        AtividadeUsuario atividade = atividadeMapper.toEntity(request);
        atividade.setUsuario(usuario);

        return atividadeRepository.save(atividade);
    }

    @Transactional(readOnly = true)
    public List<AtividadeUsuario> listarPorUsuario(UUID usuarioId) {
        return atividadeRepository.findByUsuarioIdOrderByRegistradoEmDesc(usuarioId);
    }

    @Transactional(readOnly = true)
    public List<AtividadeUsuarioResponse> listarPorUsuarioResponse(UUID usuarioId) {
        return atividadeRepository.findByUsuarioIdOrderByRegistradoEmDesc(usuarioId).stream()
                .map(atividadeMapper::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<AtividadeUsuario> listarPorUsuarioETipo(UUID usuarioId, TipoAtividade tipo) {
        return atividadeRepository.findByUsuarioIdAndTipoAtividadeOrderByRegistradoEmDesc(usuarioId, tipo);
    }

    @Transactional(readOnly = true)
    public List<AtividadeUsuarioResponse> listarPorUsuarioETipoResponse(UUID usuarioId, TipoAtividade tipo) {
        return atividadeRepository.findByUsuarioIdAndTipoAtividadeOrderByRegistradoEmDesc(usuarioId, tipo).stream()
                .map(atividadeMapper::toResponse)
                .toList();
    }
}