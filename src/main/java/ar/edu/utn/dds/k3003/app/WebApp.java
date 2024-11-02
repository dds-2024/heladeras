package ar.edu.utn.dds.k3003.app;

import io.javalin.Javalin;
import io.javalin.micrometer.MicrometerPlugin;
import io.micrometer.core.instrument.binder.jvm.JvmGcMetrics;
import io.micrometer.core.instrument.binder.jvm.JvmHeapPressureMetrics;
import io.micrometer.core.instrument.binder.jvm.JvmMemoryMetrics;
import io.micrometer.core.instrument.binder.system.FileDescriptorMetrics;
import io.micrometer.core.instrument.binder.system.ProcessorMetrics;
import io.micrometer.prometheusmetrics.PrometheusConfig;
import io.micrometer.prometheusmetrics.PrometheusMeterRegistry;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import java.util.Locale;
import java.util.TimeZone;
import java.text.SimpleDateFormat;
import com.fasterxml.jackson.databind.ObjectMapper;

import ar.edu.utn.dds.k3003.clients.ColaboradoresProxy;
import ar.edu.utn.dds.k3003.clients.ViandasProxy;
import ar.edu.utn.dds.k3003.controllers.*;
import ar.edu.utn.dds.k3003.facades.dtos.Constants;

public class WebApp {
    private static final String TOKEN = "your_token_here"; // Cambia esto por un token seguro

    public static void main(String[] args) {
        var env = System.getenv();
        var objectMapper = createObjectMapper();
        var fachada = new Fachada();
        fachada.setViandasProxy(new ViandasProxy(objectMapper));
        fachada.setColaboradoresProxy(new ColaboradoresProxy(objectMapper));
        

        var port = Integer.parseInt(env.getOrDefault("PORT", "8080"));

        // Crear el registro de métricas de Prometheus
        final var registry = new PrometheusMeterRegistry(PrometheusConfig.DEFAULT);
        registry.config().commonTags("app", "heladera-app");

        // Agregar métricas de JVM y sistema
        try (var jvmGcMetrics = new JvmGcMetrics();
             var jvmHeapPressureMetrics = new JvmHeapPressureMetrics()) {
            jvmGcMetrics.bindTo(registry);
            jvmHeapPressureMetrics.bindTo(registry);
        }
        new JvmMemoryMetrics().bindTo(registry);
        new ProcessorMetrics().bindTo(registry);
        new FileDescriptorMetrics().bindTo(registry);

        // Agregar métricas personalizadas
        var heladeraAperturas = registry.counter("heladera_aperturas_total");
        var depositosRealizados = registry.counter("depositos_realizados_total");
        var retirosRealizados = registry.counter("retiros_realizados_total");

        final var micrometerPlugin = new MicrometerPlugin(config -> config.registry = registry);
        //JavalinJackson.configure(objectMapper);

        // Configurar Javalin con Micrometer
        var app = Javalin.create(config -> {
            // config.jsonMapper(new CustomJsonMapper(objectMapper));
            config.registerPlugin(micrometerPlugin);
        }).start(port);

        // Configurar controladores
        var heladerasController = new HeladeraController(fachada);
        var depositosController = new DepositoController(fachada, heladeraAperturas, depositosRealizados);
        var retirosController = new RetiroController(fachada, heladeraAperturas, retirosRealizados);
        var temperaturasController = new TemperaturaController(fachada);
        var cleanupController = new CleanupController();
        
        // Definir rutas
        app.post("/heladeras", heladerasController::agregar);
        app.get("/heladeras/{id}", heladerasController::obtener);
        app.post("/depositos", depositosController::depositar);
        app.post("/retiros", retirosController::retirar);
        app.post("/temperaturas", temperaturasController::registrar);
        app.get("/heladeras/{id}/temperaturas", heladerasController::obtenerTemperaturas);
        app.get("/heladeras/{id}/viandas", heladerasController::obtenerCantidadViandas);
        app.delete("/cleanup", cleanupController::cleanup);
        app.post("/heladeras/suscripciones", heladerasController::suscribir);
        app.post("/heladeras/reportar-desperfecto", heladerasController::reportarIncidente);
        app.get("/heladeras/{id}/incidentes", heladerasController::obtenerIncidentes);
        app.post("/heladeras/{id}/tiempoSinReportar", heladerasController::verificarTiempoSinReportar);
        app.post("/heladeras/{id}/fraude", heladerasController::reportarFraude);
        app.put("/heladeras/{id}/capacidad", heladerasController::setCapacidad);


        // Endpoint para métricas
        app.get("/metrics", ctx -> {
            var auth = ctx.header("Authorization");
            if (auth != null && auth.equals("Bearer " + TOKEN)) {
                ctx.contentType("text/plain; version=0.0.4")
                   .result(registry.scrape());
            } else {
                ctx.status(401).json("Acceso no autorizado");
            }
        });
    }

    public static ObjectMapper createObjectMapper() {
        var objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

        var sdf = new SimpleDateFormat(Constants.DEFAULT_SERIALIZATION_FORMAT, Locale.getDefault());
        sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
        objectMapper.setDateFormat(sdf);

        return objectMapper;
    }
}
