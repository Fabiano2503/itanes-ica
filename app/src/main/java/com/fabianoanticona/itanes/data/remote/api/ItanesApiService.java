package com.fabianoanticona.itanes.data.remote.api;

import com.fabianoanticona.itanes.data.remote.dto.PlaceRemoteDto;
import java.util.List;
import retrofit2.Call;
import retrofit2.http.GET;

public interface ItanesApiService {
    @GET("places.json")
    Call<List<PlaceRemoteDto>> getPlaces();
}
