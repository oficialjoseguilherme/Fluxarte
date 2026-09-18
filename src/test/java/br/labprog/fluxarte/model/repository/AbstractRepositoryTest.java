package br.labprog.fluxarte.model.repository;

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
abstract class AbstractRepositoryTest {

    @Autowired
    protected EntityManager entityManager;

    protected void flushAndClear() {
        entityManager.flush();
        entityManager.clear();
    }
}
