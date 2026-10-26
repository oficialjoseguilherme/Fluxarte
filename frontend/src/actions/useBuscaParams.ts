import { useSearchParams } from 'react-router'

export function useBuscaParams() {
  const [params, setParams] = useSearchParams()

  function alterarConsulta(consulta: string) {
    setParams(anteriores => {
      const proximos = new URLSearchParams(anteriores)
      if (consulta) proximos.set('q', consulta)
      else proximos.delete('q')
      proximos.delete('obra')
      return proximos
    }, { replace: true, state: null })
  }

  return { consulta: params.get('q') ?? '', alterarConsulta }
}
