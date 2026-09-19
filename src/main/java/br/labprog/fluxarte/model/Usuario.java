package br.labprog.fluxarte.model;

import br.labprog.fluxarte.model.enums.TipoUsuario;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.hibernate.annotations.UuidGenerator;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Entity
@Table(name = "usuarios")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder


public class Usuario {

    @Id
    @UuidGenerator
    private UUID id;

    @Column(nullable = false)
    private String nome;

    @Column(nullable = false, unique=true)
    private String email;

    @Column(name = "senha_hash", nullable = false)
    private String senhaHash;

    @Column(name = "nome_exibicao")
    private String nomeExibicao;

    @Column(name = "data_nascimento", nullable = false)
    private LocalDate dataNascimento;

    @Column(name = "aceita_conteudo_adulto", nullable = false)
    @Builder.Default
    private Boolean aceitaConteudoAdulto = false;

    @Enumerated(EnumType.STRING)
    @Column(name = "tipo_usuario", nullable = false)
    @Builder.Default
    private TipoUsuario tipoUsuario = TipoUsuario.ESPECTADOR;

    @ManyToMany
    @JoinTable(
            name = "usuario_genero_preferido",
            joinColumns = @JoinColumn(name = "usuario_id"),
            inverseJoinColumns = @JoinColumn(name = "genero_id")
    )
    @Builder.Default
    private Set<GeneroObra> generosPreferidos = new HashSet<>();

    @ManyToMany
    @JoinTable(
            name = "usuario_favorito",
            joinColumns = @JoinColumn(name = "usuario_id"),
            inverseJoinColumns = @JoinColumn(name = "obra_id")
    )
    @Builder.Default
    private Set<ObraAudiovisual> favoritos = new HashSet<>();

    @Column(nullable = false)
    @Builder.Default
    private Boolean ativo = true;

    @CreationTimestamp
    @Column(name = "criado_em", updatable = false)
    private LocalDateTime criadoEm;

    @UpdateTimestamp
    @Column(name = "atualizado_em")
    private LocalDateTime atualizadoEm;


}

