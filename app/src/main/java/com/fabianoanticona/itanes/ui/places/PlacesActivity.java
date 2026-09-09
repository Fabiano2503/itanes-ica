package com.fabianoanticona.itanes.ui.places;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.fabianoanticona.itanes.R;
import com.fabianoanticona.itanes.data.local.entity.PlaceEntity;
import com.fabianoanticona.itanes.data.repository.PlaceRepository;
import com.fabianoanticona.itanes.ui.detail.PlaceDetailActivity;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class PlacesActivity extends AppCompatActivity implements PlaceAdapter.OnPlaceClickListener {

    private PlaceAdapter adapter;
    private PlaceRepository repository;
    private final ExecutorService executorService = Executors.newSingleThreadExecutor();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_places);

        Toolbar toolbar = findViewById(R.id.toolbarPlaces);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle(R.string.tour_name);
        }

        repository = new PlaceRepository(this);
        initRecyclerView();
        loadPlaces();
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Recargar datos al volver a la actividad para mostrar posibles actualizaciones de la sincronización
        loadPlaces();
    }

    private void initRecyclerView() {
        RecyclerView recyclerPlaces = findViewById(R.id.recyclerPlaces);
        recyclerPlaces.setLayoutManager(new LinearLayoutManager(this));
        adapter = new PlaceAdapter(this);
        recyclerPlaces.setAdapter(adapter);
    }

    private void loadPlaces() {
        executorService.execute(() -> {
            List<PlaceEntity> places = repository.getAllPlaces();
            runOnUiThread(() -> adapter.setPlaces(places));
        });
    }

    @Override
    public void onPlaceClick(int placeId) {
        Intent intent = new Intent(this, PlaceDetailActivity.class);
        intent.putExtra(PlaceDetailActivity.EXTRA_PLACE_ID, placeId);
        startActivity(intent);
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}
