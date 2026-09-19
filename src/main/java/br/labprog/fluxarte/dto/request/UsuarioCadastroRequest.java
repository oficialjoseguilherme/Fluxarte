package br.labprog.fluxarte.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;

public record UsuarioCadastroRequest(
    @NotBlank(message = "O nome é obrigatório")
    @Size(max = 225, message = "O nome não pode exceder 225 caracteres")
    String nome,

    @NotBlank(message = "O e-mail é obrigatório")
    @Email(message = "Formato de e-mail inválido")
    @Size(max = 225, message = "O e-mail não pode exceder 225 caracteres")
    String email,

    @NotBlank(message = "A senha é obrigatória")
    @Size(min = 8, max = 72, message = "A senha deve ter entre 8 e 72 caracteres")
    String senha,

    @NotNull(message = "A data de nascimento é obrigatória")
    @Past(message = "A data de nascimento deve ser uma data no passado")
    LocalDate dataNascimento,

    @Size(max = 225, message = "O nome de exibição não pode exceder 225 caracteres")
    String nomeExibicao,

    Boolean aceitaConteudoAdulto
) {
    public UsuarioCadastroRequest {
        if (nome != null) nome = nome.trim();
        if (email != null) email = email.trim().toLowerCase();
        if (nomeExibicao != null) nomeExibicao = nomeExibicao.trim();
        if (aceitaConteudoAdulto == null) aceitaConteudoAdulto = false;
    }
}
