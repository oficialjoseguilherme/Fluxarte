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
export const banner = `data:image/svg+xml,${encodeURIComponent('<svg xmlns="http://www.w3.org/2000/svg" width="1600" height="900"><rect width="1600" height="900" fill="#1d3b2a"/><circle cx="1200" cy="260" r="180" fill="#e0a64b"/><path d="M0 650L420 380L800 600L1200 340L1600 620V900H0Z" fill="#0f2418"/></svg>')}`;
// Formato igual ao do backend (GET /api/obras/{id}): enums em maiúsculas e datas ISO
export const obraCompleta: Obra = {
  ...obra,
  id: 2, titulo: 'Rios da Amazônia', tituloOriginal: 'Amazon Rivers',
  diretor: 'Ana Souza', anoProducao: 2023, idiomaOriginal: 'Português',
  classificacaoIndicativa: 'DOZE', generos: ['DOCUMENTARIO', 'ANIMACAO'],
  sinopse: 'Acompanhando ribeirinhos ao longo de um ano, o filme revela como as cheias e as secas moldam a vida, a cultura e a memória das comunidades que vivem às margens dos grandes rios da Amazônia.',
  posterUrl: poster, bannerUrl: banner, duracao: null, detalhesUrl: null,
  dataInicioExibicao: '2026-10-01T00:00:00', dataFimExibicao: '2026-12-31T23:59:59',
};
export const section: CatalogSection = {
  url: 'documentarios', titulo: 'Documentários',
  obras: Array.from({ length: 12 }, (_, index) => ({ ...obra, id: index + 1, titulo: `${obra.titulo} ${index + 1}` })),
};
