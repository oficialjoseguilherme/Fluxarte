import type { Meta, StoryObj } from '@storybook/react-vite';
import ObraRowList from '../components/obraRowList';
import { section } from '../types/example';
const meta = {
  component: ObraRowList, tags: ['autodocs'],
  args: { sections: [section, { ...section, url: 'aventuras', titulo: 'Aventuras' }], overlap: false },
  parameters: { layout: 'fullscreen' },
} satisfies Meta<typeof ObraRowList>;
export default meta;
type Story = StoryObj<typeof meta>;
export const Padrao: Story = {};
export const Vazia: Story = { args: { sections: [] } };
export const ComSobreposicao: Story = {
  args: { overlap: true },
  decorators: [(Story) => <div className="pt-48"><Story /></div>],
};