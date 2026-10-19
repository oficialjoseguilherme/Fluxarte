//para o catalogo
import type { Obra } from '../types/catalog'

//para o loginpage
import type { TokenResponse } from '../apis/authapi'



// para o cadastropage
export type AppAction =
  | { type: 'CADASTRO_INICIADO' }
  | { type: 'CADASTRO_FALHOU'; mensagem: string }
  | { type: 'CADASTRO_SUCESSO' }
  | { type: 'CADASTRO_RESETADO' }

  | { type: 'LOGIN_INICIADO' }
  | { type: 'LOGIN_SUCESSO'; tokens: TokenResponse }
  | { type: 'LOGIN_FALHOU'; mensagem: string }
  | { type: 'LOGIN_RESETADO' }

  | { type: 'CATALOGO_INICIADO'}
  | { type: 'CATALOGO_SUCESSO'; obras: Obra[] , destaque: Obra | null }
  | { type: 'CATALOGO_FALHOU'; mensagem: string }

type ActionHandler = (action: AppAction) => void

const actionHandlers = new Set<ActionHandler>()

export const dispatcher = {
  register: (handler: ActionHandler) => {
    actionHandlers.add(handler)
    return () => {
      actionHandlers.delete(handler)
    }
  },
  dispatch: (action: AppAction) => {
    for (const handler of actionHandlers) {
      handler(action)
    }
  }
}
