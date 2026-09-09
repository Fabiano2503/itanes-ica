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

        // Repositorio para carga inicial y sincronización
        PlaceRepository repository = new PlaceRepository(this);

        // Carga inicial de datos turísticos (Seed) si Room está vacío
        PlaceDataSeeder seeder = new PlaceDataSeeder(repository);
        seeder.seed();

        // Disparar sincronización con API REST
        repository.syncPlaces();

        setupBottomNavigation();
    }

    @Override
    protected void onResume() {
        super.onResume();
        BottomNavigationView bottomNav = findViewById(R.id.bottomNavigation);
        bottomNav.setSelectedItemId(R.id.nav_home);
    }

    private void setupBottomNavigation() {
        BottomNavigationView bottomNav = findViewById(R.id.bottomNavigation);
        bottomNav.setSelectedItemId(R.id.nav_home);

        bottomNav.setOnItemSelectedListener(item -> {
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
