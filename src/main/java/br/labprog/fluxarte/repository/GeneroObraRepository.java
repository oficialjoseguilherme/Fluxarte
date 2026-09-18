package br.labprog.fluxarte.repository;

import br.labprog.fluxarte.model.GeneroObra;
import br.labprog.fluxarte.model.enums.NomeGenero;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface GeneroObraRepository extends JpaRepository<GeneroObra, Long> {

    Optional<GeneroObra> findByNome(NomeGenero nome);
}