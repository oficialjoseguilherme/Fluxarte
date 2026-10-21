import { Link } from 'react-router'
import type { Obra } from '../types/catalog'
import ObraImagem from './obraImagem'
import { OBRA_CARD_WIDTH } from '../utils/obraRowGeometry'

export default function ObraCard({ obra }: { obra: Obra }) {
  return <article className="shrink-0 p-1" style={{ width: OBRA_CARD_WIDTH }}>
    <Link to={{ search: `?obra=${obra.id}` }} state={{ abertoPeloCatalogo: true }} aria-label={`${obra.titulo} — mais informações`} className="group block rounded">
      <div className="overflow-hidden rounded bg-neutral-800"><ObraImagem src={obra.posterUrl} alt={`Pôster de ${obra.titulo}`} className="aspect-[16/9] w-full object-cover transition-transform duration-200 group-hover:scale-105 group-focus-visible:scale-105 motion-reduce:transition-none" /></div>
      <h3 className="mt-2 line-clamp-2 text-sm font-semibold leading-snug">{obra.titulo}</h3>
      {obra.duracao && <p className="mt-1 text-xs text-neutral-400">{obra.duracao}</p>}
    </Link>
  </article>
}
