import { useEffect, useRef } from 'react'
import type { ReactNode } from 'react'
import IconButton from './iconButton'

export interface ObraModalProps {
  aberto: boolean
  onFechar: () => void
  tituloId: string
  children: ReactNode
}

// O <dialog> nativo com showModal() já prende o foco, fecha com Esc e devolve o foco ao elemento de origem
export default function ObraModal({ aberto, onFechar, tituloId, children }: ObraModalProps) {
  const ref = useRef<HTMLDialogElement>(null)

  useEffect(() => {
    const dialog = ref.current
    if (!dialog) return
    if (aberto && !dialog.open) dialog.showModal()
    if (!aberto && dialog.open) dialog.close()
  }, [aberto])

  useEffect(() => {
    if (!aberto) return
    const overflow = document.body.style.overflow
    document.body.style.overflow = 'hidden'
    return () => { document.body.style.overflow = overflow }
  }, [aberto])

  return <dialog ref={ref} aria-labelledby={tituloId}
    onClose={() => { if (aberto) onFechar() }}
    onClick={event => { if (event.target === event.currentTarget) onFechar() }}
    className="mx-auto my-8 max-h-[calc(100dvh-4rem)] w-full max-w-[850px] overflow-y-auto rounded-lg bg-[#181818] text-white shadow-2xl backdrop:bg-black/70 motion-safe:animate-[obra-modal-entrada_200ms_ease-out] max-sm:m-0 max-sm:h-dvh max-sm:max-h-none max-sm:max-w-none max-sm:rounded-none">
    <div className="relative">
      <IconButton label="Fechar" onClick={onFechar} className="absolute right-4 top-4 z-10 h-9 w-9 justify-center rounded-full bg-[#181818]/90 text-white">
        <svg aria-hidden="true" viewBox="0 0 24 24" className="h-6 w-6 fill-current"><path d="M19 6.41 17.59 5 12 10.59 6.41 5 5 6.41 10.59 12 5 17.59 6.41 19 12 13.41 17.59 19 19 17.59 13.41 12z" /></svg>
      </IconButton>
      {children}
    </div>
  </dialog>
}
