package com.fabianoanticona.itanes.ui.map;

import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.fabianoanticona.itanes.R;
import com.fabianoanticona.itanes.data.local.entity.PlaceEntity;
import com.fabianoanticona.itanes.data.repository.PlaceRepository;
import com.fabianoanticona.itanes.ui.detail.PlaceDetailActivity;

import org.maplibre.android.MapLibre;
import org.maplibre.android.annotations.MarkerOptions;
import org.maplibre.android.camera.CameraUpdateFactory;
import org.maplibre.android.geometry.LatLng;
import org.maplibre.android.maps.MapView;
import org.maplibre.android.maps.MapLibreMap;
import org.maplibre.android.maps.OnMapReadyCallback;
import org.maplibre.android.maps.Style;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MapActivity extends AppCompatActivity implements OnMapReadyCallback {

    private static final double MIN_ZOOM = 3.0;
    private static final double MAX_ZOOM = 19.0;

    private MapView mapView;
    private MapLibreMap mapLibreMap;
    private TextView textMapPlaceName, textMapPlaceAddress;
    private ProgressBar progressBarMap;

    private PlaceRepository repository;
    private final ExecutorService executorService = Executors.newSingleThreadExecutor();
    private PlaceEntity currentPlace;
    private int placeId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Initialize MapLibre before setContentView
        MapLibre.getInstance(this);

        setContentView(R.layout.activity_map);

        Toolbar toolbar = findViewById(R.id.toolbarMap);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        placeId = getIntent().getIntExtra(PlaceDetailActivity.EXTRA_PLACE_ID, -1);
        if (placeId == -1) {
            Toast.makeText(this, R.string.error_loading_place, Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        repository = PlaceRepository.getInstance(this);
        initViews();

        mapView.onCreate(savedInstanceState);
        mapView.getMapAsync(this);

        loadPlaceData();
    }

    private void initViews() {
        mapView = findViewById(R.id.mapView);
        textMapPlaceName = findViewById(R.id.textMapPlaceName);
        textMapPlaceAddress = findViewById(R.id.textMapPlaceAddress);
        progressBarMap = findViewById(R.id.progressBarMap);

        findViewById(R.id.btnZoomIn).setOnClickListener(v -> zoomIn());
        findViewById(R.id.btnZoomOut).setOnClickListener(v -> zoomOut());
    }

    private void loadPlaceData() {
        progressBarMap.setVisibility(View.VISIBLE);
        executorService.execute(() -> {
            currentPlace = repository.getPlaceById(placeId);
            runOnUiThread(() -> {
                if (isFinishing() || isDestroyed()) return;
                progressBarMap.setVisibility(View.GONE);
                if (currentPlace != null) {
                    displayPlaceInfo();
                    if (mapLibreMap != null) {
                        setupMapLocation();
                    }
                } else {
                    Toast.makeText(MapActivity.this, R.string.error_loading_place, Toast.LENGTH_SHORT).show();
                }
            });
        });
    }

    private void displayPlaceInfo() {
        textMapPlaceName.setText(currentPlace.getName());
        textMapPlaceAddress.setText(currentPlace.getAddress());
    }

    @Override
    public void onMapReady(@NonNull MapLibreMap mapLibreMap) {
        this.mapLibreMap = mapLibreMap;

        mapLibreMap.setStyle(new Style.Builder().fromUri("https://tiles.openfreemap.org/styles/liberty"), style -> {
            if (currentPlace != null) {
                setupMapLocation();
            }
        });
    }

    private void setupMapLocation() {
        if (currentPlace == null || mapLibreMap == null) return;

        double lat = currentPlace.getLatitude();
        double lng = currentPlace.getLongitude();

        if (isValidCoordinate(lat, lng)) {
            LatLng location = new LatLng(lat, lng);
            
            // Immediate camera move to target location and zoom
            mapLibreMap.moveCamera(CameraUpdateFactory.newLatLngZoom(location, 15));

            // Add marker
            mapLibreMap.addMarker(new MarkerOptions()
                    .position(location)
                    .title(currentPlace.getName()));

            // Set zoom limits
            mapLibreMap.setMinZoomPreference(MIN_ZOOM);
            mapLibreMap.setMaxZoomPreference(MAX_ZOOM);
        } else {
            Toast.makeText(this, R.string.error_invalid_coords, Toast.LENGTH_LONG).show();
        }
    }

    private void zoomIn() {
        if (mapLibreMap != null) {
            double currentZoom = mapLibreMap.getCameraPosition().zoom;
            if (currentZoom < MAX_ZOOM) {
                mapLibreMap.animateCamera(CameraUpdateFactory.zoomIn());
            }
        }
    }

    private void zoomOut() {
        if (mapLibreMap != null) {
            double currentZoom = mapLibreMap.getCameraPosition().zoom;
            if (currentZoom > MIN_ZOOM) {
                mapLibreMap.animateCamera(CameraUpdateFactory.zoomOut());
            }
        }
    }

    private boolean isValidCoordinate(double lat, double lng) {
        return lat >= -90 && lat <= 90 && lng >= -180 && lng <= 180;
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    // MapView Lifecycle methods
    @Override
    protected void onStart() {
        super.onStart();
        mapView.onStart();
    }

    @Override
    protected void onResume() {
        super.onResume();
        mapView.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mapView.onPause();
    }

    @Override
    protected void onStop() {
        super.onStop();
        mapView.onStop();
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        mapView.onSaveInstanceState(outState);
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mapView.onLowMemory();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mapView.onDestroy();
        executorService.shutdown();
    }
}