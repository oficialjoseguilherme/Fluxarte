import { listarObras } from '../apis/catalogoapi'
import { dispatcher } from '../dispatchers/dispatcher'
import { authStore } from '../stores/authStore'
import { catalogoStore } from '../stores/catalogoStore'

export async function carregarCatalogo(): Promise<void> {
  if (catalogoStore.getSnapshot().carregando) return

  const accessToken = authStore.getSnapshot().tokens?.accessToken

  if (!accessToken) {
    dispatcher.dispatch({
      type: 'CATALOGO_FALHOU',
      mensagem: 'Entre na sua conta para acessar o catálogo.',
    })
    return
  }

  dispatcher.dispatch({ type: 'CATALOGO_INICIADO' })

  let obras

  try {
    obras = await listarObras(accessToken)
  } catch (error) {
    dispatcher.dispatch({
      type: 'CATALOGO_FALHOU',
      mensagem:
        error instanceof Error
          ? error.message
          : 'Não foi possível carregar o catálogo.',
    })
    return
  }

  dispatcher.dispatch({
    type: 'CATALOGO_SUCESSO',
    obras,
  })
}