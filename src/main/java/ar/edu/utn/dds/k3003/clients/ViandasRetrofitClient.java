package ar.edu.utn.dds.k3003.clients;

import ar.edu.utn.dds.k3003.dtos.EstadoViandaRequest;
import ar.edu.utn.dds.k3003.facades.dtos.ViandaDTO;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.PATCH;
import retrofit2.http.Path;

public interface ViandasRetrofitClient {

  @GET("viandas/{qr}")
  Call<ViandaDTO> get(@Path("qr") String qr);

  @PATCH("viandas/{qr}/estado")
  Call<ViandaDTO> updateState(@Path("qr") String qr, @Body EstadoViandaRequest estadoViandaRequest);
}
