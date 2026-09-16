package com.fabianoanticona.itanes.ui.favorites;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.fabianoanticona.itanes.MainActivity;
import com.fabianoanticona.itanes.R;
import com.fabianoanticona.itanes.data.local.entity.PlaceEntity;
import com.fabianoanticona.itanes.data.repository.PlaceRepository;
import com.fabianoanticona.itanes.ui.detail.PlaceDetailActivity;
import com.fabianoanticona.itanes.ui.places.PlaceAdapter;
import com.fabianoanticona.itanes.ui.places.PlacesActivity;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class FavoritesActivity extends AppCompatActivity implements PlaceAdapter.OnPlaceClickListener {

    private PlaceAdapter adapter;
    private PlaceRepository repository;
    private final ExecutorService executorService = Executors.newSingleThreadExecutor();
    private LinearLayout layoutEmptyState;
    private RecyclerView recyclerFavorites;
    private BottomNavigationView bottomNavigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favorites);

        Toolbar toolbar = findViewById(R.id.toolbarFavorites);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(R.string.nav_favorites);
        }

        repository = PlaceRepository.getInstance(this);
        layoutEmptyState = findViewById(R.id.layoutEmptyState);
        recyclerFavorites = findViewById(R.id.recyclerFavorites);

        initRecyclerView();
        setupBottomNavigation();
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadFavorites();
        
        bottomNavigationView.setSelectedItemId(R.id.nav_favorites);
    }

    private void initRecyclerView() {
        boolean isTablet = getResources().getBoolean(R.bool.is_tablet);
        if (isTablet) {
            recyclerFavorites.setLayoutManager(new GridLayoutManager(this, 2));
        } else {
            recyclerFavorites.setLayoutManager(new LinearLayoutManager(this));
        }
        adapter = new PlaceAdapter(this);
        recyclerFavorites.setAdapter(adapter);
    }

    private void loadFavorites() {
        executorService.execute(() -> {
            List<PlaceEntity> favorites = repository.getFavoritePlaces();
            runOnUiThread(() -> {
                if (isFinishing() || isDestroyed()) return;
                if (favorites.isEmpty()) {
                    layoutEmptyState.setVisibility(View.VISIBLE);
                    recyclerFavorites.setVisibility(View.GONE);
                } else {
                    layoutEmptyState.setVisibility(View.GONE);
                    recyclerFavorites.setVisibility(View.VISIBLE);
                    adapter.setPlaces(favorites);
                }
            });
        });
    }

    private void setupBottomNavigation() {
        bottomNavigationView = findViewById(R.id.bottomNavigation);
        bottomNavigationView.setSelectedItemId(R.id.nav_favorites);

        bottomNavigationView.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();
            if (itemId == R.id.nav_home) {
                Intent intent = new Intent(this, MainActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                startActivity(intent);
                return true;
            } else if (itemId == R.id.nav_places) {
                Intent intent = new Intent(this, PlacesActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                startActivity(intent);
                return true;
            } else if (itemId == R.id.nav_favorites) {
                return true;
            }
            return false;
        });
    }

    @Override
    public void onPlaceClick(int placeId) {
        Intent intent = new Intent(this, PlaceDetailActivity.class);
        intent.putExtra(PlaceDetailActivity.EXTRA_PLACE_ID, placeId);
        startActivity(intent);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        executorService.shutdown();
    }
}