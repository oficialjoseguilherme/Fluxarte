package br.labprog.fluxarte.controller;

import br.labprog.fluxarte.dto.request.AtualizarPerfilRequest;
import br.labprog.fluxarte.dto.response.UsuarioResponse;
import br.labprog.fluxarte.model.Usuario;
import br.labprog.fluxarte.service.UsuarioService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/usuarios")
@RequiredArgsConstructor
public class UsuarioController {

    private final UsuarioService usuarioService;

    @GetMapping("/me")
    public ResponseEntity<UsuarioResponse> me(Authentication authentication) {
        UUID usuarioId = (UUID) authentication.getPrincipal();
        Usuario usuario = usuarioService.buscarEntidadePorId(usuarioId);
        return ResponseEntity.ok(mapearParaResponseCompleto(usuario));
    }

    @PutMapping("/me")
    public ResponseEntity<UsuarioResponse> atualizarMe(Authentication authentication,
                                                       @RequestBody AtualizarPerfilRequest request) {
        UUID usuarioId = (UUID) authentication.getPrincipal();
        Usuario usuario = usuarioService.atualizarPerfil(
                usuarioId, request.nomeExibicao(), request.aceitaConteudoAdulto()
        );
        return ResponseEntity.ok(mapearParaResponseCompleto(usuario));
    }

    @DeleteMapping("/me")
    public ResponseEntity<Void> desativarMe(Authentication authentication) {
        UUID usuarioId = (UUID) authentication.getPrincipal();
        usuarioService.desativar(usuarioId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}")
    public ResponseEntity<UsuarioResponse> buscarPorId(@PathVariable UUID id) {
        Usuario usuario = usuarioService.buscarEntidadePorId(id);
        return ResponseEntity.ok(mapearParaResponsePublico(usuario));
    }

    private UsuarioResponse mapearParaResponseCompleto(Usuario usuario) {
        return new UsuarioResponse(
                usuario.getId(),
                usuario.getNome(),
                usuario.getEmail(),
                usuario.getNomeExibicao(),
                usuario.getDataNascimento(),
                usuario.getAceitaConteudoAdulto(),
                usuario.getTipoUsuario(),
                usuario.getAtivo()
        );
    }

    private UsuarioResponse mapearParaResponsePublico(Usuario usuario) {
        return new UsuarioResponse(
                usuario.getId(), usuario.getNomeExibicao(), usuario.getTipoUsuario()
        );
    }
}