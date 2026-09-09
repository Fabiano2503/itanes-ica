package com.fabianoanticona.itanes.data.repository;

import android.content.Context;
import android.util.Log;

import com.fabianoanticona.itanes.data.local.dao.PlaceDao;
import com.fabianoanticona.itanes.data.local.database.AppDatabase;
import com.fabianoanticona.itanes.data.local.entity.PlaceEntity;
import com.fabianoanticona.itanes.data.mapper.PlaceMapper;
import com.fabianoanticona.itanes.data.remote.dto.PlaceRemoteDto;
import com.fabianoanticona.itanes.data.remote.retrofit.RetrofitClient;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PlaceRepository {

    private static final String SYNC_TAG = "ITANES_SYNC";
    private final PlaceDao placeDao;
    private final ExecutorService executorService;

    public PlaceRepository(Context context) {
        AppDatabase db = AppDatabase.getInstance(context);
        this.placeDao = db.placeDao();
        this.executorService = Executors.newSingleThreadExecutor();
    }

    public List<PlaceEntity> getAllPlaces() {
        return placeDao.getAllPlaces();
    }

    public PlaceEntity getPlaceById(int id) {
        return placeDao.getPlaceById(id);
    }

    public void insertPlaces(List<PlaceEntity> places) {
        placeDao.insertAll(places);
    }

    public void deleteAllPlaces() {
        placeDao.deleteAll();
    }

    public int getCount() {
        return placeDao.getCount();
    }

    public void syncPlaces() {
        Log.d(SYNC_TAG, "Iniciando sincronización");

        RetrofitClient.getApiService().getPlaces().enqueue(new Callback<List<PlaceRemoteDto>>() {
            @Override
            public void onResponse(Call<List<PlaceRemoteDto>> call, Response<List<PlaceRemoteDto>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<PlaceRemoteDto> remotePlaces = response.body();
                    Log.d(SYNC_TAG, remotePlaces.size() + " lugares recibidos");

                    if (!remotePlaces.isEmpty()) {
                        executorService.execute(() -> {
                            try {
                                List<PlaceEntity> entities = PlaceMapper.toEntityList(remotePlaces);
                                placeDao.insertAll(entities);
                                Log.d(SYNC_TAG, entities.size() + " lugares guardados en Room");
                                Log.d(SYNC_TAG, "Sincronización completada");
                            } catch (Exception e) {
                                Log.e(SYNC_TAG, "Error al guardar en Room: " + e.getMessage());
                            }
                        });
                    } else {
                        Log.d(SYNC_TAG, "La lista recibida está vacía, no se actualiza Room");
                    }
                } else {
                    Log.e(SYNC_TAG, "Error en la respuesta de la API: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<List<PlaceRemoteDto>> call, Throwable t) {
                Log.e(SYNC_TAG, "Error de red: " + t.getMessage());
                Log.d(SYNC_TAG, "Manteniendo datos locales previos");
            }
        });
    }
}
