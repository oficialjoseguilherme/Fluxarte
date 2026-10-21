import type { Obra } from '../types/catalog'
import Button from './button'
import ObraImagem from './obraImagem'

export default function ObraModalHero({ obra, tituloId }: { obra: Obra; tituloId: string }) {
  return <div className="relative aspect-video min-h-[280px] w-full">
    <ObraImagem src={obra.bannerUrl ?? obra.posterUrl} alt="" eager className="absolute inset-0 h-full w-full object-cover" />
    <div className="absolute inset-0 bg-[linear-gradient(to_top,#181818_0%,transparent_60%)]" />
    <div className="absolute inset-x-0 bottom-0 px-6 pb-6 sm:px-12 sm:pb-10">
      <h2 id={tituloId} className="max-w-[80%] text-3xl font-bold sm:text-5xl">{obra.titulo}</h2>
      <div className="mt-5 flex flex-wrap gap-3">
        <Button to={`/assistir/${obra.id}`} variant="light" size="featured">▶ Assistir</Button>
        <Button to={`/meu-uira/add/${obra.id}`} variant="secondary" size="featured">+ Minha lista</Button>
      </div>
    </div>
  </div>
}
