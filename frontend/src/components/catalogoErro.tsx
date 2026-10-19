import Button from './button'

export function CatalogoErro({ mensagem, onRetry }: { mensagem: string; onRetry: () => void }) {
  return <div role="alert" className="px-4 pb-20 pt-36 text-center"><h1 className="mb-4 text-2xl font-bold">Não foi possível abrir o catálogo</h1><p className="text-neutral-400">{mensagem}</p><Button type="button" onClick={onRetry} className="mt-6">Tentar novamente</Button></div>
}
