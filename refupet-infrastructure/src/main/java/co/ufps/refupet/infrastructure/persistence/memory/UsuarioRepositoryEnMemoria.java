package co.ufps.refupet.infrastructure.persistence.memory;

import co.ufps.refupet.domain.entities.Usuario;
import co.ufps.refupet.domain.enums.EstadoCuenta;
import co.ufps.refupet.domain.ids.UsuarioId;
import co.ufps.refupet.domain.repositories.IUsuarioRepository;
import co.ufps.refupet.domain.valueobjects.Email;

import java.util.List;
import java.util.Optional;

/**
 * Adaptador en memoria de IUsuarioRepository. Guarda cualquier subclase de Usuario.
 */
public class UsuarioRepositoryEnMemoria extends RepositorioEnMemoria<UsuarioId, Usuario> implements IUsuarioRepository {

    public UsuarioRepositoryEnMemoria() {
        super(Usuario::getUsuarioId);
    }

    @Override
    public Usuario save(Usuario usuario) {
        return guardar(usuario);
    }

    @Override
    public Optional<Usuario> findById(UsuarioId usuarioId) {
        return buscarPorId(usuarioId);
    }

    @Override
    public Optional<Usuario> findByEmail(Email email) {
        return filtrar(u -> u.getEmail().equals(email)).stream().findFirst();
    }

    @Override
    public boolean existsByEmail(Email email) {
        return findByEmail(email).isPresent();
    }

    @Override
    public List<Usuario> findByEstadoCuenta(EstadoCuenta estadoCuenta) {
        return filtrar(u -> u.getEstadoCuenta() == estadoCuenta);
    }
}
