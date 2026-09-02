package com.fabianoanticona.itanes.ui.detail;

import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.fabianoanticona.itanes.R;
import com.fabianoanticona.itanes.data.local.entity.FavoriteEntity;
import com.fabianoanticona.itanes.data.local.entity.PlaceEntity;
import com.fabianoanticona.itanes.data.repository.FavoriteRepository;
import com.fabianoanticona.itanes.data.repository.PlaceRepository;

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
    private Button btnFavorite;
    private boolean isFavorite = false;
    private int currentPlaceId = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_place_detail);

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
    }

    private void initViews() {
        imageDetailPlace = findViewById(R.id.imageDetailPlace);
        textDetailName = findViewById(R.id.textDetailName);
        textDetailShortDesc = findViewById(R.id.textDetailShortDesc);
        textDetailFullDesc = findViewById(R.id.textDetailFullDesc);
        textDetailAddress = findViewById(R.id.textDetailAddress);
        textDetailCoords = findViewById(R.id.textDetailCoords);
        btnFavorite = findViewById(R.id.btnFavorite);
    }

    private void loadPlaceDetail(int placeId) {
        executorService.execute(() -> {
            PlaceEntity place = repository.getPlaceById(placeId);
            runOnUiThread(() -> {
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
            runOnUiThread(this::updateFavoriteButton);
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
            runOnUiThread(this::updateFavoriteButton);
        });
    }

    private void updateFavoriteButton() {
        if (isFavorite) {
            btnFavorite.setText(R.string.btn_favorite_remove);
        } else {
            btnFavorite.setText(R.string.btn_favorite_add);
        }
    }
}