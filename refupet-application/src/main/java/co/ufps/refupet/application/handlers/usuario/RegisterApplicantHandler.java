package co.ufps.refupet.application.handlers.usuario;

import co.ufps.refupet.application.commands.usuario.RegisterApplicantCommand;
import co.ufps.refupet.application.dto.SolicitanteDTO;
import co.ufps.refupet.domain.entities.Solicitante;
import co.ufps.refupet.domain.exceptions.EmailAlreadyRegisteredException;
import co.ufps.refupet.domain.repositories.IUsuarioRepository;
import co.ufps.refupet.domain.valueobjects.Email;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;

/**
 * Regla 18: registrar a un solicitante de adopción (queda pendiente de aprobación).
 */
@ApplicationScoped
public class RegisterApplicantHandler {

    private final IUsuarioRepository usuarioRepository;

    @Inject
    public RegisterApplicantHandler(IUsuarioRepository usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
    }

    @Transactional
    public SolicitanteDTO execute(RegisterApplicantCommand command) {
        Email email = command.toEmail();
        if (usuarioRepository.existsByEmail(email)) {
            throw new EmailAlreadyRegisteredException("Ya existe un usuario registrado con el email " + email.value());
        }
        Solicitante solicitante = Solicitante.create(
                command.nombre(), email, command.telefono(), command.direccion());
        usuarioRepository.save(solicitante);
        return SolicitanteDTO.from(solicitante);
    }
}
