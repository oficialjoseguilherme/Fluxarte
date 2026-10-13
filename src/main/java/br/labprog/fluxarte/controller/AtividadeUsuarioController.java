package br.labprog.fluxarte.controller;

import br.labprog.fluxarte.dto.request.AtividadeUsuarioRequest;
import br.labprog.fluxarte.dto.response.AtividadeUsuarioResponse;
import br.labprog.fluxarte.model.enums.TipoAtividade;
import br.labprog.fluxarte.service.AtividadeUsuarioService;
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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/atividades")
@RequiredArgsConstructor
public class AtividadeUsuarioController {

    private final AtividadeUsuarioService atividadeService;

    @PreAuthorize("isAuthenticated()")
    @PostMapping
    public ResponseEntity<AtividadeUsuarioResponse> registrar(
            @AuthenticationPrincipal UUID usuarioId,
            @RequestBody @Valid AtividadeUsuarioRequest request) {
        AtividadeUsuarioResponse response = atividadeService.registrar(usuarioId, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping
    public ResponseEntity<List<AtividadeUsuarioResponse>> listarMinhasAtividades(
            @AuthenticationPrincipal UUID usuarioId,
            @RequestParam(required = false) TipoAtividade tipo) {
        if (tipo != null) {
            return ResponseEntity.ok(atividadeService.listarPorUsuarioETipo(usuarioId, tipo));
        }
        return ResponseEntity.ok(atividadeService.listarPorUsuario(usuarioId));
    }
}
