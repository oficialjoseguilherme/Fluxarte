import type { Meta, StoryObj } from '@storybook/react-vite'
import PlayerEduplay from '../components/playerEduplay'
import { resolverPlayerEduplay, urlEmbedEduplay } from '../utils/eduplay'

const meta = {
  title: 'Player/PlayerEduplay', component: PlayerEduplay, tags: ['autodocs'],
  args: { src: urlEmbedEduplay(198712), titulo: 'Vídeo de exemplo' },
  parameters: { layout: 'fullscreen' },
  decorators: [(Story) => <div className="h-[80vh]"><Story /></div>],
} satisfies Meta<typeof PlayerEduplay>
export default meta
type Story = StoryObj<typeof meta>

// Vídeo real do Eduplay (precisa de internet)
export const VideoReal: Story = {}
// Mesmo formato salvo no banco: página do Uirá convertida pelo JSON de correspondência (idFilme 2147 → videoId 198712)
export const ConvertidoDoUira: Story = { args: { src: resolverPlayerEduplay('https://uira.ufma.br/filme?idFilme=2147&cinemateca=true')! } }
