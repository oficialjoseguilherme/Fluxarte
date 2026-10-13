package br.labprog.fluxarte.controller;

import br.labprog.fluxarte.dto.request.ObraAudiovisualRequest;
import br.labprog.fluxarte.dto.response.ObraAudiovisualResponse;
import br.labprog.fluxarte.model.enums.NomeGenero;
import br.labprog.fluxarte.model.enums.StatusObra;
import br.labprog.fluxarte.service.ObraAudiovisualService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/obras")
@RequiredArgsConstructor
public class ObraAudiovisualController {

    private final ObraAudiovisualService obraService;

    @PostMapping
    public ResponseEntity<ObraAudiovisualResponse> cadastrar(@RequestBody ObraAudiovisualRequest request) {
        ObraAudiovisualResponse response = obraService.cadastrar(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping
    public ResponseEntity<List<ObraAudiovisualResponse>> listarTodas() {
        return ResponseEntity.ok(obraService.listarTodas());
    }

    @GetMapping("/{id}")
    public ResponseEntity<ObraAudiovisualResponse> buscarPorId(@PathVariable Long id) {
        return ResponseEntity.ok(obraService.buscarPorId(id));
    }

    @GetMapping("/busca")
    public ResponseEntity<List<ObraAudiovisualResponse>> buscar(
            @RequestParam(required = false) String titulo,
            @RequestParam(required = false) String diretor) {
        if (titulo != null && !titulo.isBlank()) {
            return ResponseEntity.ok(obraService.buscarPorTitulo(titulo));
        }
        if (diretor != null && !diretor.isBlank()) {
            return ResponseEntity.ok(obraService.buscarPorDiretor(diretor));
        }
        return ResponseEntity.ok(obraService.listarTodas());
    }

    @PutMapping("/{id}")
    public ResponseEntity<ObraAudiovisualResponse> atualizar(
            @PathVariable Long id,
            @RequestBody ObraAudiovisualRequest request) {
        return ResponseEntity.ok(obraService.atualizar(id, request));
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<ObraAudiovisualResponse> alterarStatus(
            @PathVariable Long id,
            @RequestParam StatusObra status) {
        return ResponseEntity.ok(obraService.alterarStatus(id, status));
    }

    @PatchMapping("/{id}/disponibilidade")
    public ResponseEntity<ObraAudiovisualResponse> alterarDisponibilidade(
            @PathVariable Long id,
            @RequestParam boolean disponivel) {
        return ResponseEntity.ok(obraService.alterarDisponibilidade(id, disponivel));
    }

    @PostMapping("/{id}/generos")
    public ResponseEntity<ObraAudiovisualResponse> adicionarGenero(
            @PathVariable Long id,
            @RequestParam NomeGenero genero) {
        return ResponseEntity.ok(obraService.adicionarGenero(id, genero));
    }

    @DeleteMapping("/{id}/generos/{genero}")
    public ResponseEntity<ObraAudiovisualResponse> removerGenero(
            @PathVariable Long id,
            @PathVariable NomeGenero genero) {
        return ResponseEntity.ok(obraService.removerGenero(id, genero));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> excluir(@PathVariable Long id) {
        obraService.excluir(id);
        return ResponseEntity.noContent().build();
    }
}
