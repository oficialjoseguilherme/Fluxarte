import { useEffect, useId, useMemo, useSyncExternalStore } from 'react'
import { Navigate, useLocation, useNavigate, useSearchParams } from 'react-router'
import { carregarCatalogo } from '../actions/catalogoActions'
import { criarSecoes } from '../apis/catalogoDados'
import { authStore } from '../stores/authStore'
import { catalogoStore } from '../stores/catalogoStore'
import { ObraDestaque } from '../components/obraDestaque'
import ObraRowList from '../components/obraRowList'
import { CatalogoSkeleton } from '../components/catalogoSkeleton'
import { CatalogoErro } from '../components/catalogoErro'
import ObraModal from '../components/obraModal'
import ObraModalHero from '../components/obraModalHero'
import ObraInfo from '../components/obraInfo'

export default function CatalogoPage() {
  const { tokens } = useSyncExternalStore(authStore.subscribe, authStore.getSnapshot)
  const { obras, destaque, carregando, erro } = useSyncExternalStore(catalogoStore.subscribe, catalogoStore.getSnapshot)
  const accessToken = tokens?.accessToken
  const sections = useMemo(() => criarSecoes(obras), [obras])
  const [searchParams, setSearchParams] = useSearchParams()
  const location = useLocation()
  const navigate = useNavigate()
  const tituloId = useId()
  const obraId = Number(searchParams.get('obra'))
  const obraSelecionada = useMemo(() => obras.find(obra => obra.id === obraId) ?? null, [obras, obraId])
  useEffect(() => {
    if (accessToken) void carregarCatalogo()
  }, [accessToken])
  if (!accessToken) return <Navigate to="/login" replace />

  // Se o modal foi aberto a partir do catálogo, voltar no histórico; se veio por link direto, só remove o ?obra
  function fecharObra() {
    if (location.state?.abertoPeloCatalogo) navigate(-1)
    else setSearchParams(params => { params.delete('obra'); return params }, { replace: true })
  }

  return <>
      {carregando ? <CatalogoSkeleton /> : erro ? <CatalogoErro mensagem={erro} onRetry={() => void carregarCatalogo()} /> : <>
        {destaque ? <ObraDestaque item={destaque} /> : <h1 className="px-4 pb-10 pt-32 text-4xl font-bold sm:px-8">Acervo Uirá</h1>}
        {obras.length > 0 ? <ObraRowList sections={sections} overlap={!!destaque} /> : <p role="status" className="px-4 pb-20 text-neutral-400 sm:px-8">Nenhuma obra disponível no catálogo.</p>}
      </>}
      <ObraModal aberto={!!obraSelecionada} onFechar={fecharObra} tituloId={tituloId}>
        {obraSelecionada && <><ObraModalHero obra={obraSelecionada} tituloId={tituloId} /><ObraInfo obra={obraSelecionada} /></>}
      </ObraModal>
  </>
}
