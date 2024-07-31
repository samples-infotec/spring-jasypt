import { inject, Injectable } from '@angular/core';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { map, Observable } from 'rxjs';

import dayjs from 'dayjs/esm';

import { isPresent } from 'app/core/util/operators';
import { DATE_FORMAT } from 'app/config/input.constants';
import { ApplicationConfigService } from 'app/core/config/application-config.service';
import { createRequestOption } from 'app/core/request/request-util';
import { IPersona, NewPersona } from '../persona.model';

export type PartialUpdatePersona = Partial<IPersona> & Pick<IPersona, 'id'>;

type RestOf<T extends IPersona | NewPersona> = Omit<T, 'fechaNacimiento'> & {
  fechaNacimiento?: string | null;
};

export type RestPersona = RestOf<IPersona>;

export type NewRestPersona = RestOf<NewPersona>;

export type PartialUpdateRestPersona = RestOf<PartialUpdatePersona>;

export type EntityResponseType = HttpResponse<IPersona>;
export type EntityArrayResponseType = HttpResponse<IPersona[]>;

@Injectable({ providedIn: 'root' })
export class PersonaService {
  protected http = inject(HttpClient);
  protected applicationConfigService = inject(ApplicationConfigService);

  protected resourceUrl = this.applicationConfigService.getEndpointFor('api/personas');

  create(persona: NewPersona): Observable<EntityResponseType> {
    const copy = this.convertDateFromClient(persona);
    return this.http
      .post<RestPersona>(this.resourceUrl, copy, { observe: 'response' })
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  update(persona: IPersona): Observable<EntityResponseType> {
    const copy = this.convertDateFromClient(persona);
    return this.http
      .put<RestPersona>(`${this.resourceUrl}/${this.getPersonaIdentifier(persona)}`, copy, { observe: 'response' })
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  partialUpdate(persona: PartialUpdatePersona): Observable<EntityResponseType> {
    const copy = this.convertDateFromClient(persona);
    return this.http
      .patch<RestPersona>(`${this.resourceUrl}/${this.getPersonaIdentifier(persona)}`, copy, { observe: 'response' })
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  find(id: number): Observable<EntityResponseType> {
    return this.http
      .get<RestPersona>(`${this.resourceUrl}/${id}`, { observe: 'response' })
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  query(req?: any): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http
      .get<RestPersona[]>(this.resourceUrl, { params: options, observe: 'response' })
      .pipe(map(res => this.convertResponseArrayFromServer(res)));
  }

  delete(id: number): Observable<HttpResponse<{}>> {
    return this.http.delete(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  getPersonaIdentifier(persona: Pick<IPersona, 'id'>): number {
    return persona.id;
  }

  comparePersona(o1: Pick<IPersona, 'id'> | null, o2: Pick<IPersona, 'id'> | null): boolean {
    return o1 && o2 ? this.getPersonaIdentifier(o1) === this.getPersonaIdentifier(o2) : o1 === o2;
  }

  addPersonaToCollectionIfMissing<Type extends Pick<IPersona, 'id'>>(
    personaCollection: Type[],
    ...personasToCheck: (Type | null | undefined)[]
  ): Type[] {
    const personas: Type[] = personasToCheck.filter(isPresent);
    if (personas.length > 0) {
      const personaCollectionIdentifiers = personaCollection.map(personaItem => this.getPersonaIdentifier(personaItem));
      const personasToAdd = personas.filter(personaItem => {
        const personaIdentifier = this.getPersonaIdentifier(personaItem);
        if (personaCollectionIdentifiers.includes(personaIdentifier)) {
          return false;
        }
        personaCollectionIdentifiers.push(personaIdentifier);
        return true;
      });
      return [...personasToAdd, ...personaCollection];
    }
    return personaCollection;
  }

  protected convertDateFromClient<T extends IPersona | NewPersona | PartialUpdatePersona>(persona: T): RestOf<T> {
    return {
      ...persona,
      fechaNacimiento: persona.fechaNacimiento?.format(DATE_FORMAT) ?? null,
    };
  }

  protected convertDateFromServer(restPersona: RestPersona): IPersona {
    return {
      ...restPersona,
      fechaNacimiento: restPersona.fechaNacimiento ? dayjs(restPersona.fechaNacimiento) : undefined,
    };
  }

  protected convertResponseFromServer(res: HttpResponse<RestPersona>): HttpResponse<IPersona> {
    return res.clone({
      body: res.body ? this.convertDateFromServer(res.body) : null,
    });
  }

  protected convertResponseArrayFromServer(res: HttpResponse<RestPersona[]>): HttpResponse<IPersona[]> {
    return res.clone({
      body: res.body ? res.body.map(item => this.convertDateFromServer(item)) : null,
    });
  }
}
