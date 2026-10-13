import { useEffect, useSyncExternalStore } from 'react'
import type { FormEvent } from 'react'
import { Link, useNavigate } from 'react-router'
import { cadastrar, resetarCadastro } from '../actions/authActions'
import { cadastroStore } from '../stores/cadastroStore'



// Informações de cadastro : nome, email, senha e data de nascimento.
export default function CadastroPage() {
  const navigate = useNavigate()
  const {enviando, sucesso, erro} = useSyncExternalStore(
    cadastroStore.subscribe,
    cadastroStore.getSnapshot,
  )

  useEffect(() => {
      resetarCadastro()

      return () => {
        resetarCadastro()
      }
  },[])

  useEffect(() => {
  if (sucesso) {
    navigate('/login', {
      replace: true,
      state: {
        mensagem: 'Cadastro realizado! Entre com sua conta.',
      },
    })
  }
}, [sucesso, navigate])
  function handleSubmit(event: FormEvent<HTMLFormElement>) {
    event.preventDefault()
    const dados = new FormData(event.currentTarget)
    void cadastrar({
      nome: dados.get('nome') as string,
      email: dados.get('email') as string,
      senha: dados.get('senha') as string,
      dataNascimento: dados.get('dataNascimento') as string,
    })
  }
  return (
    <main className="flex min-h-screen items-center justify-center bg-zinc-950 p-6 text-white">
        <div className="max-w-xl w-[448px]">
          <h1 className="text-3xl font-bold mb-4">Crie sua conta</h1>
          <form onSubmit={handleSubmit} 
                className="mt-6 flex flex-col gap-4" >
            <div>
              <label htmlFor="name" className="block text-sm font-medium text-gray-300 mb-1">
                Nome
              </label>
              <input
                type="text"
                id="name"
                name="nome"
                required
                className="w-full bg-zinc-800 border border-zinc-600 rounded-md py-2 px-3 focus:outline-none focus:ring-2 focus:ring-blue-500"
                placeholder="Enter your name"
              />
            </div>
            <div>
              <label htmlFor="email" className="block text-sm font-medium text-gray-300 mb-1">
                Email
              </label>
              <input
                type="email"
                id="email"
                name="email"
                required
                className="w-full bg-zinc-800 border border-zinc-600 rounded-md py-2 px-3 focus:outline-none focus:ring-2 focus:ring-blue-500"
                placeholder="Enter your email"
              />
            </div>
            <div>
              <label htmlFor="password" className="block text-sm font-medium text-gray-300 mb-1">
                Password
              </label>
              <input
                type="password"
                id="password"
                name="senha"
                required
                className="w-full bg-zinc-800 border border-zinc-600 rounded-md py-2 px-3 focus:outline-none focus:ring-2 focus:ring-blue-500"
                placeholder="Enter your password"
              />
              <label htmlFor="birthday" className="block text-sm font-medium text-gray-300 mb-1 mt-4">
                Data de Nascimento
              </label>
              <input
                  type="date"
                  id="birthday"
                  name="dataNascimento"
                  required
                  className="w-full bg-zinc-800 border border-zinc-600 rounded-md py-2 px-3 focus:outline-none focus:ring-2 focus:ring-blue-500"
                  placeholder="Enter your birthday"
                />
            </div>
            {erro && <p className="text-red-500 text-sm mt-2">{erro}</p>}
            <button
              type="submit"
              disabled={enviando}
              className="w-full rounded-md bg-red-600 px-4 py-2 font-bold text-white hover:bg-red-700 disabled:cursor-not-allowed disabled:opacity-50">
              {enviando ? 'Cadastrando…' : 'Cadastrar'}
            </button>
          </form>
          <p className="text-sm text-gray-400 mt-4">
            Já tem uma conta?{' '}
            <Link to="/login" className="text-red-500 hover:text-red-600">
              Faça login
            </Link>
          </p>
        </div>
      </main>
  );
}
