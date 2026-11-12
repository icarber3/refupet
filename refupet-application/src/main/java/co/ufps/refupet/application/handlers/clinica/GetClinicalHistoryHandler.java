package co.ufps.refupet.application.handlers.clinica;

import co.ufps.refupet.application.commands.seguridad.ValidateRolePermissionCommand;
import co.ufps.refupet.application.dto.HistorialClinicoDTO;
import co.ufps.refupet.application.dto.RegistroClinicoDTO;
import co.ufps.refupet.application.handlers.seguridad.ValidateRolePermissionHandler;
import co.ufps.refupet.application.queries.clinica.GetClinicalHistoryQuery;
import co.ufps.refupet.domain.entities.Animal;
import co.ufps.refupet.domain.entities.RegistroClinico;
import co.ufps.refupet.domain.enums.Operacion;
import co.ufps.refupet.domain.exceptions.AnimalNotFoundException;
import co.ufps.refupet.domain.ids.AnimalId;
import co.ufps.refupet.domain.repositories.IAnimalRepository;
import co.ufps.refupet.domain.repositories.IDesparasitacionRepository;
import co.ufps.refupet.domain.repositories.IEsterilizacionRepository;
import co.ufps.refupet.domain.repositories.IEvaluacionClinicaRepository;
import co.ufps.refupet.domain.repositories.ITratamientoRepository;
import co.ufps.refupet.domain.repositories.IVacunacionRepository;
import co.ufps.refupet.domain.valueobjects.EstadoSalud;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * Regla 10: consultar el historial clínico de un animal.
 *
 * Polimorfismo: todos los registros (evaluaciones, vacunas, desparasitaciones, tratamientos
 * y esterilizaciones) se tratan como RegistroClinico, se ordenan por fecha y se convierten
 * con el mismo RegistroClinicoDTO.from(...).
 */
@ApplicationScoped
public class GetClinicalHistoryHandler {

    private final IAnimalRepository animalRepository;
    private final IEvaluacionClinicaRepository evaluacionRepository;
    private final IVacunacionRepository vacunacionRepository;
    private final IDesparasitacionRepository desparasitacionRepository;
    private final ITratamientoRepository tratamientoRepository;
    private final IEsterilizacionRepository esterilizacionRepository;
    private final ValidateRolePermissionHandler permisos;

    @Inject
    public GetClinicalHistoryHandler(IAnimalRepository animalRepository,
                                     IEvaluacionClinicaRepository evaluacionRepository,
                                     IVacunacionRepository vacunacionRepository,
                                     IDesparasitacionRepository desparasitacionRepository,
                                     ITratamientoRepository tratamientoRepository,
                                     IEsterilizacionRepository esterilizacionRepository,
                                     ValidateRolePermissionHandler permisos) {
        this.animalRepository = animalRepository;
        this.evaluacionRepository = evaluacionRepository;
        this.vacunacionRepository = vacunacionRepository;
        this.desparasitacionRepository = desparasitacionRepository;
        this.tratamientoRepository = tratamientoRepository;
        this.esterilizacionRepository = esterilizacionRepository;
        this.permisos = permisos;
    }

    public HistorialClinicoDTO execute(GetClinicalHistoryQuery query) {
        permisos.execute(new ValidateRolePermissionCommand(query.actorId(), Operacion.CONSULTAR_HISTORIAL_CLINICO));

        AnimalId animalId = query.toAnimalId();
        Animal animal = animalRepository.findById(animalId)
                .orElseThrow(() -> new AnimalNotFoundException(animalId));

        List<RegistroClinico> registros = new ArrayList<>();
        registros.addAll(evaluacionRepository.findByAnimalId(animalId));
        registros.addAll(vacunacionRepository.findByAnimalId(animalId));
        registros.addAll(desparasitacionRepository.findByAnimalId(animalId));
        registros.addAll(tratamientoRepository.findByAnimalId(animalId));
        registros.addAll(esterilizacionRepository.findByAnimalId(animalId));

        List<RegistroClinicoDTO> historial = registros.stream()
                .sorted(Comparator.comparing(RegistroClinico::getFecha))
                .map(RegistroClinicoDTO::from)
                .toList();

        return new HistorialClinicoDTO(
                animal.getAnimalId().value(),
                animal.getNombre(),
                animal.getEstadoSalud().map(EstadoSalud::descripcion).orElse(null),
                animal.isEsterilizado(),
                historial);
    }
}
