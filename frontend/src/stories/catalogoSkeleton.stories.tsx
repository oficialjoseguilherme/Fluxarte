import type { Meta, StoryObj } from '@storybook/react-vite';
import { CatalogoSkeleton } from '../components/catalogoSkeleton';
const meta = { component: CatalogoSkeleton, tags: ['autodocs'], parameters: { layout: 'fullscreen' } } satisfies Meta<typeof CatalogoSkeleton>;
export default meta;
type Story = StoryObj<typeof meta>;
export const Carregando: Story = {};