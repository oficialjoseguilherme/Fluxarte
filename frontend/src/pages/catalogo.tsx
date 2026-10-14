import { useEffect, useSyncExternalStore } from 'react'
import { Link, Navigate } from 'react-router'
import { carregarCatalogo } from '../actions/catalogoActions'
import { catalogoStore } from '../stores/catalogoStore'
import { authStore } from '../stores/authStore'
import { ObraDestaque } from '../components/obraDestaque'

export default function CatalogoPage() {
  const { tokens } = useSyncExternalStore(
    authStore.subscribe,
    authStore.getSnapshot,
  )

  const { carregando, erro, obras } = useSyncExternalStore(
    catalogoStore.subscribe,
    catalogoStore.getSnapshot,
  )

  const accessToken = tokens?.accessToken

  useEffect(() => {
    if (accessToken) {
      void carregarCatalogo()
    }
  }, [accessToken])

  if (!tokens) {
    return <Navigate to="/login" replace />
  }
  const obra = obras.find((obra) => obra.exibivel)
  return (
    <main className="min-h-screen w-full bg-zinc-950 px-6 py-8 text-white">
      <h1 className="mb-8 text-3xl font-bold">Catálogo Fluxarte</h1>
      {obra ? <ObraDestaque item={obra} /> : <h1 className="px-4 pb-6 pt-28 text-3xl font-bold sm:px-8">Catálogo</h1>}

      {carregando && (
        <p role="status">Carregando catálogo…</p>
      )}

      {erro && (
        <div role="alert">
          <p className="text-red-400">{erro}</p>

          <button
            type="button"
            onClick={() => void carregarCatalogo()}
            className="mt-4 rounded bg-red-600 px-4 py-2"
          >
            Tentar novamente
          </button>
        </div>
      )}

      {!carregando && !erro && obras.length === 0 && (
        <p>Nenhuma obra cadastrada.</p>
      )}

      <div className="grid grid-cols-1 gap-6 sm:grid-cols-2 lg:grid-cols-4">
        {obras.map((obra) => (
          <article
            key={obra.id}
            className="overflow-hidden rounded-lg bg-zinc-900"
          >
            {obra.posterUrl ? (
              <img
                src={obra.posterUrl}
                alt={`Capa de ${obra.titulo}`}
                loading="lazy"
                className="aspect-video w-full object-cover"
                onError={(event) => {
                  event.currentTarget.hidden = true
                }}
              />
            ) : (
              <div className="flex aspect-video items-center justify-center bg-zinc-800 text-zinc-400">
                Sem capa
              </div>
            )}

            <div className="p-4">
              <h2 className="text-lg font-semibold">
                {obra.titulo}
              </h2>

              <p className="mt-2 line-clamp-3 text-sm text-zinc-400">
                {obra.sinopse || 'Sinopse não disponível.'}
              </p>

              {obra.exibivel ? (
                <Link
                  to={`/assistir/${obra.id}`}
                  className="mt-4 inline-block rounded bg-red-600 px-4 py-2 hover:bg-red-700"
                >
                  Assistir
                </Link>
              ) : (
                <p className="mt-4 text-sm text-zinc-500">
                  Reprodução indisponível
                </p>
              )}
            </div>
          </article>
        ))}
      </div>
    </main>
  )
}
