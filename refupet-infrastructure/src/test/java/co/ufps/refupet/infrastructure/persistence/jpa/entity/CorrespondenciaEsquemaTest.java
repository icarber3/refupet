package co.ufps.refupet.infrastructure.persistence.jpa.entity;

import jakarta.persistence.Column;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Table;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Contrasta, sin base de datos, el mapeo JPA con el esquema existente tal como se leyó de
 * information_schema.COLUMNS. Complementa a {@code generation=validate}, que comprueba tipos de las
 * columnas mapeadas pero NO detecta columnas del esquema que se hayan olvidado en el mapeo.
 *
 * Verifica, por tabla: (1) se mapean exactamente las columnas no generadas; (2) las columnas STORED
 * GENERATED no se mapean (nunca deben escribirse); (3) las columnas CHAR(36), TEXT y ENUM declaran su
 * tipo con columnDefinition (sin ello validate falla: comprobado con un control negativo en CHAR(36)).
 */
class CorrespondenciaEsquemaTest {

    /** Tipo relevante para el mapeo: C = CHAR(36), T = TEXT, E = ENUM, O = otro (varchar, date, datetime, int, tinyint). */
    private static Map<String, String> columnas(String... nombreTipo) {
        Map<String, String> mapa = new LinkedHashMap<>();
        for (int i = 0; i < nombreTipo.length; i += 2) {
            mapa.put(nombreTipo[i], nombreTipo[i + 1]);
        }
        return mapa;
    }

    private static final Map<String, Map<String, String>> ESQUEMA = new TreeMap<>(Map.ofEntries(
            Map.entry("usuario", columnas("id", "C", "rol", "E", "nombre", "O", "email", "O", "estado_cuenta", "E",
                    "fecha_registro", "O", "telefono", "O", "direccion", "O")),
            Map.entry("animal", columnas("id", "C", "nombre", "O", "especie", "E", "raza", "O", "descripcion", "T",
                    "fecha_ingreso", "O", "ingreso_registrado_por", "C", "procedencia", "O", "condicion_inicial", "T",
                    "ubicacion", "E", "estado_salud_descripcion", "T", "estado_salud_fecha", "O",
                    "estado_salud_actualizado_por", "C", "esterilizado", "O")),
            Map.entry("observacion_comportamiento", columnas("id", "C", "animal_id", "C", "descripcion", "T",
                    "registrado_por", "C", "fecha_registro", "O")),
            Map.entry("evaluacion_clinica", columnas("id", "C", "animal_id", "C", "veterinario_id", "C", "fecha", "O",
                    "hallazgos", "T", "diagnostico", "T", "observaciones", "T")),
            Map.entry("vacunacion", columnas("id", "C", "animal_id", "C", "veterinario_id", "C", "fecha", "O",
                    "vacuna", "O", "observaciones", "T")),
            Map.entry("desparasitacion", columnas("id", "C", "animal_id", "C", "veterinario_id", "C", "fecha", "O",
                    "producto", "O", "observaciones", "T")),
            Map.entry("tratamiento", columnas("id", "C", "animal_id", "C", "veterinario_id", "C", "fecha", "O",
                    "descripcion", "T", "indicaciones", "T", "observaciones", "T")),
            Map.entry("esterilizacion", columnas("id", "C", "animal_id", "C", "veterinario_id", "C", "fecha", "O",
                    "observaciones", "T")),
            Map.entry("cuarentena", columnas("id", "C", "animal_id", "C", "fecha_inicio", "O", "dias_minimos", "O",
                    "iniciada_por", "C", "estado", "E", "salida_autorizada_por", "C", "fecha_autorizacion_salida", "O",
                    "fecha_finalizacion", "O")),
            Map.entry("solicitud_adopcion", columnas("id", "C", "animal_id", "C", "solicitante_id", "C", "comentarios", "T",
                    "fecha_solicitud", "O", "estado", "E", "compatibilidad_compatible", "O", "compatibilidad_observaciones", "T",
                    "compatibilidad_evaluado_por", "C", "compatibilidad_fecha", "O", "decidida_por", "C",
                    "fecha_decision", "O", "motivo_rechazo", "T")),
            Map.entry("adopcion", columnas("id", "C", "solicitud_id", "C", "animal_id", "C", "solicitante_id", "C",
                    "fecha_formalizacion", "O", "formalizada_por", "C", "estado", "E", "devolucion_fecha", "O",
                    "devolucion_motivo", "T", "devolucion_registrada_por", "C")),
            Map.entry("seguimiento_adopcion", columnas("id", "C", "adopcion_id", "C", "fecha", "O", "observaciones", "T",
                    "registrado_por", "C"))));

    /** Columnas STORED GENERATED del esquema: no deben aparecer en ningún mapeo. */
    private static final Set<String> GENERADAS = Set.of("animal_id_vigente", "animal_id_activa", "animal_id_pendiente");

    private static final List<Class<?>> ENTIDADES = List.of(UsuarioJpa.class, AnimalJpa.class,
            ObservacionComportamientoJpa.class, EvaluacionClinicaJpa.class, VacunacionJpa.class, DesparasitacionJpa.class,
            TratamientoJpa.class, EsterilizacionJpa.class, CuarentenaJpa.class, SolicitudAdopcionJpa.class,
            AdopcionJpa.class, SeguimientoAdopcionJpa.class);

    /** Columnas mapeadas (nombre → columnDefinition, "" si no tiene), incluidas las heredadas y las de @JoinColumn. */
    private static Map<String, String> mapeadas(Class<?> entidad) {
        Map<String, String> resultado = new TreeMap<>();
        for (Class<?> c = entidad; c != null && c != Object.class; c = c.getSuperclass()) {
            for (Field campo : c.getDeclaredFields()) {
                Column columna = campo.getAnnotation(Column.class);
                JoinColumn union = campo.getAnnotation(JoinColumn.class);
                if (columna != null) {
                    assertNull(resultado.put(columna.name(), columna.columnDefinition()), "columna repetida " + columna.name());
                } else if (union != null) {
                    assertNull(resultado.put(union.name(), union.columnDefinition()), "columna repetida " + union.name());
                }
            }
        }
        return resultado;
    }

    @Test
    void cadaTablaDelEsquemaTieneExactamenteUnaEntidad() {
        Set<String> tablas = new TreeSet<>();
        for (Class<?> entidad : ENTIDADES) {
            assertTrue(tablas.add(entidad.getAnnotation(Table.class).name()), "tabla repetida en " + entidad.getSimpleName());
        }
        assertEquals(ESQUEMA.keySet(), tablas);
    }

    @Test
    void seMapeanTodasLasColumnasNoGeneradasYNingunaGenerada() {
        for (Class<?> entidad : ENTIDADES) {
            String tabla = entidad.getAnnotation(Table.class).name();
            Set<String> mapeadas = mapeadas(entidad).keySet();
            assertEquals(new TreeSet<>(ESQUEMA.get(tabla).keySet()), mapeadas, "columnas de " + tabla);
            for (String generada : GENERADAS) {
                assertFalse(mapeadas.contains(generada), generada + " no debe mapearse en " + tabla);
            }
        }
    }

    @Test
    void char36TextYEnumDeclaranSuTipo() {
        for (Class<?> entidad : ENTIDADES) {
            String tabla = entidad.getAnnotation(Table.class).name();
            Map<String, String> definiciones = mapeadas(entidad);
            ESQUEMA.get(tabla).forEach((columna, tipo) -> {
                String definicion = definiciones.get(columna).toUpperCase();
                switch (tipo) {
                    case "C" -> assertEquals("CHAR(36)", definicion, tabla + "." + columna);
                    case "T" -> assertEquals("TEXT", definicion, tabla + "." + columna);
                    case "E" -> assertTrue(definicion.startsWith("ENUM("), tabla + "." + columna);
                    default -> assertEquals("", definicion, tabla + "." + columna);
                }
            });
        }
    }
}
