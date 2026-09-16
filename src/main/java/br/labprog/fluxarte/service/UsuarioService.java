package br.labprog.fluxarte.service;

import br.labprog.fluxarte.model.TipoUsuario;
import br.labprog.fluxarte.model.Usuario;
import br.labprog.fluxarte.repository.UsuarioRepository;
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

    @Transactional
    public Usuario cadastrar(String nome, String email, String senha, LocalDate dataNascimento) {
        if (usuarioRepository.existsByEmail(email)) {
            throw new IllegalArgumentException("Ja existe um usuario cadastrado com esse email");
        }

        Usuario usuario = Usuario.builder()
                .nome(nome)
                .email(email)
                .senhaHash(passwordEncoder.encode(senha))
                .dataNascimento(dataNascimento)
                .nomeExibicao(nome)
                .aceitaConteudoAdulto(false)
                .tipoUsuario(TipoUsuario.ESPECTADOR)
                .ativo(true)
                .build();

        return usuarioRepository.save(usuario);
    }

    @Transactional(readOnly = true)
    public Usuario buscarPorId(UUID id) {
        return usuarioRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Usuario nao encontrado: " + id));
    }

    @Transactional
    public boolean autenticar(String email, String senha) {
        return usuarioRepository.findByEmail(email)
                .filter(Usuario::getAtivo)
                .map(usuario -> passwordEncoder.matches(senha, usuario.getSenhaHash()))
                .orElse(false);
    }
}