package br.labprog.fluxarte.model;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "historico_visualizacao")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(of = "id")
@ToString(exclude = {"usuario", "obra", "midia"})
public class HistoricoVisualizacao {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "usuario_id", nullable = false)
    private Usuario usuario;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "obra_id", nullable = false)
    private ObraAudiovisual obra;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "midia_id", nullable = false)
    private MidiaStreaming midia;

    @CreationTimestamp
    @Column(name = "data_visualizacao", nullable = false, updatable = false)
    private LocalDateTime dataVisualizacao;

    @Column(name = "tempo_assistido_segundos")
    private Integer tempoAssistidoSegundos;

    @Column(nullable = false)
    private Boolean concluido;
}
