package ar.edu.utn.dds.k3003.dtos;

import ar.edu.utn.dds.k3003.facades.dtos.EstadoViandaEnum;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class EstadoViandaRequest {
    private EstadoViandaEnum estado;

    public EstadoViandaRequest(EstadoViandaEnum estado) {
        this.estado = estado;
    }
}
