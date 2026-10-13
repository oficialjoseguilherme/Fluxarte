export type AppAction =
  | { type: 'CADASTRO_INICIADO' }
  | { type: 'CADASTRO_FALHOU'; mensagem: string }
  | { type: 'CADASTRO_SUCESSO' }
  | { type: 'CADASTRO_RESETADO' }

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
