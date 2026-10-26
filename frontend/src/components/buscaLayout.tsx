import { useMemo, useSyncExternalStore } from 'react'
import { Navigate, Outlet } from 'react-router'
import { authStore } from '../stores/authStore'
import { catalogoStore } from '../stores/catalogoStore'
import { useBuscaParams } from '../actions/useBuscaParams'
import { sugerirBusca } from '../utils/buscaObras'
import BuscaCampo from './buscaCampo'
import Header from './header'
import Footer from './rodape'

export default function BuscaLayout() {
  const { tokens } = useSyncExternalStore(authStore.subscribe, authStore.getSnapshot)
  const { obras, carregando, erro } = useSyncExternalStore(catalogoStore.subscribe, catalogoStore.getSnapshot)
  const { consulta, alterarConsulta } = useBuscaParams()
  const sugestoes = useMemo(() => carregando || erro ? [] : sugerirBusca(obras, consulta), [obras, consulta, carregando, erro])
  if (!tokens?.accessToken) return <Navigate to="/login" replace />
  return <div className="flex min-h-screen flex-col bg-[#111] font-sans text-white">
    <a href="#conteudo" className="sr-only z-[1000] rounded bg-white p-3 text-black focus:not-sr-only focus:fixed focus:left-4 focus:top-4">Ir para o conteúdo</a>
    <Header campoBusca={<BuscaCampo consulta={consulta} sugestoes={sugestoes} onConsulta={alterarConsulta} />} />
    <main id="conteudo" tabIndex={-1} className="w-full flex-1 px-[30px] pb-16 pt-[92px] max-[760px]:px-[15px] max-[760px]:pt-[144px]"><Outlet /></main>
    <Footer />
  </div>
}
