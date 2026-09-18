package br.labprog.fluxarte.model.repository;

import br.labprog.fluxarte.model.GeneroObra;
import br.labprog.fluxarte.model.enums.NomeGenero;
import br.labprog.fluxarte.repository.GeneroObraRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;

import static org.junit.jupiter.api.Assertions.*;

class GeneroObraRepositoryTest extends AbstractRepositoryTest {

    @Autowired GeneroObraRepository repository;

    @Test
    void deveSalvarBuscarPorNomeERemoverGenero() {
        GeneroObra salvo = repository.saveAndFlush(ModelFixtures.genero(NomeGenero.ANIMACAO));
        Long id = salvo.getId();
        flushAndClear();

        GeneroObra recuperado = repository.findByNome(NomeGenero.ANIMACAO).orElseThrow();
        assertEquals(id, recuperado.getId());
        assertEquals(NomeGenero.ANIMACAO, recuperado.getNome());

        repository.delete(recuperado);
        repository.flush();
        assertFalse(repository.findById(id).isPresent());
    }

    @Test
    void naoDevePermitirNomeDuplicado() {
        repository.saveAndFlush(ModelFixtures.genero(NomeGenero.FICCAO));

        assertThrows(DataIntegrityViolationException.class,
                () -> repository.saveAndFlush(ModelFixtures.genero(NomeGenero.FICCAO)));
    }
}
