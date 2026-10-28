import type { Midia } from '../types/midia'

export async function buscarMidiaPrincipal(
  obraId: number,
  accessToken: string,
): Promise<Midia | null> {
  const response = await fetch(`/api/midias/obra/${obraId}?tipo=PRINCIPAL`, {
    headers: {
      Authorization: `Bearer ${accessToken}`,
    },
  })

  const data = await response.json().catch(() => null)

  if (!response.ok) {
    if (data && typeof data.mensagem === 'string') {
      throw new Error(data.mensagem)
    }
    else {
      throw new Error('Não foi possível carregar o vídeo.')
    }
  }

  if (!Array.isArray(data)) {
    throw new Error('Resposta de mídia inválida.')
  }

  return (data[0] as Midia | undefined) ?? null
}
