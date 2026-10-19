export interface Obra {
  id: number
  titulo: string
  tituloOriginal: string | null
  duracao: string | null
  diretor: string | null
  sinopse: string | null
  anoProducao: number | null
  idiomaOriginal: string | null
  posterUrl: string | null
  bannerUrl: string | null
  detalhesUrl: string | null 
  dataInicioExibicao: string | null
  dataFimExibicao: string | null
  classificacaoIndicativa: string | null
  generos: string[]
  exibivel: boolean
}

export interface CatalogSection {
  url: string;
  titulo: string;
  obras: Obra[];
}