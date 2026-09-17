package br.labprog.fluxarte.model;

import br.labprog.fluxarte.model.enums.AlgoritmoRecomendacao;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

import org.hibernate.annotations.CreationTimestamp;

@Entity
@Table(name = "recomendacao")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(of = "id")
@ToString(exclude = {"usuario", "obra"})
public class Recomendacao {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "usuario_id", nullable = false)
    private Usuario usuario;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "obra_id", nullable = false)
    private ObraAudiovisual obra;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private AlgoritmoRecomendacao algoritmo;

    private Float score;

    @CreationTimestamp
    @Column(name = "gerada_em", nullable = false, updatable = false)
    private LocalDateTime geradaEm;
}
