package br.labprog.fluxarte.repository;

import br.labprog.fluxarte.model.MidiaStreaming;
import br.labprog.fluxarte.model.enums.TipoMidia;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MidiaStreamingRepository extends JpaRepository<MidiaStreaming, Long> {

    List<MidiaStreaming> findByObraId(Long obraId);

    List<MidiaStreaming> findByObraIdAndTipoMidia(Long obraId, TipoMidia tipoMidia);
}