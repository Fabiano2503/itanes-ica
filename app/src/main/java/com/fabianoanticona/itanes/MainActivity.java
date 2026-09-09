package com.fabianoanticona.itanes;

import android.content.Intent;
import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.fabianoanticona.itanes.data.local.seed.PlaceDataSeeder;
import com.fabianoanticona.itanes.data.remote.dto.PlaceRemoteDto;
import com.fabianoanticona.itanes.data.remote.retrofit.RetrofitClient;
import com.fabianoanticona.itanes.data.repository.PlaceRepository;
import com.fabianoanticona.itanes.ui.places.PlacesActivity;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "ITANES_API";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        findViewById(R.id.buttonExplore).setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, PlacesActivity.class);
            startActivity(intent);
        });

        // Carga inicial de datos turísticos (Seed)
        PlaceRepository repository = new PlaceRepository(this);
        PlaceDataSeeder seeder = new PlaceDataSeeder(repository);
        seeder.seed();

        testRemoteApi();
    }

    private void testRemoteApi() {
        RetrofitClient.getApiService().getPlaces().enqueue(new Callback<List<PlaceRemoteDto>>() {
            @Override
            public void onResponse(Call<List<PlaceRemoteDto>> call, Response<List<PlaceRemoteDto>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<PlaceRemoteDto> places = response.body();
                    android.util.Log.d(TAG, "Respuesta recibida correctamente");
                    android.util.Log.d(TAG, "Total lugares: " + places.size());
                    for (PlaceRemoteDto p : places) {
                        android.util.Log.d(TAG, p.getId() + " - " + p.getName());
                    }
                } else {
                    android.util.Log.e(TAG, "Error en la respuesta: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<List<PlaceRemoteDto>> call, Throwable t) {
                android.util.Log.e(TAG, "Error de red: " + t.getMessage());
            }
        });
    }
}