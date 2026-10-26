import type { Meta, StoryObj } from '@storybook/react-vite'
import { expect, fn, userEvent, within } from 'storybook/test'
import BuscaErro from '../components/buscaErro'

const meta = {
  component: BuscaErro,
  tags: ['autodocs'],
  args: { mensagem: 'Verifique sua conexão e tente novamente.', onRetry: fn() },
  decorators: [Story => <div className="min-h-screen bg-[#111] px-[30px] text-white"><Story /></div>],
} satisfies Meta<typeof BuscaErro>

export default meta
type Story = StoryObj<typeof meta>
export const Padrao: Story = {
  play: async ({ canvasElement, args }) => {
    await userEvent.click(within(canvasElement).getByRole('button', { name: 'Tentar novamente' }))
    await expect(args.onRetry).toHaveBeenCalledOnce()
  },
}
