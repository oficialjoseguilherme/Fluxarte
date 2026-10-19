import type { CatalogSection, Obra } from '../types/catalog'

function text(value: unknown): string | null {
  return typeof value === 'string' && value.trim() ? value.trim() : null
}

function httpUrl(value: unknown): string | null {
  const candidate = text(value)
  if (!candidate) return null
  try {
    const url = new URL(candidate)
    return ['http:', 'https:'].includes(url.protocol) ? url.href : null
  } catch { return null }
}

export function normalizarCatalogo(data: unknown): Obra[] {
  if (!Array.isArray(data)) throw new Error('O arquivo do catálogo deve conter uma lista de obras.')
  const ids = new Set<number>()
  return data.map((entry: unknown, index) => {
    if (!entry || typeof entry !== 'object') throw new Error(`Obra inválida na posição ${index + 1}.`)
    const raw = entry as Record<string, unknown>
    const id = raw.idFilme
    const titulo = text(raw.titulo)
    if (typeof id !== 'number' || !Number.isFinite(id) || !titulo || ids.has(id)) throw new Error(`ID ou título inválido na posição ${index + 1}.`)
    ids.add(id)
    const generos = Array.isArray(raw.generos)
      ? [...new Set(raw.generos.flatMap((value: unknown) => typeof value === 'string' ? value.split('/') : [])
        .map(value => value.normalize('NFC').trim().replace(/\s+/gu, ' ').toLocaleLowerCase('pt-BR'))
        .filter(value => value && value !== 'none'))]
      : []
    return {
      id, titulo, generos,
      duracao: text(raw.duracao),
      classificacaoIndicativa: text(raw.classificacao),
      sinopse: text(raw.sinopse),
      posterUrl: httpUrl(raw.poster),
      detalhesUrl: httpUrl(raw.detalhesUrl),
      diretor: null, tituloOriginal: null, anoProducao: null, idiomaOriginal: null,
      bannerUrl: null, dataInicioExibicao: null, dataFimExibicao: null, exibivel: true,
    }
  })
}

export function criarSecoes(obras: Obra[]): CatalogSection[] {
  if (!obras.length) return []
  const generos = [...new Set(obras.flatMap(obra => obra.generos))]
    .sort((a, b) => a.localeCompare(b, 'pt-BR'))
  const sections: CatalogSection[] = [
    { url: 'todas-as-obras', titulo: 'Todas as obras', obras: obras },
    ...generos.map(genero => ({
      url: `genero-${encodeURIComponent(genero)}`,
      titulo: genero.charAt(0).toLocaleUpperCase('pt-BR') + genero.slice(1),
      obras: obras.filter(obra => obra.generos.includes(genero)),
    })),
  ]
  const semGenero = obras.filter(obra => !obra.generos.length)
  if (semGenero.length) sections.push({ url: 'sem-genero', titulo: 'Sem gênero', obras: semGenero })
  return sections
}

export function escolherDestaque(obras: Obra[], random: () => number = Math.random): Obra | null {
  if (!obras.length) return null
  return obras[Math.floor(random() * obras.length)]
}
