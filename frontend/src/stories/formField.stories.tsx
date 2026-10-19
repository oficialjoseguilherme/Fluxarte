import type { Meta, StoryObj } from '@storybook/react-vite'
import { expect, userEvent, within } from 'storybook/test'
import FormField from '../components/formField'

const meta = {
  title: 'Reutilizáveis/FormField', component: FormField, tags: ['autodocs'],
  args: { id: 'nome', name: 'nome', label: 'Nome', type: 'text', required: true, autoComplete: 'name', placeholder: 'Seu nome' },
  decorators: [(Story) => <div className="w-80"><Story /></div>],
} satisfies Meta<typeof FormField>
export default meta
type Story = StoryObj<typeof meta>
export const Texto: Story = {
  play: async ({ canvasElement }) => {
    const input = within(canvasElement).getByLabelText('Nome')
    await userEvent.type(input, 'Maria')
    await expect(input).toHaveValue('Maria')
  },
}
export const Email: Story = { args: { id: 'email', name: 'email', label: 'Email', type: 'email', autoComplete: 'email', placeholder: 'seu@email.com' } }
export const Senha: Story = { args: { id: 'senha', name: 'senha', label: 'Senha', type: 'password', autoComplete: 'current-password', placeholder: 'Sua senha' } }
export const Data: Story = { args: { id: 'nascimento', name: 'dataNascimento', label: 'Data de Nascimento', type: 'date', autoComplete: 'bday', placeholder: undefined } }
