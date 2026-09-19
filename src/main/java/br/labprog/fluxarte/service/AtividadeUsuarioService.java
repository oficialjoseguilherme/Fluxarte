package br.labprog.fluxarte.service;

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

    // a atividade e um log: so e registrada e consultada, nunca alterada
    // BUSCA exige termoBusca; os demais tipos exigem obraIdReferenciada
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
            // obraIdReferenciada e um Long simples (sem FK), entao a existencia e checada aqui
            obraService.buscarPorId(obraIdReferenciada);
        }

        AtividadeUsuario atividade = AtividadeUsuario.builder()
                .usuario(usuario)
                .tipoAtividade(tipo)
                .termoBusca(tipo == TipoAtividade.BUSCA ? termoBusca.trim() : null)
                .obraIdReferenciada(tipo == TipoAtividade.BUSCA ? null : obraIdReferenciada)
                .metadadosJson(metadadosJson)
                .build();

        return atividadeRepository.save(atividade);
    }

    @Transactional(readOnly = true)
    public List<AtividadeUsuario> listarPorUsuario(UUID usuarioId) {
        return atividadeRepository.findByUsuarioIdOrderByRegistradoEmDesc(usuarioId);
    }

    @Transactional(readOnly = true)
    public List<AtividadeUsuario> listarPorUsuarioETipo(UUID usuarioId, TipoAtividade tipo) {
        return atividadeRepository.findByUsuarioIdAndTipoAtividadeOrderByRegistradoEmDesc(usuarioId, tipo);
    }
}