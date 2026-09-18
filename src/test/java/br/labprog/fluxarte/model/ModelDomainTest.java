package br.labprog.fluxarte.model;

import br.labprog.fluxarte.model.enums.ClassificacaoIndicativa;
import br.labprog.fluxarte.model.enums.Resolucao;
import br.labprog.fluxarte.model.enums.StatusObra;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class ModelDomainTest {

    @Test
    void deveConsiderarLimitesDaJanelaDeExibicaoInclusivos() {
        LocalDateTime inicio = LocalDateTime.of(2026, 9, 1, 0, 0);
        LocalDateTime fim = LocalDateTime.of(2026, 9, 30, 23, 59);
        ObraAudiovisual obra = obraExibivel();
        obra.setDataInicioExibicao(inicio);
        obra.setDataFimExibicao(fim);

        assertTrue(obra.isExibivelEm(inicio));
        assertTrue(obra.isExibivelEm(fim));
        assertFalse(obra.isExibivelEm(inicio.minusNanos(1)));
        assertFalse(obra.isExibivelEm(fim.plusNanos(1)));
    }

    @Test
    void deveExigirDisponibilidadeEStatusAtivoParaExibicao() {
        ObraAudiovisual obra = obraExibivel();
        assertTrue(obra.isExibivelEm(LocalDateTime.now()));

        obra.setDisponivel(false);
        assertFalse(obra.isExibivelEm(LocalDateTime.now()));
        obra.setDisponivel(true);
        obra.setStatus(StatusObra.INATIVO);
        assertFalse(obra.isExibivelEm(LocalDateTime.now()));
    }

    @Test
    void deveCalcularPercentualAssistido() {
        MidiaStreaming midia = MidiaStreaming.builder().duracaoSegundos(200).build();
        ProgressoVisualizacao progresso = ProgressoVisualizacao.builder()
                .midia(midia).posicaoSegundos(50).build();

        assertEquals(25.0, progresso.getPercentualAssistido());
    }

    @Test
    void deveRetornarNuloQuandoDuracaoNaoPermiteCalculo() {
        ProgressoVisualizacao progresso = ProgressoVisualizacao.builder().posicaoSegundos(10).build();
        assertNull(progresso.getPercentualAssistido());

        progresso.setMidia(MidiaStreaming.builder().build());
        assertNull(progresso.getPercentualAssistido());

        progresso.getMidia().setDuracaoSegundos(0);
        assertNull(progresso.getPercentualAssistido());
    }

    @Test
    void deveExporRegrasDosEnums() {
        assertEquals(18, ClassificacaoIndicativa.DEZOITO.getIdadeMinima());
        assertTrue(ClassificacaoIndicativa.DEZOITO.isAdulto());
        assertFalse(ClassificacaoIndicativa.DEZESSEIS.isAdulto());
        assertEquals(Resolucao.R1080P, Resolucao.fromValor("1080p"));
        assertThrows(IllegalArgumentException.class, () -> Resolucao.fromValor("4k"));
    }

    private ObraAudiovisual obraExibivel() {
        return ObraAudiovisual.builder()
                .disponivel(true)
                .status(StatusObra.ATIVO)
                .build();
    }
}
