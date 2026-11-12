package co.ufps.refupet.domain;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Regla de arquitectura: el dominio solo usa Java estándar. Ningún fuente de src/main/java puede importar
 * JPA, Hibernate, Quarkus, CDI, Jakarta REST/Transactions ni las capas externas de RefuPet.
 */
class ArquitecturaDominioTest {

    private static final List<String> PROHIBIDOS = List.of(
            "import jakarta.", "import javax.", "import io.quarkus", "import org.hibernate",
            "import co.ufps.refupet.application", "import co.ufps.refupet.infrastructure", "import co.ufps.refupet.api");

    @Test
    void elDominioNoDependeDeFrameworksNiDeOtrasCapas() throws IOException {
        Path fuentes = Path.of("src", "main", "java");
        assertTrue(Files.isDirectory(fuentes), "se ejecuta desde el directorio del módulo");
        List<String> violaciones;
        try (Stream<Path> archivos = Files.walk(fuentes)) {
            violaciones = archivos.filter(p -> p.toString().endsWith(".java"))
                    .flatMap(p -> lineas(p).stream()
                            .filter(l -> PROHIBIDOS.stream().anyMatch(l.strip()::startsWith))
                            .map(l -> p.getFileName() + ": " + l.strip()))
                    .toList();
        }
        assertEquals(List.of(), violaciones);
    }

    private static List<String> lineas(Path archivo) {
        try {
            return Files.readAllLines(archivo);
        } catch (IOException e) {
            throw new IllegalStateException(e);
        }
    }
}
