export interface Obra {
  id: number
  titulo: string
  tituloOriginal: string | null
  diretor: string | null
  sinopse: string | null
  anoProducao: number | null
  idiomaOriginal: string | null
  posterUrl: string | null
  bannerUrl: string | null
  dataInicioExibicao: string | null
  dataFimExibicao: string | null
  classificacaoIndicativa: string | null
  generos: string[]
  exibivel: boolean
}