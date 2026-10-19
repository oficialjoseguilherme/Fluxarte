import type { Meta, StoryObj } from '@storybook/react-vite';
import Footer from '../components/rodape';
const meta = { component: Footer, tags: ['autodocs'], parameters: { layout: 'fullscreen' } } satisfies Meta<typeof Footer>;
export default meta;
type Story = StoryObj<typeof meta>;
export const Padrao: Story = {};