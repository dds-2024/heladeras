package ar.edu.utn.dds.k3003.controllers;

import java.util.NoSuchElementException;
import io.javalin.http.Context;
import io.javalin.http.HttpStatus;
import ar.edu.utn.dds.k3003.app.Fachada;
import ar.edu.utn.dds.k3003.dtos.DepositoDTO;
import io.micrometer.core.instrument.Counter;

public class DepositoController {
    private final Fachada fachada;
    private final Counter aperturasHeladeras;
    private final Counter depositosRealizados;

    public DepositoController(Fachada fachada, Counter aperturasHeladeras, Counter depositosRealizados) {
        this.fachada = fachada;
        this.aperturasHeladeras = aperturasHeladeras;
        this.depositosRealizados = depositosRealizados;
    }

    public void depositar(Context context) {
        var depositoDTO = context.bodyAsClass(DepositoDTO.class);

        try {
            this.fachada.depositar(depositoDTO.getHeladeraId(), depositoDTO.getQrVianda());
            //Incrementar el contador de aperturas de heladeras
            aperturasHeladeras.increment();
            // Incrementar el contador de depósitos realizados
            depositosRealizados.increment();
            //Si no tiro ex devuelvo OK
            context.status(HttpStatus.OK);
        } catch (NoSuchElementException ex) {
            context.result(ex.getLocalizedMessage());
            context.status(HttpStatus.NOT_FOUND);
        }

        
        

    }
}
