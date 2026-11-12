package co.ufps.refupet.infrastructure.config;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.inject.Produces;
import jakarta.inject.Singleton;

import java.time.Clock;

/**
 * Productor CDI del reloj que usan los casos de uso (fechas por defecto, periodo mínimo de cuarentena).
 *
 * En producción es el reloj del sistema con la zona horaria por defecto, el mismo que usa
 * {@link RefuPetAplicacion#RefuPetAplicacion()} en el ensamblaje manual. Se produce como
 * {@code @Singleton} (sin proxy de cliente) porque {@link Clock} es inmutable.
 */
@ApplicationScoped
public class RelojProducer {

    @Produces
    @Singleton
    Clock reloj() {
        return Clock.systemDefaultZone();
    }
}
