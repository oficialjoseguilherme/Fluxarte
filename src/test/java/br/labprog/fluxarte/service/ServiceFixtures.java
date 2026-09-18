package br.labprog.fluxarte.service;

import br.labprog.fluxarte.model.Evento;
import br.labprog.fluxarte.model.EventoObra;
import br.labprog.fluxarte.model.MidiaStreaming;
import br.labprog.fluxarte.model.ObraAudiovisual;
import br.labprog.fluxarte.model.Usuario;
import br.labprog.fluxarte.model.enums.CategoriaEvento;
import br.labprog.fluxarte.model.enums.ClassificacaoIndicativa;
import br.labprog.fluxarte.model.enums.FormatoMidia;
import br.labprog.fluxarte.model.enums.Resolucao;
import br.labprog.fluxarte.model.enums.StatusObra;
import br.labprog.fluxarte.model.enums.TipoMidia;
import br.labprog.fluxarte.model.enums.TipoPlayer;
import br.labprog.fluxarte.model.enums.TipoUsuario;

import java.time.LocalDate;
import java.time.LocalDateTime;

final class ServiceFixtures {

    private ServiceFixtures() {
    }

    static Usuario usuario(String email) {
        return Usuario.builder()
                .nome("Pessoa Teste")
                .email(email)
                .senhaHash("{noop}segredo")
                .nomeExibicao("Pessoa Teste")
                .dataNascimento(LocalDate.of(1990, 1, 1))
                .aceitaConteudoAdulto(false)
                .tipoUsuario(TipoUsuario.ESPECTADOR)
                .ativo(true)
                .build();
    }

    static ObraAudiovisual obra(String titulo) {
        return ObraAudiovisual.builder()
                .titulo(titulo)
                .tituloOriginal(titulo + " Original")
                .diretor("Diretora Teste")
                .sinopse("Sinopse")
                .anoProducao(2026)
                .idiomaOriginal("pt-BR")
                .posterUrl("https://cdn.test/poster.jpg")
                .bannerUrl("https://cdn.test/banner.jpg")
                .disponivel(true)
                .status(StatusObra.ATIVO)
                .classificacaoIndicativa(ClassificacaoIndicativa.LIVRE)
                .build();
    }

    static MidiaStreaming midia(ObraAudiovisual obra, TipoMidia tipo) {
        return MidiaStreaming.builder()
                .obra(obra)
                .tipoMidia(tipo)
                .cdnStreamUrl("https://cdn.test/video.m3u8")
                .eduplayEmbedUrl("https://eduplay.test/embed/1")
                .resolucao(Resolucao.R1080P)
                .formato(FormatoMidia.HLS)
                .playerType(TipoPlayer.NATIVE)
                .duracaoSegundos(1200)
                .thumbsSpriteUrl("https://cdn.test/sprite.jpg")
                .build();
    }

    static Evento evento(String nome) {
        return Evento.builder()
                .nome(nome)
                .descricao("Evento de teste")
                .dataInicio(LocalDate.now().minusDays(1))
                .dataFim(LocalDate.now().plusDays(1))
                .bannerUrl("https://cdn.test/evento.jpg")
                .build();
    }

    static EventoObra eventoObra() {
        return EventoObra.builder()
                .categoria(CategoriaEvento.MOSTRA)
                .dataEstreiaEventoInicio(LocalDateTime.now().plusDays(1))
                .dataEstreiaEventoFim(LocalDateTime.now().plusDays(2))
                .premiacao("Menção honrosa")
                .destaque(true)
                .build();
    }
}
