import { useState } from 'react';
import { Link } from 'react-router';
import type { Obra } from '../types/catalog';

export function ObraDestaque({ item }: { item: Obra }) {
  const [failedSource, setFailedSource] = useState<string | null>(null);
  const src  = item.bannerUrl ?? item.posterUrl;
  const year = item.anoProducao;
  const actionClass = 'inline-flex items-center gap-2 rounded px-5 py-3 font-bold transition-opacity hover:opacity-75 focus-visible:outline-2 focus-visible:outline-offset-4 focus-visible:outline-white';
  return (
    <section className="relative isolate flex min-h-[80vh] items-center overflow-hidden bg-neutral-900 sm:min-h-screen">
      {src && failedSource !== src && <img src={src} alt="Foto" onError={() => setFailedSource(src)} className="absolute inset-0 -z-20 h-full w-full object-cover" />}
      <div className="absolute inset-0 -z-10 bg-[linear-gradient(to_top,#111_5%,transparent_90%),linear-gradient(to_right,#111_20%,transparent_100%)]" />
      <div className="w-full px-4 pb-24 pt-36 sm:px-8 sm:pb-36">
        <h1 className="max-w-3xl text-4xl font-bold sm:text-6xl">{item.titulo}</h1>
        <div className="mt-4 flex flex-wrap gap-4 font-bold">
          <span className="text-white">{item.classificacaoIndicativa} Anos</span>
          {year && <span>{year}</span>}
        </div>
        <p className="mt-4 max-w-xl text-base text-neutral-300 sm:text-xl">{item.sinopse ? (item.sinopse.length > 250 ? `${item.sinopse.slice(0, 247)}…` : item.sinopse) : 'Sinopse indisponível.'}</p>
        <div className="mt-5 flex flex-wrap gap-3">
          <Link to={`/assistir/${item.id}`} className={`${actionClass} bg-white text-black`}>▶ Assistir</Link>
          <Link to={{ search: `?obra=${item.id}` }} state={{ abertoPeloCatalogo: true }} className={`${actionClass} bg-neutral-500/70 text-white`}>ⓘ Mais informações</Link>
          <Link to={`/meu-uira/add/${item.id}`} className={`${actionClass} bg-neutral-700 text-white`}>+ Minha lista</Link>
        </div>
        {!!item.generos.length && <p className="mt-4 max-w-xl text-sm text-neutral-400"><strong>Gêneros: </strong>{item.generos.join(', ')}</p>}
      </div>
    </section>
  );
}