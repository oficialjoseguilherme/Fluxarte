import type { Obra } from '../types/catalog'

const LIMIAR_CORRECAO = 0.5

export interface SugestaoBusca {
  texto: string
  tipo: 'Título' | 'Diretor'
}

export function normalizarBusca(texto: string): string {
  return texto.normalize('NFD').replace(/\p{M}/gu, '').toLocaleLowerCase('pt-BR').trim().replace(/\s+/gu, ' ')
}

export function filtrarObras(obras: Obra[], consulta: string): Obra[] {
  const termo = normalizarBusca(consulta)
  if (!termo) return []
  return obras.filter(obra => [obra.titulo, obra.diretor].some(texto => texto && normalizarBusca(texto).includes(termo)))
}

function candidatos(obras: Obra[]): SugestaoBusca[] {
  const unicos = new Map<string, SugestaoBusca>()
  for (const obra of obras) {
    for (const [texto, tipo] of [[obra.titulo, 'Título'], [obra.diretor, 'Diretor']] as const) {
      if (!texto?.trim()) continue
      const chave = normalizarBusca(texto)
      if (!unicos.has(chave)) unicos.set(chave, { texto: texto.trim(), tipo })
    }
  }
  return [...unicos.values()]
}

export function sugerirBusca(obras: Obra[], consulta: string): SugestaoBusca[] {
  const termo = normalizarBusca(consulta)
  if (termo.length < 2) return []
  return candidatos(obras)
    .filter(item => normalizarBusca(item.texto).includes(termo))
    .sort((a, b) => Number(normalizarBusca(b.texto).startsWith(termo)) - Number(normalizarBusca(a.texto).startsWith(termo)) || a.texto.localeCompare(b.texto, 'pt-BR'))
    .slice(0, 5)
}

function similaridade(a: string, b: string): number {
  let anterior = Array.from({ length: b.length + 1 }, (_, i) => i)
  for (let i = 1; i <= a.length; i++) {
    const atual = [i]
    for (let j = 1; j <= b.length; j++) {
      atual[j] = Math.min(atual[j - 1] + 1, anterior[j] + 1, anterior[j - 1] + Number(a[i - 1] !== b[j - 1]))
    }
    anterior = atual
  }
  return 1 - anterior[b.length] / Math.max(a.length, b.length)
}

function pontuarNome(termo: string, nome: string): number {
  const palavras = nome.split(' ')
  let melhor = 0
  for (let inicio = 0; inicio < palavras.length; inicio++) {
    for (let fim = inicio + 1; fim <= palavras.length; fim++) {
      const trecho = palavras.slice(inicio, fim).join(' ')
      // Comprimentos muito diferentes nunca atingiriam o limite de semelhança.
      if (Math.min(termo.length, trecho.length) / Math.max(termo.length, trecho.length) < LIMIAR_CORRECAO) continue
      melhor = Math.max(melhor, similaridade(termo, trecho))
    }
  }
  return melhor
}

export function corrigirBusca(obras: Obra[], consulta: string): SugestaoBusca[] {
  const termo = normalizarBusca(consulta)
  if (termo.length < 2) return []
  return candidatos(obras)
    .filter(item => normalizarBusca(item.texto) !== termo)
    .map(item => ({ item, pontos: pontuarNome(termo, normalizarBusca(item.texto)) }))
    .filter(({ pontos }) => pontos >= LIMIAR_CORRECAO)
    .sort((a, b) => b.pontos - a.pontos || a.item.texto.localeCompare(b.item.texto, 'pt-BR'))
    .slice(0, 3)
    .map(({ item }) => item)
}
