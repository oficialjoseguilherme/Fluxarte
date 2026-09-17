package br.labprog.fluxarte.model;

import br.labprog.fluxarte.model.enums.FormatoMidia;
import br.labprog.fluxarte.model.enums.Resolucao;
import br.labprog.fluxarte.model.enums.TipoMidia;
import br.labprog.fluxarte.model.enums.TipoPlayer;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "midia_streaming")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(of = "id")
@ToString(exclude = {"obra", "progressos", "historicos"})
public class MidiaStreaming {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "obra_id", nullable = false)
    private ObraAudiovisual obra;

    @Enumerated(EnumType.STRING)
    @Column(name = "tipo_midia", nullable = false, length = 20)
    private TipoMidia tipoMidia;

    // url completa do iframe da RPN/eduplay
    @Column(name = "eduplay_embed_url", columnDefinition = "TEXT")
    private String eduplayEmbedUrl;

    // url assinada da CDN
    @Column(name = "cdn_stream_url", columnDefinition = "TEXT")
    private String cdnStreamUrl;

    @Column(length = 10)
    private Resolucao resolucao;

    @Enumerated(EnumType.STRING)
    @Column(length = 10)
    private FormatoMidia formato;

    @Enumerated(EnumType.STRING)
    @Column(name = "player_type", length = 10)
    private TipoPlayer playerType;

    //duração da mídia, não da obra em si (ex: o trailer pode ter uma duração, mas ela não é a duração da OBRA em si)
    @Column(name = "duracao_segundos")
    private Integer duracaoSegundos;

    @Column(name = "thumbs_sprite_url")
    private String thumbsSpriteUrl;

    @Column(name = "atualizado_em")
    private LocalDateTime atualizadoEm;

    @OneToMany(mappedBy = "midia")
    @Builder.Default
    private List<ProgressoVisualizacao> progressos = new ArrayList<>();

    @OneToMany(mappedBy = "midia")
    @Builder.Default
    private List<HistoricoVisualizacao> historicos = new ArrayList<>();
}

