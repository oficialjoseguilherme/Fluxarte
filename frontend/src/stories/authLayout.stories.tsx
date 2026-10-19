import type { Meta, StoryObj } from '@storybook/react-vite'
import AuthLayout from '../components/authLayout'
import Button from '../components/button'
import FormField from '../components/formField'

const meta = {
  title: 'Reutilizáveis/AuthLayout', component: AuthLayout, tags: ['autodocs'], parameters: { layout: 'fullscreen' },
  args: { width: 'login', children: <><h1 className="mb-4 text-3xl font-bold">Entrar</h1><form className="flex flex-col gap-4" onSubmit={event => event.preventDefault()}><FormField id="email" name="email" type="email" label="Email" required /><Button type="submit" size="form" fullWidth>Entrar</Button></form></> },
} satisfies Meta<typeof AuthLayout>
export default meta
type Story = StoryObj<typeof meta>
export const Login: Story = {}
export const Cadastro: Story = { args: { width: 'cadastro' } }
