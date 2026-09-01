package com.fabianoanticona.itanes;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.fabianoanticona.itanes.data.local.entity.PlaceEntity;
import com.fabianoanticona.itanes.data.repository.PlaceRepository;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class PlacesActivity extends AppCompatActivity {

    private RecyclerView recyclerPlaces;
    private PlaceAdapter adapter;
    private PlaceRepository repository;
    private final ExecutorService executorService = Executors.newSingleThreadExecutor();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_places);

        repository = new PlaceRepository(this);
        initRecyclerView();
        loadPlaces();
    }

    private void initRecyclerView() {
        recyclerPlaces = findViewById(R.id.recyclerPlaces);
        recyclerPlaces.setLayoutManager(new LinearLayoutManager(this));
        adapter = new PlaceAdapter();
        recyclerPlaces.setAdapter(adapter);
    }

    private void loadPlaces() {
        executorService.execute(() -> {
            List<PlaceEntity> places = repository.getAllPlaces();
            runOnUiThread(() -> {
                adapter.setPlaces(places);
            });
        });
    }
}