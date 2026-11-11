import { listarObras } from '../apis/catalogoapi'
import { buscarRecomendacoesHome } from '../apis/recomendacaoapi'
import { escolherDestaque } from '../apis/catalogoDados'
import { dispatcher } from '../dispatchers/dispatcher'
import { authStore } from '../stores/authStore'
import { catalogoStore } from '../stores/catalogoStore'
import type { Obra } from '../types/catalog'

export async function carregarCatalogo(): Promise<void> {
  if (catalogoStore.getSnapshot().carregando) return
  const accessToken = authStore.getSnapshot().tokens?.accessToken
  if (!accessToken) {
    dispatcher.dispatch({ type: 'CATALOGO_FALHOU', mensagem: 'Entre na sua conta para acessar o catálogo.' })
    return
  }
  dispatcher.dispatch({ type: 'CATALOGO_INICIADO' })
  try {
    const [obras, recomendacaoHome] = await Promise.all([
      listarObras(accessToken),
      buscarRecomendacoesHome(accessToken).catch(() => null),
    ])
    let destaque: Obra | null = null
    if (recomendacaoHome?.destaque) {
      destaque = obras.find(o => o.id === recomendacaoHome.destaque?.obraId) ?? null
    }
    if (!destaque) {
      destaque = escolherDestaque(obras)
    }
    dispatcher.dispatch({
      type: 'CATALOGO_SUCESSO',
      obras,
      destaque,
      prateleiras: recomendacaoHome?.prateleiras ?? [],
    })
  } catch (error) {
    dispatcher.dispatch({ type: 'CATALOGO_FALHOU', mensagem: error instanceof Error ? error.message : 'Não foi possível carregar o catálogo.' })
  }
}
