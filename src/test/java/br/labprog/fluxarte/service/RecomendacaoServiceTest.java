package br.labprog.fluxarte.service;

import br.labprog.fluxarte.model.GeneroObra;
import br.labprog.fluxarte.model.MidiaStreaming;
import br.labprog.fluxarte.model.ObraAudiovisual;
import br.labprog.fluxarte.model.Recomendacao;
import br.labprog.fluxarte.model.Usuario;
import br.labprog.fluxarte.model.enums.AlgoritmoRecomendacao;
import br.labprog.fluxarte.model.enums.ClassificacaoIndicativa;
import br.labprog.fluxarte.model.enums.NomeGenero;
import br.labprog.fluxarte.model.enums.TipoMidia;
import br.labprog.fluxarte.repository.GeneroObraRepository;
import br.labprog.fluxarte.repository.RecomendacaoRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDate;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

class RecomendacaoServiceTest extends AbstractServiceTest {

    @Autowired RecomendacaoService service;
    @Autowired HistoricoVisualizacaoService historicoService;
    @Autowired GeneroObraRepository generoRepository;
    @Autowired RecomendacaoRepository repository;

    @Test
    void deveRecomendarPorPreferenciaEOrdenarPorScore() {
        GeneroObra documentario = generoRepository.saveAndFlush(
                GeneroObra.builder().nome(NomeGenero.DOCUMENTARIO).build());
        GeneroObra ficcao = generoRepository.saveAndFlush(
                GeneroObra.builder().nome(NomeGenero.FICCAO).build());
        Usuario usuario = salvarUsuario("preferencia-recomendacao@teste.com");
        usuario.getGenerosPreferidos().add(documentario);
        usuario.getGenerosPreferidos().add(ficcao);
        usuarioRepository.saveAndFlush(usuario);
        ObraAudiovisual completa = salvarObra("Dois gêneros");
        completa.getGeneros().add(documentario);
        completa.getGeneros().add(ficcao);
        obraRepository.saveAndFlush(completa);
        ObraAudiovisual parcial = salvarObra("Um gênero");
        parcial.getGeneros().add(documentario);
        obraRepository.saveAndFlush(parcial);

        var resultado = service.gerarParaUsuario(usuario.getId());

        assertEquals(2, resultado.size());
        assertEquals(completa.getId(), resultado.get(0).getObra().getId());
        assertEquals(1.0f, resultado.get(0).getScore());
        assertEquals(0.5f, resultado.get(1).getScore());
        assertEquals(AlgoritmoRecomendacao.PREFERENCIA_GENERO, resultado.get(0).getAlgoritmo());
        assertEquals(resultado.get(0).getId(), service.listarPorUsuario(usuario.getId()).get(0).getId());
    }

    @Test
    void deveExcluirObraAssistidaERecomendarPorDiretor() {
        Usuario usuario = salvarUsuario("diretor-recomendacao@teste.com");
        ObraAudiovisual assistida = salvarObra("Já assistida");
        assistida.setDiretor("Direção Comum");
        obraRepository.saveAndFlush(assistida);
        MidiaStreaming midia = salvarMidia(assistida, TipoMidia.PRINCIPAL);
        historicoService.registrar(usuario.getId(), midia.getId(), 1200, true);
        ObraAudiovisual candidata = salvarObra("Mesmo diretor");
        candidata.setDiretor("  direção comum  ");
        obraRepository.saveAndFlush(candidata);

        var resultado = service.gerarParaUsuario(usuario.getId());

        assertEquals(1, resultado.size());
        assertEquals(candidata.getId(), resultado.get(0).getObra().getId());
        assertEquals(AlgoritmoRecomendacao.SIMILAR_DIRETOR, resultado.get(0).getAlgoritmo());
        assertEquals(1.0f, resultado.get(0).getScore());
        assertTrue(resultado.stream().noneMatch(r -> r.getObra().getId().equals(assistida.getId())));
    }

    @Test
    void deveManterSomenteOMelhorAlgoritmoPorObraESubstituirListaAnterior() {
        GeneroObra genero = generoRepository.saveAndFlush(
                GeneroObra.builder().nome(NomeGenero.ANIMACAO).build());
        Usuario usuario = salvarUsuario("melhor-recomendacao@teste.com");
        usuario.getGenerosPreferidos().add(genero);
        usuarioRepository.saveAndFlush(usuario);
        ObraAudiovisual assistida = salvarObra("Base histórica");
        assistida.setDiretor("Diretor igual");
        assistida.getGeneros().add(genero);
        obraRepository.saveAndFlush(assistida);
        historicoService.registrar(usuario.getId(),
                salvarMidia(assistida, TipoMidia.PRINCIPAL).getId(), 100, true);
        ObraAudiovisual candidata = salvarObra("Candidata única");
        candidata.setDiretor("Diretor igual");
        candidata.getGeneros().add(genero);
        obraRepository.saveAndFlush(candidata);
        Recomendacao antiga = repository.saveAndFlush(Recomendacao.builder()
                .usuario(usuario).obra(assistida).algoritmo(AlgoritmoRecomendacao.HISTORICO)
                .score(0.1f).build());

        var resultado = service.gerarParaUsuario(usuario.getId());
        repository.flush();

        assertEquals(1, resultado.size());
        assertEquals(candidata.getId(), resultado.get(0).getObra().getId());
        assertEquals(1.0f, resultado.get(0).getScore());
        assertFalse(repository.existsById(antiga.getId()));
    }

    @Test
    void deveRespeitarClassificacaoIdadeEPreferenciaAdulta() {
        GeneroObra genero = generoRepository.saveAndFlush(
                GeneroObra.builder().nome(NomeGenero.FICCAO).build());
        Usuario menor = salvarUsuario("menor-recomendacao@teste.com");
        menor.setDataNascimento(LocalDate.now().minusYears(12));
        menor.setAceitaConteudoAdulto(false);
        menor.getGenerosPreferidos().add(genero);
        usuarioRepository.saveAndFlush(menor);
        ObraAudiovisual adulto = salvarObra("Adulto");
        adulto.setClassificacaoIndicativa(ClassificacaoIndicativa.DEZOITO);
        adulto.getGeneros().add(genero);
        obraRepository.saveAndFlush(adulto);
        ObraAudiovisual permitido = salvarObra("Permitido");
        permitido.setClassificacaoIndicativa(ClassificacaoIndicativa.DEZ);
        permitido.getGeneros().add(genero);
        obraRepository.saveAndFlush(permitido);

        var resultado = service.gerarParaUsuario(menor.getId());

        assertEquals(1, resultado.size());
        assertEquals(permitido.getId(), resultado.get(0).getObra().getId());
    }

    @Test
    void deveLimitarResultadoAVinteRecomendacoes() {
        GeneroObra genero = generoRepository.saveAndFlush(
                GeneroObra.builder().nome(NomeGenero.DOCUMENTARIO).build());
        Usuario usuario = salvarUsuario("limite-recomendacao@teste.com");
        usuario.getGenerosPreferidos().add(genero);
        usuarioRepository.saveAndFlush(usuario);
        var obras = new ArrayList<ObraAudiovisual>();
        for (int i = 0; i < 25; i++) {
            ObraAudiovisual obra = ServiceFixtures.obra("Candidata " + i);
            obra.getGeneros().add(genero);
            obras.add(obra);
        }
        obraRepository.saveAllAndFlush(obras);

        assertEquals(20, service.gerarParaUsuario(usuario.getId()).size());
        assertEquals(20, repository.findByUsuarioIdOrderByScoreDesc(usuario.getId()).size());
    }
}
