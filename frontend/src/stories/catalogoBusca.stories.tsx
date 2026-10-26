import type { Meta, StoryObj } from '@storybook/react-vite'
import { spyOn } from 'storybook/test'
import { Link, Navigate, Route, Routes, useLocation, useNavigate } from 'react-router'
import CatalogoLayout from '../components/catalogoLayout'
import BuscaLayout from '../components/buscaLayout'
import CatalogoPage from '../pages/catalogo'
import BuscaPage from '../pages/busca'
import { authStore } from '../stores/authStore'
import { catalogoStore } from '../stores/catalogoStore'
import { dispatcher } from '../dispatchers/dispatcher'
import { obraCompleta } from '../types/example'
import type { Obra } from '../types/catalog'

// Dados exclusivos desta demonstração. As imagens são SVGs locais em data URLs.
const exemplos = [
  { titulo: 'Cidade de Deus', diretor: 'Fernando Meirelles', generos: ['DRAMA'] },
  { titulo: 'Cidade dos Homens', diretor: 'Paulo Morelli', generos: ['DRAMA'] },
  { titulo: 'Cidade Baixa', diretor: 'Sérgio Machado', generos: ['DRAMA'] },
  { titulo: 'Central do Brasil', diretor: 'Walter Salles', generos: ['DRAMA'] },
  { titulo: 'Ainda Estou Aqui', diretor: 'Walter Salles', generos: ['DRAMA'] },
  { titulo: 'Rios da Amazônia', diretor: 'Ana Souza', generos: ['DOCUMENTARIO'] },
  { titulo: 'Caminhos do Brasil', diretor: 'Ana Souza', generos: ['DOCUMENTARIO'] },
  { titulo: 'Memórias do Maranhão', diretor: 'Maria Santos', generos: ['DOCUMENTARIO'] },
  { titulo: 'Ventos do Nordeste', diretor: 'Maria Santos', generos: ['AVENTURA'] },
  { titulo: 'Entre Rios e Montanhas', diretor: 'Pedro Lima', generos: ['AVENTURA'] },
  { titulo: 'A Última Viagem', diretor: 'Pedro Lima', generos: ['DRAMA', 'AVENTURA'] },
  { titulo: 'Histórias da Nossa Terra', diretor: null, generos: ['DOCUMENTARIO'] },
]

const obras: Obra[] = exemplos.map((exemplo, indice) => ({
  ...obraCompleta,
  ...exemplo,
  id: indice + 1,
  tituloOriginal: null,
  anoProducao: 2020 + indice % 6,
  duracao: `${80 + indice * 3} min`,
  sinopse: `Dados de demonstração para explorar os detalhes de “${exemplo.titulo}”, a navegação do catálogo e os resultados da busca.`,
  dataInicioExibicao: null,
  dataFimExibicao: null,
}))

function CatalogoBuscaDemo() {
  const location = useLocation()
  const navigate = useNavigate()
  const controle = 'rounded border border-neutral-600 px-3 py-1 hover:border-white focus-visible:outline-2 focus-visible:outline-white'

  return <div className="min-h-screen bg-[#111] pb-24 text-white">
    <Routes>
      <Route element={<CatalogoLayout />}>
        <Route path="/catalogo" element={<CatalogoPage />} />
      </Route>
      <Route element={<BuscaLayout />}>
        <Route path="/busca" element={<BuscaPage />} />
      </Route>
      <Route path="/" element={<Navigate to="/catalogo" replace />} />
      <Route path="*" element={<div className="px-[30px] py-24">
        <h1 className="mb-4 text-2xl font-bold">Esta demonstração inclui catálogo e busca.</h1>
        <Link to="/catalogo" className={controle}>Voltar ao catálogo</Link>
      </div>} />
    </Routes>
    <aside aria-label="Controles da demonstração" className="fixed inset-x-0 bottom-0 z-[1000] flex flex-wrap items-center gap-3 border-t border-neutral-700 bg-neutral-950 px-4 py-3 text-xs">
      <span className="text-neutral-400">Demonstração com dados locais</span>
      <button type="button" className={controle} onClick={() => navigate(-1)}>← Voltar</button>
      <button type="button" className={controle} onClick={() => navigate(1)}>Avançar →</button>
      <output aria-label="Rota atual" className="min-w-0 break-all text-neutral-300">{location.pathname}{location.search}</output>
    </aside>
  </div>
}

const meta = {
  title: 'Demonstrações/Catálogo e busca',
  component: CatalogoBuscaDemo,
  parameters: {
    layout: 'fullscreen',
    routerPath: '/catalogo',
    docs: {
      story: { inline: false, iframeHeight: 900 },
      description: {
        component: 'Navegação manual sem backend ou login. Clique na lupa para abrir a busca e no logo FLUXARTE para voltar ao catálogo. Pesquise “Cidade” para autocomplete, “Walter Salles” por diretor ou “Cidde” para correções. Os cards abrem os detalhes reais. Use os controles inferiores para voltar e avançar no histórico. Reprodução e lista pessoal ficam fora desta demonstração.',
      },
    },
  },
  beforeEach: () => {
    const authAnterior = authStore.getSnapshot()
    const catalogoAnterior = catalogoStore.getSnapshot()
    const fetchOriginal = window.fetch.bind(window)
    const fetchMock = spyOn(window, 'fetch').mockImplementation(async (input, init) => {
      const endereco = input instanceof Request ? input.url : String(input)
      const metodo = init?.method ?? (input instanceof Request ? input.method : 'GET')
      if (new URL(endereco, window.location.href).pathname === '/api/obras' && metodo.toUpperCase() === 'GET') {
        return new Response(JSON.stringify(obras), { status: 200, headers: { 'Content-Type': 'application/json' } })
      }
      return fetchOriginal(input, init)
    })
    dispatcher.dispatch({ type: 'LOGIN_SUCESSO', tokens: { accessToken: 'demo-local', refreshToken: 'demo-local', tipo: 'Bearer' } })
    dispatcher.dispatch({ type: 'CATALOGO_SUCESSO', obras, destaque: obras[0] })

    return () => {
      fetchMock.mockRestore()
      dispatcher.dispatch(authAnterior.tokens
        ? { type: 'LOGIN_SUCESSO', tokens: authAnterior.tokens }
        : { type: 'LOGIN_FALHOU', mensagem: authAnterior.erro ?? '' })
      if (catalogoAnterior.carregando) dispatcher.dispatch({ type: 'CATALOGO_INICIADO' })
      else if (catalogoAnterior.erro) dispatcher.dispatch({ type: 'CATALOGO_FALHOU', mensagem: catalogoAnterior.erro })
      else dispatcher.dispatch({ type: 'CATALOGO_SUCESSO', obras: catalogoAnterior.obras, destaque: catalogoAnterior.destaque })
    }
  },
} satisfies Meta<typeof CatalogoBuscaDemo>

export default meta
type Story = StoryObj<typeof meta>

export const NavegacaoLivre: Story = {}
export const ComecarPelaBusca: Story = { parameters: { routerPath: '/busca' } }
