import type { Obra } from '../types/obra'

export async function listarObras(
  accessToken: string,
): Promise<Obra[]> {
  const response = await fetch('/api/obras', {
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
        throw new Error('Não foi possível carregar o catálogo.')
    }
  }

  if (!Array.isArray(data)) {
    throw new Error('Resposta do catálogo inválida.')
  }

  return data as Obra[]
}