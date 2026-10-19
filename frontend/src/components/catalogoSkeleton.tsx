export function CatalogoSkeleton() {
  return <div role="status" className="space-y-8 px-4 pb-12 pt-28 motion-safe:animate-pulse sm:px-8">
    <span className="sr-only">Carregando catálogo…</span>
    <div aria-hidden="true" className="h-[55vh] rounded bg-neutral-900" />
    <div aria-hidden="true" className="flex gap-3 overflow-hidden">{[0, 1, 2, 3, 4, 5].map(index => <div key={index} className="h-48 w-32 shrink-0 rounded bg-neutral-800" />)}</div>
  </div>
}
