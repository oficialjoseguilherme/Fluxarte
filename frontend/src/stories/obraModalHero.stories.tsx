import type { Meta, StoryObj } from '@storybook/react-vite'
import { expect, within } from 'storybook/test'
import ObraModalHero from '../components/obraModalHero'
import { obraCompleta } from '../types/example'

const meta = {
  title: 'Detalhes da obra/ObraModalHero', component: ObraModalHero, tags: ['autodocs'],
  args: { obra: obraCompleta, tituloId: 'titulo-obra' },
  decorators: [(Story) => <div className="max-w-[850px] bg-[#181818] text-white"><Story /></div>],
} satisfies Meta<typeof ObraModalHero>
export default meta
type Story = StoryObj<typeof meta>

export const ComBanner: Story = {
  play: async ({ canvasElement }) => {
    await expect(within(canvasElement).getByRole('link', { name: /Assistir/ })).toHaveAttribute('href', '/assistir/2')
  },
}
export const SoPoster: Story = { args: { obra: { ...obraCompleta, bannerUrl: null } } }
export const SemImagens: Story = { args: { obra: { ...obraCompleta, bannerUrl: null, posterUrl: null } } }
export const TituloLongo: Story = { args: { obra: { ...obraCompleta, titulo: 'Memórias submersas: histórias das comunidades ribeirinhas do Maranhão' } } }
