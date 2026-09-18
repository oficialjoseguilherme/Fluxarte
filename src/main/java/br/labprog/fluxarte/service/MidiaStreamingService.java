package br.labprog.fluxarte.service;

import br.labprog.fluxarte.model.MidiaStreaming;
import br.labprog.fluxarte.model.ObraAudiovisual;
import br.labprog.fluxarte.model.enums.TipoMidia;
import br.labprog.fluxarte.model.enums.TipoPlayer;
import br.labprog.fluxarte.repository.MidiaStreamingRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.NoSuchElementException;

@Service
@RequiredArgsConstructor
public class MidiaStreamingService {

    private final MidiaStreamingRepository midiaRepository;
    private final ObraAudiovisualService obraService;

    // uma obra pode ter varias midias (ex: a mesma PRINCIPAL em 480p, 720p e 1080p)
    @Transactional
    public MidiaStreaming cadastrar(Long obraId, MidiaStreaming dados) {
        validar(dados);

        ObraAudiovisual obra = obraService.buscarPorId(obraId);

        MidiaStreaming midia = MidiaStreaming.builder()
                .obra(obra)
                .tipoMidia(dados.getTipoMidia())
                .eduplayEmbedUrl(dados.getEduplayEmbedUrl())
                .cdnStreamUrl(dados.getCdnStreamUrl())
                .resolucao(dados.getResolucao())
                .formato(dados.getFormato())
                .playerType(dados.getPlayerType())
                .duracaoSegundos(dados.getDuracaoSegundos())
                .thumbsSpriteUrl(dados.getThumbsSpriteUrl())
                .atualizadoEm(LocalDateTime.now())
                .build();

        return midiaRepository.save(midia);
    }

    @Transactional
    public MidiaStreaming atualizar(Long id, MidiaStreaming dados) {
        validar(dados);

        MidiaStreaming midia = buscarPorId(id);

        midia.setTipoMidia(dados.getTipoMidia());
        midia.setEduplayEmbedUrl(dados.getEduplayEmbedUrl());
        midia.setCdnStreamUrl(dados.getCdnStreamUrl());
        midia.setResolucao(dados.getResolucao());
        midia.setFormato(dados.getFormato());
        midia.setPlayerType(dados.getPlayerType());
        midia.setDuracaoSegundos(dados.getDuracaoSegundos());
        midia.setThumbsSpriteUrl(dados.getThumbsSpriteUrl());
        midia.setAtualizadoEm(LocalDateTime.now());

        return midiaRepository.save(midia);
    }

    @Transactional(readOnly = true)
    public MidiaStreaming buscarPorId(Long id) {
        return midiaRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Midia nao encontrada: " + id));
    }

    @Transactional(readOnly = true)
    public List<MidiaStreaming> listarPorObra(Long obraId) {
        return midiaRepository.findByObraId(obraId);
    }

    @Transactional(readOnly = true)
    public List<MidiaStreaming> listarPorObraETipo(Long obraId, TipoMidia tipoMidia) {
        return midiaRepository.findByObraIdAndTipoMidia(obraId, tipoMidia);
    }

    @Transactional
    public void excluir(Long id) {
        MidiaStreaming midia = buscarPorId(id);
        midiaRepository.delete(midia);
    }

    private void validar(MidiaStreaming dados) {
        if (dados == null) {
            throw new IllegalArgumentException("Dados da midia sao obrigatorios");
        }
        if (dados.getTipoMidia() == null) {
            throw new IllegalArgumentException("Tipo da midia e obrigatorio");
        }
        if (dados.getDuracaoSegundos() != null && dados.getDuracaoSegundos() <= 0) {
            throw new IllegalArgumentException("Duracao da midia deve ser maior que zero");
        }
        // EMBED usa o iframe da RNP; NATIVE usa a URL assinada da CDN
        if (dados.getPlayerType() == TipoPlayer.EMBED && isVazio(dados.getEduplayEmbedUrl())) {
            throw new IllegalArgumentException("Player EMBED exige a URL de embed do Eduplay");
        }
        if (dados.getPlayerType() == TipoPlayer.NATIVE && isVazio(dados.getCdnStreamUrl())) {
            throw new IllegalArgumentException("Player NATIVE exige a URL de stream da CDN");
        }
    }

    private boolean isVazio(String valor) {
        return valor == null || valor.isBlank();
    }
}