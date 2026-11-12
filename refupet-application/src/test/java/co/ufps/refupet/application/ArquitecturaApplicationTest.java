package co.ufps.refupet.application;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Regla de arquitectura: Application depende del dominio y, como única excepción, de las anotaciones
 * estándar de Jakarta CDI ({@code jakarta.enterprise.context}, {@code jakarta.inject}) y Transactions
 * ({@code jakarta.transaction}). No puede usar Quarkus, JPA/Hibernate, Jakarta REST ni las capas externas.
 */
class ArquitecturaApplicationTest {

    private static final List<String> PERMITIDOS_JAKARTA = List.of(
            "import jakarta.enterprise.context.", "import jakarta.inject.", "import jakarta.transaction.");

    private static final List<String> PROHIBIDOS = List.of(
            "import io.quarkus", "import org.hibernate", "import javax.",
            "import co.ufps.refupet.infrastructure", "import co.ufps.refupet.api");

    @Test
    void applicationSoloUsaElDominioYAnotacionesEstandarDeJakarta() throws IOException {
        Path fuentes = Path.of("src", "main", "java");
        assertTrue(Files.isDirectory(fuentes), "se ejecuta desde el directorio del módulo");
        List<String> violaciones;
        try (Stream<Path> archivos = Files.walk(fuentes)) {
            violaciones = archivos.filter(p -> p.toString().endsWith(".java"))
                    .flatMap(p -> lineas(p).stream()
                            .map(String::strip)
                            .filter(ArquitecturaApplicationTest::prohibida)
                            .map(l -> p.getFileName() + ": " + l))
                    .toList();
        }
        assertEquals(List.of(), violaciones);
    }

    private static boolean prohibida(String linea) {
        if (linea.startsWith("import jakarta.")) {
            return PERMITIDOS_JAKARTA.stream().noneMatch(linea::startsWith);
        }
        return PROHIBIDOS.stream().anyMatch(linea::startsWith);
    }

    private static List<String> lineas(Path archivo) {
        try {
            return Files.readAllLines(archivo);
        } catch (IOException e) {
            throw new IllegalStateException(e);
        }
    }
}
