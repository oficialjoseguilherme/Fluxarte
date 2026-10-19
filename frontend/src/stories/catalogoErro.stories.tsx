import type { Meta, StoryObj } from '@storybook/react-vite';
import { fn } from 'storybook/test';
import { CatalogoErro } from '../components/catalogoErro';

const meta = {
  component: CatalogoErro,
  tags: ['autodocs'],
  args: {
    mensagem: 'Nao foi possivel carregar as obras. Tente novamente.',
    onRetry: fn(),
  },
  decorators: [
    (Story) => <div className="min-h-screen bg-neutral-950 text-white"><Story /></div>,
  ],
} satisfies Meta<typeof CatalogoErro>;

export default meta;
type Story = StoryObj<typeof meta>;

export const Padrao: Story = {};

export const SemConexao: Story = {
  args: { mensagem: 'Verifique sua conexao com a internet e tente novamente.' },
};

export const ServidorIndisponivel: Story = {
  args: { mensagem: 'O catalogo esta temporariamente indisponivel. Tente novamente em instantes.' },
};