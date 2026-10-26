import { useState } from 'react'
import type { ComponentProps } from 'react'
import type { Meta, StoryObj } from '@storybook/react-vite'
import { expect, fn, userEvent, within } from 'storybook/test'
import BuscaCampo from '../components/buscaCampo'

function CampoInterativo(args: ComponentProps<typeof BuscaCampo>) {
  const [consulta, setConsulta] = useState(args.consulta)
  return <BuscaCampo {...args} consulta={consulta} onConsulta={texto => {
    args.onConsulta(texto)
    setConsulta(texto)
  }} />
}

const meta = {
  component: BuscaCampo,
  tags: ['autodocs'],
  args: { consulta: '', sugestoes: [], onConsulta: fn() },
  render: args => <CampoInterativo {...args} />,
  decorators: [Story => <div className="min-h-80 bg-[#111] p-6 text-white"><div className="w-[280px]"><Story /></div></div>],
} satisfies Meta<typeof BuscaCampo>

export default meta
type Story = StoryObj<typeof meta>

export const Vazio: Story = {}
export const ComAutocomplete: Story = {
  args: { consulta: 'Cid', sugestoes: [
    { texto: 'Cidade de Deus', tipo: 'Título' },
    { texto: 'Cidade dos Homens', tipo: 'Título' },
  ] },
  play: async ({ canvasElement }) => {
    const canvas = within(canvasElement)
    await userEvent.click(canvas.getByRole('combobox'))
    await expect(canvas.getByRole('listbox')).toBeVisible()
  },
}
export const NomeLongo: Story = {
  args: { consulta: 'Fernando Meirelles', sugestoes: [{ texto: 'Fernando Meirelles', tipo: 'Diretor' }] },
}
