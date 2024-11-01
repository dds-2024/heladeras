package ar.edu.utn.dds.k3003.dtos;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SuscripcionDTO {
    private Integer colaboradorId;
    private Integer heladeraId;
    private Integer umbralViandasMinimas;
    private Integer umbralViandasMaximas;
    private Boolean notificarDesperfecto;
}
