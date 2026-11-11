import { dispatcher } from '../dispatchers/dispatcher'
import type { Obra, PrateleiraRecomendacao } from '../types/catalog'

interface CatalogoState {
  carregando: boolean
  erro: string | null
  obras: Obra[]
  destaque: Obra | null
  prateleiras: PrateleiraRecomendacao[]
}

let estado: CatalogoState = {
  carregando: false,
  erro: null,
  obras: [],
  destaque: null,
  prateleiras: [],
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
        prateleiras: [],
      }
      break

    case 'CATALOGO_SUCESSO':
      estado = {
        carregando: false,
        erro: null,
        obras: action.obras,
        destaque: action.destaque,
        prateleiras: action.prateleiras ?? [],
      }
      break

    case 'CATALOGO_FALHOU':
      estado = {
        carregando: false,
        erro: action.mensagem,
        obras: [],
        destaque: null,
        prateleiras: [],
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
