package br.labprog.fluxarte.controller;

import br.labprog.fluxarte.dto.request.ProgressoVisualizacaoRequest;
import br.labprog.fluxarte.dto.response.ProgressoVisualizacaoResponse;
import br.labprog.fluxarte.service.ProgressoVisualizacaoService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/progresso")
@RequiredArgsConstructor
public class ProgressoVisualizacaoController {

    private final ProgressoVisualizacaoService progressoService;

    @PreAuthorize("isAuthenticated()")
    @PostMapping
    public ResponseEntity<ProgressoVisualizacaoResponse> salvarProgresso(
            @AuthenticationPrincipal UUID usuarioId,
            @RequestBody @Valid ProgressoVisualizacaoRequest request) {
        return progressoService.salvarProgresso(usuarioId, request)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.noContent().build());
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping
    public ResponseEntity<List<ProgressoVisualizacaoResponse>> listarMeuProgresso(
            @AuthenticationPrincipal UUID usuarioId) {
        return ResponseEntity.ok(progressoService.listarPorUsuario(usuarioId));
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/{midiaId}")
    public ResponseEntity<ProgressoVisualizacaoResponse> buscarProgresso(
            @AuthenticationPrincipal UUID usuarioId,
            @PathVariable Long midiaId) {
        return progressoService.buscarProgresso(usuarioId, midiaId)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PreAuthorize("isAuthenticated()")
    @DeleteMapping("/{midiaId}")
    public ResponseEntity<Void> removerProgresso(
            @AuthenticationPrincipal UUID usuarioId,
            @PathVariable Long midiaId) {
        progressoService.remover(usuarioId, midiaId);
        return ResponseEntity.noContent().build();
    }
}
