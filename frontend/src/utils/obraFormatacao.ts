const GENEROS: Record<string, string> = {
  DOCUMENTARIO: 'Documentário',
  FICCAO: 'Ficção',
  ANIMACAO: 'Animação',
}

export function formatarGenero(genero: string): string {
  return GENEROS[genero.toUpperCase()] ?? genero.charAt(0).toLocaleUpperCase('pt-BR') + genero.slice(1).toLocaleLowerCase('pt-BR')
}

export function formatarData(iso: string | null): string | null {
  if (!iso) return null
  const data = new Date(iso)
  return Number.isNaN(data.getTime()) ? null : data.toLocaleDateString('pt-BR')
}

export function periodoExibicao(inicio: string | null, fim: string | null): string | null {
  const de = formatarData(inicio)
  const ate = formatarData(fim)
  if (de && ate) return `De ${de} até ${ate}`
  if (ate) return `Até ${ate}`
  if (de) return `A partir de ${de}`
  return null
}
