package ar.edu.utn.dds.k3003.clients;

import ar.edu.utn.dds.k3003.dtos.SuscripcionDTO;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface ColaboradoresRetrofitClient {
    @POST("colaboradores/gestionarIncidente")
    Call<Void> gestionarIncidente(@Body SuscripcionDTO gestionarIncidenteRequest);
}
