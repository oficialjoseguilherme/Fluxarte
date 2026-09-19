package br.labprog.fluxarte.repository;

import br.labprog.fluxarte.model.RefreshToken;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {

    Optional<RefreshToken> findByTokenAndRevogadoFalse(String token);

    Optional<RefreshToken> findByToken(String token);

    List<RefreshToken> findByUsuarioIdAndRevogadoFalse(UUID usuarioId);
}