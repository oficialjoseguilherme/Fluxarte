import type { Meta, StoryObj } from '@storybook/react-vite'
import { expect, fn, userEvent, within } from 'storybook/test'
import RowArrow from '../components/rowArrow'

const meta = {
  title: 'Reutilizáveis/RowArrow', component: RowArrow, tags: ['autodocs'],
  args: { direction: 'left', label: 'Voltar em Documentário', controls: 'fileira-demo', onClick: fn() },
  decorators: [(Story) => <div className="group relative h-[225px] w-80"><div id="fileira-demo" /><Story /></div>],
} satisfies Meta<typeof RowArrow>
export default meta
type Story = StoryObj<typeof meta>
export const Esquerda: Story = {
  play: async ({ canvasElement, args }) => {
    await userEvent.hover(canvasElement)
    await userEvent.click(within(canvasElement).getByRole('button'))
    await expect(args.onClick).toHaveBeenCalled()
  },
}
export const Direita: Story = { args: { direction: 'right', label: 'Avançar em Documentário' } }
