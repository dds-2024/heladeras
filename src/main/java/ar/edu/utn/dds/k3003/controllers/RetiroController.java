package ar.edu.utn.dds.k3003.controllers;

import java.util.NoSuchElementException;
import io.javalin.http.Context;
import io.javalin.http.HttpStatus;
import io.micrometer.core.instrument.Counter;

import ar.edu.utn.dds.k3003.app.Fachada;
import ar.edu.utn.dds.k3003.facades.dtos.RetiroDTO;

public class RetiroController {
    private final Fachada fachada;
    private final Counter aperturasHeladeras;
    private final Counter retirosRealizados;

    public RetiroController(Fachada fachada, Counter aperturasHeladeras, Counter retirosRealizados) {
        this.fachada = fachada;
        this.aperturasHeladeras = aperturasHeladeras;
        this.retirosRealizados = retirosRealizados;
    }

    public void retirar(Context context) {
        var retiroDTO = context.bodyAsClass(RetiroDTO.class);
        
        try {
            
            this.fachada.retirar(retiroDTO);
            // Incrementar el contador de aperturas de heladeras
            aperturasHeladeras.increment();
            // Incrementar el contador de retiros realizados
            retirosRealizados.increment();
            //Si no tiro ex devuelvo OK
            context.status(HttpStatus.OK);
            
        } catch (NoSuchElementException ex) {
            context.result(ex.getLocalizedMessage());
            context.status(HttpStatus.NOT_FOUND);
        }
    }
}
