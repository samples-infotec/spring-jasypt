package mx.infotec.sample.service.mapper;

import static mx.infotec.sample.domain.PersonaAsserts.*;
import static mx.infotec.sample.domain.PersonaTestSamples.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class PersonaMapperTest {

    private PersonaMapper personaMapper;

    @BeforeEach
    void setUp() {
        personaMapper = new PersonaMapperImpl();
    }

    @Test
    void shouldConvertToDtoAndBack() {
        var expected = getPersonaSample1();
        var actual = personaMapper.toEntity(personaMapper.toDto(expected));
        assertPersonaAllPropertiesEquals(expected, actual);
    }
}
