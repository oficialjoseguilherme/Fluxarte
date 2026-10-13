import { register } from '../apis/authapi'
import type { registerRequest } from "../apis/authapi"
import { dispatcher } from '../dispatchers/dispatcher'
import { cadastroStore } from '../stores/cadastroStore'
import { login } from '../apis/authapi'
import type { LoginRequest } from '../apis/authapi'
import { authStore } from '../stores/authStore'

export async function entrar(dados: LoginRequest): Promise<void> {
  if (authStore.getSnapshot().enviando) return

  dispatcher.dispatch({ type: 'LOGIN_INICIADO' })

  let tokens

  try {
    tokens = await login(dados)
  } catch (error) {
    dispatcher.dispatch({
      type: 'LOGIN_FALHOU',
      mensagem:
        error instanceof Error
          ? error.message
          : 'Não foi possível entrar.',
    })

    return
  }

  dispatcher.dispatch({
    type: 'LOGIN_SUCESSO',
    tokens,
  })
}

export function resetarLogin(): void {
  dispatcher.dispatch({ type: 'LOGIN_RESETADO' })
}

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