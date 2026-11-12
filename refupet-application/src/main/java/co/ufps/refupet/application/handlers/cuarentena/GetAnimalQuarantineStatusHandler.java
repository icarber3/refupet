package co.ufps.refupet.application.handlers.cuarentena;

import co.ufps.refupet.application.commands.seguridad.ValidateRolePermissionCommand;
import co.ufps.refupet.application.dto.CuarentenaDTO;
import co.ufps.refupet.application.handlers.seguridad.ValidateRolePermissionHandler;
import co.ufps.refupet.application.queries.cuarentena.GetAnimalQuarantineStatusQuery;
import co.ufps.refupet.application.services.CuarentenaService;
import co.ufps.refupet.domain.entities.Cuarentena;
import co.ufps.refupet.domain.enums.Operacion;
import co.ufps.refupet.domain.exceptions.AnimalNotFoundException;
import co.ufps.refupet.domain.ids.AnimalId;
import co.ufps.refupet.domain.repositories.IAnimalRepository;
import co.ufps.refupet.domain.repositories.ICuarentenaRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import java.util.Comparator;
import java.util.Optional;

/**
 * Devuelve la cuarentena activa del animal o, si no tiene, la más reciente.
 * Vacío si el animal nunca estuvo en cuarentena.
 */
@ApplicationScoped
public class GetAnimalQuarantineStatusHandler {

    private final IAnimalRepository animalRepository;
    private final ICuarentenaRepository cuarentenaRepository;
    private final CuarentenaService cuarentenas;
    private final ValidateRolePermissionHandler permisos;

    @Inject
    public GetAnimalQuarantineStatusHandler(IAnimalRepository animalRepository,
                                            ICuarentenaRepository cuarentenaRepository,
                                            CuarentenaService cuarentenas, ValidateRolePermissionHandler permisos) {
        this.animalRepository = animalRepository;
        this.cuarentenaRepository = cuarentenaRepository;
        this.cuarentenas = cuarentenas;
        this.permisos = permisos;
    }

    public Optional<CuarentenaDTO> execute(GetAnimalQuarantineStatusQuery query) {
        permisos.execute(new ValidateRolePermissionCommand(query.actorId(), Operacion.CONSULTAR_CUARENTENAS));
        AnimalId animalId = query.toAnimalId();
        if (!animalRepository.existsById(animalId)) {
            throw new AnimalNotFoundException(animalId);
        }
        Optional<Cuarentena> cuarentena = cuarentenas.activaDe(animalId)
                .or(() -> cuarentenaRepository.findByAnimalId(animalId).stream()
                        .max(Comparator.comparing(c -> c.getPeriodo().fechaInicio())));
        return cuarentena.map(cuarentenas::toDTO);
    }
}
