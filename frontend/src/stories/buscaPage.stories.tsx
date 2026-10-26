import type { Meta, StoryObj } from '@storybook/react-vite'
import { expect, spyOn, userEvent, waitFor, within } from 'storybook/test'
import { useLocation, useNavigate } from 'react-router'
import App from '../App'
import { dispatcher } from '../dispatchers/dispatcher'
import { obraCompleta } from '../types/example'

function PaginaComHistorico() {
  const location = useLocation()
  const navigate = useNavigate()
  return <>
    <App />
    <div className="bg-neutral-900 p-4 text-white">
      <button type="button" onClick={() => navigate(-1)}>Voltar no histórico</button>
      <output aria-label="Endereço">{location.pathname}{location.search}</output>
    </div>
  </>
}

const meta = {
  title: 'Busca/Página integrada',
  component: PaginaComHistorico,
  parameters: { layout: 'fullscreen', routerPath: '/busca', docs: { story: { inline: false } } },
  beforeEach: () => {
    const fetchMock = spyOn(window, 'fetch').mockImplementation(async () => new Response(JSON.stringify([obraCompleta]), {
      status: 200, headers: { 'Content-Type': 'application/json' },
    }))
    dispatcher.dispatch({ type: 'LOGIN_SUCESSO', tokens: { accessToken: 'storybook-token', refreshToken: 'storybook-refresh', tipo: 'Bearer' } })
    return () => {
      fetchMock.mockRestore()
      dispatcher.dispatch({ type: 'LOGIN_FALHOU', mensagem: '' })
    }
  },
} satisfies Meta<typeof PaginaComHistorico>

export default meta
type Story = StoryObj<typeof meta>

export const PesquisaEDetalhes: Story = {
  play: async ({ canvasElement }) => {
    const canvas = within(canvasElement)
    const campo = await canvas.findByRole('combobox')
    await expect(within(canvas.getByRole('banner')).getByRole('combobox')).toBe(campo)
    await expect(canvas.queryByRole('link', { name: 'Buscar' })).not.toBeInTheDocument()
    await userEvent.type(campo, 'amazonia')
    const card = await canvas.findByRole('link', { name: /Rios da Amazônia — mais informações/ })
    await expect(canvasElement.scrollWidth).toBeLessThanOrEqual(window.innerWidth)
    await expect(canvas.getByRole('region', { name: 'Buscar obras' }).getBoundingClientRect().top).toBeGreaterThanOrEqual(canvas.getByRole('banner').getBoundingClientRect().bottom + 24)
    await expect(canvas.getByRole('main').getBoundingClientRect().top).toBeGreaterThanOrEqual(0)
    await expect(canvas.getByLabelText('Endereço')).toHaveTextContent('/busca?q=amazonia')
    await userEvent.keyboard('{Escape}')
    await userEvent.click(card)
    await waitFor(() => expect(canvas.getByRole('dialog')).toBeVisible())
    await expect(canvas.getByLabelText('Endereço')).toHaveTextContent('/busca?q=amazonia&obra=2')
    await userEvent.click(canvas.getByRole('button', { name: 'Fechar' }))
    await expect(canvas.getByLabelText('Endereço')).toHaveTextContent('/busca?q=amazonia')
    await expect(campo).toHaveValue('amazonia')
    await expect(card).toHaveFocus()
    await userEvent.click(card)
    await waitFor(() => expect(canvas.getByRole('dialog')).toBeVisible())
    // Dispara o fechamento nativo, usado também pelo Escape do navegador.
    // O teclado sintético do Storybook não executa essa ação padrão do <dialog>.
    ;(canvas.getByRole('dialog') as HTMLDialogElement).close()
    await waitFor(() => expect(canvas.queryByRole('dialog')).not.toBeInTheDocument())
    await userEvent.click(canvas.getByRole('button', { name: 'Voltar no histórico' }))
    // A entrada inicial foi substituída: não há uma entrada para cada letra digitada.
    await expect(canvas.getByLabelText('Endereço')).toHaveTextContent('/busca?q=amazonia')
  },
}

export const LinkDireto: Story = {
  parameters: { routerPath: '/busca?q=amazonia&obra=2' },
  play: async ({ canvasElement }) => {
    const canvas = within(canvasElement)
    await waitFor(() => expect(canvas.getByRole('dialog')).toBeVisible())
    await userEvent.click(canvas.getByRole('button', { name: 'Fechar' }))
    await expect(canvas.getByRole('combobox')).toHaveValue('amazonia')
    await expect(canvas.getByLabelText('Endereço')).toHaveTextContent('/busca?q=amazonia')
    await expect(canvas.getByLabelText('Endereço')).not.toHaveTextContent('obra=')
  },
}

export const SemAutenticacao: Story = {
  beforeEach: () => { dispatcher.dispatch({ type: 'LOGIN_FALHOU', mensagem: '' }) },
  play: async ({ canvasElement }) => {
    await expect(await within(canvasElement).findByLabelText('Endereço')).toHaveTextContent('/login')
  },
}

export const IconeDoCabecalho: Story = {
  parameters: { routerPath: '/catalogo' },
  play: async ({ canvasElement }) => {
    const canvas = within(canvasElement)
    await expect(canvas.queryByRole('combobox')).not.toBeInTheDocument()
    await userEvent.click(await canvas.findByRole('link', { name: 'Buscar' }))
    await expect(await canvas.findByRole('heading', { name: 'Buscar obras' })).toHaveClass('sr-only')
    await expect(within(canvas.getByRole('banner')).getByRole('combobox')).toBeVisible()
    await expect(canvas.getByLabelText('Endereço')).toHaveTextContent('/busca')
  },
}
