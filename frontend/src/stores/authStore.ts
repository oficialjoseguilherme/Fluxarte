import { dispatcher } from '../dispatchers/dispatcher'
import type { TokenResponse } from '../apis/authapi'

interface AuthState {
  enviando: boolean
  erro: string | null
  tokens: TokenResponse | null
}

let estado: AuthState = {
  enviando: false,
  erro: null,
  tokens: null,
}

const listeners = new Set<() => void>()

function notificar(): void {
  listeners.forEach((listener) => listener())
}

dispatcher.register((action) => {
  switch (action.type) {
    case 'LOGIN_INICIADO':
      estado = {
        ...estado,
        enviando: true,
        erro: null,
      }
      break

    case 'LOGIN_SUCESSO':
      estado = {
        enviando: false,
        erro: null,
        tokens: action.tokens,
      }
      break

    case 'LOGIN_FALHOU':
      estado = {
        enviando: false,
        erro: action.mensagem,
        tokens: null,
      }
      break

    case 'LOGIN_RESETADO':
      estado = {
        ...estado,
        enviando: false,
        erro: null,
      }
      break

    default:
      return
  }

  notificar()
})

export const authStore = {
  getSnapshot(): AuthState {
    return estado
  },

  subscribe(listener: () => void): () => void {
    listeners.add(listener)

    return () => {
      listeners.delete(listener)
    }
  },
}