package br.labprog.fluxarte.controller;

import br.labprog.fluxarte.dto.auth.*;
import br.labprog.fluxarte.model.RefreshToken;
import br.labprog.fluxarte.model.Usuario;
import br.labprog.fluxarte.security.JwtService;
import br.labprog.fluxarte.service.AuthService;
import br.labprog.fluxarte.service.UsuarioService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;
    private final JwtService jwtService;
    private final UsuarioService usuarioService;

    @PostMapping("/registrar")
    public ResponseEntity<?> registrar(@RequestBody RegistroRequest request) {
        Usuario usuario = usuarioService.cadastrar(
                request.nome(), request.email(), request.senha(), request.dataNascimento()
        );
        return ResponseEntity.status(HttpStatus.CREATED).body(mapearParaResponse(usuario));
    }

    private UsuarioResponse mapearParaResponse(Usuario usuario) {
        return new UsuarioResponse(
                usuario.getId(), usuario.getNome(), usuario.getEmail(),
                usuario.getNomeExibicao(), usuario.getTipoUsuario()
        );
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest request) {
        RefreshToken refreshToken = authService.login(request.email(), request.senha());
        return ResponseEntity.ok(gerarResposta(refreshToken));
    }

    @PostMapping("/renovar")
    public ResponseEntity<?> renovar(@RequestBody RenovarTokenRequest request) {
        RefreshToken novoRefreshToken = authService.renovar(request.refreshToken());
        return ResponseEntity.ok(gerarResposta(novoRefreshToken));
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logout(@RequestBody LogoutRequest request) {
        authService.logout(request.refreshToken());
        return ResponseEntity.noContent().build();
    }

    private TokenResponse gerarResposta(RefreshToken refreshToken) {
        String accessToken = jwtService.gerarToken(
                refreshToken.getUsuario().getId(),
                refreshToken.getUsuario().getEmail()
        );
        return new TokenResponse(accessToken, refreshToken.getToken(), "Bearer");
    }
}