import correspondencias from '../data/idFilmeVideoId.json'

// O banco guarda a página do filme no Uirá (?idFilme=); o player precisa do id do vídeo no Eduplay
const VIDEO_POR_FILME = new Map(correspondencias.map(item => [item.idFilme, item.videoId]))

export function urlEmbedEduplay(videoId: number): string {
  return `https://eduplay.rnp.br/app/video/embed/${videoId}`
}

// Aceita URL do Eduplay, do player do Guarnicê (?videoId=) ou da página do Uirá (?idFilme=)
export function resolverPlayerEduplay(url: string | null): string | null {
  if (!url) return null
  let endereco: URL
  try { endereco = new URL(url) } catch { return null }

  const embed = endereco.pathname.match(/\/video\/embed\/(\d+)/)
  if (endereco.hostname === 'eduplay.rnp.br' && embed) return urlEmbedEduplay(Number(embed[1]))

  const videoId = Number(endereco.searchParams.get('videoId'))
  if (videoId > 0) return urlEmbedEduplay(videoId)

  const mapeado = VIDEO_POR_FILME.get(Number(endereco.searchParams.get('idFilme')))
  return mapeado ? urlEmbedEduplay(mapeado) : null
}
