package br.labprog.fluxarte.controller;

import br.labprog.fluxarte.dto.request.AtualizarPerfilRequest;
import br.labprog.fluxarte.model.Usuario;
import br.labprog.fluxarte.service.UsuarioService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.NoSuchElementException;
import java.util.UUID;

@RestController
@RequestMapping("/api/usuarios")
@RequiredArgsConstructor
public class UsuarioController {

    private final UsuarioService usuarioService;

    @GetMapping("/me")
    public ResponseEntity<?> me(Authentication authentication) {
        UUID usuarioId = (UUID) authentication.getPrincipal();
        Usuario usuario = usuarioService.buscarPorId(usuarioId);
        return ResponseEntity.ok(mapearParaResponseCompleto(usuario));
    }

    @PutMapping("/me")
    public ResponseEntity<?> atualizarMe(Authentication authentication,
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
    public ResponseEntity<?> buscarPorId(@PathVariable UUID id) {
        try {
            Usuario usuario = usuarioService.buscarPorId(id);
            return ResponseEntity.ok(mapearParaResponsePublico(usuario));
        } catch (NoSuchElementException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    private br.labprog.fluxarte.dto.auth.UsuarioResponse mapearParaResponseCompleto(Usuario usuario) {
        return new br.labprog.fluxarte.dto.auth.UsuarioResponse(
                usuario.getId(), usuario.getNome(), usuario.getEmail(),
                usuario.getNomeExibicao(), usuario.getTipoUsuario()
        );
    }

    private br.labprog.fluxarte.dto.response.UsuarioResponse mapearParaResponsePublico(Usuario usuario) {
        return new br.labprog.fluxarte.dto.response.UsuarioResponse(
                usuario.getId(), usuario.getNomeExibicao(), usuario.getTipoUsuario()
        );
    }
}