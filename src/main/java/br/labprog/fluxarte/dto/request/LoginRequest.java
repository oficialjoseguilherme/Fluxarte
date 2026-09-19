package br.labprog.fluxarte.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record LoginRequest(
    @NotBlank(message = "O e-mail é obrigatório")
    @Email(message = "Formato de e-mail inválido")
    String email,

    @NotBlank(message = "A senha é obrigatória")
    String senha
) {
    public LoginRequest {
        if (email != null) email = email.trim().toLowerCase();
    }
}
