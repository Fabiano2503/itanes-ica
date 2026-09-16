package com.fabianoanticona.itanes;

import android.content.Intent;
import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.fabianoanticona.itanes.data.local.seed.PlaceDataSeeder;
import com.fabianoanticona.itanes.data.repository.PlaceRepository;
import com.fabianoanticona.itanes.ui.favorites.FavoritesActivity;
import com.fabianoanticona.itanes.ui.places.PlacesActivity;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity extends AppCompatActivity {

    private enum SyncState {
        NOT_STARTED,
        IN_PROGRESS,
        COMPLETED_SUCCESS
    }

    private static SyncState syncState = SyncState.NOT_STARTED;
    private BottomNavigationView bottomNavigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, 0);
            return insets;
        });

        findViewById(R.id.buttonExplore).setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, PlacesActivity.class);
            startActivity(intent);
        });

        if (syncState == SyncState.NOT_STARTED) {
            syncState = SyncState.IN_PROGRESS;

            // Repositorio para carga inicial y sincronización
            PlaceRepository repository = PlaceRepository.getInstance(this);

            // Carga inicial de datos turísticos (Seed) si Room está vacío
            PlaceDataSeeder seeder = new PlaceDataSeeder(repository);
            seeder.seed(new PlaceDataSeeder.SeedCallback() {
                @Override
                public void onComplete() {
                    // Disparar sincronización con API REST solo después de terminar el seed
                    repository.syncPlaces(new PlaceRepository.SyncCallback() {
                        @Override
                        public void onSuccess() {
                            syncState = SyncState.COMPLETED_SUCCESS;
                        }

                        @Override
                        public void onFailure() {
                            syncState = SyncState.NOT_STARTED;
                        }
                    });
                }
            });
        }

        setupBottomNavigation();
    }

    @Override
    protected void onResume() {
        super.onResume();
        bottomNavigationView.setSelectedItemId(R.id.nav_home);
    }

    private void setupBottomNavigation() {
        bottomNavigationView = findViewById(R.id.bottomNavigation);
        bottomNavigationView.setSelectedItemId(R.id.nav_home);

        bottomNavigationView.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();
            if (itemId == R.id.nav_home) {
                return true;
            } else if (itemId == R.id.nav_places) {
                Intent intent = new Intent(this, PlacesActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                startActivity(intent);
                return true;
            } else if (itemId == R.id.nav_favorites) {
                Intent intent = new Intent(this, FavoritesActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                startActivity(intent);
                return true;
            }
            return false;
        });
    }
}
