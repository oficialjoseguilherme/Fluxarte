import type { RecomendacaoHomeResponse } from '../types/catalog'

export async function buscarRecomendacoesHome(accessToken: string): Promise<RecomendacaoHomeResponse | null> {
  const response = await fetch('/api/recomendacoes/home', {
    headers: {
      Authorization: `Bearer ${accessToken}`,
    },
  })
  if (!response.ok) return null
  return response.json().catch(() => null)
}

export async function atualizarGenerosPreferidos(accessToken: string, generos: string[]): Promise<string[]> {
  const response = await fetch('/api/usuarios/me/generos-preferidos', {
    method: 'PUT',
    headers: {
      'Content-Type': 'application/json',
      Authorization: `Bearer ${accessToken}`,
    },
    body: JSON.stringify({ generos }),
  })
  if (!response.ok) throw new Error('Não foi possível atualizar as preferências.')
  const data = await response.json().catch(() => null)
  return data?.generos ?? []
}

export async function listarGenerosPreferidos(accessToken: string): Promise<string[]> {
  const response = await fetch('/api/usuarios/me/generos-preferidos', {
    headers: {
      Authorization: `Bearer ${accessToken}`,
    },
  })
  if (!response.ok) return []
  const data = await response.json().catch(() => null)
  return data?.generos ?? []
}

export interface GeneroItem {
  id: string
  rotulo: string
}

export async function listarGenerosDisponiveis(accessToken: string): Promise<GeneroItem[]> {
  const response = await fetch('/api/generos', {
    headers: {
      Authorization: `Bearer ${accessToken}`,
    },
  })
  if (!response.ok) return []
  const data = await response.json().catch(() => null)
  return Array.isArray(data) ? (data as GeneroItem[]) : []
}
