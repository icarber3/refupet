package co.ufps.refupet.integracion;

import io.quarkus.test.junit.QuarkusTestProfile;

import java.util.Set;

/**
 * Perfil de prueba que selecciona los repositorios en memoria ({@link RepositoriosEnMemoriaProducer})
 * en lugar de los adaptadores JPA. Lo usan las pruebas de transacciones y de API, de modo que ninguna
 * operación llega a las tablas de la base de datos (solo el arranque valida el esquema).
 * Compartirlo evita que Quarkus se reinicie entre esas clases de prueba.
 */
public class PerfilRepositoriosEnMemoria implements QuarkusTestProfile {

    @Override
    public Set<Class<?>> getEnabledAlternatives() {
        return Set.of(RepositoriosEnMemoriaProducer.class);
    }
}
