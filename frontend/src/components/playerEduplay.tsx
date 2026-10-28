import { useState } from 'react'

export default function PlayerEduplay({ src, titulo }: { src: string; titulo: string }) {
  const [carregado, setCarregado] = useState(false)
  return <div className="relative h-full w-full bg-black">
    {!carregado && <div role="status" className="absolute inset-0 flex items-center justify-center text-neutral-400">
      <span className="h-10 w-10 rounded-full border-4 border-white/20 border-t-white motion-safe:animate-spin" aria-hidden="true" />
      <span className="sr-only">Carregando vídeo…</span>
    </div>}
    <iframe src={src} title={`Assistir ${titulo}`} onLoad={() => setCarregado(true)}
      allow="autoplay; fullscreen; picture-in-picture; encrypted-media" allowFullScreen
      className="h-full w-full border-0" />
  </div>
}
