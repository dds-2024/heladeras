package ar.edu.utn.dds.k3003.controllers;

import java.util.List;
import java.util.NoSuchElementException;

import ar.edu.utn.dds.k3003.app.Fachada;
import ar.edu.utn.dds.k3003.dtos.IncidenteDTO;
import ar.edu.utn.dds.k3003.dtos.SuscripcionDTO;
import ar.edu.utn.dds.k3003.dtos.TiempoDTO;
import ar.edu.utn.dds.k3003.facades.dtos.HeladeraDTO;
import ar.edu.utn.dds.k3003.model.Incidente;
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
            var heladeraDTO = this.fachada.buscarHeladeraXId(id);
            context.json(heladeraDTO);
        } catch (NoSuchElementException ex) {
            context.result(ex.getLocalizedMessage());
            context.status(HttpStatus.NOT_FOUND);
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
}
