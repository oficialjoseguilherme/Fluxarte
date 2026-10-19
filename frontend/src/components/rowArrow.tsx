import type { MouseEventHandler } from 'react'

export interface RowArrowProps {
  direction: 'left' | 'right'
  label: string
  controls: string
  onClick: MouseEventHandler<HTMLButtonElement>
  disabled?: boolean
}

export default function RowArrow({ direction, label, controls, onClick, disabled = false }: RowArrowProps) {
  return <button type="button" aria-label={label} aria-controls={controls} onClick={onClick} disabled={disabled}
    className={`absolute z-[99] flex h-[225px] w-10 items-center justify-center overflow-hidden border-0 bg-black/60 p-0 text-white opacity-0 transition-opacity duration-500 group-hover:opacity-100 focus-visible:opacity-100 max-[760px]:opacity-100 ${direction === 'left' ? 'left-0' : 'right-0'}`}>
    <svg aria-hidden="true" viewBox="0 0 24 24" className="h-[50px] w-[50px] shrink-0 fill-current">
      <path d={direction === 'left' ? 'M15.41 7.41 14 6l-6 6 6 6 1.41-1.41L10.83 12z' : 'M8.59 16.59 10 18l6-6-6-6-1.41 1.41L13.17 12z'} />
    </svg>
  </button>
}
