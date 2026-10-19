import { useId, useState, useSyncExternalStore } from 'react'
import type { CatalogSection } from '../types/catalog'
import ObraCard from './obraCard'
import RowArrow from './rowArrow'
import { circularPosition, OBRA_CARD_WIDTH, rowWindow } from '../utils/obraRowGeometry'

function subscribeResize(listener: () => void) {
  window.addEventListener('resize', listener)
  return () => window.removeEventListener('resize', listener)
}

export default function ObraRow({ section }: { section: CatalogSection }) {
  const id = useId()
  const [scroll, setScroll] = useState({ position: 0, travel: 0 })
  const viewportWidth = useSyncExternalStore(subscribeResize, () => window.innerWidth, () => 0)
  const listWidth = section.obras.length * OBRA_CARD_WIDTH
  const position = circularPosition(scroll.position, listWidth)
  const visibleWindow = rowWindow(position, viewportWidth, section.obras.length)
  const moving = scroll.travel !== 0

  function move(direction: number) {
    if (moving) return
    const travel = visibleWindow.step * direction
    if (window.matchMedia('(prefers-reduced-motion: reduce)').matches) {
      setScroll({ position: circularPosition(position + travel, listWidth), travel: 0 })
    } else {
      setScroll({ position, travel })
    }
  }

  function finishMovement() {
    if (moving) setScroll({ position: circularPosition(position + scroll.travel, listWidth), travel: 0 })
  }
  if (!section.obras.length) return null
  return (
    <section id={section.url} aria-labelledby={`${id}-titulo`} className="group relative mb-[30px] scroll-mt-24">
      <h2 id={`${id}-titulo`} className="mb-0 ml-[30px] text-2xl font-bold">{section.titulo}</h2>
      <RowArrow direction="left" label={`Voltar em ${section.titulo}`} controls={id} disabled={moving} onClick={() => move(-1)} />
      <RowArrow direction="right" label={`Avançar em ${section.titulo}`} controls={id} disabled={moving} onClick={() => move(1)} />
      <div id={id} className="overflow-x-hidden pl-[30px]">
        <div
          className={`flex ${moving ? 'transition-[margin-left] duration-500 ease-[ease]' : ''}`}
          style={{ marginLeft: visibleWindow.offset - scroll.travel, width: visibleWindow.slots.length * OBRA_CARD_WIDTH }}
          onTransitionEnd={event => { if (event.target === event.currentTarget && event.propertyName === 'margin-left') finishMovement() }}
          onTransitionCancel={event => { if (event.target === event.currentTarget && event.propertyName === 'margin-left') finishMovement() }}
        >
          {visibleWindow.slots.map(slot => <ObraCard key={slot.key} obra={section.obras[slot.index]} />)}
        </div>
      </div>
    </section>
  )
}
