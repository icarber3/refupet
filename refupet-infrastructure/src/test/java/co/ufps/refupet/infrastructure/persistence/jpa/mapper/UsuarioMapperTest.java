package co.ufps.refupet.infrastructure.persistence.jpa.mapper;

import co.ufps.refupet.domain.entities.Administrador;
import co.ufps.refupet.domain.entities.Solicitante;
import co.ufps.refupet.domain.entities.Usuario;
import co.ufps.refupet.domain.entities.Veterinario;
import co.ufps.refupet.domain.entities.Voluntario;
import co.ufps.refupet.domain.enums.EstadoCuenta;
import co.ufps.refupet.domain.enums.RolUsuario;
import co.ufps.refupet.domain.exceptions.InvalidDomainDataException;
import co.ufps.refupet.domain.ids.UsuarioId;
import co.ufps.refupet.domain.valueobjects.Email;
import co.ufps.refupet.infrastructure.persistence.jpa.entity.UsuarioJpa;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Pruebas unitarias (sin base de datos) del Data Mapper de Usuario:
 * ida y vuelta dominio → JPA → dominio para todos los roles, estados y campos opcionales.
 */
class UsuarioMapperTest {

    private static final LocalDateTime FECHA = LocalDateTime.of(2026, 3, 15, 10, 30, 45);
    private static final UsuarioId ID = UsuarioId.of("3f2b8c1e-6d4a-4b7e-9a51-0c2d7e8f9a10");

    private static void assertMismosDatosComunes(Usuario esperado, Usuario obtenido) {
        assertEquals(esperado.getClass(), obtenido.getClass());
        assertEquals(esperado.getUsuarioId(), obtenido.getUsuarioId());
        assertEquals(esperado.getRol(), obtenido.getRol());
        assertEquals(esperado.getNombre(), obtenido.getNombre());
        assertEquals(esperado.getEmail(), obtenido.getEmail());
        assertEquals(esperado.getEstadoCuenta(), obtenido.getEstadoCuenta());
        assertEquals(esperado.getFechaRegistro(), obtenido.getFechaRegistro());
    }

    @Nested
    @DisplayName("Ida y vuelta dominio → JPA → dominio")
    class IdaYVuelta {

        @Test
        void administrador() {
            Usuario original = Administrador.of(ID, "Ana Admin", Email.of("ana@refupet.co"), EstadoCuenta.ACTIVA, FECHA);
            Usuario reconstruido = UsuarioMapper.toDomain(UsuarioMapper.toJpa(original));
            assertInstanceOf(Administrador.class, reconstruido);
            assertMismosDatosComunes(original, reconstruido);
        }

        @Test
        void veterinario() {
            Usuario original = Veterinario.of(ID, "Victor Vet", Email.of("victor@refupet.co"),
                    EstadoCuenta.PENDIENTE_APROBACION, FECHA);
            Usuario reconstruido = UsuarioMapper.toDomain(UsuarioMapper.toJpa(original));
            assertInstanceOf(Veterinario.class, reconstruido);
            assertMismosDatosComunes(original, reconstruido);
        }

        @Test
        void voluntario() {
            Usuario original = Voluntario.of(ID, "Valeria Vol", Email.of("valeria@refupet.co"),
                    EstadoCuenta.SUSPENDIDA, FECHA);
            Usuario reconstruido = UsuarioMapper.toDomain(UsuarioMapper.toJpa(original));
            assertInstanceOf(Voluntario.class, reconstruido);
            assertMismosDatosComunes(original, reconstruido);
        }

        @Test
        void solicitanteConContacto() {
            Solicitante original = Solicitante.of(ID, "Sara Sol", Email.of("sara@correo.com"),
                    "3001234567", "Calle 1 # 2-3", EstadoCuenta.ACTIVA, FECHA);
            Usuario reconstruido = UsuarioMapper.toDomain(UsuarioMapper.toJpa(original));
            Solicitante solicitante = assertInstanceOf(Solicitante.class, reconstruido);
            assertMismosDatosComunes(original, solicitante);
            assertEquals("3001234567", solicitante.getTelefono());
            assertEquals("Calle 1 # 2-3", solicitante.getDireccion());
        }

        @Test
        void solicitanteSinContacto() {
            Solicitante original = Solicitante.of(ID, "Sara Sol", Email.of("sara@correo.com"),
                    null, null, EstadoCuenta.PENDIENTE_APROBACION, FECHA);
            Solicitante reconstruido = assertInstanceOf(Solicitante.class,
                    UsuarioMapper.toDomain(UsuarioMapper.toJpa(original)));
            assertNull(reconstruido.getTelefono());
            assertNull(reconstruido.getDireccion());
        }

        @Test
        void solicitanteSoloConTelefono() {
            Solicitante original = Solicitante.of(ID, "Sara Sol", Email.of("sara@correo.com"),
                    "3001234567", null, EstadoCuenta.ACTIVA, FECHA);
            Solicitante reconstruido = assertInstanceOf(Solicitante.class,
                    UsuarioMapper.toDomain(UsuarioMapper.toJpa(original)));
            assertEquals("3001234567", reconstruido.getTelefono());
            assertNull(reconstruido.getDireccion());
        }

        @ParameterizedTest
        @EnumSource(EstadoCuenta.class)
        void conservaCualquierEstadoDeCuenta(EstadoCuenta estado) {
            Usuario original = Veterinario.of(ID, "Victor Vet", Email.of("victor@refupet.co"), estado, FECHA);
            assertEquals(estado, UsuarioMapper.toDomain(UsuarioMapper.toJpa(original)).getEstadoCuenta());
        }

        @Test
        void usuarioCreadoConFabricaCreateConservaIdYFecha() {
            Usuario original = Usuario.create(RolUsuario.VOLUNTARIO, "Nuevo", Email.of("nuevo@refupet.co"));
            assertMismosDatosComunes(original, UsuarioMapper.toDomain(UsuarioMapper.toJpa(original)));
        }
    }

    @Nested
    @DisplayName("Dominio → JPA")
    class HaciaJpa {

        @Test
        void copiaCadaColumna() {
            Solicitante solicitante = Solicitante.of(ID, "Sara Sol", Email.of("sara@correo.com"),
                    "3001234567", "Calle 1", EstadoCuenta.ACTIVA, FECHA);
            UsuarioJpa jpa = UsuarioMapper.toJpa(solicitante);
            assertEquals(ID.value(), jpa.getId());
            assertEquals(RolUsuario.SOLICITANTE, jpa.getRol());
            assertEquals("Sara Sol", jpa.getNombre());
            assertEquals("sara@correo.com", jpa.getEmail());
            assertEquals(EstadoCuenta.ACTIVA, jpa.getEstadoCuenta());
            assertEquals(FECHA, jpa.getFechaRegistro());
            assertEquals("3001234567", jpa.getTelefono());
            assertEquals("Calle 1", jpa.getDireccion());
        }

        /** ck_usuario_contacto_solicitante: telefono y direccion deben ser NULL si el rol no es SOLICITANTE. */
        @ParameterizedTest
        @EnumSource(value = RolUsuario.class, names = "SOLICITANTE", mode = EnumSource.Mode.EXCLUDE)
        void rolesNoSolicitanteSinDatosDeContacto(RolUsuario rol) {
            UsuarioJpa jpa = UsuarioMapper.toJpa(Usuario.create(rol, "Persona", Email.of("p@refupet.co")));
            assertEquals(rol, jpa.getRol());
            assertNull(jpa.getTelefono());
            assertNull(jpa.getDireccion());
        }
    }

    @Nested
    @DisplayName("JPA → dominio")
    class HaciaDominio {

        private UsuarioJpa fila(RolUsuario rol) {
            UsuarioJpa jpa = new UsuarioJpa();
            jpa.setId(ID.value());
            jpa.setRol(rol);
            jpa.setNombre("Persona");
            jpa.setEmail("persona@refupet.co");
            jpa.setEstadoCuenta(EstadoCuenta.ACTIVA);
            jpa.setFechaRegistro(FECHA);
            return jpa;
        }

        @ParameterizedTest
        @EnumSource(RolUsuario.class)
        void elRolDeterminaLaSubclase(RolUsuario rol) {
            Usuario usuario = UsuarioMapper.toDomain(fila(rol));
            assertEquals(rol, usuario.getRol());
            Class<?> esperada = switch (rol) {
                case ADMINISTRADOR -> Administrador.class;
                case VETERINARIO -> Veterinario.class;
                case VOLUNTARIO -> Voluntario.class;
                case SOLICITANTE -> Solicitante.class;
            };
            assertEquals(esperada, usuario.getClass());
        }

        @Test
        void filaJpaIdaYVuelta() {
            UsuarioJpa original = fila(RolUsuario.SOLICITANTE);
            original.setTelefono("555");
            UsuarioJpa copia = UsuarioMapper.toJpa(UsuarioMapper.toDomain(original));
            assertEquals(original.getId(), copia.getId());
            assertEquals(original.getRol(), copia.getRol());
            assertEquals(original.getNombre(), copia.getNombre());
            assertEquals(original.getEmail(), copia.getEmail());
            assertEquals(original.getEstadoCuenta(), copia.getEstadoCuenta());
            assertEquals(original.getFechaRegistro(), copia.getFechaRegistro());
            assertEquals("555", copia.getTelefono());
            assertNull(copia.getDireccion());
        }

        @Test
        void aplicaLasInvariantesDelDominioAlReconstruir() {
            UsuarioJpa jpa = fila(RolUsuario.ADMINISTRADOR);
            jpa.setEmail("sin-arroba");
            assertThrows(InvalidDomainDataException.class, () -> UsuarioMapper.toDomain(jpa));
        }

        @Test
        void rechazaFilaSinRol() {
            assertThrows(NullPointerException.class, () -> UsuarioMapper.toDomain(fila(null)));
        }
    }
}
