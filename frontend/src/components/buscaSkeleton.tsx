export default function BuscaSkeleton() {
  return <div role="status" className="motion-safe:animate-pulse">
    <span className="sr-only">Carregando obras…</span>
    <div aria-hidden="true" className="grid grid-cols-1 gap-x-4 gap-y-6 min-[480px]:grid-cols-2 md:grid-cols-3 xl:grid-cols-4 2xl:grid-cols-5">
      {Array.from({ length: 10 }, (_, index) => <div key={index} className="min-w-0 p-1">
        <div className="aspect-video rounded bg-neutral-800" />
        <div className="mt-2 h-4 w-3/4 rounded bg-neutral-800" />
        <div className="mt-2 h-3 w-1/4 rounded bg-neutral-800" />
      </div>)}
    </div>
  </div>
}
