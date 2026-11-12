package co.ufps.refupet.infrastructure.persistence.jpa.mapper;

import co.ufps.refupet.domain.entities.Administrador;
import co.ufps.refupet.domain.entities.Solicitante;
import co.ufps.refupet.domain.entities.Usuario;
import co.ufps.refupet.domain.entities.Veterinario;
import co.ufps.refupet.domain.entities.Voluntario;
import co.ufps.refupet.domain.ids.UsuarioId;
import co.ufps.refupet.domain.valueobjects.Email;
import co.ufps.refupet.infrastructure.persistence.jpa.entity.UsuarioJpa;

import java.util.Objects;

/**
 * Data Mapper entre el agregado {@link Usuario} (y sus subclases) y {@link UsuarioJpa}.
 *
 * Solo traduce datos: no contiene reglas de negocio. La reconstrucción usa las fábricas
 * {@code of(...)} del dominio, que vuelven a aplicar sus invariantes (nombre obligatorio,
 * formato de email, estado y fecha obligatorios).
 */
public final class UsuarioMapper {

    private UsuarioMapper() {
    }

    /** Dominio → persistencia. Teléfono y dirección solo existen para el Solicitante. */
    public static UsuarioJpa toJpa(Usuario usuario) {
        Objects.requireNonNull(usuario, "usuario");
        UsuarioJpa jpa = new UsuarioJpa();
        jpa.setId(usuario.getUsuarioId().value());
        jpa.setRol(usuario.getRol());
        jpa.setNombre(usuario.getNombre());
        jpa.setEmail(usuario.getEmail().value());
        jpa.setEstadoCuenta(usuario.getEstadoCuenta());
        jpa.setFechaRegistro(usuario.getFechaRegistro());
        if (usuario instanceof Solicitante solicitante) {
            jpa.setTelefono(solicitante.getTelefono());
            jpa.setDireccion(solicitante.getDireccion());
        }
        return jpa;
    }

    /** Persistencia → dominio: la columna {@code rol} decide qué subclase de Usuario se reconstruye. */
    public static Usuario toDomain(UsuarioJpa jpa) {
        Objects.requireNonNull(jpa, "usuarioJpa");
        UsuarioId id = UsuarioId.of(jpa.getId());
        Email email = Email.of(jpa.getEmail());
        return switch (Objects.requireNonNull(jpa.getRol(), "rol")) {
            case ADMINISTRADOR -> Administrador.of(id, jpa.getNombre(), email,
                    jpa.getEstadoCuenta(), jpa.getFechaRegistro());
            case VETERINARIO -> Veterinario.of(id, jpa.getNombre(), email,
                    jpa.getEstadoCuenta(), jpa.getFechaRegistro());
            case VOLUNTARIO -> Voluntario.of(id, jpa.getNombre(), email,
                    jpa.getEstadoCuenta(), jpa.getFechaRegistro());
            case SOLICITANTE -> Solicitante.of(id, jpa.getNombre(), email, jpa.getTelefono(),
                    jpa.getDireccion(), jpa.getEstadoCuenta(), jpa.getFechaRegistro());
        };
    }
}
