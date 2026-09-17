package br.labprog.fluxarte.repository;

import br.labprog.fluxarte.model.ObraAudiovisual;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ObraAudiovisualRepository extends JpaRepository<ObraAudiovisual, Long> {

    Page<ObraAudiovisual> findByGeneros_Id(Long generoId, Pageable pageable);
}