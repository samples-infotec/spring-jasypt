import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpResponse } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';
import { of, Subject, from } from 'rxjs';

import { PersonaService } from '../service/persona.service';
import { IPersona } from '../persona.model';
import { PersonaFormService } from './persona-form.service';

import { PersonaUpdateComponent } from './persona-update.component';

describe('Persona Management Update Component', () => {
  let comp: PersonaUpdateComponent;
  let fixture: ComponentFixture<PersonaUpdateComponent>;
  let activatedRoute: ActivatedRoute;
  let personaFormService: PersonaFormService;
  let personaService: PersonaService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule, RouterTestingModule.withRoutes([]), PersonaUpdateComponent],
      providers: [
        FormBuilder,
        {
          provide: ActivatedRoute,
          useValue: {
            params: from([{}]),
          },
        },
      ],
    })
      .overrideTemplate(PersonaUpdateComponent, '')
      .compileComponents();

    fixture = TestBed.createComponent(PersonaUpdateComponent);
    activatedRoute = TestBed.inject(ActivatedRoute);
    personaFormService = TestBed.inject(PersonaFormService);
    personaService = TestBed.inject(PersonaService);

    comp = fixture.componentInstance;
  });

  describe('ngOnInit', () => {
    it('Should update editForm', () => {
      const persona: IPersona = { id: 456 };

      activatedRoute.data = of({ persona });
      comp.ngOnInit();

      expect(comp.persona).toEqual(persona);
    });
  });

  describe('save', () => {
    it('Should call update service on save for existing entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IPersona>>();
      const persona = { id: 123 };
      jest.spyOn(personaFormService, 'getPersona').mockReturnValue(persona);
      jest.spyOn(personaService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ persona });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: persona }));
      saveSubject.complete();

      // THEN
      expect(personaFormService.getPersona).toHaveBeenCalled();
      expect(comp.previousState).toHaveBeenCalled();
      expect(personaService.update).toHaveBeenCalledWith(expect.objectContaining(persona));
      expect(comp.isSaving).toEqual(false);
    });

    it('Should call create service on save for new entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IPersona>>();
      const persona = { id: 123 };
      jest.spyOn(personaFormService, 'getPersona').mockReturnValue({ id: null });
      jest.spyOn(personaService, 'create').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ persona: null });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: persona }));
      saveSubject.complete();

      // THEN
      expect(personaFormService.getPersona).toHaveBeenCalled();
      expect(personaService.create).toHaveBeenCalled();
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).toHaveBeenCalled();
    });

    it('Should set isSaving to false on error', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IPersona>>();
      const persona = { id: 123 };
      jest.spyOn(personaService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ persona });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.error('This is an error!');

      // THEN
      expect(personaService.update).toHaveBeenCalled();
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).not.toHaveBeenCalled();
    });
  });
});
