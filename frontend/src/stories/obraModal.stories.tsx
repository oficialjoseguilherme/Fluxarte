import { useState } from 'react'
import type { Meta, StoryObj } from '@storybook/react-vite'
import { expect, fn, userEvent, within } from 'storybook/test'
import Button from '../components/button'
import ObraInfo from '../components/obraInfo'
import ObraModal from '../components/obraModal'
import ObraModalHero from '../components/obraModalHero'
import type { Obra } from '../types/catalog'
import { obraCompleta } from '../types/example'

function Conteudo({ obra }: { obra: Obra }) {
  return <><ObraModalHero obra={obra} tituloId="titulo-obra" /><ObraInfo obra={obra} /></>
}

// Permite abrir e fechar de verdade no Storybook
function ModalInterativo({ obra }: { obra: Obra }) {
  const [aberto, setAberto] = useState(true)
  return <div className="min-h-screen bg-[#111] p-8">
    <Button onClick={() => setAberto(true)}>Abrir detalhes</Button>
    <ObraModal aberto={aberto} onFechar={() => setAberto(false)} tituloId="titulo-obra"><Conteudo obra={obra} /></ObraModal>
  </div>
}

const meta = {
  title: 'Detalhes da obra/ObraModal', component: ObraModal, tags: ['autodocs'],
  // Na página Docs cada story roda num iframe próprio; senão os modais abertos cobririam a documentação
  parameters: { layout: 'fullscreen', docs: { story: { inline: false, iframeHeight: 640 } } },
  args: { aberto: true, onFechar: fn(), tituloId: 'titulo-obra', children: <Conteudo obra={obraCompleta} /> },
} satisfies Meta<typeof ObraModal>
export default meta
type Story = StoryObj<typeof meta>

export const Aberto: Story = {
  play: async ({ args }) => {
    // o <dialog> modal fica na top layer, então buscamos no documento inteiro
    const tela = within(document.body)
    await expect(tela.getByRole('dialog', { name: obraCompleta.titulo })).toBeVisible()
    await expect(tela.getByRole('link', { name: /Assistir/ })).toHaveAttribute('href', '/assistir/2')
    await userEvent.click(tela.getByRole('button', { name: 'Fechar' }))
    await expect(args.onFechar).toHaveBeenCalled()
  },
}
export const Interativo: Story = { render: () => <ModalInterativo obra={obraCompleta} /> }
export const SinopseLonga: Story = { args: { children: <Conteudo obra={{ ...obraCompleta, sinopse: obraCompleta.sinopse!.repeat(6) }} /> } }
export const SemImagens: Story = { args: { children: <Conteudo obra={{ ...obraCompleta, bannerUrl: null, posterUrl: null }} /> } }
export const MinimoDeCampos: Story = {
  args: { children: <Conteudo obra={{ ...obraCompleta, diretor: null, sinopse: null, anoProducao: null, idiomaOriginal: null, tituloOriginal: null, classificacaoIndicativa: null, generos: [], dataInicioExibicao: null, dataFimExibicao: null }} /> },
}
