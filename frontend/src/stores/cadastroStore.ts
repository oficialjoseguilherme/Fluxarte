import { dispatcher } from '../dispatchers/dispatcher'

interface CadastroState {
  enviando: boolean
  sucesso: boolean
  erro: string | null
}

const estadoInicial: CadastroState = {
  enviando: false,
  sucesso: false,
  erro: null,
}

let estado: CadastroState = estadoInicial

const listeners = new Set<() => void>()

function notificar(): void {
  listeners.forEach((listener) => listener())
}

dispatcher.register((action) => {
  switch (action.type) {
    case 'CADASTRO_INICIADO':
      estado = {
        enviando: true,
        sucesso: false,
        erro: null,
      }
      break

    case 'CADASTRO_FALHOU':
      estado = {
        enviando: false,
        sucesso: false,
        erro: action.mensagem,
      }
      break

    case 'CADASTRO_SUCESSO':
      estado = {
        enviando: false,
        sucesso: true,
        erro: null,
      }
      break

          case 'CADASTRO_RESETADO':
      estado = {
        enviando: false,
        sucesso: false,
        erro: null,
      }
      break

    default:
      return
  }

  notificar()
})

export const cadastroStore = {
  getSnapshot(): CadastroState {
    return estado
  },

  subscribe(listener: () => void): () => void {
    listeners.add(listener)

    return () => {
      listeners.delete(listener)
    }
  },
}