package mx.infotec.sample.service.mapper;

import mx.infotec.sample.domain.Persona;
import mx.infotec.sample.service.dto.PersonaDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link Persona} and its DTO {@link PersonaDTO}.
 */
@Mapper(componentModel = "spring")
public interface PersonaMapper extends EntityMapper<PersonaDTO, Persona> {}
