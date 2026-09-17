package br.labprog.fluxarte.model;

import br.labprog.fluxarte.model.enums.TipoAtividade;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "atividades_usuario")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder

public class AtividadeUsuario {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "usuario_id", nullable = false)
    private Usuario usuario;

    @Enumerated(EnumType.STRING)
    @Column(name = "tipo_atividade", nullable = false)
    private TipoAtividade tipoAtividade;

    @Column(name = "termo_busca")
    private String termoBusca;

    @Column(name = "obra_id_referenciada")
    private Long obraIdReferenciada;

    //@Lob
    //@Column(name = "metadados_json")
    //private String metadadosJson;

    @CreationTimestamp
    @Column(name = "registrado_em", updatable = false)
    private LocalDateTime registradoEm;
}
