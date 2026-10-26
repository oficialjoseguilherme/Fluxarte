import type { Meta, StoryObj } from '@storybook/react-vite'
import { expect, fn, userEvent, within } from 'storybook/test'
import { useBuscaParams } from '../actions/useBuscaParams'
import BuscaCampo from '../components/buscaCampo'
import Header from '../components/header'
import BuscaConteudo from '../components/buscaConteudo'
import { obra, obraCompleta } from '../types/example'
import { corrigirBusca, filtrarObras, normalizarBusca, sugerirBusca } from '../utils/buscaObras'

const obras = [
  { ...obra, titulo: 'Cidade de Deus', diretor: 'Fernando Meirelles' },
  { ...obraCompleta, titulo: 'Central do Brasil', diretor: 'Walter Salles' },
  { ...obra, id: 3, titulo: 'Ainda Estou Aqui', diretor: 'Walter Salles' },
  { ...obra, id: 4, titulo: 'Amazônia', diretor: null },
]

function BuscaComUrl(args: React.ComponentProps<typeof BuscaConteudo>) {
  const { consulta, alterarConsulta } = useBuscaParams()
  function atualizarConsulta(texto: string) {
    args.onConsulta(texto)
    alterarConsulta(texto)
  }
  return <>
    <Header campoBusca={<BuscaCampo consulta={consulta} sugestoes={args.carregando || args.erro ? [] : sugerirBusca(args.obras, consulta)} onConsulta={atualizarConsulta} />} />
    <BuscaConteudo {...args} consulta={consulta} onConsulta={atualizarConsulta} />
  </>
}

const meta = {
  component: BuscaConteudo,
  tags: ['autodocs'],
  parameters: { layout: 'fullscreen' },
  args: { obras, consulta: '', carregando: false, erro: null, onConsulta: fn(), onRetry: fn() },
  render: args => <BuscaComUrl {...args} />,
  decorators: [Story => <div className="min-h-screen bg-[#111] px-[30px] pb-16 pt-[92px] text-white max-[760px]:px-[15px] max-[760px]:pt-[144px]"><Story /></div>],
} satisfies Meta<typeof BuscaConteudo>

export default meta
type Story = StoryObj<typeof meta>

export const Inicial: Story = {
  play: async ({ canvasElement }) => {
    const canvas = within(canvasElement)
    await expect(canvas.getByRole('status')).toHaveTextContent('Digite um título ou diretor')
    await expect(canvas.queryByRole('article')).not.toBeInTheDocument()
  },
}

export const AutocompleteComTeclado: Story = {
  play: async ({ canvasElement }) => {
    const canvas = within(canvasElement)
    const campo = canvas.getByRole('combobox')
    await userEvent.type(campo, 'Cid')
    await expect(canvas.getByRole('option', { name: 'Cidade de Deus Título' })).toBeVisible()
    await userEvent.keyboard('{ArrowDown}')
    await expect(campo).toHaveAttribute('aria-activedescendant')
    await userEvent.keyboard('{Escape}')
    await expect(campo).toHaveAttribute('aria-expanded', 'false')
    await userEvent.keyboard('{ArrowUp}{Enter}')
    await expect(campo).toHaveValue('Cidade de Deus')
    await expect(canvas.queryByRole('listbox')).not.toBeInTheDocument()
    await expect(canvas.getByRole('link', { name: /Cidade de Deus/ })).toHaveAttribute('href', '/catalogo?q=Cidade+de+Deus&obra=1')
  },
}

export const BuscaPorDiretor: Story = {
  play: async ({ canvasElement }) => {
    const canvas = within(canvasElement)
    await userEvent.type(canvas.getByRole('combobox'), 'Walter')
    await userEvent.click(canvas.getByRole('option', { name: 'Walter Salles Diretor' }))
    await expect(canvas.getByRole('status')).toHaveTextContent('2 obras encontradas')
    await userEvent.click(canvas.getByRole('button', { name: 'Limpar busca' }))
    await expect(canvas.getByRole('combobox')).toHaveValue('')
    await expect(canvas.queryByRole('article')).not.toBeInTheDocument()
  },
}

export const Correcao: Story = {
  parameters: { routerPath: '/busca?q=Cidde%20de%20Deus' },
  play: async ({ canvasElement }) => {
    const canvas = within(canvasElement)
    await expect(canvas.getByRole('status')).toHaveTextContent('Nenhuma obra encontrada')
    await userEvent.click(canvas.getByRole('button', { name: 'Cidade de Deus' }))
    await expect(canvas.getByRole('combobox')).toHaveValue('Cidade de Deus')
    await expect(canvas.getByRole('status')).toHaveTextContent('1 obra encontrada')
  },
}

export const SemResultados: Story = { parameters: { routerPath: '/busca?q=zzzzzzzz' } }
export const CorrecoesSeparadas: Story = {
  args: { obras: [
    { ...obra, titulo: 'Cidade de Deus' },
    { ...obra, id: 2, titulo: 'Cidade dos Homens' },
    { ...obra, id: 3, titulo: 'Cidade Baixa' },
  ] },
  parameters: { routerPath: '/busca?q=Cidde' },
  play: async ({ canvasElement }) => {
    const correcoes = within(canvasElement).getByRole('navigation', { name: 'Correções de busca' })
    await expect(within(correcoes).getAllByRole('button')).toHaveLength(3)
    await expect(within(correcoes).getAllByText('|')).toHaveLength(2)
    await expect(within(canvasElement).queryByRole('article')).not.toBeInTheDocument()
  },
}
export const CatalogoVazio: Story = { args: { obras: [] }, parameters: { routerPath: '/busca?q=filme' } }
export const Carregando: Story = { args: { carregando: true } }
export const Falha: Story = {
  args: { erro: 'Verifique sua conexão e tente novamente.' },
  play: async ({ canvasElement, args }) => {
    await userEvent.click(within(canvasElement).getByRole('button', { name: 'Tentar novamente' }))
    await expect(args.onRetry).toHaveBeenCalledOnce()
  },
}

export const RegrasDeBusca: Story = {
  play: async () => {
    await expect(normalizarBusca('  AMAZÔNIA   BRASILEIRA  ')).toBe('amazonia brasileira')
    await expect(filtrarObras(obras, 'amazonia').map(item => item.id)).toEqual([4])
    await expect(filtrarObras(obras, '  WALTER   SALLES ').map(item => item.id)).toEqual([2, 3])
    await expect(filtrarObras(obras, '   ')).toEqual([])
    await expect(sugerirBusca(obras, 'w')).toEqual([])
    await expect(sugerirBusca(obras, 'wal')).toEqual([{ texto: 'Walter Salles', tipo: 'Diretor' }])
    await expect(sugerirBusca(obras, 'c').length).toBe(0)
    await expect(corrigirBusca(obras, 'Cidde de Deus')[0].texto).toBe('Cidade de Deus')
    await expect(corrigirBusca(obras, 'Meireles')[0].texto).toBe('Fernando Meirelles')
    await expect(corrigirBusca(obras, 'zzzzzzzz')).toEqual([])
    await expect(corrigirBusca(obras, 'De')).toEqual([])
    await expect(corrigirBusca(obras, 'Cidade')).toEqual([])
    const muitos = Array.from({ length: 8 }, (_, i) => ({ ...obra, id: i, titulo: `Cidade ${i}` }))
    await expect(sugerirBusca(muitos, 'cidade')).toHaveLength(5)
    await expect(corrigirBusca(muitos, 'Cidde')).toHaveLength(3)
  },
}
