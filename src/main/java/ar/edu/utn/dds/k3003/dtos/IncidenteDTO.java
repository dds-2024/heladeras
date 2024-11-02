package ar.edu.utn.dds.k3003.dtos;

import java.time.LocalDateTime;

import lombok.Getter;
import lombok.Setter;
import ar.edu.utn.dds.k3003.model.EstadoIncidente;
import ar.edu.utn.dds.k3003.model.TipoIncidente;

@Getter
@Setter
public class IncidenteDTO {
    private Integer heladeraId;
    private TipoIncidente tipo;
    private String descripcion;
    private EstadoIncidente estado;
}
