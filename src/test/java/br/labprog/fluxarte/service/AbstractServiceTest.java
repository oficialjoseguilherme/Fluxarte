package br.labprog.fluxarte.service;

import br.labprog.fluxarte.model.MidiaStreaming;
import br.labprog.fluxarte.model.ObraAudiovisual;
import br.labprog.fluxarte.model.Usuario;
import br.labprog.fluxarte.model.enums.TipoMidia;
import br.labprog.fluxarte.repository.MidiaStreamingRepository;
import br.labprog.fluxarte.repository.ObraAudiovisualRepository;
import br.labprog.fluxarte.repository.UsuarioRepository;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.transaction.annotation.Transactional;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@ActiveProfiles("test")
@Transactional
abstract class AbstractServiceTest {

    @Autowired protected EntityManager entityManager;
    @Autowired protected UsuarioRepository usuarioRepository;
    @Autowired protected ObraAudiovisualRepository obraRepository;
    @Autowired protected MidiaStreamingRepository midiaRepository;

    protected Usuario salvarUsuario(String email) {
        return usuarioRepository.saveAndFlush(ServiceFixtures.usuario(email));
    }

    protected ObraAudiovisual salvarObra(String titulo) {
        return obraRepository.saveAndFlush(ServiceFixtures.obra(titulo));
    }

    protected MidiaStreaming salvarMidia(ObraAudiovisual obra, TipoMidia tipo) {
        return midiaRepository.saveAndFlush(ServiceFixtures.midia(obra, tipo));
    }

    protected void flushAndClear() {
        entityManager.flush();
        entityManager.clear();
    }
}
