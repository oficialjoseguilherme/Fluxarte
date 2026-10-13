// loginpage:

export interface LoginRequest {
  email: string
  senha: string
}

export interface TokenResponse {
  accessToken: string
  refreshToken: string
  tipo: string
}

export async function login(
  request: LoginRequest,
): Promise<TokenResponse> {
  const response = await fetch('/api/auth/login', {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json',
    },
    body: JSON.stringify(request),
  })

  const data = await response.json().catch(() => null)

  if (!response.ok) {
    if (data && typeof data.mensagem === 'string') {
      throw new Error(data.mensagem)
    }
    else {
      throw new Error('Não foi possível entrar.')
    }
  }

  if (typeof data.accessToken !== 'string' || typeof data.refreshToken !== 'string' || data.tipo !== 'Bearer') {
        throw new Error('Resposta de login inválida.')
      }

  return {
    accessToken: data.accessToken,
    refreshToken: data.refreshToken,
    tipo: data.tipo,
  }
}

// cadastropage:
export interface registerRequest {
  nome: string
  email: string
  senha: string
  dataNascimento: string
}

export async function register(request: registerRequest): Promise<void> {
  const response = await fetch('/api/auth/registrar', {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json',
    },
    body: JSON.stringify(request),
  })
    if (!response.ok) {
        const erro = await response.json().catch(()=> null )
        if (erro && erro.mensagem && typeof erro.mensagem === 'string') {
            throw new Error(erro.mensagem)
        }
        else {
            throw new Error("Erro desconhecido ao registrar usuário")
        }
    }
}