package br.labprog.fluxarte.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

import org.hibernate.annotations.UpdateTimestamp;

@Entity
@Table(
        name = "progresso_visualizacao",
        uniqueConstraints = @UniqueConstraint(columnNames = {"usuario_id", "midia_id"})
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(of = "id")
@ToString(exclude = {"usuario", "obra", "midia"})
public class ProgressoVisualizacao {

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

    //ponto em que o usuário parou
    @Column(name = "posicao_segundos", nullable = false)
    private Integer posicaoSegundos;

    @UpdateTimestamp 
    @Column(name = "atualizado_em")
    private LocalDateTime atualizadoEm;

    //percentual assistido
    @Transient
    public Double getPercentualAssistido() {
        if (midia == null || midia.getDuracaoSegundos() == null || midia.getDuracaoSegundos() == 0) {
            return null;
        }
        return (posicaoSegundos * 100.0) / midia.getDuracaoSegundos();
    }
}

