package ar.edu.utn.dds.k3003.controllers;

import java.util.List;
import java.util.NoSuchElementException;

import ar.edu.utn.dds.k3003.app.Fachada;
import ar.edu.utn.dds.k3003.dtos.CapacidadDTO;
import ar.edu.utn.dds.k3003.dtos.IncidenteDTO;
import ar.edu.utn.dds.k3003.dtos.SuscripcionDTO;
import ar.edu.utn.dds.k3003.dtos.TiempoDTO;
import ar.edu.utn.dds.k3003.facades.dtos.HeladeraDTO;
import io.javalin.http.Context;
import io.javalin.http.HttpStatus;

public class HeladeraController {
    private final Fachada fachada;

    public HeladeraController(Fachada fachada) {
        this.fachada = fachada;
    }

    public void agregar(Context context) {
        var heladeraDTO = context.bodyAsClass(HeladeraDTO.class);
        var heladeraDTORta = this.fachada.agregar(heladeraDTO);
        context.json(heladeraDTORta);
        context.status(HttpStatus.CREATED);
    }

    public void obtener(Context context) {
        var id = context.pathParamAsClass("id", Integer.class).get();

        try {
            //var heladeraDTO = this.fachada.buscarHeladeraXId(id);
            var heladera = this.fachada.buscarHeladeraCompletaXId(id);
            context.json(heladera);
        } catch (NoSuchElementException ex) {
            context.result(ex.getLocalizedMessage());
            context.status(HttpStatus.NOT_FOUND);
        }
    }

    public void obtenerTodas(Context context) {
        try {
            // Obtener todas las heladeras a través de la fachada
            var heladeras = this.fachada.buscarTodasLasHeladeras();

            // Enviar la lista como respuesta en formato JSON
            context.json(heladeras);
        } catch (Exception ex) {
            // Manejo de errores generales
            context.result("Error al obtener las heladeras: " + ex.getLocalizedMessage());
            context.status(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    public void obtenerTemperaturas(Context context) {
        var id = context.pathParamAsClass("id", Integer.class).get();

        try {
            //Primero busco si existe la heladera
            var heladeraDTO = this.fachada.buscarHeladeraXId(id);

            //Si no me tiro ex, entonces busco temperaturas
            var temperaturasDTO = this.fachada.obtenerTemperaturas(heladeraDTO.getId());
            context.json(temperaturasDTO);
        } catch (NoSuchElementException ex) {
            context.result(ex.getLocalizedMessage());
            context.status(HttpStatus.NOT_FOUND);
        }
    }

    public void obtenerCantidadViandas(Context context) {
        var heladeraId = context.pathParamAsClass("id", Integer.class).get();

        try {
            var cantidadViandas = this.fachada.cantidadViandas(heladeraId);
            
            context.json(cantidadViandas);
        } catch (NoSuchElementException ex) {
            context.result(ex.getLocalizedMessage());
            context.status(HttpStatus.NOT_FOUND);
        }
    }

    public void suscribir(Context context) {
        var suscripcionDTO = context.bodyAsClass(SuscripcionDTO.class);
        try {
            var suscripcionRta = this.fachada.suscribir(suscripcionDTO);
            context.json(suscripcionRta);
            context.status(HttpStatus.CREATED);
        } catch (NoSuchElementException ex) {
            context.result(ex.getLocalizedMessage());
            context.status(HttpStatus.NOT_FOUND);
        }
    }

    public void marcarDesperfecto(Context context) {
        var heladeraId = context.pathParamAsClass("id", Integer.class).get();
        
        try {
            this.fachada.marcarDesperfecto(heladeraId);
            context.status(HttpStatus.OK);
        } catch (NoSuchElementException ex) {
            context.result(ex.getLocalizedMessage());
            context.status(HttpStatus.NOT_FOUND);
        }
    }

    public void obtenerIncidentes(Context ctx) {
        Integer heladeraId = ctx.pathParamAsClass("id", Integer.class).get();
        List<IncidenteDTO> incidentes = fachada.obtenerIncidentes(heladeraId);
        ctx.json(incidentes);
    }

    public void reportarIncidente(Context ctx) {
        IncidenteDTO incidenteDTO = ctx.bodyAsClass(IncidenteDTO.class);
        fachada.reportarIncidente(incidenteDTO);
        ctx.status(201);
    }

    public void verificarTiempoSinReportar(Context context) {
        var heladeraId = context.pathParamAsClass("id", Integer.class).get();
        var tiempoDTO = context.bodyAsClass(TiempoDTO.class);
        
        try {
            this.fachada.verificarTiempoSinReportar(heladeraId, tiempoDTO.getMinutos());
            context.status(HttpStatus.OK);
        } catch (NoSuchElementException ex) {
            context.result(ex.getLocalizedMessage());
            context.status(HttpStatus.NOT_FOUND);
        }
    }

    public void reportarFraude(Context context) {
        var heladeraId = context.pathParamAsClass("id", Integer.class).get();
        
        try {
            this.fachada.reportarFraude(heladeraId);
            context.status(HttpStatus.OK);
        } catch (NoSuchElementException ex) {
            context.result(ex.getLocalizedMessage());
            context.status(HttpStatus.NOT_FOUND);
        }
    }

    public void setCapacidad(Context context) {
        var heladeraId = context.pathParamAsClass("id", Integer.class).get();
        var capacidadDTO = context.bodyAsClass(CapacidadDTO.class);
        
        try {
            this.fachada.setCapacidad(heladeraId, capacidadDTO.getCapacidad());
            context.status(HttpStatus.OK);
        } catch (NoSuchElementException ex) {
            context.result(ex.getLocalizedMessage());
            context.status(HttpStatus.NOT_FOUND);
        } catch (IllegalArgumentException ex) {
            context.result(ex.getMessage());
            context.status(HttpStatus.BAD_REQUEST);
        }
    }

    public void setActiva(Context context) {
        var heladeraId = context.pathParamAsClass("id", Integer.class).get();
        
        try {
            this.fachada.reactivarHeladera(heladeraId);
            context.status(HttpStatus.OK);
        } catch (NoSuchElementException ex) {
            context.result(ex.getLocalizedMessage());
            context.status(HttpStatus.NOT_FOUND);
        } catch (IllegalArgumentException ex) {
            context.result(ex.getMessage());
            context.status(HttpStatus.BAD_REQUEST);
        }
    }
}
