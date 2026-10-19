import type { Meta, StoryObj } from '@storybook/react-vite'
import { fn } from 'storybook/test'
import IconButton from '../components/iconButton'

const meta = {
  title: 'Reutilizáveis/IconButton', component: IconButton, tags: ['autodocs'],
  args: { label: 'Buscar', onClick: fn(), children: <svg aria-hidden="true" viewBox="0 0 24 24" className="h-6 w-6" fill="none" stroke="currentColor" strokeWidth="2"><circle cx="10" cy="10" r="6" /><path d="m15 15 6 6" /></svg> },
} satisfies Meta<typeof IconButton>
export default meta
type Story = StoryObj<typeof meta>
export const Busca: Story = {}
export const ComBadge: Story = { args: {
  label: 'Notificações (8 não lidas)', className: 'relative',
  children: <><svg aria-hidden="true" viewBox="0 0 24 24" className="h-6 w-6 fill-current"><path d="M12 22a2 2 0 0 0 2-2h-4a2 2 0 0 0 2 2m6-6v-5c0-3.07-1.63-5.64-4.5-6.32V4a1.5 1.5 0 0 0-3 0v.68C7.64 5.36 6 7.92 6 11v5l-2 2v1h16v-1z" /></svg><span aria-hidden="true" className="absolute right-0 top-0 h-[17px] min-w-[17px] rounded-full bg-[#e50914] px-1 text-center text-[11px] font-bold leading-[17px]">8</span></>,
} }
export const Perfil: Story = { args: {
  label: 'Conta', className: 'gap-[2px] pl-[10px]',
  children: <><img src="/favicon.svg" alt="" className="h-8 w-8 rounded" /><svg aria-hidden="true" viewBox="0 0 24 24" className="h-6 w-6 fill-current"><path d="m7 10 5 5 5-5z" /></svg></>,
} }
