package co.ufps.refupet.domain.enums;

/**
 * Operaciones del sistema sujetas a control por perfil (regla 39:
 * "los perfiles solo deben ejecutar las operaciones que les corresponden").
 * Cada subclase de Usuario declara cuáles puede ejecutar.
 */
public enum Operacion {
    REGISTRAR_INGRESO_ANIMAL("registrar el ingreso de animales"),
    REGISTRAR_OBSERVACION_COMPORTAMIENTO("registrar observaciones de comportamiento"),
    CONSULTAR_ANIMALES("consultar los animales registrados"),
    CONSULTAR_ANIMALES_DISPONIBLES("consultar animales disponibles para adopción"),
    GESTIONAR_INFORMACION_CLINICA("gestionar información clínica, tratamientos, vacunaciones y esterilizaciones"),
    CONSULTAR_HISTORIAL_CLINICO("consultar el historial clínico"),
    GESTIONAR_CUARENTENA("registrar inicio de cuarentena y paso a población general"),
    AUTORIZAR_SALIDA_CUARENTENA("autorizar la salida de cuarentena"),
    CONSULTAR_CUARENTENAS("consultar cuarentenas"),
    PRESENTAR_SOLICITUD_ADOPCION("presentar solicitudes de adopción"),
    GESTIONAR_SOLICITUDES_ADOPCION("consultar, evaluar, aprobar o rechazar solicitudes de adopción"),
    GESTIONAR_ADOPCIONES("formalizar adopciones, registrar seguimientos y devoluciones"),
    GESTIONAR_USUARIOS("gestionar usuarios");

    private final String descripcion;

    Operacion(String descripcion) {
        this.descripcion = descripcion;
    }

    public String getDescripcion() {
        return descripcion;
    }
}
