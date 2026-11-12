package co.ufps.refupet.infrastructure.persistence.memory;

import co.ufps.refupet.domain.entities.Cuarentena;
import co.ufps.refupet.domain.ids.AnimalId;
import co.ufps.refupet.domain.ids.CuarentenaId;
import co.ufps.refupet.domain.repositories.ICuarentenaRepository;

import java.util.List;
import java.util.Optional;

/**
 * Adaptador en memoria de ICuarentenaRepository.
 */
public class CuarentenaRepositoryEnMemoria extends RepositorioEnMemoria<CuarentenaId, Cuarentena>
        implements ICuarentenaRepository {

    public CuarentenaRepositoryEnMemoria() {
        super(Cuarentena::getCuarentenaId);
    }

    @Override
    public Cuarentena save(Cuarentena cuarentena) {
        return guardar(cuarentena);
    }

    @Override
    public Optional<Cuarentena> findById(CuarentenaId cuarentenaId) {
        return buscarPorId(cuarentenaId);
    }

    @Override
    public List<Cuarentena> findByAnimalId(AnimalId animalId) {
        return filtrar(c -> c.getAnimalId().equals(animalId));
    }
}
