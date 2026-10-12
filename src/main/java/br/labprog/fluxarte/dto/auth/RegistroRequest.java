package br.labprog.fluxarte.dto.auth;

import java.time.LocalDate;

public record RegistroRequest(String nome, String email, String senha, LocalDate dataNascimento) {
}