import type { CatalogSection } from '../types/catalog'
import ObraRow from './obraRow'

export default function ObraRowList({ sections, overlap }: { sections: CatalogSection[]; overlap: boolean }) {
  return <div className={`relative z-30 space-y-8 pb-12 ${overlap ? '-mt-36' : ''}`}>{sections.map(section => <ObraRow key={section.url} section={section} />)}</div>
}
