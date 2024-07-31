package mx.infotec.sample.service;

import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import mx.infotec.sample.domain.Persona;
import mx.infotec.sample.repository.PersonaRepository;
import mx.infotec.sample.service.dto.PersonaDTO;
import mx.infotec.sample.service.mapper.PersonaMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link mx.infotec.sample.domain.Persona}.
 */
@Service
@Transactional
public class PersonaService {

    private final Logger log = LoggerFactory.getLogger(PersonaService.class);

    private final PersonaRepository personaRepository;

    private final PersonaMapper personaMapper;

    public PersonaService(PersonaRepository personaRepository, PersonaMapper personaMapper) {
        this.personaRepository = personaRepository;
        this.personaMapper = personaMapper;
    }

    /**
     * Save a persona.
     *
     * @param personaDTO the entity to save.
     * @return the persisted entity.
     */
    public PersonaDTO save(PersonaDTO personaDTO) {
        log.debug("Request to save Persona : {}", personaDTO);
        Persona persona = personaMapper.toEntity(personaDTO);
        persona = personaRepository.save(persona);
        return personaMapper.toDto(persona);
    }

    /**
     * Update a persona.
     *
     * @param personaDTO the entity to save.
     * @return the persisted entity.
     */
    public PersonaDTO update(PersonaDTO personaDTO) {
        log.debug("Request to update Persona : {}", personaDTO);
        Persona persona = personaMapper.toEntity(personaDTO);
        persona = personaRepository.save(persona);
        return personaMapper.toDto(persona);
    }

    /**
     * Partially update a persona.
     *
     * @param personaDTO the entity to update partially.
     * @return the persisted entity.
     */
    public Optional<PersonaDTO> partialUpdate(PersonaDTO personaDTO) {
        log.debug("Request to partially update Persona : {}", personaDTO);

        return personaRepository
            .findById(personaDTO.getId())
            .map(existingPersona -> {
                personaMapper.partialUpdate(existingPersona, personaDTO);

                return existingPersona;
            })
            .map(personaRepository::save)
            .map(personaMapper::toDto);
    }

    /**
     * Get all the personas.
     *
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public List<PersonaDTO> findAll() {
        log.debug("Request to get all Personas");
        return personaRepository.findAll().stream().map(personaMapper::toDto).collect(Collectors.toCollection(LinkedList::new));
    }

    /**
     * Get one persona by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Transactional(readOnly = true)
    public Optional<PersonaDTO> findOne(Long id) {
        log.debug("Request to get Persona : {}", id);
        return personaRepository.findById(id).map(personaMapper::toDto);
    }

    /**
     * Delete the persona by id.
     *
     * @param id the id of the entity.
     */
    public void delete(Long id) {
        log.debug("Request to delete Persona : {}", id);
        personaRepository.deleteById(id);
    }
}
