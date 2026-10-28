import Button from './button'

export interface PlayerIndisponivelProps {
  titulo: string
  mensagem?: string
  onVoltar: () => void
}

export default function PlayerIndisponivel({ titulo, mensagem = 'Este filme ainda não está disponível para assistir aqui.', onVoltar }: PlayerIndisponivelProps) {
  return <div role="alert" className="flex h-full flex-col items-center justify-center px-6 text-center">
    <svg aria-hidden="true" viewBox="0 0 24 24" className="mb-6 h-16 w-16 fill-neutral-600"><path d="M21 6.5 17 10.5V7a1 1 0 0 0-1-1H9.82L21 17.18zM3.27 2 2 3.27 4.73 6H4a1 1 0 0 0-1 1v10a1 1 0 0 0 1 1h12c.21 0 .39-.08.54-.18L19.73 21 21 19.73z" /></svg>
    <h1 className="text-3xl font-bold">Filme indisponível</h1>
    <p className="mt-3 max-w-md text-neutral-400"><span className="font-semibold text-neutral-200">{titulo}</span> — {mensagem}</p>
    <Button onClick={onVoltar} variant="light" className="mt-8">Voltar ao catálogo</Button>
  </div>
}
