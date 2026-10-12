import { Link } from "react-router";

export default function LoginPage() {
  return (
    <main className="flex min-h-screen items-center justify-center bg-zinc-950 p-6 text-white">
      <div className="max-w-md w-[448px]">
        <h1 className="text-3xl font-bold mb-4">Informe seus dados para <br /> entrar</h1>
        <p className="text-sm text-gray-400 mt-4">
          Ou crie uma conta.
        </p>
        <form>
          <div className="mb-4">
            <label htmlFor="email" className="block text-sm font-medium text-gray-300 mb-1">
              Email
            </label>
            <input
              type="email"
              id="email"
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
              className="w-full bg-zinc-800 border border-zinc-600 rounded-md py-2 px-3 focus:outline-none focus:ring-2 focus:ring-blue-500"
              placeholder="Enter your password"
            />
          </div>
          <button
            type="submit"
            className="w-full bg-red-600 hover:bg-red-700 text-white font-bold py-2 px-4 rounded-md focus:outline-none focus:ring-2 focus:ring-blue-500"
          >
            Continuar
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
