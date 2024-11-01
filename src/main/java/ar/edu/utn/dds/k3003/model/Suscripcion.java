package ar.edu.utn.dds.k3003.model;

import javax.persistence.*;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "suscripciones")
public class Suscripcion {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    private Integer colaboradorId;
    private Integer heladeraId;
    private Integer umbralViandasMinimas;
    private Integer umbralViandasMaximas;
    private Boolean notificarDesperfecto;
}
