package br.labprog.fluxarte.controller;

import br.labprog.fluxarte.dto.request.EventoObraRequest;
import br.labprog.fluxarte.dto.response.EventoObraResponse;
import br.labprog.fluxarte.service.EventoObraService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/eventos/{eventoId}/obras")
@RequiredArgsConstructor
public class EventoObraController {

    private final EventoObraService eventoObraService;

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping
    public ResponseEntity<EventoObraResponse> adicionarObra(
            @PathVariable Long eventoId,
            @RequestBody @Valid EventoObraRequest request) {
        EventoObraResponse response = eventoObraService.adicionarObra(eventoId, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping
    public ResponseEntity<List<EventoObraResponse>> listarObrasDoEvento(
            @PathVariable Long eventoId,
            @RequestParam(required = false) Boolean destaque) {
        if (Boolean.TRUE.equals(destaque)) {
            return ResponseEntity.ok(eventoObraService.listarDestaquesDoEvento(eventoId));
        }
        return ResponseEntity.ok(eventoObraService.listarPorEvento(eventoId));
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/{id}")
    public ResponseEntity<EventoObraResponse> buscarPorId(
            @PathVariable Long eventoId,
            @PathVariable Long id) {
        return ResponseEntity.ok(eventoObraService.buscarPorId(id));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{id}")
    public ResponseEntity<EventoObraResponse> atualizar(
            @PathVariable Long eventoId,
            @PathVariable Long id,
            @RequestBody @Valid EventoObraRequest request) {
        return ResponseEntity.ok(eventoObraService.atualizar(id, request));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> remover(
            @PathVariable Long eventoId,
            @PathVariable Long id) {
        eventoObraService.remover(id);
        return ResponseEntity.noContent().build();
    }
}
