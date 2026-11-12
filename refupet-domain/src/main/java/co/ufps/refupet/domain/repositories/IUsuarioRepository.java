package co.ufps.refupet.domain.repositories;

import co.ufps.refupet.domain.entities.Usuario;
import co.ufps.refupet.domain.enums.EstadoCuenta;
import co.ufps.refupet.domain.ids.UsuarioId;
import co.ufps.refupet.domain.valueobjects.Email;

import java.util.List;
import java.util.Optional;

/**
 * Puerto de persistencia de usuarios (todas las subclases de Usuario).
 */
public interface IUsuarioRepository {
    Usuario save(Usuario usuario);

    Optional<Usuario> findById(UsuarioId usuarioId);

    Optional<Usuario> findByEmail(Email email);

    boolean existsByEmail(Email email);

    List<Usuario> findByEstadoCuenta(EstadoCuenta estadoCuenta);
}
