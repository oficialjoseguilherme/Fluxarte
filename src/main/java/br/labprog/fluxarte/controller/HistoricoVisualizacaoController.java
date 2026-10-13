package br.labprog.fluxarte.controller;

import br.labprog.fluxarte.dto.request.HistoricoVisualizacaoRequest;
import br.labprog.fluxarte.dto.response.HistoricoVisualizacaoResponse;
import br.labprog.fluxarte.service.HistoricoVisualizacaoService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/historico")
@RequiredArgsConstructor
public class HistoricoVisualizacaoController {

    private final HistoricoVisualizacaoService historicoService;

    @PreAuthorize("isAuthenticated()")
    @PostMapping
    public ResponseEntity<HistoricoVisualizacaoResponse> registrar(
            @AuthenticationPrincipal UUID usuarioId,
            @RequestBody @Valid HistoricoVisualizacaoRequest request) {
        HistoricoVisualizacaoResponse response = historicoService.registrar(usuarioId, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping
    public ResponseEntity<List<HistoricoVisualizacaoResponse>> listarMeuHistorico(
            @AuthenticationPrincipal UUID usuarioId) {
        return ResponseEntity.ok(historicoService.listarPorUsuario(usuarioId));
    }
}
