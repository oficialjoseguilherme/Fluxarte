import { useEffect, useSyncExternalStore } from 'react'
import type { FormEvent } from 'react'
import { Link, useNavigate } from 'react-router'
import { entrar, resetarLogin } from '../actions/authActions'
import { authStore } from '../stores/authStore'



export default function LoginPage() {
  const navigate = useNavigate()
    const {enviando, erro, tokens} = useSyncExternalStore(
      authStore.subscribe,
      authStore.getSnapshot,
    )

    useEffect(() => {
        resetarLogin()

        return () => {
          resetarLogin()
        }
    },[])

    useEffect(() => {
    if (tokens) {
      navigate('/catalogo', {
        replace: true,
        state: {
          mensagem: 'Login realizado! Bem-vindo ao nosso catálogo.',
        },
      })
    }
  }, [tokens, navigate])

    function handleSubmit(event: FormEvent<HTMLFormElement>) {
      event.preventDefault()
      const dados = new FormData(event.currentTarget)
      void entrar({
        email: dados.get('email') as string,
        senha: dados.get('senha') as string,
      })
    }


  return (
    <main className="flex min-h-screen items-center justify-center bg-zinc-950 p-6 text-white">
      <div className="max-w-md w-[448px]">
        <h1 className="text-3xl font-bold mb-4">Informe seus dados para <br /> entrar</h1>
        <p className="text-sm text-gray-400 mt-4">
          Ou crie uma conta.
        </p>
        <form onSubmit={handleSubmit} className="mt-6 flex flex-col gap-4" >
          <div className="mb-4">
            <label htmlFor="email" className="block text-sm font-medium text-gray-300 mb-1">
              Email
            </label>
            <input
              type="email"
              id="email"
              name="email"
              autoComplete="example@example.com"
              required
              className="w-full bg-zinc-800 border border-zinc-600 rounded-md py-2 px-3 focus:outline-none focus:ring-2 focus:ring-blue-500"
              placeholder="Enter your email"
            />
          </div>
          <div className="mb-4">
            <label htmlFor="password" className="block text-sm font-medium text-gray-300 mb-1">
              Password
            </label>
            <input
              type="password"
              id="password"
              name="senha"
              autoComplete="current-password"
              required
              className="w-full bg-zinc-800 border border-zinc-600 rounded-md py-2 px-3 focus:outline-none focus:ring-2 focus:ring-blue-500"
              placeholder="Enter your password"
            />
          </div>
          {erro && ( <p role="alert" className="mb-4 text-sm text-red-400"> {erro} </p> )}
          <button
            type="submit"
            disabled={enviando}
            className="w-full rounded-md bg-red-600 px-4 py-2 font-bold text-white hover:bg-red-700 disabled:cursor-not-allowed disabled:opacity-50">
            {enviando ? 'Entrando…' : 'Entrar'}
          </button>
        </form>
        <p className="text-sm text-gray-400 mt-4">
          Não tem uma conta?{' '}
          <Link to="/register" className="text-red-500 hover:text-red-600">
            Registre-se
          </Link>
        </p>
      </div>
    </main>
  );
}
