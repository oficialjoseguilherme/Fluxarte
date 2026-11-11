package br.labprog.fluxarte.controller;

import br.labprog.fluxarte.dto.response.GeneroResponse;
import br.labprog.fluxarte.model.enums.NomeGenero;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Arrays;
import java.util.List;

@RestController
@RequestMapping("/api/generos")
public class GeneroController {

    @GetMapping
    public ResponseEntity<List<GeneroResponse>> listar() {
        List<GeneroResponse> generos = Arrays.stream(NomeGenero.values())
                .map(g -> new GeneroResponse(g, g.getRotulo()))
                .toList();
        return ResponseEntity.ok(generos);
    }
}
