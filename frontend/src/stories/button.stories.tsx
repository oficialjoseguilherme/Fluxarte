import type { Meta, StoryObj } from '@storybook/react-vite'
import { expect, fn, userEvent, within } from 'storybook/test'
import Button from '../components/button'

const meta = {
  title: 'Reutilizáveis/Button', component: Button, tags: ['autodocs'],
  args: { children: 'Tentar novamente', onClick: fn() },
} satisfies Meta<typeof Button>
export default meta
type Story = StoryObj<typeof meta>

export const Acao: Story = {
  play: async ({ canvasElement, args }) => {
    await userEvent.click(within(canvasElement).getByRole('button'))
    await expect(args.onClick).toHaveBeenCalled()
  },
}
export const Externo: Story = {
  args: { href: 'https://uira.ufma.br', target: '_blank', rel: 'noopener noreferrer', variant: 'light', size: 'featured', children: <>↗ Ver no Uirá<span className="sr-only"> (nova aba)</span></> },
  play: async ({ canvasElement }) => {
    const link = within(canvasElement).getByRole('link')
    await expect(link).toHaveAttribute('target', '_blank')
    await expect(link).toHaveAttribute('rel', 'noopener noreferrer')
  },
}
export const Interno: Story = { args: { to: '/list/add/2664', variant: 'secondary', size: 'featured', children: '+ Minha lista' } }
export const Retorno: Story = { args: { to: '/catalogo', variant: 'light', children: 'Voltar ao catálogo' } }
export const Submit: Story = { args: { type: 'submit', size: 'form', fullWidth: true, children: 'Entrar' } }
export const SubmitDesabilitado: Story = {
  args: { type: 'submit', size: 'form', fullWidth: true, disabled: true, children: 'Cadastrando…' },
  play: async ({ canvasElement }) => { await expect(within(canvasElement).getByRole('button')).toBeDisabled() },
}
