import { listarObras } from '../apis/catalogoapi'
import { escolherDestaque } from '../apis/catalogoDados'
import { dispatcher } from '../dispatchers/dispatcher'
import { authStore } from '../stores/authStore'
import { catalogoStore } from '../stores/catalogoStore'

export async function carregarCatalogo(): Promise<void> {
  if (catalogoStore.getSnapshot().carregando) return
  if (!authStore.getSnapshot().tokens?.accessToken) {
    dispatcher.dispatch({ type: 'CATALOGO_FALHOU', mensagem: 'Entre na sua conta para acessar o catálogo.' })
    return
  }
  dispatcher.dispatch({ type: 'CATALOGO_INICIADO' })
  try {
    const accesToken = authStore.getSnapshot().tokens?.accessToken
    if (!accesToken) throw new Error('Token de acesso ausente.')
    const obras = await listarObras(accesToken)
    dispatcher.dispatch({ type: 'CATALOGO_SUCESSO', obras, destaque: escolherDestaque(obras) })
  } catch (error) {
    dispatcher.dispatch({ type: 'CATALOGO_FALHOU', mensagem: error instanceof Error ? error.message : 'Não foi possível carregar o catálogo.' })
  }
}
