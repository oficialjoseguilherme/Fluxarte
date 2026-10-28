import { useEffect, useState, useSyncExternalStore } from 'react'
import { Navigate, useNavigate, useParams } from 'react-router'
import { buscarMidiaPrincipal } from '../apis/midiaapi'
import { authStore } from '../stores/authStore'
import { catalogoStore } from '../stores/catalogoStore'
import { resolverPlayerEduplay } from '../utils/eduplay'
import IconButton from '../components/iconButton'
import PlayerEduplay from '../components/playerEduplay'
import PlayerIndisponivel from '../components/playerIndisponivel'

type Resultado =
  | { obraId: number; tipo: 'pronto'; src: string }
  | { obraId: number; tipo: 'indisponivel'; mensagem?: string }

export default function PlayerPage() {
  const { id } = useParams()
  const obraId = Number(id)
  const navigate = useNavigate()
  const { tokens } = useSyncExternalStore(authStore.subscribe, authStore.getSnapshot)
  const { obras } = useSyncExternalStore(catalogoStore.subscribe, catalogoStore.getSnapshot)
  const accessToken = tokens?.accessToken
  const titulo = obras.find(obra => obra.id === obraId)?.titulo ?? 'Esta obra'
  const [resultado, setResultado] = useState<Resultado | null>(null)
  // Enquanto não chega o resultado desta obra, a tela fica em "carregando"
  const estado = resultado?.obraId === obraId ? resultado : { tipo: 'carregando' as const }

  useEffect(() => {
    if (!accessToken || !(obraId > 0)) return
    let ativo = true
    buscarMidiaPrincipal(obraId, accessToken)
      .then(midia => {
        const src = midia?.playerType === 'EMBED' ? resolverPlayerEduplay(midia.eduplayEmbedUrl) : null
        if (ativo) setResultado(src ? { obraId, tipo: 'pronto', src } : { obraId, tipo: 'indisponivel' })
      })
      .catch((error: unknown) => {
        if (ativo) setResultado({ obraId, tipo: 'indisponivel', mensagem: error instanceof Error ? error.message : undefined })
      })
    return () => { ativo = false }
  }, [obraId, accessToken])

  if (!accessToken) return <Navigate to="/login" replace />
  if (!(obraId > 0)) return <Navigate to="/catalogo" replace />

  // Volta para onde o usuário estava (o modal da obra); se a página foi aberta direto, vai ao catálogo
  function voltar() {
    if (window.history.state?.idx > 0) navigate(-1)
    else navigate(`/catalogo?obra=${obraId}`, { replace: true })
  }

  return <main className="relative h-dvh w-full overflow-hidden bg-black text-white">
    <IconButton label="Voltar" onClick={voltar} className="absolute left-4 top-4 z-10 gap-2 rounded-full bg-black/60 pr-4 text-white">
      <svg aria-hidden="true" viewBox="0 0 24 24" className="h-7 w-7 fill-current"><path d="M20 11H7.83l5.59-5.59L12 4l-8 8 8 8 1.41-1.41L7.83 13H20z" /></svg>
      <span className="text-sm font-semibold">Voltar</span>
    </IconButton>
    {estado.tipo === 'carregando' && <div role="status" className="flex h-full items-center justify-center text-neutral-400">Carregando…</div>}
    {estado.tipo === 'pronto' && <PlayerEduplay src={estado.src} titulo={titulo} />}
    {estado.tipo === 'indisponivel' && <PlayerIndisponivel titulo={titulo} mensagem={estado.mensagem} onVoltar={voltar} />}
  </main>
}
