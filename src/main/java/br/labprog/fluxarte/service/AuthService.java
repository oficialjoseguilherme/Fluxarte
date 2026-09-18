package br.labprog.fluxarte.service;

import br.labprog.fluxarte.model.RefreshToken;
import br.labprog.fluxarte.model.Usuario;
import br.labprog.fluxarte.repository.RefreshTokenRepository;
import br.labprog.fluxarte.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
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

    // validade do refresh token definida
    private static final int VALIDADE_DIAS = 30;
    private static final int TAMANHO_TOKEN_BYTES = 32;

    private final SecureRandom secureRandom = new SecureRandom();

    private final RefreshTokenRepository refreshTokenRepository;
    private final UsuarioRepository usuarioRepository;
    private final UsuarioService usuarioService;

    // a mensagem e a mesma para email inexistente, senha errada ou usuario inativo,
    // para nao revelar se o email esta cadastrado
    @Transactional
    public RefreshToken login(String email, String senha) {
        if (email == null || senha == null || !usuarioService.autenticar(email, senha)) {
            throw new BadCredentialsException("Credenciais invalidas");
        }

        Usuario usuario = usuarioRepository.findByEmail(email)
                .orElseThrow(() -> new BadCredentialsException("Credenciais invalidas"));

        return emitirToken(usuario);
    }

    // rotacao: o token usado e revogado e um novo e emitido no lugar
    @Transactional
    public RefreshToken renovar(String tokenValor) {
        RefreshToken atual = refreshTokenRepository.findByTokenAndRevogadoFalse(tokenValor)
                .orElseThrow(() -> new BadCredentialsException("Refresh token invalido"));

        if (atual.getExpiraEm().isBefore(LocalDateTime.now())) {
            throw new BadCredentialsException("Refresh token expirado");
        }
        if (!Boolean.TRUE.equals(atual.getUsuario().getAtivo())) {
            throw new BadCredentialsException("Usuario inativo");
        }

        atual.setRevogado(true);
        refreshTokenRepository.save(atual);

        return emitirToken(atual.getUsuario());
    }

    // sem erro se o token nao existir ou ja estiver revogado: o resultado desejado ja e verdadeiro
    @Transactional
    public void logout(String tokenValor) {
        refreshTokenRepository.findByTokenAndRevogadoFalse(tokenValor).ifPresent(token -> {
            token.setRevogado(true);
            refreshTokenRepository.save(token);
        });
    }

    // encerra todas as sessoes do usuario (ex: troca de senha ou suspeita de vazamento)
    @Transactional
    public void logoutTodasSessoes(UUID usuarioId) {
        refreshTokenRepository.findByUsuarioIdAndRevogadoFalse(usuarioId).forEach(token -> {
            token.setRevogado(true);
            refreshTokenRepository.save(token);
        });
    }

    private RefreshToken emitirToken(Usuario usuario) {
        RefreshToken token = RefreshToken.builder()
                .usuario(usuario)
                .token(gerarTokenSeguro())
                .expiraEm(LocalDateTime.now().plusDays(VALIDADE_DIAS))
                .revogado(false)
                .build();

        return refreshTokenRepository.save(token);
    }

    private String gerarTokenSeguro() {
        byte[] bytes = new byte[TAMANHO_TOKEN_BYTES];
        secureRandom.nextBytes(bytes);
        return Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);
    }
}