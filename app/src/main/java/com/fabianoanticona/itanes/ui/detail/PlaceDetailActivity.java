package com.fabianoanticona.itanes.ui.detail;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.bumptech.glide.Glide;
import com.fabianoanticona.itanes.R;
import com.fabianoanticona.itanes.data.local.entity.FavoriteEntity;
import com.fabianoanticona.itanes.data.local.entity.PlaceEntity;
import com.fabianoanticona.itanes.data.repository.FavoriteRepository;
import com.fabianoanticona.itanes.data.repository.PlaceRepository;
import com.fabianoanticona.itanes.ui.map.MapActivity;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class PlaceDetailActivity extends AppCompatActivity {

    public static final String EXTRA_PLACE_ID = "com.fabianoanticona.itanes.EXTRA_PLACE_ID";

    private PlaceRepository repository;
    private FavoriteRepository favoriteRepository;
    private final ExecutorService executorService = Executors.newSingleThreadExecutor();

    private ImageView imageDetailPlace;
    private TextView textDetailName, textDetailShortDesc, textDetailFullDesc, textDetailAddress, textDetailCoords;
    private Button btnFavorite, btnShare, btnMap, btnDirections;
    private boolean isFavorite = false;
    private int currentPlaceId = -1;
    private PlaceEntity currentPlace;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_place_detail);

        Toolbar toolbar = findViewById(R.id.toolbarDetail);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        repository = new PlaceRepository(this);
        favoriteRepository = new FavoriteRepository(this);
        initViews();

        currentPlaceId = getIntent().getIntExtra(EXTRA_PLACE_ID, -1);
        if (currentPlaceId == -1) {
            Toast.makeText(this, R.string.error_loading_place, Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        loadPlaceDetail(currentPlaceId);
        checkFavoriteStatus(currentPlaceId);

        btnFavorite.setOnClickListener(v -> toggleFavorite());
        btnShare.setOnClickListener(v -> sharePlace());
        btnMap.setOnClickListener(v -> openMap());
        btnDirections.setOnClickListener(v -> openDirections());
    }

    private void initViews() {
        imageDetailPlace = findViewById(R.id.imageDetailPlace);
        textDetailName = findViewById(R.id.textDetailName);
        textDetailShortDesc = findViewById(R.id.textDetailShortDesc);
        textDetailFullDesc = findViewById(R.id.textDetailFullDesc);
        textDetailAddress = findViewById(R.id.textDetailAddress);
        textDetailCoords = findViewById(R.id.textDetailCoords);
        btnFavorite = findViewById(R.id.btnFavorite);
        btnShare = findViewById(R.id.btnShare);
        btnMap = findViewById(R.id.btnMap);
        btnDirections = findViewById(R.id.btnDirections);
    }

    private void loadPlaceDetail(int placeId) {
        executorService.execute(() -> {
            PlaceEntity place = repository.getPlaceById(placeId);
            runOnUiThread(() -> {
                if (isFinishing() || isDestroyed()) return;
                if (place != null) {
                    displayPlace(place);
                } else {
                    Toast.makeText(PlaceDetailActivity.this, R.string.error_loading_place, Toast.LENGTH_SHORT).show();
                    finish();
                }
            });
        });
    }

    private void displayPlace(PlaceEntity place) {
        this.currentPlace = place;
        textDetailName.setText(place.getName());
        textDetailShortDesc.setText(place.getShortDescription());
        textDetailFullDesc.setText(place.getDescription());
        textDetailAddress.setText(place.getAddress());

        String coords = String.format(Locale.getDefault(), "%.6f, %.6f", place.getLatitude(), place.getLongitude());
        textDetailCoords.setText(coords);

        Glide.with(this)
                .load(place.getImageUrl())
                .placeholder(android.R.drawable.ic_menu_gallery)
                .error(android.R.drawable.stat_notify_error)
                .into(imageDetailPlace);
    }

    private void checkFavoriteStatus(int placeId) {
        executorService.execute(() -> {
            isFavorite = favoriteRepository.isFavorite(placeId);
            runOnUiThread(() -> {
                if (isFinishing() || isDestroyed()) return;
                updateFavoriteButton();
            });
        });
    }

    private void toggleFavorite() {
        executorService.execute(() -> {
            if (isFavorite) {
                favoriteRepository.removeFavorite(currentPlaceId);
            } else {
                String timestamp = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(new Date());
                favoriteRepository.addFavorite(new FavoriteEntity(currentPlaceId, timestamp));
            }
            isFavorite = !isFavorite;
            runOnUiThread(() -> {
                if (isFinishing() || isDestroyed()) return;
                updateFavoriteButton();
            });
        });
    }

    private void updateFavoriteButton() {
        if (isFavorite) {
            btnFavorite.setText(R.string.btn_favorite_remove);
        } else {
            btnFavorite.setText(R.string.btn_favorite_add);
        }
    }

    private void sharePlace() {
        if (currentPlace == null) return;

        String shareBody = getString(R.string.share_message_format,
                currentPlace.getName(),
                currentPlace.getShortDescription(),
                currentPlace.getAddress(),
                getString(R.string.app_name));

        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.setType("text/plain");
        shareIntent.putExtra(Intent.EXTRA_SUBJECT, currentPlace.getName());
        shareIntent.putExtra(Intent.EXTRA_TEXT, shareBody);

        try {
            startActivity(Intent.createChooser(shareIntent, getString(R.string.share_chooser_title)));
        } catch (ActivityNotFoundException e) {
            Toast.makeText(this, R.string.share_error, Toast.LENGTH_SHORT).show();
        }
    }

    private void openMap() {
        Intent intent = new Intent(this, MapActivity.class);
        intent.putExtra(EXTRA_PLACE_ID, currentPlaceId);
        startActivity(intent);
    }

    private void openDirections() {
        if (currentPlace == null) return;

        double latitude = currentPlace.getLatitude();
        double longitude = currentPlace.getLongitude();

        if (latitude < -90 || latitude > 90 || longitude < -180 || longitude > 180) {
            Toast.makeText(this, R.string.error_invalid_location, Toast.LENGTH_SHORT).show();
            return;
        }

        String uriString = String.format(Locale.US,
                "https://www.google.com/maps/dir/?api=1&destination=%.6f,%.6f&travelmode=driving",
                latitude, longitude);

        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(uriString));

        try {
            startActivity(intent);
        } catch (ActivityNotFoundException e) {
            Toast.makeText(this, R.string.error_no_navigation_app, Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        executorService.shutdown();
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}