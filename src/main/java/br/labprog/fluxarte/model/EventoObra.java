package br.labprog.fluxarte.model;

import br.labprog.fluxarte.model.enums.CategoriaEvento;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(of = "id")
@ToString(exclude = {"evento", "obra"})
@Table(name = "evento_obra", uniqueConstraints = @UniqueConstraint(columnNames = {"evento_id", "obra_id"}))

public class EventoObra {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "evento_id", nullable = false)
    private Evento evento;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "obra_id", nullable = false)
    private ObraAudiovisual obra;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private CategoriaEvento categoria;

    //a janela de maior prioridade deve ser a do evento, em relação à a geral da obra
    @Column(name = "data_estreia_evento_inicio")
    private LocalDateTime dataEstreiaEventoInicio;

    @Column(name = "data_estreia_evento_fim")
    private LocalDateTime dataEstreiaEventoFim;

    //TODO: verificar a lógica de premiação de evento
    @Column(length = 80)
    private String premiacao; //Melhor filme ou Null caso não ocorra uma premiação

    @Column(nullable = false)
    private Boolean destaque;
}
