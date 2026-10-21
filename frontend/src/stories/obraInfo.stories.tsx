import type { Meta, StoryObj } from '@storybook/react-vite'
import { expect, within } from 'storybook/test'
import ObraInfo from '../components/obraInfo'
import { obra, obraCompleta } from '../types/example'

const meta = {
  title: 'Detalhes da obra/ObraInfo', component: ObraInfo, tags: ['autodocs'],
  args: { obra: obraCompleta },
  decorators: [(Story) => <div className="max-w-[850px] bg-[#181818] pt-6 text-white"><Story /></div>],
} satisfies Meta<typeof ObraInfo>
export default meta
type Story = StoryObj<typeof meta>

export const Completa: Story = {
  play: async ({ canvasElement }) => {
    const canvas = within(canvasElement)
    await expect(canvas.getByText('Ana Souza')).toBeVisible()
    await expect(canvas.getByText('Documentário, Animação')).toBeVisible()
  },
}
export const DadosDeExemploAntigos: Story = { args: { obra } }
export const SemDiretor: Story = { args: { obra: { ...obraCompleta, diretor: null } } }
export const SemSinopse: Story = { args: { obra: { ...obraCompleta, sinopse: null } } }
export const SemPeriodoDeExibicao: Story = { args: { obra: { ...obraCompleta, dataInicioExibicao: null, dataFimExibicao: null } } }
export const MinimoDeCampos: Story = {
  args: { obra: { ...obraCompleta, diretor: null, sinopse: null, anoProducao: null, idiomaOriginal: null, tituloOriginal: null, classificacaoIndicativa: null, generos: [], dataInicioExibicao: null, dataFimExibicao: null } },
}
