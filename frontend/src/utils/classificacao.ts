// Aceita o enum do backend (LIVRE, DEZ, DOZE...) e textos como "Livre", "12" ou "16 anos"
const CLASSIFICACOES = [
  { chaves: ['LIVRE', 'L', '0'], rotulo: 'L', descricao: 'Livre para todos os públicos', cor: 'bg-[#0c9447] text-white' },
  { chaves: ['DEZ', '10'], rotulo: '10', descricao: 'Não recomendado para menores de 10 anos', cor: 'bg-[#0f7dc2] text-white' },
  { chaves: ['DOZE', '12'], rotulo: '12', descricao: 'Não recomendado para menores de 12 anos', cor: 'bg-[#f8c411] text-black' },
  { chaves: ['QUATORZE', 'CATORZE', '14'], rotulo: '14', descricao: 'Não recomendado para menores de 14 anos', cor: 'bg-[#e67824] text-white' },
  { chaves: ['DEZESSEIS', '16'], rotulo: '16', descricao: 'Não recomendado para menores de 16 anos', cor: 'bg-[#db2827] text-white' },
  { chaves: ['DEZOITO', '18'], rotulo: '18', descricao: 'Não recomendado para menores de 18 anos', cor: 'bg-black text-white ring-1 ring-white/40' },
]

export type ClassificacaoInfo = (typeof CLASSIFICACOES)[number]

export function classificacaoIndicativa(valor: string | null): ClassificacaoInfo | null {
  if (!valor) return null
  const chave = valor.normalize('NFD').replace(/\p{Diacritic}/gu, '').trim().toUpperCase().replace(/\s*ANOS$/, '')
  return CLASSIFICACOES.find(classificacao => classificacao.chaves.includes(chave)) ?? null
}
