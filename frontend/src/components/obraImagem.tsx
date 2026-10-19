import { useState } from 'react'

export default function ObraImagem({ src, alt, className, eager = false }: {
  src: string | null
  alt: string
  className: string
  eager?: boolean
}) {
  const [failedSource, setFailedSource] = useState<string | null>(null)
  if (!src || failedSource === src) {
    return <div role={alt ? 'img' : undefined} aria-label={alt || undefined} aria-hidden={!alt || undefined} className={`${className} flex items-center justify-center bg-neutral-800 p-3 text-center text-xs text-neutral-400`}>{alt ? 'Imagem indisponível' : null}</div>
  }
  return <img src={src} alt={alt} loading={eager ? 'eager' : 'lazy'} fetchPriority={eager ? 'high' : 'auto'} onError={() => setFailedSource(src)} className={className} />
}