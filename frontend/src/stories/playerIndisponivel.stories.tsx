import type { Meta, StoryObj } from '@storybook/react-vite'
import { expect, fn, userEvent, within } from 'storybook/test'
import PlayerIndisponivel from '../components/playerIndisponivel'

const meta = {
  title: 'Player/PlayerIndisponivel', component: PlayerIndisponivel, tags: ['autodocs'],
  args: { titulo: 'Casa de reabilitação (PE)', onVoltar: fn() },
  parameters: { layout: 'fullscreen' },
  decorators: [(Story) => <div className="h-screen bg-black text-white"><Story /></div>],
} satisfies Meta<typeof PlayerIndisponivel>
export default meta
type Story = StoryObj<typeof meta>

export const SemCorrespondencia: Story = {
  play: async ({ canvasElement, args }) => {
    const canvas = within(canvasElement)
    await expect(canvas.getByRole('heading', { name: 'Filme indisponível' })).toBeVisible()
    await userEvent.click(canvas.getByRole('button', { name: 'Voltar ao catálogo' }))
    await expect(args.onVoltar).toHaveBeenCalled()
  },
}
export const ErroDaApi: Story = { args: { mensagem: 'Não foi possível carregar o vídeo.' } }
