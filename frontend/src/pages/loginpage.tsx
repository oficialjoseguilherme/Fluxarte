import { useEffect, useSyncExternalStore } from 'react'
import type { FormEvent } from 'react'
import { Link, useNavigate } from 'react-router'
import { entrar, resetarLogin } from '../actions/authActions'
import { authStore } from '../stores/authStore'
import Button from '../components/button'
import FormField from '../components/formField'
import AuthLayout from '../components/authLayout'

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
    <AuthLayout width="login">
        <h1 className="text-3xl font-bold mb-4">Informe seus dados para <br /> entrar</h1>
        <p className="text-sm text-gray-400 mt-4">
          Ou crie uma conta.
        </p>
        <form onSubmit={handleSubmit} className="mt-6 flex flex-col gap-4" >
          <FormField wrapperClassName="mb-4" id="email" label="Email"
              type="email"
              name="email"
              autoComplete="email"
              required
              placeholder="seu@email.com"
             />
          <FormField wrapperClassName="mb-4" id="password" label="Senha"
              type="password"
              name="senha"
              autoComplete="current-password"
              required
              placeholder="Sua senha"
             />
          {erro && ( <p role="alert" className="mb-4 text-sm text-red-400"> {erro} </p> )}
          <Button type="submit" size="form" fullWidth disabled={enviando}>
            {enviando ? 'Entrando…' : 'Entrar'}
          </Button>
        </form>
        <p className="text-sm text-gray-400 mt-4">
          Não tem uma conta?{' '}
          <Link to="/register" className="text-red-500 hover:text-red-600">
            Registre-se
          </Link>
        </p>
      </AuthLayout>
  );
}
