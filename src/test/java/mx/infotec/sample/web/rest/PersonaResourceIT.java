package mx.infotec.sample.web.rest;

import static mx.infotec.sample.domain.PersonaAsserts.*;
import static mx.infotec.sample.web.rest.TestUtil.createUpdateProxyForBean;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.EntityManager;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Random;
import java.util.concurrent.atomic.AtomicLong;
import mx.infotec.sample.IntegrationTest;
import mx.infotec.sample.domain.Persona;
import mx.infotec.sample.repository.PersonaRepository;
import mx.infotec.sample.service.dto.PersonaDTO;
import mx.infotec.sample.service.mapper.PersonaMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

/**
 * Integration tests for the {@link PersonaResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class PersonaResourceIT {

    private static final String DEFAULT_NOMBRE = "AAAAAAAAAA";
    private static final String UPDATED_NOMBRE = "BBBBBBBBBB";

    private static final String DEFAULT_PRIMER_APELLIDO = "AAAAAAAAAA";
    private static final String UPDATED_PRIMER_APELLIDO = "BBBBBBBBBB";

    private static final String DEFAULT_SEGUNDO_APELLIDO = "AAAAAAAAAA";
    private static final String UPDATED_SEGUNDO_APELLIDO = "BBBBBBBBBB";

    private static final LocalDate DEFAULT_FECHA_NACIMIENTO = LocalDate.ofEpochDay(0L);
    private static final LocalDate UPDATED_FECHA_NACIMIENTO = LocalDate.now(ZoneId.systemDefault());

    private static final Long DEFAULT_EDAD = 0L;
    private static final Long UPDATED_EDAD = 1L;

    private static final String DEFAULT_CURP = "AAAAAAAAAA";
    private static final String UPDATED_CURP = "BBBBBBBBBB";

    private static final String ENTITY_API_URL = "/api/personas";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private ObjectMapper om;

    @Autowired
    private PersonaRepository personaRepository;

    @Autowired
    private PersonaMapper personaMapper;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restPersonaMockMvc;

    private Persona persona;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Persona createEntity(EntityManager em) {
        Persona persona = new Persona()
            .nombre(DEFAULT_NOMBRE)
            .primerApellido(DEFAULT_PRIMER_APELLIDO)
            .segundoApellido(DEFAULT_SEGUNDO_APELLIDO)
            .fechaNacimiento(DEFAULT_FECHA_NACIMIENTO)
            .edad(DEFAULT_EDAD)
            .curp(DEFAULT_CURP);
        return persona;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Persona createUpdatedEntity(EntityManager em) {
        Persona persona = new Persona()
            .nombre(UPDATED_NOMBRE)
            .primerApellido(UPDATED_PRIMER_APELLIDO)
            .segundoApellido(UPDATED_SEGUNDO_APELLIDO)
            .fechaNacimiento(UPDATED_FECHA_NACIMIENTO)
            .edad(UPDATED_EDAD)
            .curp(UPDATED_CURP);
        return persona;
    }

    @BeforeEach
    public void initTest() {
        persona = createEntity(em);
    }

    @Test
    @Transactional
    void createPersona() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        // Create the Persona
        PersonaDTO personaDTO = personaMapper.toDto(persona);
        var returnedPersonaDTO = om.readValue(
            restPersonaMockMvc
                .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(personaDTO)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString(),
            PersonaDTO.class
        );

        // Validate the Persona in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        var returnedPersona = personaMapper.toEntity(returnedPersonaDTO);
        assertPersonaUpdatableFieldsEquals(returnedPersona, getPersistedPersona(returnedPersona));
    }

    @Test
    @Transactional
    void createPersonaWithExistingId() throws Exception {
        // Create the Persona with an existing ID
        persona.setId(1L);
        PersonaDTO personaDTO = personaMapper.toDto(persona);

        long databaseSizeBeforeCreate = getRepositoryCount();

        // An entity with an existing ID cannot be created, so this API call must fail
        restPersonaMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(personaDTO)))
            .andExpect(status().isBadRequest());

        // Validate the Persona in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void checkNombreIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        persona.setNombre(null);

        // Create the Persona, which fails.
        PersonaDTO personaDTO = personaMapper.toDto(persona);

        restPersonaMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(personaDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkPrimerApellidoIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        persona.setPrimerApellido(null);

        // Create the Persona, which fails.
        PersonaDTO personaDTO = personaMapper.toDto(persona);

        restPersonaMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(personaDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void getAllPersonas() throws Exception {
        // Initialize the database
        personaRepository.saveAndFlush(persona);

        // Get all the personaList
        restPersonaMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(persona.getId().intValue())))
            .andExpect(jsonPath("$.[*].nombre").value(hasItem(DEFAULT_NOMBRE)))
            .andExpect(jsonPath("$.[*].primerApellido").value(hasItem(DEFAULT_PRIMER_APELLIDO)))
            .andExpect(jsonPath("$.[*].segundoApellido").value(hasItem(DEFAULT_SEGUNDO_APELLIDO)))
            .andExpect(jsonPath("$.[*].fechaNacimiento").value(hasItem(DEFAULT_FECHA_NACIMIENTO.toString())))
            .andExpect(jsonPath("$.[*].edad").value(hasItem(DEFAULT_EDAD.intValue())))
            .andExpect(jsonPath("$.[*].curp").value(hasItem(DEFAULT_CURP)));
    }

    @Test
    @Transactional
    void getPersona() throws Exception {
        // Initialize the database
        personaRepository.saveAndFlush(persona);

        // Get the persona
        restPersonaMockMvc
            .perform(get(ENTITY_API_URL_ID, persona.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(persona.getId().intValue()))
            .andExpect(jsonPath("$.nombre").value(DEFAULT_NOMBRE))
            .andExpect(jsonPath("$.primerApellido").value(DEFAULT_PRIMER_APELLIDO))
            .andExpect(jsonPath("$.segundoApellido").value(DEFAULT_SEGUNDO_APELLIDO))
            .andExpect(jsonPath("$.fechaNacimiento").value(DEFAULT_FECHA_NACIMIENTO.toString()))
            .andExpect(jsonPath("$.edad").value(DEFAULT_EDAD.intValue()))
            .andExpect(jsonPath("$.curp").value(DEFAULT_CURP));
    }

    @Test
    @Transactional
    void getNonExistingPersona() throws Exception {
        // Get the persona
        restPersonaMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingPersona() throws Exception {
        // Initialize the database
        personaRepository.saveAndFlush(persona);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the persona
        Persona updatedPersona = personaRepository.findById(persona.getId()).orElseThrow();
        // Disconnect from session so that the updates on updatedPersona are not directly saved in db
        em.detach(updatedPersona);
        updatedPersona
            .nombre(UPDATED_NOMBRE)
            .primerApellido(UPDATED_PRIMER_APELLIDO)
            .segundoApellido(UPDATED_SEGUNDO_APELLIDO)
            .fechaNacimiento(UPDATED_FECHA_NACIMIENTO)
            .edad(UPDATED_EDAD)
            .curp(UPDATED_CURP);
        PersonaDTO personaDTO = personaMapper.toDto(updatedPersona);

        restPersonaMockMvc
            .perform(
                put(ENTITY_API_URL_ID, personaDTO.getId()).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(personaDTO))
            )
            .andExpect(status().isOk());

        // Validate the Persona in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedPersonaToMatchAllProperties(updatedPersona);
    }

    @Test
    @Transactional
    void putNonExistingPersona() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        persona.setId(longCount.incrementAndGet());

        // Create the Persona
        PersonaDTO personaDTO = personaMapper.toDto(persona);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restPersonaMockMvc
            .perform(
                put(ENTITY_API_URL_ID, personaDTO.getId()).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(personaDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Persona in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchPersona() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        persona.setId(longCount.incrementAndGet());

        // Create the Persona
        PersonaDTO personaDTO = personaMapper.toDto(persona);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restPersonaMockMvc
            .perform(
                put(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(personaDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Persona in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamPersona() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        persona.setId(longCount.incrementAndGet());

        // Create the Persona
        PersonaDTO personaDTO = personaMapper.toDto(persona);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restPersonaMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(personaDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Persona in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdatePersonaWithPatch() throws Exception {
        // Initialize the database
        personaRepository.saveAndFlush(persona);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the persona using partial update
        Persona partialUpdatedPersona = new Persona();
        partialUpdatedPersona.setId(persona.getId());

        partialUpdatedPersona.nombre(UPDATED_NOMBRE).primerApellido(UPDATED_PRIMER_APELLIDO);

        restPersonaMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedPersona.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedPersona))
            )
            .andExpect(status().isOk());

        // Validate the Persona in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersonaUpdatableFieldsEquals(createUpdateProxyForBean(partialUpdatedPersona, persona), getPersistedPersona(persona));
    }

    @Test
    @Transactional
    void fullUpdatePersonaWithPatch() throws Exception {
        // Initialize the database
        personaRepository.saveAndFlush(persona);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the persona using partial update
        Persona partialUpdatedPersona = new Persona();
        partialUpdatedPersona.setId(persona.getId());

        partialUpdatedPersona
            .nombre(UPDATED_NOMBRE)
            .primerApellido(UPDATED_PRIMER_APELLIDO)
            .segundoApellido(UPDATED_SEGUNDO_APELLIDO)
            .fechaNacimiento(UPDATED_FECHA_NACIMIENTO)
            .edad(UPDATED_EDAD)
            .curp(UPDATED_CURP);

        restPersonaMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedPersona.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedPersona))
            )
            .andExpect(status().isOk());

        // Validate the Persona in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersonaUpdatableFieldsEquals(partialUpdatedPersona, getPersistedPersona(partialUpdatedPersona));
    }

    @Test
    @Transactional
    void patchNonExistingPersona() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        persona.setId(longCount.incrementAndGet());

        // Create the Persona
        PersonaDTO personaDTO = personaMapper.toDto(persona);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restPersonaMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, personaDTO.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(personaDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Persona in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchPersona() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        persona.setId(longCount.incrementAndGet());

        // Create the Persona
        PersonaDTO personaDTO = personaMapper.toDto(persona);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restPersonaMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(personaDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Persona in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamPersona() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        persona.setId(longCount.incrementAndGet());

        // Create the Persona
        PersonaDTO personaDTO = personaMapper.toDto(persona);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restPersonaMockMvc
            .perform(patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(om.writeValueAsBytes(personaDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Persona in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deletePersona() throws Exception {
        // Initialize the database
        personaRepository.saveAndFlush(persona);

        long databaseSizeBeforeDelete = getRepositoryCount();

        // Delete the persona
        restPersonaMockMvc
            .perform(delete(ENTITY_API_URL_ID, persona.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
    }

    protected long getRepositoryCount() {
        return personaRepository.count();
    }

    protected void assertIncrementedRepositoryCount(long countBefore) {
        assertThat(countBefore + 1).isEqualTo(getRepositoryCount());
    }

    protected void assertDecrementedRepositoryCount(long countBefore) {
        assertThat(countBefore - 1).isEqualTo(getRepositoryCount());
    }

    protected void assertSameRepositoryCount(long countBefore) {
        assertThat(countBefore).isEqualTo(getRepositoryCount());
    }

    protected Persona getPersistedPersona(Persona persona) {
        return personaRepository.findById(persona.getId()).orElseThrow();
    }

    protected void assertPersistedPersonaToMatchAllProperties(Persona expectedPersona) {
        assertPersonaAllPropertiesEquals(expectedPersona, getPersistedPersona(expectedPersona));
    }

    protected void assertPersistedPersonaToMatchUpdatableProperties(Persona expectedPersona) {
        assertPersonaAllUpdatablePropertiesEquals(expectedPersona, getPersistedPersona(expectedPersona));
    }
}
