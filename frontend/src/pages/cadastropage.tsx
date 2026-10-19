import { useEffect, useSyncExternalStore } from 'react'
import type { FormEvent } from 'react'
import { Link, useNavigate } from 'react-router'
import { cadastrar, resetarCadastro } from '../actions/authActions'
import { cadastroStore } from '../stores/cadastroStore'
import Button from '../components/button'
import FormField from '../components/formField'
import AuthLayout from '../components/authLayout'

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
    <AuthLayout width="cadastro">
          <h1 className="text-3xl font-bold mb-4">Crie sua conta</h1>
          <form onSubmit={handleSubmit} 
                className="mt-6 flex flex-col gap-4" >
            <FormField id="name" label="Nome"
                type="text"
                name="nome"
                required
                autoComplete="name"
                placeholder="Seu nome"
               />
            <FormField id="email" label="Email"
                type="email"
                name="email"
                required
                autoComplete="email"
                placeholder="seu@email.com"
               />
            <div>
              <FormField id="password" label="Senha"
                type="password"
                name="senha"
                required
                autoComplete="new-password"
                placeholder="Crie uma senha"
               />
              <FormField id="birthday" label="Data de Nascimento" labelClassName="mt-4"
                  type="date"
                  name="dataNascimento"
                  required
                  placeholder="Enter your birthday"
                 />
            </div>
            {erro && <p role="alert" className="text-red-500 text-sm mt-2">{erro}</p>}
            <Button type="submit" size="form" fullWidth disabled={enviando}>
              {enviando ? 'Cadastrando…' : 'Cadastrar'}
            </Button>
          </form>
          <p className="text-sm text-gray-400 mt-4">
            Já tem uma conta?{' '}
            <Link to="/login" className="text-red-500 hover:text-red-600">
              Faça login
            </Link>
          </p>
        </AuthLayout>
  );
}
