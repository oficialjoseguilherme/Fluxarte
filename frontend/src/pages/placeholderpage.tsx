import Button  from '../components/button'

export default function Placeholder({ title }: { title: string }) {
  return <section className="min-h-[65vh] px-[30px] pb-16 pt-32">
    <h1 className="text-3xl font-bold">{title}</h1>
    <p className="mt-4 text-neutral-400">Esta seção ainda não foi implementada.</p>
    <Button to="/catalogo" variant="light" className="mt-6">Voltar ao catálogo</Button>
  </section>
}