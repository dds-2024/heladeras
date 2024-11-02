package ar.edu.utn.dds.k3003.clients;

import ar.edu.utn.dds.k3003.dtos.EstadoViandaRequest;
import ar.edu.utn.dds.k3003.facades.FachadaHeladeras;
import ar.edu.utn.dds.k3003.facades.FachadaViandas;
import ar.edu.utn.dds.k3003.facades.dtos.EstadoViandaEnum;
import ar.edu.utn.dds.k3003.facades.dtos.ViandaDTO;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.javalin.http.HttpStatus;
import java.util.*;
import lombok.SneakyThrows;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.jackson.JacksonConverterFactory;

public class ViandasProxy implements FachadaViandas {

  private final String endpoint;
  private final ViandasRetrofitClient service;

  public ViandasProxy(ObjectMapper objectMapper) {

    var env = System.getenv();
    this.endpoint = env.getOrDefault("URL_VIANDAS", "https://two024-dds-tp-viandas.onrender.com");
    
    var retrofit =
        new Retrofit.Builder()
            .baseUrl(this.endpoint)
            .addConverterFactory(JacksonConverterFactory.create(objectMapper))
            .build();

    this.service = retrofit.create(ViandasRetrofitClient.class);
    //this.service = new MockViandasRetrofitClient();
  }

  @Override
  public ViandaDTO agregar(ViandaDTO viandaDTO) {
    return null;
  }

  @SneakyThrows
  @Override
  public ViandaDTO modificarEstado(String qr, EstadoViandaEnum estadoViandaEnum)
      throws NoSuchElementException {
    EstadoViandaRequest estadoViandaRequest = new EstadoViandaRequest(estadoViandaEnum);
    Response<ViandaDTO> response = service.updateState(qr, estadoViandaRequest).execute();

    if (response.isSuccessful()) {
      return response.body();
    }
    if (response.code() == HttpStatus.NOT_FOUND.getCode()) {
      throw new NoSuchElementException("No se encontró la vianda con QR: " + qr);
    }
    throw new RuntimeException("Error al conectarse con el componente viandas");
  }

  @Override
  public List<ViandaDTO> viandasDeColaborador(Long aLong, Integer integer, Integer integer1)
      throws NoSuchElementException {
    return null;
  }

  @SneakyThrows
  @Override
  public ViandaDTO buscarXQR(String qr) throws NoSuchElementException {
    Response<ViandaDTO> execute = service.get(qr).execute();

    if (execute.isSuccessful()) {
      return execute.body();
    }
    if (execute.code() == HttpStatus.NOT_FOUND.getCode()) {
      throw new NoSuchElementException("no se encontro la vianda " + qr);
    }
    throw new RuntimeException("Error conectandose con el componente viandas");
  }

  @Override
  public void setHeladerasProxy(FachadaHeladeras fachadaHeladeras) {}

  @Override
  public boolean evaluarVencimiento(String s) throws NoSuchElementException {
    return false;
  }

  @Override
  public ViandaDTO modificarHeladera(String s, int i) {
    return null;
  }
}