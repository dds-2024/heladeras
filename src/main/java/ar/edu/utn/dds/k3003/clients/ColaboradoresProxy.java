package ar.edu.utn.dds.k3003.clients;

import ar.edu.utn.dds.k3003.dtos.SuscripcionDTO;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.*;
import lombok.SneakyThrows;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.jackson.JacksonConverterFactory;

public class ColaboradoresProxy {
    private final String endpoint;
    private final ColaboradoresRetrofitClient service;

    public ColaboradoresProxy(ObjectMapper objectMapper) {

    var env = System.getenv();
    this.endpoint = env.getOrDefault("URL_COLABORADORES", "https://two024-dds-tp-viandas.onrender.com");
    
    var retrofit =
        new Retrofit.Builder()
            .baseUrl(this.endpoint)
            .addConverterFactory(JacksonConverterFactory.create(objectMapper))
            .build();

    this.service = retrofit.create(ColaboradoresRetrofitClient.class);
  }

  @SneakyThrows
  public void gestionarIncidente(SuscripcionDTO suscripcionDTO)
      throws NoSuchElementException {
    Response<Void> response = service.gestionarIncidente(suscripcionDTO).execute();

    if (response.isSuccessful()) {
      return;
    }
    throw new RuntimeException("Error al conectarse con el componente colaboradores");
  }

  
}
