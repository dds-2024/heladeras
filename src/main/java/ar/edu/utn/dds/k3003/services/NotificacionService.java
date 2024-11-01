package ar.edu.utn.dds.k3003.services;

import ar.edu.utn.dds.k3003.model.Heladera;
import ar.edu.utn.dds.k3003.model.Suscripcion;
import ar.edu.utn.dds.k3003.repositories.SuscripcionRepository;

import java.util.List;

public class NotificacionService {
    private final SuscripcionRepository suscripcionRepository;
    
    public NotificacionService() {
        this.suscripcionRepository = new SuscripcionRepository();
    }
    
    public void verificarNotificaciones(Heladera heladera) {
        List<Suscripcion> suscripciones = suscripcionRepository.findByHeladeraId(heladera.getId());
        
        for (Suscripcion suscripcion : suscripciones) {
            // Verificar umbral mínimo
            if (suscripcion.getUmbralViandasMinimas() != null && 
                heladera.getOcupacion() <= suscripcion.getUmbralViandasMinimas()) {
                notificar(suscripcion.getColaboradorId(), 
                    "Quedan " + heladera.getOcupacion() + " viandas en la heladera " + heladera.getId());
            }
            
            // Verificar umbral máximo
            if (suscripcion.getUmbralViandasMaximas() != null && 
                heladera.getCapacidad() - heladera.getOcupacion() <= suscripcion.getUmbralViandasMaximas()) {
                notificar(suscripcion.getColaboradorId(), 
                    "Faltan " + (heladera.getCapacidad() - heladera.getOcupacion()) + 
                    " viandas para que la heladera " + heladera.getId() + " esté llena");
            }
            
            // Verificar desperfecto
            if (suscripcion.getNotificarDesperfecto() && !heladera.getActiva()) {
                notificar(suscripcion.getColaboradorId(), 
                    "La heladera " + heladera.getId() + " tiene un desperfecto");
            }
        }
    }
    
    private void notificar(Integer colaboradorId, String mensaje) {
        System.out.println("Notificando al colaborador " + colaboradorId + ": " + mensaje);
    }
} 