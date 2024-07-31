package mx.infotec.sample.domain;

import static mx.infotec.sample.domain.PersonaTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import mx.infotec.sample.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class PersonaTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Persona.class);
        Persona persona1 = getPersonaSample1();
        Persona persona2 = new Persona();
        assertThat(persona1).isNotEqualTo(persona2);

        persona2.setId(persona1.getId());
        assertThat(persona1).isEqualTo(persona2);

        persona2 = getPersonaSample2();
        assertThat(persona1).isNotEqualTo(persona2);
    }
}
