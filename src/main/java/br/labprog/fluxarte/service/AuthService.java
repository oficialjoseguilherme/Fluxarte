package br.labprog.fluxarte.service;

import br.labprog.fluxarte.dto.request.LoginRequest;
import br.labprog.fluxarte.dto.request.RefreshTokenRequest;
import br.labprog.fluxarte.dto.response.LoginResponse;
import br.labprog.fluxarte.model.RefreshToken;
import br.labprog.fluxarte.model.Usuario;
import br.labprog.fluxarte.repository.RefreshTokenRepository;
import br.labprog.fluxarte.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.Base64;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final SecureRandom secureRandom = new SecureRandom();

    private final RefreshTokenRepository refreshTokenRepository;
    private final UsuarioRepository usuarioRepository;
    private final UsuarioService usuarioService;

    @Value("${fluxarte.security.refresh-token.validade-dias:30}")
    private int validadeDias;

    @Value("${fluxarte.security.refresh-token.tamanho-bytes:32}")
    private int tamanhoTokenBytes;

    @Transactional
    public LoginResponse login(LoginRequest request) {
        if (request == null || request.email() == null || request.senha() == null) {
            throw new BadCredentialsException("Credenciais invalidas");
        }
        RefreshToken token = login(request.email(), request.senha());
        Usuario usuario = token.getUsuario();
        return new LoginResponse(
                token.getToken(),
                token.getExpiraEm(),
                "Bearer",
                usuario.getId(),
                usuario.getNome(),
                usuario.getEmail()
        );
    }

    @Transactional
    public LoginResponse renovar(RefreshTokenRequest request) {
        if (request == null || request.refreshToken() == null || request.refreshToken().isBlank()) {
            throw new BadCredentialsException("Refresh token invalido");
        }
        RefreshToken novo = renovar(request.refreshToken());
        Usuario usuario = novo.getUsuario();
        return new LoginResponse(
                novo.getToken(),
                novo.getExpiraEm(),
                "Bearer",
                usuario.getId(),
                usuario.getNome(),
                usuario.getEmail()
        );
    }

    @Transactional
    public RefreshToken login(String email, String senha) {
        if (email == null || senha == null || !usuarioService.autenticar(email, senha)) {
            throw new BadCredentialsException("Credenciais invalidas");
        }

        Usuario usuario = usuarioRepository.findByEmail(email.trim().toLowerCase())
                .orElseThrow(() -> new BadCredentialsException("Credenciais invalidas"));

        return emitirToken(usuario);
    }

    @Transactional
    public RefreshToken renovar(String tokenValor) {
        if (tokenValor == null || tokenValor.isBlank()) {
            throw new BadCredentialsException("Refresh token invalido");
        }

        RefreshToken atual = refreshTokenRepository.findByToken(tokenValor)
                .orElseThrow(() -> new BadCredentialsException("Refresh token invalido"));

        if (Boolean.TRUE.equals(atual.getRevogado())) {
            logoutTodasSessoes(atual.getUsuario().getId());
            throw new BadCredentialsException("Tentativa de reuso de sessao detectada");
        }

        if (atual.getExpiraEm().isBefore(LocalDateTime.now())) {
            atual.setRevogado(true);
            refreshTokenRepository.save(atual);
            throw new BadCredentialsException("Refresh token expirado");
        }

        if (!Boolean.TRUE.equals(atual.getUsuario().getAtivo())) {
            throw new BadCredentialsException("Usuario inativo");
        }

        atual.setRevogado(true);
        refreshTokenRepository.save(atual);

        return emitirToken(atual.getUsuario());
    }

    @Transactional
    public void logout(String tokenValor) {
        if (tokenValor == null || tokenValor.isBlank()) {
            return;
        }
        refreshTokenRepository.findByTokenAndRevogadoFalse(tokenValor).ifPresent(token -> {
            token.setRevogado(true);
            refreshTokenRepository.save(token);
        });
    }

    @Transactional
    public void logoutTodasSessoes(UUID usuarioId) {
        if (usuarioId == null) {
            return;
        }
        refreshTokenRepository.findByUsuarioIdAndRevogadoFalse(usuarioId).forEach(token -> {
            token.setRevogado(true);
            refreshTokenRepository.save(token);
        });
    }

    private RefreshToken emitirToken(Usuario usuario) {
        RefreshToken token = RefreshToken.builder()
                .usuario(usuario)
                .token(gerarTokenSeguro())
                .expiraEm(LocalDateTime.now().plusDays(validadeDias))
                .revogado(false)
                .build();

        return refreshTokenRepository.save(token);
    }

    private String gerarTokenSeguro() {
        byte[] bytes = new byte[tamanhoTokenBytes];
        secureRandom.nextBytes(bytes);
        return Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);
    }
}