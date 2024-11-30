package ar.edu.utn.dds.k3003.controllers;

import ar.edu.utn.dds.k3003.repositories.HeladeraRepository;
import ar.edu.utn.dds.k3003.repositories.IncidenteRepository;
import ar.edu.utn.dds.k3003.repositories.OperacionRepository;
import ar.edu.utn.dds.k3003.repositories.SuscripcionRepository;
import ar.edu.utn.dds.k3003.repositories.TemperaturaRepository;
import io.javalin.http.Context;
import io.javalin.http.HttpStatus;

public class CleanupController {

    private final HeladeraRepository heladeraRepository;
    private final OperacionRepository operacionRepository;
    private final TemperaturaRepository temperaturaRepository;
    private final IncidenteRepository incidenteRepository;
    private final SuscripcionRepository suscripcionRepository;
    
    public CleanupController() {
        this.heladeraRepository = new HeladeraRepository();
        this.operacionRepository = new OperacionRepository();
        this.temperaturaRepository = new TemperaturaRepository();
        this.incidenteRepository = new IncidenteRepository();
        this.suscripcionRepository = new SuscripcionRepository();
    }

    public void cleanup(Context context) {
        heladeraRepository.deleteAll();
        operacionRepository.deleteAll();
        temperaturaRepository.deleteAll();
        incidenteRepository.deleteAll();
        suscripcionRepository.deleteAll();
        context.status(HttpStatus.OK);
    }
}
