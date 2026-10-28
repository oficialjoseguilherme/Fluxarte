// Igual ao MidiaStreamingResponse do backend
export interface Midia {
  id: number
  obraId: number
  tipoMidia: 'PRINCIPAL' | 'TRAILER' | 'VIDEO_DIRETOR'
  playerType: 'NATIVE' | 'EMBED'
  eduplayEmbedUrl: string | null
  cdnStreamUrl: string | null
  resolucao: string | null
  formato: 'MP4' | 'HLS' | null
  duracaoSegundos: number | null
  thumbsSpriteUrl: string | null
}
