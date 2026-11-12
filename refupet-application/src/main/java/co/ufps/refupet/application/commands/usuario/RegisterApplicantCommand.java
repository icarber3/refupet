package co.ufps.refupet.application.commands.usuario;

import co.ufps.refupet.application.Entrada;
import co.ufps.refupet.domain.valueobjects.Email;

/**
 * Regla 18: registro de un solicitante de adopción. No requiere un usuario previo:
 * la cuenta queda pendiente hasta que un administrador la apruebe.
 */
public record RegisterApplicantCommand(
        String nombre,
        String email,
        String telefono,
        String direccion
) {
    public RegisterApplicantCommand {
        Entrada.requerido(nombre, "nombre");
        Entrada.requerido(email, "email");
    }

    public Email toEmail() { return Email.of(email); }
}
