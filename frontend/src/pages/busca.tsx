import { useEffect, useId, useMemo, useSyncExternalStore } from 'react'
import { useLocation, useNavigate, useSearchParams } from 'react-router'
import { carregarCatalogo } from '../actions/catalogoActions'
import { useBuscaParams } from '../actions/useBuscaParams'
import { authStore } from '../stores/authStore'
import { catalogoStore } from '../stores/catalogoStore'
import BuscaConteudo from '../components/buscaConteudo'
import ObraModal from '../components/obraModal'
import ObraModalHero from '../components/obraModalHero'
import ObraInfo from '../components/obraInfo'

export default function BuscaPage() {
  const { tokens } = useSyncExternalStore(authStore.subscribe, authStore.getSnapshot)
  const { obras, carregando, erro } = useSyncExternalStore(catalogoStore.subscribe, catalogoStore.getSnapshot)
  const accessToken = tokens?.accessToken
  const [searchParams, setSearchParams] = useSearchParams()
  const { consulta, alterarConsulta } = useBuscaParams()
  const location = useLocation()
  const navigate = useNavigate()
  const tituloId = useId()
  const obraId = Number(searchParams.get('obra'))
  const obraSelecionada = useMemo(() => obras.find(obra => obra.id === obraId) ?? null, [obras, obraId])

  useEffect(() => {
    if (accessToken) void carregarCatalogo()
  }, [accessToken])

  function fecharObra() {
    if (location.state?.abertoPelaBusca) navigate(-1)
    else setSearchParams(params => { params.delete('obra'); return params }, { replace: true })
  }

  return <>
    <BuscaConteudo obras={obras} consulta={consulta} carregando={carregando} erro={erro}
      onRetry={() => void carregarCatalogo()}
      onConsulta={alterarConsulta} />
    <ObraModal aberto={!!obraSelecionada} onFechar={fecharObra} tituloId={tituloId}>
      {obraSelecionada && <><ObraModalHero obra={obraSelecionada} tituloId={tituloId} /><ObraInfo obra={obraSelecionada} /></>}
    </ObraModal>
  </>
}
