package br.labprog.fluxarte.model;

import br.labprog.fluxarte.model.enums.NomeGenero;
import jakarta.persistence.*;
import lombok.*;

import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "genero_obra")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(of = "id")
@ToString(exclude = "obras")
public class GeneroObra {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, unique = true, length = 20)
    private NomeGenero nome;

    @ManyToMany(mappedBy = "generos")
    @Builder.Default
    private Set<ObraAudiovisual> obras = new HashSet<>();
}