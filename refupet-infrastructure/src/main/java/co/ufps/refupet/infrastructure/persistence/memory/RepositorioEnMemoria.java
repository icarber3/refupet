package co.ufps.refupet.infrastructure.persistence.memory;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.Predicate;

/**
 * Almacenamiento temporal en memoria basado en colecciones de Java (regla 40).
 *
 * Se usa un LinkedHashMap indexado por el Id del agregado: guardar con el mismo Id
 * reemplaza (actualiza) y conserva el orden de inserción para las consultas.
 * Esta clase solo guarda y filtra datos; NO contiene reglas de negocio.
 *
 * Decisión: se almacenan directamente los objetos de dominio, sin modelos ni mapeadores
 * intermedios, porque no hay diferencias entre el dominio y la estructura de almacenamiento
 * (a diferencia de Streamflix, donde JPA obliga a tener entidades *Jpa y mapeo).
 *
 * @param <ID> tipo del identificador (Value Object)
 * @param <E>  tipo del agregado almacenado
 */
public abstract class RepositorioEnMemoria<ID, E> {

    private final Map<ID, E> almacen = new LinkedHashMap<>();
    private final Function<E, ID> extractorId;

    protected RepositorioEnMemoria(Function<E, ID> extractorId) {
        this.extractorId = Objects.requireNonNull(extractorId);
    }

    protected E guardar(E entidad) {
        Objects.requireNonNull(entidad, "No se puede guardar un valor nulo");
        almacen.put(extractorId.apply(entidad), entidad);
        return entidad;
    }

    protected Optional<E> buscarPorId(ID id) {
        return Optional.ofNullable(almacen.get(id));
    }

    protected boolean existe(ID id) {
        return almacen.containsKey(id);
    }

    protected List<E> todos() {
        return List.copyOf(almacen.values());
    }

    protected List<E> filtrar(Predicate<E> criterio) {
        return almacen.values().stream().filter(criterio).toList();
    }
}
