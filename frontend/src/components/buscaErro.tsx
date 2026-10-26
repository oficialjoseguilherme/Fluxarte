import Button from './button'

export default function BuscaErro({ mensagem, onRetry }: { mensagem: string; onRetry: () => void }) {
  return <div role="alert" className="py-10">
    <h2 className="text-xl font-semibold">Não foi possível carregar as obras</h2>
    <p className="mt-2 text-neutral-400">{mensagem}</p>
    <Button type="button" onClick={onRetry} className="mt-4">Tentar novamente</Button>
  </div>
}
