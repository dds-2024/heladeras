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
    
    @Enumerated(EnumType.STRING)
    private TipoSuscripcion tipoSuscripcion;
    
    private Integer cantidadViandas; // umbral de viandas según el tipo de suscripción
    private boolean enviada = false;
}
