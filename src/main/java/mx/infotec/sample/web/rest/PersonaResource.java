package mx.infotec.sample.web.rest;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import mx.infotec.sample.repository.PersonaRepository;
import mx.infotec.sample.service.PersonaService;
import mx.infotec.sample.service.dto.PersonaDTO;
import mx.infotec.sample.web.rest.errors.BadRequestAlertException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import tech.jhipster.web.util.HeaderUtil;
import tech.jhipster.web.util.ResponseUtil;

/**
 * REST controller for managing {@link mx.infotec.sample.domain.Persona}.
 */
@RestController
@RequestMapping("/api/personas")
public class PersonaResource {

    private final Logger log = LoggerFactory.getLogger(PersonaResource.class);

    private static final String ENTITY_NAME = "persona";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final PersonaService personaService;

    private final PersonaRepository personaRepository;

    public PersonaResource(PersonaService personaService, PersonaRepository personaRepository) {
        this.personaService = personaService;
        this.personaRepository = personaRepository;
    }

    /**
     * {@code POST  /personas} : Create a new persona.
     *
     * @param personaDTO the personaDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new personaDTO, or with status {@code 400 (Bad Request)} if the persona has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("")
    public ResponseEntity<PersonaDTO> createPersona(@Valid @RequestBody PersonaDTO personaDTO) throws URISyntaxException {
        log.debug("REST request to save Persona : {}", personaDTO);
        if (personaDTO.getId() != null) {
            throw new BadRequestAlertException("A new persona cannot already have an ID", ENTITY_NAME, "idexists");
        }
        personaDTO = personaService.save(personaDTO);
        return ResponseEntity.created(new URI("/api/personas/" + personaDTO.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, personaDTO.getId().toString()))
            .body(personaDTO);
    }

    /**
     * {@code PUT  /personas/:id} : Updates an existing persona.
     *
     * @param id the id of the personaDTO to save.
     * @param personaDTO the personaDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated personaDTO,
     * or with status {@code 400 (Bad Request)} if the personaDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the personaDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/{id}")
    public ResponseEntity<PersonaDTO> updatePersona(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody PersonaDTO personaDTO
    ) throws URISyntaxException {
        log.debug("REST request to update Persona : {}, {}", id, personaDTO);
        if (personaDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, personaDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!personaRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        personaDTO = personaService.update(personaDTO);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, personaDTO.getId().toString()))
            .body(personaDTO);
    }

    /**
     * {@code PATCH  /personas/:id} : Partial updates given fields of an existing persona, field will ignore if it is null
     *
     * @param id the id of the personaDTO to save.
     * @param personaDTO the personaDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated personaDTO,
     * or with status {@code 400 (Bad Request)} if the personaDTO is not valid,
     * or with status {@code 404 (Not Found)} if the personaDTO is not found,
     * or with status {@code 500 (Internal Server Error)} if the personaDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<PersonaDTO> partialUpdatePersona(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody PersonaDTO personaDTO
    ) throws URISyntaxException {
        log.debug("REST request to partial update Persona partially : {}, {}", id, personaDTO);
        if (personaDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, personaDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!personaRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<PersonaDTO> result = personaService.partialUpdate(personaDTO);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, personaDTO.getId().toString())
        );
    }

    /**
     * {@code GET  /personas} : get all the personas.
     *
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of personas in body.
     */
    @GetMapping("")
    public List<PersonaDTO> getAllPersonas() {
        log.debug("REST request to get all Personas");
        return personaService.findAll();
    }

    /**
     * {@code GET  /personas/:id} : get the "id" persona.
     *
     * @param id the id of the personaDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the personaDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    public ResponseEntity<PersonaDTO> getPersona(@PathVariable("id") Long id) {
        log.debug("REST request to get Persona : {}", id);
        Optional<PersonaDTO> personaDTO = personaService.findOne(id);
        return ResponseUtil.wrapOrNotFound(personaDTO);
    }

    /**
     * {@code DELETE  /personas/:id} : delete the "id" persona.
     *
     * @param id the id of the personaDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePersona(@PathVariable("id") Long id) {
        log.debug("REST request to delete Persona : {}", id);
        personaService.delete(id);
        return ResponseEntity.noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
            .build();
    }
}
