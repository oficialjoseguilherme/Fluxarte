import type { Obra } from '../types/catalog'
import ClassificacaoBadge from './classificacaoBadge'
import { formatarGenero, periodoExibicao } from '../utils/obraFormatacao'

export default function ObraInfo({ obra }: { obra: Obra }) {
  const detalhes = [
    { rotulo: 'Direção', valor: obra.diretor },
    { rotulo: 'Gêneros', valor: obra.generos.map(formatarGenero).join(', ') || null },
    { rotulo: 'Título original', valor: obra.tituloOriginal !== obra.titulo ? obra.tituloOriginal : null },
    { rotulo: 'Idioma original', valor: obra.idiomaOriginal },
    { rotulo: 'Em exibição', valor: periodoExibicao(obra.dataInicioExibicao, obra.dataFimExibicao) },
  ].filter(detalhe => detalhe.valor)
  return <div className="grid gap-8 px-6 pb-10 sm:px-12 md:grid-cols-[2fr_1fr]">
    <div>
      <div className="flex flex-wrap items-center gap-3 text-neutral-300">
        {obra.anoProducao != null && <span>{obra.anoProducao}</span>}
        <ClassificacaoBadge classificacao={obra.classificacaoIndicativa} />
        {obra.duracao && <span>{obra.duracao}</span>}
      </div>
      <p className="mt-4 leading-relaxed text-neutral-200">{obra.sinopse ?? 'Sinopse indisponível.'}</p>
    </div>
    {detalhes.length > 0 && <dl className="space-y-3 text-sm">
      {detalhes.map(detalhe => <div key={detalhe.rotulo}><dt className="inline text-neutral-500">{detalhe.rotulo}: </dt><dd className="inline text-neutral-200">{detalhe.valor}</dd></div>)}
    </dl>}
  </div>
}
