import type { Meta, StoryObj } from '@storybook/react-vite'
import BuscaSkeleton from '../components/buscaSkeleton'

const meta = {
  component: BuscaSkeleton,
  tags: ['autodocs'],
  parameters: { layout: 'fullscreen' },
  decorators: [Story => <div className="min-h-screen bg-[#111] p-[30px] text-white"><Story /></div>],
} satisfies Meta<typeof BuscaSkeleton>

export default meta
type Story = StoryObj<typeof meta>
export const Padrao: Story = {}
