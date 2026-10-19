import { useSyncExternalStore } from 'react'
import { Navigate, Outlet } from 'react-router'
import { authStore } from '../stores/authStore'
import Header from './header'
import Footer from './rodape'

export default function CatalogoLayout() {
  const { tokens } = useSyncExternalStore(authStore.subscribe, authStore.getSnapshot)
  if (!tokens?.accessToken) return <Navigate to="/login" replace />
  return <div id="inicio" className="min-h-screen bg-[#111] font-sans text-white">
    <a href="#conteudo" className="sr-only z-[1000] rounded bg-white p-3 text-black focus:not-sr-only focus:fixed focus:left-4 focus:top-4">Ir para o conteúdo</a>
    <Header />
    <main id="conteudo" tabIndex={-1}><Outlet /></main>
    <Footer />
  </div>
}
