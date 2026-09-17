package br.labprog.fluxarte.model;

import br.labprog.fluxarte.model.enums.ClassificacaoIndicativa;
import br.labprog.fluxarte.model.enums.StatusObra;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
@Table(name = "obra_audiovisual")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(of = "id")
@ToString(exclude = {"generos", "favoritadaPor", "midias", "eventoObras",
        "progressos", "historicos", "recomendacoes"})

public class ObraAudiovisual {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 255)
    private String titulo;

    @Column(name = "titulo_original", length = 255)
    private String tituloOriginal;

    //lembrando que a busca pode ser feita pelo diretor
    @Column(length = 200)
    private String diretor;

    @Column(columnDefinition = "TEXT")
    private String sinopse;

    @Column(name = "ano_producao")
    private Integer anoProducao;

    @Column(name = "idioma_original", length = 10)
    private String idiomaOriginal;

    @Column(name = "poster_url")
    private String posterUrl;

    @Column(name = "banner_url")
    private String bannerUrl;

    @Column(nullable = false)
    private Boolean disponivel;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private StatusObra status;

    // se null, não há restrição de inicio
    @Column(name = "data_inicio_exibicao")
    private LocalDateTime dataInicioExibicao;

    // se null, a obra é permanente
    @Column(name = "data_fim_exibicao")
    private LocalDateTime dataFimExibicao;

    @Enumerated(EnumType.STRING)
    @Column(name = "classificacao_indicativa", nullable = false, length = 20)
    private ClassificacaoIndicativa classificacaoIndicativa;

    @Column(name = "criado_em", nullable = false, updatable = false)
    private LocalDateTime criadoEm;

    @Column(name = "atualizado_em")
    private LocalDateTime atualizadoEm;

    @ManyToMany
    @JoinTable(
            name = "obra_genero",
            joinColumns = @JoinColumn(name = "obra_id"),
            inverseJoinColumns = @JoinColumn(name = "genero_id")
    )
    @Builder.Default
    private Set<GeneroObra> generos = new HashSet<>();

    @ManyToMany(mappedBy = "favoritos")
    @Builder.Default
    private Set<Usuario> favoritadaPor = new HashSet<>();

    @OneToMany(mappedBy = "obra", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<MidiaStreaming> midias = new ArrayList<>();

    @OneToMany(mappedBy = "obra", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<EventoObra> eventoObras = new ArrayList<>();

    @OneToMany(mappedBy = "obra", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<ProgressoVisualizacao> progressos = new ArrayList<>();

    @OneToMany(mappedBy = "obra", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<HistoricoVisualizacao> historicos = new ArrayList<>();

    @OneToMany(mappedBy = "obra", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<Recomendacao> recomendacoes = new ArrayList<>();

    //obra está no período de exibição?
    @Transient
    public boolean isExibivelEm(LocalDateTime momento) {
        if (dataInicioExibicao != null && momento.isBefore(dataInicioExibicao)) {
            return false;
        }
        if (dataFimExibicao != null && momento.isAfter(dataFimExibicao)) {
            return false;
        }
        return Boolean.TRUE.equals(disponivel) && status == StatusObra.ATIVO;
    }
}
