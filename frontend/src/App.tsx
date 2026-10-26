import { Navigate, Route, Routes } from 'react-router'
import Catalogo from './pages/catalogo'
import CadastroPage from './pages/cadastropage.tsx'
import LoginPage from './pages/loginpage.tsx'
import PlayerPage from './pages/playerpage.tsx'
import CatalogoLayout from './components/catalogoLayout'
import BuscaLayout from './components/buscaLayout'
import BuscaPage from './pages/busca'
import Placeholder from './pages/placeholderpage'
import { NAV_ITEMS } from './utils/navItens'
export default function App() {
  return (
      <Routes>
        <Route path="/register" element={<CadastroPage />} />
        <Route path="/login" element={<LoginPage />} />
        <Route element={<BuscaLayout />}>
          <Route path="/busca" element={<BuscaPage />} />
        </Route>
        <Route element={<CatalogoLayout />}>
          <Route path="/catalogo" element={<Catalogo />} />
          <Route path="/assistir" element={<PlayerPage />} />
          <Route path="/assistir/:id" element={<PlayerPage />} />
          {NAV_ITEMS.map(item => <Route key={item.path} path={item.path} element={<Placeholder title={item.label} />} />)}
          <Route path="/meu-uira/add/:id" element={<Placeholder title="Minha lista" />} />
        </Route>
        <Route path="*" element={<Navigate to="/login" replace />} />
      </Routes>
  )
}
