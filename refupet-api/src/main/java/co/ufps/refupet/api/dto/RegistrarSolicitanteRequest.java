package co.ufps.refupet.api.dto;

import co.ufps.refupet.application.commands.usuario.RegisterApplicantCommand;

/** Cuerpo JSON de {@code POST /api/solicitantes}. La validación de los campos la hacen el Command y el dominio. */
public record RegistrarSolicitanteRequest(String nombre, String email, String telefono, String direccion) {

    public RegisterApplicantCommand toCommand() {
        return new RegisterApplicantCommand(nombre, email, telefono, direccion);
    }
}
