import { Navigate, Route, Routes } from 'react-router'
import Catalogo from './pages/catalogo'
import CadastroPage from './pages/cadastropage.tsx'
import LoginPage from './pages/loginpage.tsx'

export default function App() {
  return (
    <main className="flex min-h-screen items-center justify-center bg-zinc-950 p-6 text-white">
      <Routes>
        <Route path="/register" element={<CadastroPage />} />
        <Route path="/login" element={<LoginPage />} />
        <Route path="/catalogo" element={<Catalogo />} />
        <Route path="*" element={<Navigate to="/login" replace />} />
      </Routes>
    </main>
  )
}