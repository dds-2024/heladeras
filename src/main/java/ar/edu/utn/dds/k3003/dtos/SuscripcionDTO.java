package ar.edu.utn.dds.k3003.dtos;

import ar.edu.utn.dds.k3003.model.TipoSuscripcion;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SuscripcionDTO {
    private Integer colaboradorId;
    private Integer heladeraId;
    private TipoSuscripcion tipoSuscripcion;
    private Integer cantidadViandas;
    private boolean enviada;
}
