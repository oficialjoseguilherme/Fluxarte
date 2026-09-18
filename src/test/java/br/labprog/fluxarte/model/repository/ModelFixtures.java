package br.labprog.fluxarte.model.repository;

import br.labprog.fluxarte.model.Evento;
import br.labprog.fluxarte.model.GeneroObra;
import br.labprog.fluxarte.model.MidiaStreaming;
import br.labprog.fluxarte.model.ObraAudiovisual;
import br.labprog.fluxarte.model.Usuario;
import br.labprog.fluxarte.model.enums.ClassificacaoIndicativa;
import br.labprog.fluxarte.model.enums.FormatoMidia;
import br.labprog.fluxarte.model.enums.NomeGenero;
import br.labprog.fluxarte.model.enums.Resolucao;
import br.labprog.fluxarte.model.enums.StatusObra;
import br.labprog.fluxarte.model.enums.TipoMidia;
import br.labprog.fluxarte.model.enums.TipoPlayer;

import java.time.LocalDate;

final class ModelFixtures {

    private ModelFixtures() {
    }

    static Usuario usuario(String email) {
        return Usuario.builder()
                .nome("Pessoa Teste")
                .email(email)
                .senhaHash("hash-seguro")
                .nomeExibicao("Pessoa")
                .dataNascimento(LocalDate.of(1990, 1, 1))
                .build();
    }

    static GeneroObra genero(NomeGenero nome) {
        return GeneroObra.builder().nome(nome).build();
    }

    static ObraAudiovisual obra(String titulo) {
        return ObraAudiovisual.builder()
                .titulo(titulo)
                .tituloOriginal(titulo + " Original")
                .diretor("Diretora Teste")
                .sinopse("Sinopse da obra")
                .anoProducao(2025)
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
                .eduplayEmbedUrl("https://eduplay.test/embed/1")
                .cdnStreamUrl("https://cdn.test/video.m3u8")
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
                .dataInicio(LocalDate.of(2026, 9, 1))
                .dataFim(LocalDate.of(2026, 9, 30))
                .bannerUrl("https://cdn.test/evento.jpg")
                .build();
    }
}
