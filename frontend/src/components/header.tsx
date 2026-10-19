import { NavLink } from 'react-router'
import { useScrolledPast } from '../actions/useScrolledPast'
import IconButton from './iconButton'
import { NAV_ITEMS } from '../utils/navItens'

const NOTIFICATION_COUNT = 8


export default function Header() {
  const scrolled = useScrolledPast()
  return (
    <header className={`fixed inset-x-0 top-0 z-[999] flex h-[68px] items-center justify-between gap-5 px-[30px] text-white transition-[background-color] duration-[400ms] ease-[ease] max-[760px]:px-[15px] ${scrolled ? 'bg-[#141414]' : 'bg-[linear-gradient(to_bottom,rgba(0,0,0,0.75),transparent)]'}`}>
      <div className="flex min-w-0 flex-1 items-center gap-6">
        <NavLink to="/catalogo" aria-label="Fluxarte — início" className="flex shrink-0 text-2xl font-black tracking-tight text-[#e50914] max-[760px]:text-xl">FLUXARTE</NavLink>
        <nav aria-label="Principal" className="min-w-0 overflow-hidden max-[1100px]:hidden">
          <ul className="m-0 flex list-none items-center gap-1 p-0">
            {NAV_ITEMS.map(item => <li key={item.path}><NavLink to={item.path} className={({ isActive }) => `block whitespace-nowrap rounded-full px-[14px] py-[7px] text-sm transition-[color,background-color] duration-200 hover:text-white ${isActive ? 'bg-white/[0.12] font-bold text-white' : 'text-[#e5e5e5]'}`}>{item.label}</NavLink></li>)}
          </ul>
        </nav>
      </div>
      <div className="flex min-w-0 items-center gap-1">
        <IconButton label="Buscar"><svg aria-hidden="true" viewBox="0 0 24 24" className="h-6 w-6 fill-current"><path d="M15.5 14h-.79l-.28-.27A6.47 6.47 0 0 0 16 9.5 6.5 6.5 0 1 0 9.5 16c1.61 0 3.09-.59 4.23-1.57l.27.28v.79l5 4.99L20.49 19zM9.5 14A4.5 4.5 0 1 1 14 9.5 4.5 4.5 0 0 1 9.5 14" /></svg></IconButton>
        <IconButton label={`Notificações (${NOTIFICATION_COUNT} não lidas)`} className="relative"><svg aria-hidden="true" viewBox="0 0 24 24" className="h-6 w-6 fill-current"><path d="M12 22a2 2 0 0 0 2-2h-4a2 2 0 0 0 2 2m6-6v-5c0-3.07-1.63-5.64-4.5-6.32V4a1.5 1.5 0 0 0-3 0v.68C7.64 5.36 6 7.92 6 11v5l-2 2v1h16v-1z" /></svg><span aria-hidden="true" className="absolute right-0 top-0 h-[17px] min-w-[17px] rounded-full bg-[#e50914] px-1 text-center text-[11px] font-bold leading-[17px]">{NOTIFICATION_COUNT}</span></IconButton>
        <IconButton label="Conta" className="gap-[2px] pl-[10px]"><img src="/favicon.svg" alt="" className="h-8 w-8 rounded" /><svg aria-hidden="true" viewBox="0 0 24 24" className="h-6 w-6 fill-current"><path d="m7 10 5 5 5-5z" /></svg></IconButton>
      </div>
    </header>
  )
}
