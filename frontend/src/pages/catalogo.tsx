import { useEffect, useMemo, useSyncExternalStore } from 'react'
import { Navigate } from 'react-router'
import { carregarCatalogo } from '../actions/catalogoActions'
import { criarSecoes } from '../apis/catalogoDados'
import { authStore } from '../stores/authStore'
import { catalogoStore } from '../stores/catalogoStore'
import { ObraDestaque } from '../components/obraDestaque'
import ObraRowList from '../components/obraRowList'
import { CatalogoSkeleton } from '../components/catalogoSkeleton'
import { CatalogoErro } from '../components/catalogoErro'

export default function CatalogoPage() {
  const { tokens } = useSyncExternalStore(authStore.subscribe, authStore.getSnapshot)
  const { obras, destaque, carregando, erro } = useSyncExternalStore(catalogoStore.subscribe, catalogoStore.getSnapshot)
  const accessToken = tokens?.accessToken
  const sections = useMemo(() => criarSecoes(obras), [obras])
  useEffect(() => {
    if (accessToken) void carregarCatalogo()
  }, [accessToken])
  if (!accessToken) return <Navigate to="/login" replace />

  return <>
      {carregando ? <CatalogoSkeleton /> : erro ? <CatalogoErro mensagem={erro} onRetry={() => void carregarCatalogo()} /> : <>
        {destaque ? <ObraDestaque item={destaque} /> : <h1 className="px-4 pb-10 pt-32 text-4xl font-bold sm:px-8">Acervo Uirá</h1>}
        {obras.length > 0 ? <ObraRowList sections={sections} overlap={!!destaque} /> : <p role="status" className="px-4 pb-20 text-neutral-400 sm:px-8">Nenhuma obra disponível no catálogo.</p>}
      </>}
  </>
}
