package mx.infotec.sample.domain;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;

public class PersonaTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    public static Persona getPersonaSample1() {
        return new Persona()
            .id(1L)
            .nombre("nombre1")
            .primerApellido("primerApellido1")
            .segundoApellido("segundoApellido1")
            .edad(1L)
            .curp("curp1");
    }

    public static Persona getPersonaSample2() {
        return new Persona()
            .id(2L)
            .nombre("nombre2")
            .primerApellido("primerApellido2")
            .segundoApellido("segundoApellido2")
            .edad(2L)
            .curp("curp2");
    }

    public static Persona getPersonaRandomSampleGenerator() {
        return new Persona()
            .id(longCount.incrementAndGet())
            .nombre(UUID.randomUUID().toString())
            .primerApellido(UUID.randomUUID().toString())
            .segundoApellido(UUID.randomUUID().toString())
            .edad(longCount.incrementAndGet())
            .curp(UUID.randomUUID().toString());
    }
}
