package co.ufps.refupet.domain.valueobjects;

import co.ufps.refupet.domain.exceptions.InvalidDomainDataException;
import co.ufps.refupet.domain.ids.UsuarioId;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

/** Invariantes de los Value Objects del dominio (los mappers JPA dependen de que se vuelvan a aplicar al reconstruir). */
class ValueObjectsTest {

    private static final UsuarioId USUARIO = UsuarioId.of("u-1");
    private static final LocalDate HOY = LocalDate.of(2026, 6, 15);
    private static final LocalDateTime AHORA = LocalDateTime.of(2026, 6, 15, 10, 0);

    @Nested
    class EmailVO {
        @Test
        void seNormalizaAMinusculasYSinEspacios() {
            assertEquals("ana@refupet.co", Email.of("  Ana@RefuPet.CO ").value());
            assertEquals(Email.of("ana@refupet.co"), Email.of("ANA@refupet.co"));
            assertEquals(Email.of("ana@refupet.co").hashCode(), Email.of("ANA@refupet.co").hashCode());
        }

        @Test
        void rechazaFormatosInvalidos() {
            for (String invalido : new String[]{"sin-arroba", "a@b", "a b@c.co", "@refupet.co", "", "   "}) {
                assertThrows(InvalidDomainDataException.class, () -> Email.of(invalido), invalido);
            }
            assertThrows(InvalidDomainDataException.class, () -> Email.of(null));
        }
    }

    @Nested
    class Periodo {
        @Test
        void calculaFechaMinimaYCumplimiento() {
            PeriodoCuarentena periodo = new PeriodoCuarentena(HOY, 10);
            assertEquals(HOY.plusDays(10), periodo.fechaMinimaSalida());
            assertFalse(periodo.estaCumplido(HOY.plusDays(9)));
            assertTrue(periodo.estaCumplido(HOY.plusDays(10)));
            assertTrue(periodo.estaCumplido(HOY.plusDays(11)));
        }

        @Test
        void diasRestantesNuncaNegativos() {
            PeriodoCuarentena periodo = new PeriodoCuarentena(HOY, 10);
            assertEquals(10, periodo.diasRestantes(HOY));
            assertEquals(1, periodo.diasRestantes(HOY.plusDays(9)));
            assertEquals(0, periodo.diasRestantes(HOY.plusDays(30)));
        }

        /** Coincide con ck_cuarentena_dias_minimos (dias_minimos > 0). */
        @Test
        void diasMinimosDebenSerPositivosYFechaObligatoria() {
            assertThrows(InvalidDomainDataException.class, () -> new PeriodoCuarentena(HOY, 0));
            assertThrows(InvalidDomainDataException.class, () -> new PeriodoCuarentena(HOY, -1));
            assertThrows(InvalidDomainDataException.class, () -> new PeriodoCuarentena(null, 5));
            assertThrows(InvalidDomainDataException.class, () -> new PeriodoCuarentena(HOY, 5).estaCumplido(null));
        }
    }

    @Nested
    class RegistrosYEstados {
        @Test
        void registroIngresoNormalizaOpcionales() {
            RegistroIngreso ingreso = new RegistroIngreso(HOY, USUARIO, "  ", " Rescate ");
            assertNull(ingreso.procedencia());
            assertEquals("Rescate", ingreso.condicionInicial());
            assertThrows(InvalidDomainDataException.class, () -> new RegistroIngreso(null, USUARIO, null, null));
            assertThrows(InvalidDomainDataException.class, () -> new RegistroIngreso(HOY, null, null, null));
        }

        /** EstadoSalud es "todo o nada" (ck_animal_estado_salud): sus tres campos son obligatorios. */
        @Test
        void estadoSaludExigeSusTresCampos() {
            assertEquals("Estable", new EstadoSalud(" Estable ", AHORA, USUARIO).descripcion());
            assertThrows(InvalidDomainDataException.class, () -> new EstadoSalud(" ", AHORA, USUARIO));
            assertThrows(InvalidDomainDataException.class, () -> new EstadoSalud("Estable", null, USUARIO));
            assertThrows(InvalidDomainDataException.class, () -> new EstadoSalud("Estable", AHORA, null));
        }

        /** ck_solicitud_compatibilidad: compatible, evaluado_por y fecha obligatorios; observaciones opcional. */
        @Test
        void evaluacionCompatibilidadConObservacionesOpcionales() {
            assertNull(new EvaluacionCompatibilidad(false, "  ", USUARIO, AHORA).observaciones());
            assertThrows(InvalidDomainDataException.class, () -> new EvaluacionCompatibilidad(true, null, null, AHORA));
            assertThrows(InvalidDomainDataException.class, () -> new EvaluacionCompatibilidad(true, null, USUARIO, null));
        }

        /** ck_adopcion_devolucion: fecha, motivo y responsable obligatorios. */
        @Test
        void devolucionExigeFechaMotivoYResponsable() {
            assertEquals("Mudanza", new Devolucion(HOY, " Mudanza ", USUARIO).motivo());
            assertThrows(InvalidDomainDataException.class, () -> new Devolucion(null, "Mudanza", USUARIO));
            assertThrows(InvalidDomainDataException.class, () -> new Devolucion(HOY, " ", USUARIO));
            assertThrows(InvalidDomainDataException.class, () -> new Devolucion(HOY, "Mudanza", null));
        }

        @Test
        void losRecordsSeComparanPorValor() {
            assertEquals(new Devolucion(HOY, "M", USUARIO), new Devolucion(HOY, "M", UsuarioId.of("u-1")));
            assertEquals(new PeriodoCuarentena(HOY, 3), new PeriodoCuarentena(HOY, 3));
        }
    }
}
