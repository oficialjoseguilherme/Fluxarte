import type { Meta, StoryObj } from '@storybook/react-vite';
import Header from '../components/header';
const meta = {
  component: Header, tags: ['autodocs'], parameters: { layout: 'fullscreen' },
  decorators: [(Story) => <div className="min-h-screen bg-neutral-900 pt-24"><Story /><p className="px-8">Prévia do cabeçalho do catálogo</p></div>],
} satisfies Meta<typeof Header>;
export default meta;
type Story = StoryObj<typeof meta>;
export const Padrao: Story = {};
export const BombandoAtivo: Story = { parameters: { routerPath: '/bombando' } };
export const PaginaLonga: Story = { decorators: [(Story) => <div className="min-h-[250vh]"><Story /><p className="px-8">Role a página para visualizar o fundo do cabeçalho.</p></div>] };