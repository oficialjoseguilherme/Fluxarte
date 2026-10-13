import { register } from '../apis/authapi'
import type { registerRequest } from '../apis/authapi'
import { dispatcher } from '../dispatchers/dispatcher'
import { cadastroStore } from '../stores/cadastroStore'

export async function cadastrar(dados: registerRequest): Promise<void> {
  if (cadastroStore.getSnapshot().enviando) return

  dispatcher.dispatch({ type: 'CADASTRO_INICIADO' })

  try {
    await register(dados)
  } catch (error) {
    dispatcher.dispatch({
      type: 'CADASTRO_FALHOU',
      mensagem:
        error instanceof Error
          ? error.message
          : 'Não foi possível realizar o cadastro.',
    })

    return
  }

  dispatcher.dispatch({ type: 'CADASTRO_SUCESSO' })
}

export function resetarCadastro(): void {
  dispatcher.dispatch({ type: 'CADASTRO_RESETADO' })
}