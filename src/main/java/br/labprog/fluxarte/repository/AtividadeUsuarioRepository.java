package br.labprog.fluxarte.repository;

import br.labprog.fluxarte.model.AtividadeUsuario;
import br.labprog.fluxarte.model.enums.TipoAtividade;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface AtividadeUsuarioRepository extends JpaRepository<AtividadeUsuario, Long> {

    List<AtividadeUsuario> findByUsuarioIdOrderByRegistradoEmDesc(UUID usuarioId);

    List<AtividadeUsuario> findByUsuarioIdAndTipoAtividadeOrderByRegistradoEmDesc(UUID usuarioId,
                                                                                  TipoAtividade tipoAtividade);
}