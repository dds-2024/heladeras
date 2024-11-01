package ar.edu.utn.dds.k3003.model;

/**
 * Enum que representa los diferentes tipos de notificaciones que pueden recibir los colaboradores
 * sobre el estado de las heladeras.
 */
public enum TipoNotificacion {
    /**
     * Notificación cuando quedan pocas viandas en la heladera
     */
    VIANDAS_MINIMAS("Pocas viandas disponibles"),
    
    /**
     * Notificación cuando la heladera está casi llena
     */
    VIANDAS_MAXIMAS("Heladera casi llena"),
    
    /**
     * Notificación cuando la heladera sufre un desperfecto técnico
     */
    DESPERFECTO("Desperfecto técnico detectado"),
    
    /**
     * Notificación cuando se detecta un intento de fraude
     */
    FRAUDE("Intento de fraude detectado"),
    
    /**
     * Notificación cuando hay un problema de temperatura
     */
    TEMPERATURA("Problema de temperatura"),
    
    /**
     * Notificación cuando hay un problema de conexión
     */
    CONEXION("Problema de conexión");
    
    private final String descripcion;
    
    TipoNotificacion(String descripcion) {
        this.descripcion = descripcion;
    }
    
    public String getDescripcion() {
        return descripcion;
    }
    
    @Override
    public String toString() {
        return this.descripcion;
    }
} 