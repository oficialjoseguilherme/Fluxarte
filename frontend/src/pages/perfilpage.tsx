import { useEffect, useState, useSyncExternalStore } from 'react'
import { Link, Navigate } from 'react-router'
import { authStore } from '../stores/authStore'
import {
  atualizarGenerosPreferidos,
  listarGenerosDisponiveis,
  listarGenerosPreferidos,
  type GeneroItem,
} from '../apis/recomendacaoapi'
import Button from '../components/button'

export default function PerfilPage() {
  const { tokens } = useSyncExternalStore(authStore.subscribe, authStore.getSnapshot)
  const accessToken = tokens?.accessToken
  const [generosDisponiveis, setGenerosDisponiveis] = useState<GeneroItem[]>([])
  const [selecionados, setSelecionados] = useState<Set<string>>(new Set())
  const [salvando, setSalvando] = useState(false)
  const [mensagem, setMensagem] = useState<string | null>(null)

  useEffect(() => {
    if (!accessToken) return
    void Promise.all([
      listarGenerosDisponiveis(accessToken),
      listarGenerosPreferidos(accessToken),
    ]).then(([disponiveis, preferidos]) => {
      setGenerosDisponiveis(disponiveis)
      setSelecionados(new Set(preferidos))
    })
  }, [accessToken])

  if (!accessToken) return <Navigate to="/login" replace />

  function alternarGenero(id: string) {
    setSelecionados(prev => {
      const proximo = new Set(prev)
      if (proximo.has(id)) proximo.delete(id)
      else proximo.add(id)
      return proximo
    })
  }

  async function salvar() {
    if (!accessToken) return
    setSalvando(true)
    setMensagem(null)
    try {
      await atualizarGenerosPreferidos(accessToken, Array.from(selecionados))
      setMensagem('Preferências salvas com sucesso! Suas recomendações foram atualizadas.')
    } catch {
      setMensagem('Não foi possível salvar suas preferências.')
    } finally {
      setSalvando(false)
    }
  }

  return (
    <main className="min-h-screen w-full bg-zinc-950 px-6 py-28 text-white sm:px-12">
      <div className="mx-auto max-w-3xl">
        <h1 className="text-3xl font-bold">Preferências e Recomendações</h1>
        <p className="mt-2 text-neutral-400">
          Escolha seus gêneros favoritos para calibrar o motor de recomendações do catálogo Uirá.
        </p>

        {mensagem && (
          <div role="status" className="mt-4 rounded bg-neutral-800 p-3 text-sm text-green-400">
            {mensagem}
          </div>
        )}

        <div className="mt-8 flex flex-wrap gap-2.5">
          {generosDisponiveis.map(({ id, rotulo }) => {
            const ativo = selecionados.has(id)
            return (
              <button
                key={id}
                type="button"
                onClick={() => alternarGenero(id)}
                className={`rounded-full px-4 py-2 text-sm font-semibold transition-colors duration-150 ${
                  ativo
                    ? 'bg-red-600 text-white'
                    : 'bg-neutral-800 text-neutral-300 hover:bg-neutral-700'
                }`}
              >
                {rotulo}
              </button>
            )
          })}
        </div>

        <div className="mt-8 flex items-center gap-4">
          <Button onClick={salvar} disabled={salvando}>
            {salvando ? 'Salvando...' : 'Salvar Preferências'}
          </Button>
          <Link to="/catalogo" className="text-sm text-neutral-400 hover:underline">
            Voltar ao catálogo
          </Link>
        </div>
      </div>
    </main>
  )
}
