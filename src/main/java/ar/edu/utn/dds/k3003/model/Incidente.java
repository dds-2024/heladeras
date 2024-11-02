package ar.edu.utn.dds.k3003.model;

import java.time.LocalDateTime;

import javax.persistence.*;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "incidentes")
public class Incidente {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    private Integer heladeraId;
    
    @Enumerated(EnumType.STRING)
    private TipoIncidente tipo; 
    
    @Enumerated(EnumType.STRING)
    private EstadoIncidente estado;
    private LocalDateTime fecha;
    private String descripcion;
} 