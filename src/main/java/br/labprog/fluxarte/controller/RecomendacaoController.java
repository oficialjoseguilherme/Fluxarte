package br.labprog.fluxarte.controller;

import br.labprog.fluxarte.dto.response.RecomendacaoHomeResponse;
import br.labprog.fluxarte.dto.response.RecomendacaoResponse;
import br.labprog.fluxarte.mapper.RecomendacaoMapper;
import br.labprog.fluxarte.model.Recomendacao;
import br.labprog.fluxarte.service.RecomendacaoService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/recomendacoes")
@RequiredArgsConstructor
public class RecomendacaoController {

    private final RecomendacaoService recomendacaoService;
    private final RecomendacaoMapper recomendacaoMapper;

    @PreAuthorize("isAuthenticated()")
    @GetMapping
    public ResponseEntity<List<RecomendacaoResponse>> listar(Authentication authentication) {
        UUID usuarioId = (UUID) authentication.getPrincipal();
        List<Recomendacao> recomendacoes = recomendacaoService.obterOuGerarRecomendacoes(usuarioId);
        List<RecomendacaoResponse> responses = recomendacoes.stream()
                .map(recomendacaoMapper::toResponse)
                .toList();
        return ResponseEntity.ok(responses);
    }

    @PreAuthorize("isAuthenticated()")
    @PostMapping("/atualizar")
    public ResponseEntity<List<RecomendacaoResponse>> atualizar(Authentication authentication) {
        UUID usuarioId = (UUID) authentication.getPrincipal();
        List<Recomendacao> recomendacoes = recomendacaoService.gerarParaUsuario(usuarioId);
        List<RecomendacaoResponse> responses = recomendacoes.stream()
                .map(recomendacaoMapper::toResponse)
                .toList();
        return ResponseEntity.ok(responses);
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/home")
    public ResponseEntity<RecomendacaoHomeResponse> home(Authentication authentication) {
        UUID usuarioId = (UUID) authentication.getPrincipal();
        RecomendacaoHomeResponse response = recomendacaoService.obterHome(usuarioId);
        return ResponseEntity.ok(response);
    }
}
