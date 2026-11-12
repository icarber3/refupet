package co.ufps.refupet.domain.entities;

import co.ufps.refupet.domain.enums.EstadoCuenta;
import co.ufps.refupet.domain.enums.Operacion;
import co.ufps.refupet.domain.enums.RolUsuario;
import co.ufps.refupet.domain.exceptions.AccountApprovalNotAllowedException;
import co.ufps.refupet.domain.exceptions.InvalidDomainDataException;
import co.ufps.refupet.domain.exceptions.InvalidUserStateException;
import co.ufps.refupet.domain.exceptions.UnauthorizedRoleOperationException;
import co.ufps.refupet.domain.valueobjects.Email;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class UsuarioTest {

    private static Usuario activo(RolUsuario rol) {
        Usuario usuario = Usuario.create(rol, "Persona " + rol, Email.of(rol.name().toLowerCase() + "@refupet.co"));
        usuario.aprobar();
        return usuario;
    }

    @Nested
    @DisplayName("Ciclo de vida de la cuenta")
    class CicloDeVida {

        @Test
        @DisplayName("Una cuenta nueva queda pendiente de aprobación")
        void cuentaNuevaQuedaPendiente() {
            Usuario usuario = Usuario.create(RolUsuario.VOLUNTARIO, "Ana", Email.of("ana@refupet.co"));
            assertEquals(EstadoCuenta.PENDIENTE_APROBACION, usuario.getEstadoCuenta());
            assertInstanceOf(Voluntario.class, usuario);
        }

        @Test
        @DisplayName("Aprobar, suspender y activar siguen las transiciones válidas")
        void transicionesValidas() {
            Usuario usuario = Usuario.create(RolUsuario.VETERINARIO, "Luis", Email.of("luis@refupet.co"));
            usuario.aprobar();
            assertEquals(EstadoCuenta.ACTIVA, usuario.getEstadoCuenta());
            usuario.suspender();
            assertEquals(EstadoCuenta.SUSPENDIDA, usuario.getEstadoCuenta());
            usuario.activar();
            assertEquals(EstadoCuenta.ACTIVA, usuario.getEstadoCuenta());
        }

        @Test
        @DisplayName("No se puede aprobar una cuenta que no está pendiente")
        void noSeApruebaDosVeces() {
            Usuario usuario = activo(RolUsuario.VOLUNTARIO);
            assertThrows(AccountApprovalNotAllowedException.class, usuario::aprobar);
        }

        @Test
        @DisplayName("Una cuenta pendiente no se activa: debe aprobarse")
        void pendienteNoSeActiva() {
            Usuario usuario = Usuario.create(RolUsuario.VOLUNTARIO, "Ana", Email.of("ana@refupet.co"));
            assertThrows(InvalidUserStateException.class, usuario::activar);
        }

        @Test
        @DisplayName("Solo se suspende una cuenta activa")
        void soloSeSuspendeActiva() {
            Usuario usuario = Usuario.create(RolUsuario.VOLUNTARIO, "Ana", Email.of("ana@refupet.co"));
            assertThrows(InvalidUserStateException.class, usuario::suspender);
        }

        @Test
        @DisplayName("Modificar datos valida nombre obligatorio")
        void actualizarDatos() {
            Usuario usuario = activo(RolUsuario.ADMINISTRADOR);
            usuario.actualizarDatos("Nuevo Nombre", Email.of("nuevo@refupet.co"));
            assertEquals("Nuevo Nombre", usuario.getNombre());
            assertEquals("nuevo@refupet.co", usuario.getEmail().value());
            assertThrows(InvalidDomainDataException.class,
                    () -> usuario.actualizarDatos(" ", Email.of("x@refupet.co")));
        }
    }

    @Nested
    @DisplayName("Permisos por perfil (polimorfismo)")
    class Permisos {

        @Test
        @DisplayName("Cada perfil solo puede ejecutar sus operaciones")
        void permisosPorPerfil() {
            assertTrue(activo(RolUsuario.VOLUNTARIO).puedeRealizar(Operacion.REGISTRAR_INGRESO_ANIMAL));
            assertFalse(activo(RolUsuario.VOLUNTARIO).puedeRealizar(Operacion.GESTIONAR_INFORMACION_CLINICA));
            assertTrue(activo(RolUsuario.VETERINARIO).puedeRealizar(Operacion.AUTORIZAR_SALIDA_CUARENTENA));
            assertFalse(activo(RolUsuario.VETERINARIO).puedeRealizar(Operacion.GESTIONAR_USUARIOS));
            assertTrue(activo(RolUsuario.ADMINISTRADOR).puedeRealizar(Operacion.GESTIONAR_SOLICITUDES_ADOPCION));
            assertTrue(activo(RolUsuario.SOLICITANTE).puedeRealizar(Operacion.PRESENTAR_SOLICITUD_ADOPCION));
            assertFalse(activo(RolUsuario.SOLICITANTE).puedeRealizar(Operacion.CONSULTAR_ANIMALES));
        }

        @Test
        @DisplayName("Una operación ajena al perfil lanza UnauthorizedRoleOperationException")
        void operacionAjena() {
            Usuario solicitante = activo(RolUsuario.SOLICITANTE);
            assertThrows(UnauthorizedRoleOperationException.class,
                    () -> solicitante.verificarPermiso(Operacion.GESTIONAR_USUARIOS));
        }

        @Test
        @DisplayName("Una cuenta suspendida no puede operar")
        void suspendidaNoOpera() {
            Usuario voluntario = activo(RolUsuario.VOLUNTARIO);
            voluntario.suspender();
            assertThrows(InvalidUserStateException.class,
                    () -> voluntario.verificarPermiso(Operacion.REGISTRAR_INGRESO_ANIMAL));
        }
    }

    @Test
    @DisplayName("El email se valida y se normaliza")
    void email() {
        assertEquals("ana@refupet.co", Email.of("  ANA@RefuPet.co ").value());
        assertThrows(InvalidDomainDataException.class, () -> Email.of("sin-arroba"));
    }
}
