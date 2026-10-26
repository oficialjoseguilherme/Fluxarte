import { useId, useRef, useState } from 'react'
import type { SugestaoBusca } from '../utils/buscaObras'

interface BuscaCampoProps {
  consulta: string
  sugestoes: SugestaoBusca[]
  onConsulta: (consulta: string) => void
}

export default function BuscaCampo({ consulta, sugestoes, onConsulta }: BuscaCampoProps) {
  const id = useId()
  const inputRef = useRef<HTMLInputElement>(null)
  const [abertas, setAbertas] = useState(false)
  const [indiceAtivo, setIndiceAtivo] = useState(-1)
  const mostrarSugestoes = abertas && sugestoes.length > 0
  const ativo = mostrarSugestoes && indiceAtivo < sugestoes.length ? indiceAtivo : -1

  function selecionar(texto: string) {
    onConsulta(texto)
    inputRef.current?.focus()
    setAbertas(false)
    setIndiceAtivo(-1)
  }

  return <form role="search" aria-label="Buscar obras" className="w-full" onSubmit={event => {
    event.preventDefault()
    if (ativo >= 0) selecionar(sugestoes[ativo].texto)
    else setAbertas(false)
  }}>
    <label htmlFor={`${id}-campo`} className="sr-only">Título ou diretor</label>
    <div className="relative" onBlur={event => {
      if (!event.currentTarget.contains(event.relatedTarget)) {
        setAbertas(false)
        setIndiceAtivo(-1)
      }
    }}>
      <div className="flex h-10 items-center border border-neutral-400 bg-[#141414] focus-within:border-white focus-within:ring-1 focus-within:ring-white">
        <svg aria-hidden="true" viewBox="0 0 24 24" className="ml-2 h-5 w-5 shrink-0 fill-current"><path d="M15.5 14h-.79l-.28-.27A6.47 6.47 0 0 0 16 9.5 6.5 6.5 0 1 0 9.5 16c1.61 0 3.09-.59 4.23-1.57l.27.28v.79l5 4.99L20.49 19zM9.5 14A4.5 4.5 0 1 1 14 9.5 4.5 4.5 0 0 1 9.5 14" /></svg>
        <input ref={inputRef} id={`${id}-campo`} type="text" role="combobox" aria-autocomplete="list"
          aria-expanded={mostrarSugestoes} aria-controls={mostrarSugestoes ? `${id}-sugestoes` : undefined}
          aria-activedescendant={ativo >= 0 ? `${id}-opcao-${ativo}` : undefined}
          autoComplete="off" maxLength={200} value={consulta} placeholder="Título ou diretor"
          className="min-w-0 flex-1 bg-transparent px-2 py-2 text-sm outline-none placeholder:text-neutral-400"
          onChange={event => {
            onConsulta(event.target.value)
            setIndiceAtivo(-1)
            setAbertas(true)
          }}
          onFocus={() => setAbertas(true)}
          onKeyDown={event => {
            if (event.nativeEvent.isComposing) return
            if ((event.key === 'ArrowDown' || event.key === 'ArrowUp') && sugestoes.length) {
              event.preventDefault()
              setAbertas(true)
              setIndiceAtivo(event.key === 'ArrowDown' ? (ativo + 1) % sugestoes.length : (ativo <= 0 ? sugestoes.length : ativo) - 1)
            } else if (event.key === 'Escape') {
              event.preventDefault()
              setAbertas(false)
              setIndiceAtivo(-1)
            }
          }} />
        {consulta && <button type="button" aria-label="Limpar busca" className="flex h-9 w-9 shrink-0 items-center justify-center text-neutral-300 hover:text-white focus-visible:outline-2 focus-visible:-outline-offset-2 focus-visible:outline-white" onClick={() => selecionar('')}>
          <svg aria-hidden="true" viewBox="0 0 24 24" className="h-5 w-5 fill-current"><path d="m18.3 5.7-1.4-1.4-4.9 4.9-4.9-4.9-1.4 1.4 4.9 4.9-4.9 4.9 1.4 1.4 4.9-4.9 4.9 4.9 1.4-1.4-4.9-4.9z" /></svg>
        </button>}
      </div>
      {mostrarSugestoes && <ul id={`${id}-sugestoes`} role="listbox" aria-label="Sugestões de busca" className="absolute inset-x-0 top-full z-20 mt-2 max-h-80 overflow-y-auto border border-neutral-600 bg-[#202020] shadow-xl">
        {sugestoes.map((sugestao, indice) => <li key={sugestao.texto} id={`${id}-opcao-${indice}`} role="option" aria-selected={ativo === indice}
          className={`flex cursor-pointer items-center justify-between gap-3 px-3 py-3 text-sm hover:bg-white/10 ${ativo === indice ? 'bg-white/15' : ''}`}
          onMouseDown={event => event.preventDefault()} onClick={() => selecionar(sugestao.texto)}>
          <span className="min-w-0 break-words">{sugestao.texto}</span><span className="shrink-0 text-xs text-neutral-400">{sugestao.tipo}</span>
        </li>)}
      </ul>}
    </div>
  </form>
}
