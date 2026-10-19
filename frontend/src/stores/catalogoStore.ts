import { dispatcher } from '../dispatchers/dispatcher'
import type { Obra } from '../types/catalog'

interface CatalogoState {
  carregando: boolean
  erro: string | null
  obras: Obra[]
  destaque: Obra | null
}

let estado: CatalogoState = {
  carregando: false,
  erro: null,
  obras: [],
  destaque: null,
}

const listeners = new Set<() => void>()

dispatcher.register((action) => {
  switch (action.type) {
    case 'CATALOGO_INICIADO':
      estado = {
        carregando: true,
        erro: null,
        obras: [],
        destaque: null,
      }
      break

    case 'CATALOGO_SUCESSO':
      estado = {
        carregando: false,
        erro: null,
        obras: action.obras,
        destaque: action.destaque,
      }
      break

    case 'CATALOGO_FALHOU':
      estado = {
        carregando: false,
        erro: action.mensagem,
        obras: [],
        destaque: null,
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
