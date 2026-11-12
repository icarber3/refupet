package co.ufps.refupet.integracion;

import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import jakarta.transaction.Status;
import jakarta.transaction.Synchronization;
import jakarta.transaction.TransactionSynchronizationRegistry;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Utilidad de prueba: registra, para cada operación de repositorio, en qué transacción JTA se ejecutó
 * y cómo terminó esa transacción (confirmada o revertida). También permite forzar un fallo en una
 * operación concreta para comprobar la reversión del caso de uso completo.
 */
@Singleton
public class RegistroTransacciones {

    /** Una operación de repositorio y la transacción activa en ese momento (null si no había). */
    public record Operacion(String nombre, Object transaccion) {
    }

    @Inject
    TransactionSynchronizationRegistry registro;

    private final List<Operacion> operaciones = new ArrayList<>();
    private final Map<Object, Integer> estadoFinal = new ConcurrentHashMap<>();
    private volatile String operacionQueFalla;

    public synchronized void anotar(String nombre) {
        Object transaccion = registro.getTransactionKey();
        operaciones.add(new Operacion(nombre, transaccion));
        if (transaccion != null && !estadoFinal.containsKey(transaccion)) {
            estadoFinal.put(transaccion, Status.STATUS_UNKNOWN);
            registro.registerInterposedSynchronization(new Synchronization() {
                @Override
                public void beforeCompletion() {
                }

                @Override
                public void afterCompletion(int status) {
                    estadoFinal.put(transaccion, status);
                }
            });
        }
        if (nombre.equals(operacionQueFalla)) {
            throw new IllegalStateException("Fallo simulado en " + nombre);
        }
        if (nombre.equals(operacionConViolacionDeIntegridad)) {
            // Misma forma que una restricción única de MariaDB vista a través de JPA/Hibernate
            throw new jakarta.persistence.PersistenceException("could not execute statement",
                    new java.sql.SQLIntegrityConstraintViolationException(
                            "Duplicate entry 'x' for key 'uq_cuarentena_activa_por_animal'", "23000", 1062));
        }
    }

    private volatile String operacionConViolacionDeIntegridad;

    /** Simula que la base rechaza la operación por una restricción de integridad (SQLState 23000). */
    public void violarIntegridadEn(String nombre) {
        this.operacionConViolacionDeIntegridad = nombre;
    }

    public synchronized List<Operacion> operaciones() {
        return List.copyOf(operaciones);
    }

    public Integer estadoFinal(Object transaccion) {
        return estadoFinal.get(transaccion);
    }

    public void fallarEn(String nombre) {
        this.operacionQueFalla = nombre;
    }

    public synchronized void reiniciar() {
        operaciones.clear();
        operacionQueFalla = null;
        operacionConViolacionDeIntegridad = null;
    }
}
