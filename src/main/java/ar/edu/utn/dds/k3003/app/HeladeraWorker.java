package ar.edu.utn.dds.k3003.app;

import java.io.IOException;
import java.util.Map;

import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.DefaultConsumer;
import com.rabbitmq.client.Envelope;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import ar.edu.utn.dds.k3003.facades.dtos.TemperaturaDTO;
import ar.edu.utn.dds.k3003.app.Fachada;

public class HeladeraWorker extends DefaultConsumer {

    private String queueName;
    private final ObjectMapper objectMapper;
    private final Fachada fachada;

    protected HeladeraWorker(Channel channel, String queueName, Fachada fachada) {
        super(channel); //super(channel, false);                    
        this.queueName = queueName;
        this.objectMapper = new ObjectMapper();
        this.objectMapper.registerModule(new JavaTimeModule());
        this.fachada = fachada;

    }
    
    private void init() throws IOException {
        // Declarar la cola desde la cual consumir mensajes
        this.getChannel().queueDeclare(this.queueName, true, false, false, null);
        // Consumir mensajes de la cola
        this.getChannel().basicConsume(this.queueName, false, this);
    }
    
    @Override
    public void handleDelivery(String consumerTag, Envelope envelope,
        AMQP.BasicProperties properties, byte[] body) throws IOException {
        // Confirmar la recepción del mensaje a la mensajeria
        this.getChannel().basicAck(envelope.getDeliveryTag(), false);
        
        String message = new String(body, "UTF-8");
        System.out.println("Se recibió el siguiente payload:");
        System.out.println(message);

        try {
            TemperaturaDTO temperaturaDTO = objectMapper.readValue(message, TemperaturaDTO.class);
            fachada.temperatura(temperaturaDTO);
            System.out.println("Temperatura registrada correctamente.");
        } catch (Exception e) {
            System.out.println("Error: Los datos enviados en la cola son incorrectos.");
            e.printStackTrace();
        }
    }
    
    public static void main(String[] args) throws Exception {
        // Establecer la conexión con CloudAMQP
        Map<String, String> env = System.getenv();
        ConnectionFactory factory = new ConnectionFactory();
        
        factory.setHost(env.getOrDefault("QUEUE_HOST", "toad.rmq.cloudamqp.com"));
        factory.setUsername(env.getOrDefault("QUEUE_USERNAME", "gzpxkmid"));
        factory.setPassword(env.getOrDefault("QUEUE_PASSWORD", "QffxXn5gFxbVNGXK8LLu-mPOJmvjgC4x"));
        // En el plan más barato, el VHOST == USER
        factory.setVirtualHost(env.getOrDefault("QUEUE_USERNAME", "gzpxkmid"));
        String queueName = env.getOrDefault("QUEUE_NAME", "heladeras");
        Connection connection = factory.newConnection();
        Channel channel = connection.createChannel();
        
        HeladeraWorker worker = new HeladeraWorker(channel,queueName, new Fachada());
        worker.init();
    }
    
    }
