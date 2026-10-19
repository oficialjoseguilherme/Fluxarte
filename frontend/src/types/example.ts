import type { Obra, CatalogSection } from '../types/catalog';

export const poster = `data:image/svg+xml,${encodeURIComponent('<svg xmlns="http://www.w3.org/2000/svg" width="600" height="900"><rect width="600" height="900" fill="#173c47"/><circle cx="300" cy="300" r="160" fill="#c99c58"/><path d="M0 700L300 420L600 700V900H0Z" fill="#10252c"/><text x="300" y="800" text-anchor="middle" fill="white" font-size="42">FLUXARTE</text></svg>')}`;
export const obra: Obra = {
  id: 1, titulo: 'Caminhos do Brasil', generos: ['Documentário', 'Cultura'],
  duracao: '85 min', classificacaoIndicativa: 'Livre',
  sinopse: 'Uma jornada pelas paisagens, memórias e histórias do Brasil.',
  posterUrl: poster, detalhesUrl: 'https://example.com/obras/caminhos-do-brasil',
  tituloOriginal: null,
  diretor: null,
  anoProducao: null,
  idiomaOriginal: null,
  bannerUrl: null,
  dataInicioExibicao: null,
  dataFimExibicao: null,
  exibivel: true,
};
export const section: CatalogSection = {
  url: 'documentarios', titulo: 'Documentários',
  obras: Array.from({ length: 12 }, (_, index) => ({ ...obra, id: index + 1, titulo: `${obra.titulo} ${index + 1}` })),
};
