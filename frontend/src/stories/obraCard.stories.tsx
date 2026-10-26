import type { Meta, StoryObj } from '@storybook/react-vite';
import ObraCard from '../components/obraCard';
import { obra } from '../types/example';
import { expect, within } from 'storybook/test';
const meta = { component: ObraCard, tags: ['autodocs'], args: { obra } } satisfies Meta<typeof ObraCard>;
export default meta;
type Story = StoryObj<typeof meta>;
export const Padrao: Story = {};
export const SemImagem: Story = { args: { obra: { ...obra, posterUrl: null } } };
export const SemDetalhes: Story = { args: { obra: { ...obra, detalhesUrl: null, duracao: null } } };
export const LinkDoCatalogo: Story = {
  parameters: { routerPath: '/catalogo?q=ignorar' },
  play: async ({ canvasElement }) => {
    await expect(within(canvasElement).getByRole('link')).toHaveAttribute('href', `/catalogo?obra=${obra.id}`);
  },
};
