import { Fragment, useId, useMemo } from 'react'
import type { Obra } from '../types/catalog'
import { corrigirBusca, filtrarObras, normalizarBusca } from '../utils/buscaObras'
import BuscaErro from './buscaErro'
import BuscaSkeleton from './buscaSkeleton'
import ObraCard from './obraCard'

interface BuscaConteudoProps {
  obras: Obra[]
  consulta: string
  carregando: boolean
  erro: string | null
  onConsulta: (consulta: string) => void
  onRetry: () => void
}

export default function BuscaConteudo({ obras, consulta, carregando, erro, onConsulta, onRetry }: BuscaConteudoProps) {
  const id = useId()
  const termo = normalizarBusca(consulta)
  const resultados = useMemo(() => filtrarObras(obras, consulta), [obras, consulta])
  const correcoes = useMemo(() => corrigirBusca(obras, consulta), [obras, consulta])

  return <section aria-labelledby={`${id}-titulo`} aria-busy={carregando}>
    <h1 id={`${id}-titulo`} className="sr-only">Buscar obras</h1>
    {carregando ? <BuscaSkeleton />
      : erro ? <BuscaErro mensagem={erro} onRetry={onRetry} />
        : !termo ? <p role="status" className="text-sm text-neutral-400">Digite um título ou diretor para começar sua busca.</p>
          : !obras.length ? <p role="status" className="text-neutral-400">Nenhuma obra disponível no catálogo.</p>
            : <>
              {correcoes.length > 0 && <nav aria-label="Correções de busca" className="mb-6 flex flex-wrap items-baseline gap-x-2 gap-y-1 text-sm">
                <span className="text-neutral-400">Você quis dizer:</span>
                {correcoes.map((item, indice) => <Fragment key={item.texto}>
                  {indice > 0 && <span aria-hidden="true" className="text-neutral-500">|</span>}
                  <button type="button" className="max-w-full break-words text-left text-neutral-200 underline-offset-4 hover:text-white hover:underline focus-visible:rounded-sm focus-visible:outline-2 focus-visible:outline-offset-4 focus-visible:outline-white" onClick={() => onConsulta(item.texto)}>{item.texto}</button>
                </Fragment>)}
              </nav>}
              <section aria-label="Resultados da busca">
                {resultados.length ? <>
                  <p role="status" className="sr-only">{resultados.length} {resultados.length === 1 ? 'obra encontrada' : 'obras encontradas'}</p>
                  <div className="grid grid-cols-1 gap-x-4 gap-y-6 min-[480px]:grid-cols-2 md:grid-cols-3 xl:grid-cols-4 2xl:grid-cols-5">
                    {resultados.map(obra => <ObraCard key={obra.id} obra={obra} emGrade preservarParametros />)}
                  </div>
                </> : <p role="status" className="break-words text-neutral-400">Nenhuma obra encontrada para “{consulta.trim()}”.</p>}
              </section>
            </>}
  </section>
}
