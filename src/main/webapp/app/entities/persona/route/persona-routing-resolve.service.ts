import { inject } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { ActivatedRouteSnapshot, Router } from '@angular/router';
import { of, EMPTY, Observable } from 'rxjs';
import { mergeMap } from 'rxjs/operators';

import { IPersona } from '../persona.model';
import { PersonaService } from '../service/persona.service';

const personaResolve = (route: ActivatedRouteSnapshot): Observable<null | IPersona> => {
  const id = route.params['id'];
  if (id) {
    return inject(PersonaService)
      .find(id)
      .pipe(
        mergeMap((persona: HttpResponse<IPersona>) => {
          if (persona.body) {
            return of(persona.body);
          } else {
            inject(Router).navigate(['404']);
            return EMPTY;
          }
        }),
      );
  }
  return of(null);
};

export default personaResolve;
