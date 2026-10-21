import { classificacaoIndicativa } from '../utils/classificacao'

export default function ClassificacaoBadge({ classificacao }: { classificacao: string | null }) {
  const info = classificacaoIndicativa(classificacao)
  if (!info) return null
  return <span role="img" aria-label={info.descricao} title={info.descricao}
    className={`inline-flex h-7 min-w-7 items-center justify-center rounded px-1 text-sm font-bold ${info.cor}`}>{info.rotulo}</span>
}
