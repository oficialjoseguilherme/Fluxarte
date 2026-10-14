import { dispatcher } from '../dispatchers/dispatcher'
import type { Obra } from '../types/catalog'

interface CatalogoState {
  carregando: boolean
  erro: string | null
  obras: Obra[]
}

let estado: CatalogoState = {
  carregando: false,
  erro: null,
  obras: [],
}

const listeners = new Set<() => void>()

dispatcher.register((action) => {
  switch (action.type) {
    case 'CATALOGO_INICIADO':
      estado = {
        carregando: true,
        erro: null,
        obras: [],
      }
      break

    case 'CATALOGO_SUCESSO':
      estado = {
        carregando: false,
        erro: null,
        obras: action.obras,
      }
      break

    case 'CATALOGO_FALHOU':
      estado = {
        carregando: false,
        erro: action.mensagem,
        obras: [],
      }
      break

    default:
      return
  }

  listeners.forEach((listener) => listener())
})

export const catalogoStore = {
  getSnapshot(): CatalogoState {
    return estado
  },

  subscribe(listener: () => void): () => void {
    listeners.add(listener)

    return () => {
      listeners.delete(listener)
    }
  },
}