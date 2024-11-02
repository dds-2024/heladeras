package ar.edu.utn.dds.k3003.services;

import ar.edu.utn.dds.k3003.clients.ColaboradoresProxy;
import ar.edu.utn.dds.k3003.dtos.SuscripcionDTO;
import ar.edu.utn.dds.k3003.model.Heladera;
import ar.edu.utn.dds.k3003.model.Suscripcion;
import ar.edu.utn.dds.k3003.repositories.SuscripcionRepository;

import java.util.List;

public class NotificacionService {
    private final SuscripcionRepository suscripcionRepository;
    private final ColaboradoresProxy colaboradoresProxy;
    
    public NotificacionService(ColaboradoresProxy colaboradoresProxy) {
        this.suscripcionRepository = new SuscripcionRepository();
        this.colaboradoresProxy = colaboradoresProxy;
    }
    
    public void verificarNotificaciones(Heladera heladera) {
        List<Suscripcion> suscripciones = suscripcionRepository.findByHeladeraId(heladera.getId());
        
        for (Suscripcion suscripcion : suscripciones) {
            switch (suscripcion.getTipoSuscripcion()) {
                case VIANDAS_MINIMAS:
                    if (heladera.getOcupacion() <= suscripcion.getCantidadViandas()) {
                        notificar(suscripcion);
                    }
                    break;
                    
                case VIANDAS_MAXIMAS:
                    if (heladera.getCapacidad() - heladera.getOcupacion() <= suscripcion.getCantidadViandas()) {
                        notificar(suscripcion);
                    }
                    break;
                    
                case DESPERFECTO:
                    if (!heladera.getActiva()) {
                        notificar(suscripcion);
                    }
                    break;
            }
        }
    }
    
    private void notificar(Suscripcion suscripcion) {
        // Convertir a DTO
        SuscripcionDTO dto = new SuscripcionDTO();
        dto.setColaboradorId(suscripcion.getColaboradorId());
        dto.setHeladeraId(suscripcion.getHeladeraId());
        dto.setTipoSuscripcion(suscripcion.getTipoSuscripcion());
        dto.setCantidadViandas(suscripcion.getCantidadViandas());
        
        // Enviar notificación a colaboradores
        try {
            colaboradoresProxy.gestionarIncidente(dto);
        } catch (Exception e) {
            // Log error pero no interrumpir el flujo
            System.err.println("Error al notificar al colaborador: " + e.getMessage() + 
                             "\nDatos de la suscripción:" +
                             "\n- Colaborador ID: " + dto.getColaboradorId() +
                             "\n- Heladera ID: " + dto.getHeladeraId() +
                             "\n- Tipo Suscripción: " + dto.getTipoSuscripcion() +
                             "\n- Cantidad Viandas: " + dto.getCantidadViandas());
        }
    }
} 