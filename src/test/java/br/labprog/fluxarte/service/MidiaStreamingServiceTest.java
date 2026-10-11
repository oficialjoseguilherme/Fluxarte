package br.labprog.fluxarte.service;

import br.labprog.fluxarte.dto.request.MidiaStreamingRequest;
import br.labprog.fluxarte.dto.response.MidiaStreamingResponse;
import br.labprog.fluxarte.model.MidiaStreaming;
import br.labprog.fluxarte.model.ObraAudiovisual;
import br.labprog.fluxarte.model.enums.FormatoMidia;
import br.labprog.fluxarte.model.enums.Resolucao;
import br.labprog.fluxarte.model.enums.TipoMidia;
import br.labprog.fluxarte.model.enums.TipoPlayer;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import br.labprog.fluxarte.exceptions.RecursoNaoEncontradoException;

import static org.junit.jupiter.api.Assertions.*;

class MidiaStreamingServiceTest extends AbstractServiceTest {

    @Autowired MidiaStreamingService service;

    @Test
    void deveCadastrarAtualizarEConsultarMidia() {
        ObraAudiovisual obra = salvarObra("Obra mídia");
        MidiaStreamingRequest requestCadastro = new MidiaStreamingRequest(
                obra.getId(),
                TipoMidia.PRINCIPAL,
                TipoPlayer.EMBED,
                "https://eduplay.rnp.br/embed/123",
                null,
                Resolucao.R1080P,
                FormatoMidia.MP4,
                3600,
                "https://cdn.test/sprite-dto.jpg"
        );

        MidiaStreamingResponse salva = service.cadastrar(requestCadastro);

        assertEquals(obra.getId(), salva.obraId());
        assertEquals(TipoMidia.PRINCIPAL, salva.tipoMidia());
        assertEquals(3600, salva.duracaoSegundos());

        MidiaStreamingRequest requestAtualizacao = new MidiaStreamingRequest(
                obra.getId(),
                TipoMidia.TRAILER,
                TipoPlayer.EMBED,
                "https://eduplay.rnp.br/embed/trailer",
                null,
                Resolucao.R720P,
                FormatoMidia.MP4,
                90,
                null
        );

        MidiaStreamingResponse atualizada = service.atualizar(salva.id(), requestAtualizacao);

        assertEquals(obra.getId(), atualizada.obraId());
        assertEquals(TipoMidia.TRAILER, atualizada.tipoMidia());
        assertEquals(90, atualizada.duracaoSegundos());
        assertNotNull(service.buscarEntidadePorId(atualizada.id()).getAtualizadoEm());
        assertEquals(atualizada.id(), service.buscarPorId(atualizada.id()).id());
        assertEquals(atualizada.id(), service.buscarEntidadePorId(atualizada.id()).getId());
        assertEquals(1, service.listarPorObra(obra.getId()).size());
        assertEquals(1, service.listarPorObraETipo(obra.getId(), TipoMidia.TRAILER).size());
    }

    @Test
    void deveRejeitarRequestNulo() {
        assertThrows(IllegalArgumentException.class, () -> service.cadastrar(null));
        assertThrows(IllegalArgumentException.class, () -> service.atualizar(1L, null));
    }

    @Test
    void deveRejeitarObraOuMidiaInexistente() {
        MidiaStreamingRequest request = new MidiaStreamingRequest(
                Long.MAX_VALUE,
                TipoMidia.PRINCIPAL,
                TipoPlayer.EMBED,
                "https://eduplay.rnp.br/embed/123",
                null,
                Resolucao.R1080P,
                FormatoMidia.MP4,
                120,
                null
        );
        assertThrows(RecursoNaoEncontradoException.class, () -> service.cadastrar(request));
        assertThrows(RecursoNaoEncontradoException.class, () -> service.buscarPorId(Long.MAX_VALUE));
    }

    @Test
    void deveExcluirMidia() {
        ObraAudiovisual obra = salvarObra("Excluir mídia");
        MidiaStreaming midia = salvarMidia(obra, TipoMidia.PRINCIPAL);
        service.excluir(midia.getId());
        midiaRepository.flush();
        assertFalse(midiaRepository.existsById(midia.getId()));
    }
}
