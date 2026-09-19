package br.labprog.fluxarte.service;

import br.labprog.fluxarte.dto.request.UsuarioCadastroRequest;
import br.labprog.fluxarte.dto.response.UsuarioResponse;
import br.labprog.fluxarte.mapper.UsuarioMapper;
import br.labprog.fluxarte.model.Usuario;
import br.labprog.fluxarte.repository.UsuarioRepository;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.NoSuchElementException;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UsuarioService {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;
    private final UsuarioMapper usuarioMapper;

    @Transactional
    public UsuarioResponse cadastrar(UsuarioCadastroRequest request) {
        if (request == null) {
            throw new IllegalArgumentException("Dados de cadastro sao obrigatorios");
        }
        if (request.nome() == null || request.nome().isBlank()) {
            throw new IllegalArgumentException("O nome não pode ser nulo ou em branco.");
        }
        if (request.email() == null || request.email().isBlank()) {
            throw new IllegalArgumentException("O email não pode ser nulo ou em branco.");
        }
        if (request.senha() == null || request.senha().isBlank()) {
            throw new IllegalArgumentException("A senha não pode ser nula ou em branco");
        }

        if (usuarioRepository.existsByEmail(request.email())) {
            throw new IllegalArgumentException("Ja existe um usuario cadastrado com esse email");
        }

        Usuario usuario = usuarioMapper.toEntity(request);
        usuario.setSenhaHash(passwordEncoder.encode(request.senha()));

        Usuario salvo = usuarioRepository.save(usuario);
        return usuarioMapper.toResponse(salvo);
    }

    @Transactional
    public Usuario cadastrar(String nome, String email, String senha, LocalDate dataNascimento) {
        LocalDate data = dataNascimento != null ? dataNascimento : LocalDate.of(2000, 1, 1);
        UsuarioCadastroRequest request = new UsuarioCadastroRequest(
                nome, email, senha, data, null, false);

        UsuarioResponse response = cadastrar(request);
        return buscarEntidadePorId(response.id());
    }

    @Transactional(readOnly = true)
    public UsuarioResponse buscarPorId(UUID id) {
        return usuarioMapper.toResponse(buscarEntidadePorId(id));
    }

    @Transactional(readOnly = true)
    public Usuario buscarEntidadePorId(UUID id) {
        return usuarioRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Usuario nao encontrado: " + id));
    }

    @Transactional(readOnly = true)
    public boolean autenticar(@NonNull String email, String senha) {
        if (senha == null) {
            return false;
        }
        return usuarioRepository.findByEmail(email.trim().toLowerCase())
                .filter(usuario -> Boolean.TRUE.equals(usuario.getAtivo()))
                .map(usuario -> passwordEncoder.matches(senha, usuario.getSenhaHash()))
                .orElse(false);
    }
}