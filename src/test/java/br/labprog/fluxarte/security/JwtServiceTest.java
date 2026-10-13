package br.labprog.fluxarte.security;

import br.labprog.fluxarte.model.enums.TipoUsuario;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class JwtServiceTest {

    private static final String SECRET = "test-secret-chave-com-pelo-menos-32-caracteres";

    @Test
    void deveGerarTokenQueEleMesmoConsegueValidar() {
        JwtService jwtService = new JwtService(SECRET, 900000L);
        UUID usuarioId = UUID.randomUUID();

        String token = jwtService.gerarToken(usuarioId, "ana@fluxarte.com");

        assertThat(jwtService.tokenValido(token)).isTrue();
    }

    @Test
    void deveExtrairOMesmoUsuarioIdQueFoiColocadoNoToken() {
        JwtService jwtService = new JwtService(SECRET, 900000L);
        UUID usuarioId = UUID.randomUUID();

        String token = jwtService.gerarToken(usuarioId, "ana@fluxarte.com");

        assertThat(jwtService.extrairUsuarioId(token)).isEqualTo(usuarioId);
    }

    @Test
    void deveExtrairRolePadraoComoEspectadorQuandoNaoInformado() {
        JwtService jwtService = new JwtService(SECRET, 900000L);
        UUID usuarioId = UUID.randomUUID();

        String token = jwtService.gerarToken(usuarioId, "ana@fluxarte.com");

        assertThat(jwtService.extrairRole(token)).isEqualTo("ESPECTADOR");
    }

    @Test
    void deveExtrairRoleAdminQuandoInformado() {
        JwtService jwtService = new JwtService(SECRET, 900000L);
        UUID usuarioId = UUID.randomUUID();

        String token = jwtService.gerarToken(usuarioId, "admin@fluxarte.com", TipoUsuario.ADMIN);

        assertThat(jwtService.extrairRole(token)).isEqualTo("ADMIN");
    }

    @Test
    void deveConsiderarTokenExpiradoComoInvalido() throws InterruptedException {
        JwtService jwtService = new JwtService(SECRET, 1L);
        UUID usuarioId = UUID.randomUUID();

        String token = jwtService.gerarToken(usuarioId, "ana@fluxarte.com");
        Thread.sleep(50);

        assertThat(jwtService.tokenValido(token)).isFalse();
    }

    @Test
    void deveRejeitarTokenAssinadoComOutraChave() {
        JwtService jwtServiceOriginal = new JwtService(SECRET, 900000L);
        JwtService jwtServiceOutraChave = new JwtService("outra-chave-completamente-diferente-32c", 900000L);
        UUID usuarioId = UUID.randomUUID();

        String token = jwtServiceOriginal.gerarToken(usuarioId, "ana@fluxarte.com");

        assertThat(jwtServiceOutraChave.tokenValido(token)).isFalse();
    }

    @Test
    void deveRejeitarTokenCorrompido() {
        JwtService jwtService = new JwtService(SECRET, 900000L);
        UUID usuarioId = UUID.randomUUID();

        String token = jwtService.gerarToken(usuarioId, "ana@fluxarte.com");
        String tokenCorrompido = token + "adulterado";

        assertThat(jwtService.tokenValido(tokenCorrompido)).isFalse();
    }
}