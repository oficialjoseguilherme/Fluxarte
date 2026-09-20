package br.labprog.fluxarte.service;

import br.labprog.fluxarte.dto.request.ObraAudiovisualRequest;
import br.labprog.fluxarte.dto.response.ObraAudiovisualResponse;
import br.labprog.fluxarte.model.ObraAudiovisual;
import br.labprog.fluxarte.model.enums.ClassificacaoIndicativa;
import br.labprog.fluxarte.model.enums.NomeGenero;
import br.labprog.fluxarte.model.enums.StatusObra;
import br.labprog.fluxarte.repository.GeneroObraRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDateTime;
import java.util.NoSuchElementException;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class ObraAudiovisualServiceTest extends AbstractServiceTest {

    @Autowired ObraAudiovisualService service;
    @Autowired GeneroObraRepository generoRepository;

    @Test
    void deveCadastrarComTituloNormalizadoEValoresPadrao() {
        ObraAudiovisual dados = ServiceFixtures.obra("  Filme Teste  ");
        dados.setDisponivel(null);
        dados.setStatus(null);
        LocalDateTime antes = LocalDateTime.now().minusSeconds(1);

        ObraAudiovisual salva = service.cadastrar(dados);
        LocalDateTime depois = LocalDateTime.now().plusSeconds(1);

        assertNotNull(salva.getId());
        assertEquals("Filme Teste", salva.getTitulo());
        assertFalse(salva.getDisponivel());
        assertEquals(StatusObra.RASCUNHO, salva.getStatus());
        assertFalse(salva.getCriadoEm().isBefore(antes));
        assertFalse(salva.getAtualizadoEm().isAfter(depois));
    }

    @Test
    void deveCadastrarViaDtoComSucesso() {
        ObraAudiovisualRequest request = new ObraAudiovisualRequest(
                "  Documentario DTO  ",
                "Original Title",
                "Diretor DTO",
                "Sinopse explicativa",
                2024,
                "pt-BR",
                "https://cdn.test/poster.jpg",
                "https://cdn.test/banner.jpg",
                null,
                null,
                LocalDateTime.now().plusDays(1),
                LocalDateTime.now().plusDays(10),
                ClassificacaoIndicativa.LIVRE,
                Set.of(NomeGenero.DOCUMENTARIO)
        );

        ObraAudiovisualResponse response = service.cadastrar(request);

        assertNotNull(response.id());
        assertEquals("Documentario DTO", response.titulo());
        assertEquals("Diretor DTO", response.diretor());
        assertEquals(ClassificacaoIndicativa.LIVRE, response.classificacaoIndicativa());
    }

    @Test
    void deveAtualizarViaDtoSemAlterarStatusEDisponibilidade() {
        ObraAudiovisual existente = salvarObra("Obra DTO Antes");
        ObraAudiovisualRequest request = new ObraAudiovisualRequest(
                "  Obra DTO Atualizada  ",
                "Original Title Updated",
                "Novo Diretor",
                "Nova Sinopse",
                2023,
                "pt-BR",
                null,
                null,
                false,
                StatusObra.INATIVO,
                null,
                null,
                ClassificacaoIndicativa.QUATORZE,
                null
        );

        ObraAudiovisualResponse response = service.atualizar(existente.getId(), request);

        assertEquals("Obra DTO Atualizada", response.titulo());
        assertEquals("Novo Diretor", response.diretor());
        ObraAudiovisual recarregada = service.buscarEntidadePorId(existente.getId());
        assertEquals(StatusObra.ATIVO, recarregada.getStatus());
        assertTrue(recarregada.getDisponivel());
    }

    @Test
    void deveAtualizarDadosSemAlterarStatusEDisponibilidade() {
        ObraAudiovisual existente = salvarObra("Antes");
        ObraAudiovisual dados = ServiceFixtures.obra("  Depois  ");
        dados.setDiretor("Nova direção");
        dados.setStatus(StatusObra.INATIVO);
        dados.setDisponivel(false);

        ObraAudiovisual atualizada = service.atualizar(existente.getId(), dados);

        assertEquals("Depois", atualizada.getTitulo());
        assertEquals("Nova direção", atualizada.getDiretor());
        assertEquals(StatusObra.ATIVO, atualizada.getStatus());
        assertTrue(atualizada.getDisponivel());
    }

    @Test
    void deveValidarDadosObrigatoriosEJanela() {
        assertEquals("Dados da obra sao obrigatorios",
                assertThrows(IllegalArgumentException.class, () -> service.cadastrar((ObraAudiovisual) null)).getMessage());
        ObraAudiovisual semTitulo = ServiceFixtures.obra("x");
        semTitulo.setTitulo(" ");
        assertEquals("Titulo da obra e obrigatorio",
                assertThrows(IllegalArgumentException.class, () -> service.cadastrar(semTitulo)).getMessage());
        ObraAudiovisual semClassificacao = ServiceFixtures.obra("Sem classificação");
        semClassificacao.setClassificacaoIndicativa(null);
        assertEquals("Classificacao indicativa e obrigatoria",
                assertThrows(IllegalArgumentException.class, () -> service.cadastrar(semClassificacao)).getMessage());
        ObraAudiovisual janela = ServiceFixtures.obra("Janela");
        janela.setDataInicioExibicao(LocalDateTime.now().plusDays(2));
        janela.setDataFimExibicao(LocalDateTime.now().plusDays(1));
        assertEquals("Data fim de exibicao nao pode ser anterior a data de inicio",
                assertThrows(IllegalArgumentException.class, () -> service.cadastrar(janela)).getMessage());
    }

    @Test
    void deveConsultarCatalogoEInformarObraAusente() {
        ObraAudiovisual obra = salvarObra("Busca Especial");
        obra.setDiretor("Diretor Único");
        obraRepository.saveAndFlush(obra);

        assertEquals(obra.getId(), service.buscarPorId(obra.getId()).id());
        assertEquals(obra.getId(), service.buscarEntidadePorId(obra.getId()).getId());
        assertFalse(service.listarTodas().isEmpty());
        assertEquals(1, service.buscarPorTitulo("especial").size());
        assertEquals(1, service.buscarPorDiretor("único").size());
        assertEquals(1, service.listarExibiveis().size());
        assertThrows(NoSuchElementException.class, () -> service.buscarPorId(Long.MAX_VALUE));
    }

    @Test
    void deveAlterarStatusEDisponibilidade() {
        ObraAudiovisual obra = salvarObra("Alterações");

        assertEquals(StatusObra.INATIVO,
                service.alterarStatus(obra.getId(), StatusObra.INATIVO).getStatus());
        assertFalse(service.alterarDisponibilidade(obra.getId(), false).getDisponivel());
        assertEquals("Status da obra e obrigatorio",
                assertThrows(IllegalArgumentException.class,
                        () -> service.alterarStatus(obra.getId(), null)).getMessage());
    }

    @Test
    void deveAdicionarReutilizarERemoverGenero() {
        ObraAudiovisual primeira = salvarObra("Primeira");
        ObraAudiovisual segunda = salvarObra("Segunda");

        service.adicionarGenero(primeira.getId(), NomeGenero.DOCUMENTARIO);
        service.adicionarGenero(segunda.getId(), NomeGenero.DOCUMENTARIO);
        service.adicionarGenero(primeira.getId(), NomeGenero.DOCUMENTARIO);

        assertEquals(1, generoRepository.count());
        assertEquals(1, service.buscarPorId(primeira.getId()).generos().size());
        assertTrue(service.removerGenero(primeira.getId(), NomeGenero.DOCUMENTARIO).getGeneros().isEmpty());
        assertThrows(IllegalArgumentException.class, () -> service.adicionarGenero(primeira.getId(), null));
        assertThrows(IllegalArgumentException.class, () -> service.removerGenero(primeira.getId(), null));
    }

    @Test
    void deveExcluirObra() {
        ObraAudiovisual obra = salvarObra("Excluir");
        service.excluir(obra.getId());
        obraRepository.flush();
        assertFalse(obraRepository.existsById(obra.getId()));
    }
}
