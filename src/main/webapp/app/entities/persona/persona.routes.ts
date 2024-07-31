import { Routes } from '@angular/router';

import { UserRouteAccessService } from 'app/core/auth/user-route-access.service';
import { ASC } from 'app/config/navigation.constants';
import { PersonaComponent } from './list/persona.component';
import { PersonaDetailComponent } from './detail/persona-detail.component';
import { PersonaUpdateComponent } from './update/persona-update.component';
import PersonaResolve from './route/persona-routing-resolve.service';

const personaRoute: Routes = [
  {
    path: '',
    component: PersonaComponent,
    data: {
      defaultSort: 'id,' + ASC,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/view',
    component: PersonaDetailComponent,
    resolve: {
      persona: PersonaResolve,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: 'new',
    component: PersonaUpdateComponent,
    resolve: {
      persona: PersonaResolve,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/edit',
    component: PersonaUpdateComponent,
    resolve: {
      persona: PersonaResolve,
    },
    canActivate: [UserRouteAccessService],
  },
];

export default personaRoute;
