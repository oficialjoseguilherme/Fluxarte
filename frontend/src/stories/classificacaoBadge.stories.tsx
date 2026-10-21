import type { Meta, StoryObj } from '@storybook/react-vite'
import { expect, within } from 'storybook/test'
import ClassificacaoBadge from '../components/classificacaoBadge'

const meta = {
  title: 'Detalhes da obra/ClassificacaoBadge', component: ClassificacaoBadge, tags: ['autodocs'],
  args: { classificacao: 'DOZE' },
  argTypes: { classificacao: { control: 'select', options: ['LIVRE', 'DEZ', 'DOZE', 'QUATORZE', 'DEZESSEIS', 'DEZOITO', 'Livre', '16 anos', null] } },
  decorators: [(Story) => <div className="bg-[#181818] p-6"><Story /></div>],
} satisfies Meta<typeof ClassificacaoBadge>
export default meta
type Story = StoryObj<typeof meta>

export const Padrao: Story = {
  play: async ({ canvasElement }) => {
    await expect(within(canvasElement).getByRole('img')).toHaveAccessibleName('Não recomendado para menores de 12 anos')
  },
}
export const Todas: Story = {
  render: () => <div className="flex gap-3">
    {['LIVRE', 'DEZ', 'DOZE', 'QUATORZE', 'DEZESSEIS', 'DEZOITO'].map(valor => <ClassificacaoBadge key={valor} classificacao={valor} />)}
  </div>,
}
export const TextoLivre: Story = { args: { classificacao: 'Livre' } }
export const SemClassificacao: Story = {
  args: { classificacao: null },
  play: async ({ canvasElement }) => { await expect(within(canvasElement).queryByRole('img')).toBeNull() },
}
