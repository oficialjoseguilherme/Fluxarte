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