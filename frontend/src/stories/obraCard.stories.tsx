import type { Meta, StoryObj } from '@storybook/react-vite';
import ObraCard from '../components/obraCard';
import { obra } from '../types/example';
const meta = { component: ObraCard, tags: ['autodocs'], args: { obra } } satisfies Meta<typeof ObraCard>;
export default meta;
type Story = StoryObj<typeof meta>;
export const Padrao: Story = {};
export const SemImagem: Story = { args: { obra: { ...obra, posterUrl: null } } };
export const SemDetalhes: Story = { args: { obra: { ...obra, detalhesUrl: null, duracao: null } } };