package ar.edu.utn.dds.k3003.app;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

import ar.edu.utn.dds.k3003.clients.ColaboradoresProxy;
import ar.edu.utn.dds.k3003.dtos.IncidenteDTO;
import ar.edu.utn.dds.k3003.dtos.SuscripcionDTO;
import ar.edu.utn.dds.k3003.facades.FachadaViandas;
import ar.edu.utn.dds.k3003.facades.dtos.EstadoViandaEnum;
import ar.edu.utn.dds.k3003.facades.dtos.HeladeraDTO;
import ar.edu.utn.dds.k3003.facades.dtos.RetiroDTO;
import ar.edu.utn.dds.k3003.facades.dtos.TemperaturaDTO;
import ar.edu.utn.dds.k3003.facades.dtos.ViandaDTO;
import ar.edu.utn.dds.k3003.model.EstadoIncidente;
import ar.edu.utn.dds.k3003.model.Heladera;
import ar.edu.utn.dds.k3003.model.Incidente;
import ar.edu.utn.dds.k3003.model.Operacion;
import ar.edu.utn.dds.k3003.model.Suscripcion;
import ar.edu.utn.dds.k3003.model.Temperatura;
import ar.edu.utn.dds.k3003.model.TipoIncidente;
import ar.edu.utn.dds.k3003.repositories.HeladeraMapper;
import ar.edu.utn.dds.k3003.repositories.HeladeraRepository;
import ar.edu.utn.dds.k3003.repositories.OperacionRepository;
import ar.edu.utn.dds.k3003.repositories.SuscripcionRepository;
import ar.edu.utn.dds.k3003.repositories.TemperaturaMapper;
import ar.edu.utn.dds.k3003.repositories.TemperaturaRepository;
import ar.edu.utn.dds.k3003.repositories.IncidenteRepository;
import ar.edu.utn.dds.k3003.services.NotificacionService;

public class Fachada implements ar.edu.utn.dds.k3003.facades.FachadaHeladeras{
    private final HeladeraRepository heladeraRepository;
    private final HeladeraMapper heladeraMapper;
    private final TemperaturaRepository temperaturaRepository;
    private final TemperaturaMapper temperaturaMapper;
    private final OperacionRepository operacionRepository;
    private final SuscripcionRepository suscripcionRepository;
    private final IncidenteRepository incidenteRepository;
    
    private NotificacionService notificacionService;
    private FachadaViandas fachadaViandas;
    
    public Fachada() {
        this.heladeraRepository = new HeladeraRepository();
        this.heladeraMapper = new HeladeraMapper();
        this.temperaturaRepository = new TemperaturaRepository();
        this.temperaturaMapper = new TemperaturaMapper();
        this.operacionRepository = new OperacionRepository();
        this.suscripcionRepository = new SuscripcionRepository();
        this.incidenteRepository = new IncidenteRepository();
    }

    @Override
    public HeladeraDTO agregar(HeladeraDTO heladeraDTO) {
        // Heladera heladera = new Heladera(heladeraDTO.getId(), heladeraDTO.getNombre());
        Heladera heladera = new Heladera(heladeraDTO.getNombre());
        heladera = this.heladeraRepository.save(heladera);
        return heladeraMapper.map(heladera);
    }

    @Override 
    public void depositar(Integer heladeraId, String qrViaString) throws NoSuchElementException {
        //Buso la heladera por id en mi heladeraRepository
        Heladera heladera = heladeraRepository.findById(heladeraId);

        //valido que la vianda exista
        ViandaDTO viandaDTO = this.fachadaViandas.buscarXQR(qrViaString);
        
        //Agrego que tengo una vianda mas
        heladeraRepository.agregarVianda(heladera);
        
        //Guardo la operacion de depositar para saber cuantas veces se abrio la heladera
        Operacion operacion = new Operacion(heladera.getId(), viandaDTO.getCodigoQR());
        operacion = this.operacionRepository.save(operacion);
        
        //Modifico estado vianda a depositada
        this.fachadaViandas.modificarEstado(viandaDTO.getCodigoQR(), EstadoViandaEnum.DEPOSITADA);

        //Verifico si hay notificaciones para la heladera
        this.notificacionService.verificarNotificaciones(heladera);
    }

    @Override
    public void retirar(RetiroDTO retiro) throws NoSuchElementException {
        //Buso la heladera por id en mi heladeraRepository
        Heladera heladera = heladeraRepository.findById(retiro.getHeladeraId());

        //valido que la vianda exista
        ViandaDTO viandaDTO = this.fachadaViandas.buscarXQR(retiro.getQrVianda());

        //Resto una vianda a ocupacion ya que me la retiran
        heladeraRepository.retirarVianda(heladera);
        
        //Guardo la operacion de retiro para saber cuantas veces se abrio la heladera
        Operacion operacion = new Operacion(heladera.getId(), viandaDTO.getCodigoQR());
        operacion = this.operacionRepository.save(operacion);

        //Modifico estado vianda a retirada
        this.fachadaViandas.modificarEstado(viandaDTO.getCodigoQR(), EstadoViandaEnum.RETIRADA);

        //Verifico si hay notificaciones para la heladera
        this.notificacionService.verificarNotificaciones(heladera);
    }

    @Override
    public List<TemperaturaDTO> obtenerTemperaturas(Integer heladeraId) {
        //Obtengo las temperaturas segun la heladera
        Collection<Temperatura> temperaturas = this.temperaturaRepository.findByheladeraId(heladeraId);

        //Devuelvo la lista de temperaturas en DTO
        return this.temperaturaMapper.map(temperaturas);
    }

    @Override
    public void temperatura(TemperaturaDTO temperaturaDTO) {
        //Guardo en el repositorio la medicion
        Temperatura temperatura = new Temperatura(temperaturaDTO.getHeladeraId(), temperaturaDTO.getTemperatura(), temperaturaDTO.getFechaMedicion());
        this.temperaturaRepository.save(temperatura);
        
        //Busco temperaturas de la última hora
        Collection<Temperatura> temperaturasUltimaHora = this.temperaturaRepository.findByHeladeraIdAndLastHour(
            temperaturaDTO.getHeladeraId(), 
            temperaturaDTO.getFechaMedicion()
        );
        
        //Cuento mediciones mayores a 5 grados
        long medicionesAltas = temperaturasUltimaHora.stream()
            .filter(temp -> temp.getTemperatura() > 5)
            .count();
        
        //Si hay 3 o más mediciones altas, genero incidente
        if (medicionesAltas >= 3) {
            Incidente incidente = new Incidente();
            incidente.setHeladeraId(temperaturaDTO.getHeladeraId());
            incidente.setTipo(TipoIncidente.ALERTA_TEMPERATURA);
            incidente.setDescripcion("Se detectaron " + medicionesAltas + " mediciones superiores a 5°C en la última hora");
            incidenteRepository.save(incidente);
            
            //Marco la heladera como inactiva
            marcarDesperfecto(temperaturaDTO.getHeladeraId());
        }
    }

    @Override
    public Integer cantidadViandas(Integer heladeraId) throws NoSuchElementException {
        //Buso la heladera por id en mi heladeraRepository
        Heladera heladera = heladeraRepository.findById(heladeraId);

        //Devuelvo la cantidad de viandas que tiene la heladera = ocupacion heladera
        return heladera.getOcupacion();
    }

    @Override
    public void setViandasProxy(FachadaViandas viandas) {
        this.fachadaViandas = viandas;
    }

    public void setColaboradoresProxy(ColaboradoresProxy colaboradoresProxy) {
        //this.colaboradoresProxy = colaboradoresProxy;
        this.notificacionService = new NotificacionService(colaboradoresProxy);
    }

    public HeladeraDTO buscarHeladeraXId(Integer heladeraId) throws NoSuchElementException{
        //Busco la heladera por id en mi heladeraRepository
        Heladera heladera = heladeraRepository.findById(heladeraId);

        //Devuelvo la heladera encontrada como HeladeraDTO
        return heladeraMapper.map(heladera);
    }

    public SuscripcionDTO suscribir(SuscripcionDTO suscripcionDTO) {
        // Verificar que la heladera existe
        Heladera heladera = heladeraRepository.findById(suscripcionDTO.getHeladeraId());

        // Validar según tipo de suscripción
        switch (suscripcionDTO.getTipoSuscripcion()) {
            case VIANDAS_MINIMAS:
                if (suscripcionDTO.getCantidadViandas() == null) {
                    throw new IllegalArgumentException("Debe especificar cantidad de viandas mínimas");
                }
                if (suscripcionDTO.getCantidadViandas() < 0) {
                    throw new IllegalArgumentException("La cantidad de viandas no puede ser negativa");
                }
                break;
                
            case VIANDAS_MAXIMAS:
                if (suscripcionDTO.getCantidadViandas() == null) {
                    throw new IllegalArgumentException("Debe especificar cantidad de viandas máximas");
                }
                if (suscripcionDTO.getCantidadViandas() > heladera.getCapacidad()) {
                    throw new IllegalArgumentException("La cantidad de viandas no puede superar la capacidad de la heladera");
                }
                break;
                
            case DESPERFECTO:
                // Para desperfecto no necesitamos validar cantidadViandas
                suscripcionDTO.setCantidadViandas(null);
                break;
        }

        // Crear y guardar la suscripción
        Suscripcion suscripcion = new Suscripcion();
        suscripcion.setHeladeraId(suscripcionDTO.getHeladeraId());
        suscripcion.setColaboradorId(suscripcionDTO.getColaboradorId());
        suscripcion.setTipoSuscripcion(suscripcionDTO.getTipoSuscripcion());
        suscripcion.setCantidadViandas(suscripcionDTO.getCantidadViandas());

        suscripcion = suscripcionRepository.save(suscripcion);

        return convertirADTO(suscripcion);
    }

    private SuscripcionDTO convertirADTO(Suscripcion suscripcion) {
        SuscripcionDTO dto = new SuscripcionDTO();
        dto.setHeladeraId(suscripcion.getHeladeraId());
        dto.setColaboradorId(suscripcion.getColaboradorId());
        dto.setTipoSuscripcion(suscripcion.getTipoSuscripcion());
        dto.setCantidadViandas(suscripcion.getCantidadViandas());
        return dto;
    }

    public void marcarDesperfecto(Integer heladeraId) {
        // Buscar la heladera
        Heladera heladera = heladeraRepository.findById(heladeraId);
        
        // Marcar como inactiva
        heladera.setActiva(false);
        
        // Guardar cambios
        heladeraRepository.update(heladera);
        
        // Verificar notificaciones para avisar a los suscriptores
        this.notificacionService.verificarNotificaciones(heladera);
    }

    public List<IncidenteDTO> obtenerIncidentes(Integer heladeraId) {
        List<Incidente> incidentes = incidenteRepository.findByHeladeraId(heladeraId);
        return incidentes.stream()
                .map(incidente -> convertirIncidenteADTO(incidente))
                .collect(Collectors.toList());
    }

    public void reportarIncidente(IncidenteDTO incidenteDTO) {
        // Crear y guardar el incidente
        Incidente incidente = new Incidente();
        incidente.setHeladeraId(incidenteDTO.getHeladeraId());
        incidente.setTipo(incidenteDTO.getTipo());
        incidente.setDescripcion(incidenteDTO.getDescripcion());
        incidenteRepository.save(incidente);

        // Marcar la heladera como inactiva
        marcarDesperfecto(incidenteDTO.getHeladeraId());
    }

    private IncidenteDTO convertirIncidenteADTO(Incidente incidente) {
        IncidenteDTO dto = new IncidenteDTO();
        dto.setHeladeraId(incidente.getHeladeraId());
        dto.setTipo(incidente.getTipo());
        dto.setDescripcion(incidente.getDescripcion());
        dto.setFecha(incidente.getFecha());
        return dto;
    }

    public void verificarTiempoSinReportar(Integer heladeraId, Integer minutos) {
        // Verificar que existe la heladera
        Heladera heladera = heladeraRepository.findById(heladeraId);
        
        // Buscar última temperatura en el rango de tiempo especificado
        LocalDateTime ahora = LocalDateTime.now();
        LocalDateTime tiempoLimite = ahora.minusMinutes(minutos);
        
        boolean tieneMediciones = temperaturaRepository.existeMedicionDesde(heladeraId, tiempoLimite);
        
        if (!tieneMediciones) {
            // Crear incidente por falta de conexión
            Incidente incidente = new Incidente();
            incidente.setHeladeraId(heladeraId);
            incidente.setTipo(TipoIncidente.ALERTA_CONEXION);
            incidente.setDescripcion("No se registraron mediciones en los últimos " + minutos + " minutos");
            incidente.setFecha(LocalDateTime.now());
            incidente.setEstado(EstadoIncidente.PENDIENTE);
            incidenteRepository.save(incidente);
            
            // Marcar heladera como inactiva
            marcarDesperfecto(heladeraId);
        }
    }

    public void reportarFraude(Integer heladeraId) {
        // Verificar que existe la heladera
        Heladera heladera = heladeraRepository.findById(heladeraId);
        
        // Crear incidente de fraude
        Incidente incidente = new Incidente();
        incidente.setHeladeraId(heladeraId);
        incidente.setTipo(TipoIncidente.FRAUDE);
        incidente.setDescripcion("Se detectó un posible fraude en la heladera " + heladera.getNombre());
        incidente.setFecha(LocalDateTime.now());
        incidente.setEstado(EstadoIncidente.PENDIENTE);
        incidenteRepository.save(incidente);
        
        // Marcar heladera como inactiva
        marcarDesperfecto(heladeraId);
    }
}
