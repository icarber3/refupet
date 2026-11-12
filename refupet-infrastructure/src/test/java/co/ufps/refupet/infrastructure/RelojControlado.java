package co.ufps.refupet.infrastructure;

import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;

/**
 * Reloj de pruebas que permite avanzar el tiempo para verificar reglas temporales
 * (periodo mínimo de cuarentena, fechas futuras).
 */
class RelojControlado extends Clock {

    private static final ZoneId ZONA = ZoneId.of("America/Bogota");
    private Instant instante;

    RelojControlado(LocalDate fechaInicial) {
        this.instante = fechaInicial.atTime(12, 0).atZone(ZONA).toInstant();
    }

    void avanzarDias(long dias) {
        instante = instante.plus(Duration.ofDays(dias));
    }

    LocalDate hoy() {
        return LocalDate.now(this);
    }

    @Override
    public ZoneId getZone() {
        return ZONA;
    }

    @Override
    public Clock withZone(ZoneId zone) {
        return this;
    }

    @Override
    public Instant instant() {
        return instante;
    }
}
