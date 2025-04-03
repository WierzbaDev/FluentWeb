import {RenderMode, ServerRoute} from '@angular/ssr';

export const serverRoutes: ServerRoute[] = [
  {
    path: '**',
    renderMode: RenderMode.Prerender
  },

  {
    path: 'reset-password/:code',
    renderMode: RenderMode.Client
  },
  {
    path: 'verify/:uuid',
    renderMode: RenderMode.Client
  }
];
