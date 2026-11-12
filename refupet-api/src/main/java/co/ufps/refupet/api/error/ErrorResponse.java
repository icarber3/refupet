package co.ufps.refupet.api.error;

/**
 * Cuerpo JSON de toda respuesta de error de la API.
 *
 * @param status  código HTTP
 * @param error   categoría estable del error (para clientes): ENTRADA_INVALIDA, PERMISO_DENEGADO,
 *                NO_ENCONTRADO, CONFLICTO, ERROR_INTERNO u otra derivada del estado HTTP
 * @param mensaje descripción legible; nunca contiene detalles internos (SQL, nombres de restricciones, trazas)
 */
public record ErrorResponse(int status, String error, String mensaje) {
}
