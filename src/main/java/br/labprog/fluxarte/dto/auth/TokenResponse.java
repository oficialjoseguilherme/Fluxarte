package br.labprog.fluxarte.dto.auth;

public record TokenResponse(String accessToken, String refreshToken, String tipo) {
}