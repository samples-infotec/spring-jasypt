import { TestBed } from '@angular/core/testing';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';

import { DATE_FORMAT } from 'app/config/input.constants';
import { IPersona } from '../persona.model';
import { sampleWithRequiredData, sampleWithNewData, sampleWithPartialData, sampleWithFullData } from '../persona.test-samples';

import { PersonaService, RestPersona } from './persona.service';

const requireRestSample: RestPersona = {
  ...sampleWithRequiredData,
  fechaNacimiento: sampleWithRequiredData.fechaNacimiento?.format(DATE_FORMAT),
};

describe('Persona Service', () => {
  let service: PersonaService;
  let httpMock: HttpTestingController;
  let expectedResult: IPersona | IPersona[] | boolean | null;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
    });
    expectedResult = null;
    service = TestBed.inject(PersonaService);
    httpMock = TestBed.inject(HttpTestingController);
  });

  describe('Service methods', () => {
    it('should find an element', () => {
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.find(123).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'GET' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should create a Persona', () => {
      const persona = { ...sampleWithNewData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.create(persona).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'POST' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should update a Persona', () => {
      const persona = { ...sampleWithRequiredData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.update(persona).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'PUT' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should partial update a Persona', () => {
      const patchObject = { ...sampleWithPartialData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.partialUpdate(patchObject).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'PATCH' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should return a list of Persona', () => {
      const returnedFromService = { ...requireRestSample };

      const expected = { ...sampleWithRequiredData };

      service.query().subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'GET' });
      req.flush([returnedFromService]);
      httpMock.verify();
      expect(expectedResult).toMatchObject([expected]);
    });

    it('should delete a Persona', () => {
      const expected = true;

      service.delete(123).subscribe(resp => (expectedResult = resp.ok));

      const req = httpMock.expectOne({ method: 'DELETE' });
      req.flush({ status: 200 });
      expect(expectedResult).toBe(expected);
    });

    describe('addPersonaToCollectionIfMissing', () => {
      it('should add a Persona to an empty array', () => {
        const persona: IPersona = sampleWithRequiredData;
        expectedResult = service.addPersonaToCollectionIfMissing([], persona);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(persona);
      });

      it('should not add a Persona to an array that contains it', () => {
        const persona: IPersona = sampleWithRequiredData;
        const personaCollection: IPersona[] = [
          {
            ...persona,
          },
          sampleWithPartialData,
        ];
        expectedResult = service.addPersonaToCollectionIfMissing(personaCollection, persona);
        expect(expectedResult).toHaveLength(2);
      });

      it("should add a Persona to an array that doesn't contain it", () => {
        const persona: IPersona = sampleWithRequiredData;
        const personaCollection: IPersona[] = [sampleWithPartialData];
        expectedResult = service.addPersonaToCollectionIfMissing(personaCollection, persona);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(persona);
      });

      it('should add only unique Persona to an array', () => {
        const personaArray: IPersona[] = [sampleWithRequiredData, sampleWithPartialData, sampleWithFullData];
        const personaCollection: IPersona[] = [sampleWithRequiredData];
        expectedResult = service.addPersonaToCollectionIfMissing(personaCollection, ...personaArray);
        expect(expectedResult).toHaveLength(3);
      });

      it('should accept varargs', () => {
        const persona: IPersona = sampleWithRequiredData;
        const persona2: IPersona = sampleWithPartialData;
        expectedResult = service.addPersonaToCollectionIfMissing([], persona, persona2);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(persona);
        expect(expectedResult).toContain(persona2);
      });

      it('should accept null and undefined values', () => {
        const persona: IPersona = sampleWithRequiredData;
        expectedResult = service.addPersonaToCollectionIfMissing([], null, persona, undefined);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(persona);
      });

      it('should return initial array if no Persona is added', () => {
        const personaCollection: IPersona[] = [sampleWithRequiredData];
        expectedResult = service.addPersonaToCollectionIfMissing(personaCollection, undefined, null);
        expect(expectedResult).toEqual(personaCollection);
      });
    });

    describe('comparePersona', () => {
      it('Should return true if both entities are null', () => {
        const entity1 = null;
        const entity2 = null;

        const compareResult = service.comparePersona(entity1, entity2);

        expect(compareResult).toEqual(true);
      });

      it('Should return false if one entity is null', () => {
        const entity1 = { id: 123 };
        const entity2 = null;

        const compareResult1 = service.comparePersona(entity1, entity2);
        const compareResult2 = service.comparePersona(entity2, entity1);

        expect(compareResult1).toEqual(false);
        expect(compareResult2).toEqual(false);
      });

      it('Should return false if primaryKey differs', () => {
        const entity1 = { id: 123 };
        const entity2 = { id: 456 };

        const compareResult1 = service.comparePersona(entity1, entity2);
        const compareResult2 = service.comparePersona(entity2, entity1);

        expect(compareResult1).toEqual(false);
        expect(compareResult2).toEqual(false);
      });

      it('Should return false if primaryKey matches', () => {
        const entity1 = { id: 123 };
        const entity2 = { id: 123 };

        const compareResult1 = service.comparePersona(entity1, entity2);
        const compareResult2 = service.comparePersona(entity2, entity1);

        expect(compareResult1).toEqual(true);
        expect(compareResult2).toEqual(true);
      });
    });
  });

  afterEach(() => {
    httpMock.verify();
  });
});
