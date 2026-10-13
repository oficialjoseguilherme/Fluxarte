package br.labprog.fluxarte.controller;

import br.labprog.fluxarte.dto.request.MidiaStreamingRequest;
import br.labprog.fluxarte.dto.response.MidiaStreamingResponse;
import br.labprog.fluxarte.model.enums.TipoMidia;
import br.labprog.fluxarte.service.MidiaStreamingService;
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
@RequestMapping("/api/midias")
@RequiredArgsConstructor
public class MidiaStreamingController {

    private final MidiaStreamingService midiaService;

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping
    public ResponseEntity<MidiaStreamingResponse> cadastrar(@RequestBody @Valid MidiaStreamingRequest request) {
        MidiaStreamingResponse response = midiaService.cadastrar(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{id}")
    public ResponseEntity<MidiaStreamingResponse> atualizar(
            @PathVariable Long id,
            @RequestBody @Valid MidiaStreamingRequest request) {
        return ResponseEntity.ok(midiaService.atualizar(id, request));
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/{id}")
    public ResponseEntity<MidiaStreamingResponse> buscarPorId(@PathVariable Long id) {
        return ResponseEntity.ok(midiaService.buscarPorId(id));
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/obra/{obraId}")
    public ResponseEntity<List<MidiaStreamingResponse>> listarPorObra(
            @PathVariable Long obraId,
            @RequestParam(required = false) TipoMidia tipo) {
        if (tipo != null) {
            return ResponseEntity.ok(midiaService.listarPorObraETipo(obraId, tipo));
        }
        return ResponseEntity.ok(midiaService.listarPorObra(obraId));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> excluir(@PathVariable Long id) {
        midiaService.excluir(id);
        return ResponseEntity.noContent().build();
    }
}
