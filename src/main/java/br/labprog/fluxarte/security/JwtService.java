package br.labprog.fluxarte.security;

import br.labprog.fluxarte.model.enums.TipoUsuario;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.UUID;

@Service
public class JwtService {

    private final SecretKey key;
    private final long expiracaoMs;

    public JwtService(@Value("${jwt.secret}") String secret,
                      @Value("${jwt.expiracao-ms:900000}") long expiracaoMs) {
        this.key = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
        this.expiracaoMs = expiracaoMs;
    }

    public String gerarToken(UUID usuarioId, String email) {
        return gerarToken(usuarioId, email, TipoUsuario.ESPECTADOR);
    }

    public String gerarToken(UUID usuarioId, String email, TipoUsuario tipoUsuario) {
        Date agora = new Date();
        Date expiracao = new Date(agora.getTime() + expiracaoMs);

        return Jwts.builder()
                .subject(usuarioId.toString())
                .claim("email", email)
                .claim("role", tipoUsuario != null ? tipoUsuario.name() : TipoUsuario.ESPECTADOR.name())
                .issuedAt(agora)
                .expiration(expiracao)
                .signWith(key)
                .compact();
    }

    public UUID extrairUsuarioId(String token) {
        return UUID.fromString(extrairClaims(token).getSubject());
    }

    public String extrairRole(String token) {
        return extrairClaims(token).get("role", String.class);
    }

    public Claims extrairClaims(String token) {
        return Jwts.parser()
                .verifyWith(key)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    public boolean tokenValido(String token) {
        try {
            extrairClaims(token);
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}