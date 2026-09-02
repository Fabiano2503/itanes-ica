package com.fabianoanticona.itanes.ui.detail;

import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.fabianoanticona.itanes.R;
import com.fabianoanticona.itanes.data.local.entity.PlaceEntity;
import com.fabianoanticona.itanes.data.repository.PlaceRepository;

import java.util.Locale;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class PlaceDetailActivity extends AppCompatActivity {

    public static final String EXTRA_PLACE_ID = "com.fabianoanticona.itanes.EXTRA_PLACE_ID";
    
    private PlaceRepository repository;
    private final ExecutorService executorService = Executors.newSingleThreadExecutor();
    
    private ImageView imageDetailPlace;
    private TextView textDetailName, textDetailShortDesc, textDetailFullDesc, textDetailAddress, textDetailCoords;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_place_detail);

        repository = new PlaceRepository(this);
        initViews();

        int placeId = getIntent().getIntExtra(EXTRA_PLACE_ID, -1);
        if (placeId == -1) {
            Toast.makeText(this, R.string.error_loading_place, Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        loadPlaceDetail(placeId);
    }

    private void initViews() {
        imageDetailPlace = findViewById(R.id.imageDetailPlace);
        textDetailName = findViewById(R.id.textDetailName);
        textDetailShortDesc = findViewById(R.id.textDetailShortDesc);
        textDetailFullDesc = findViewById(R.id.textDetailFullDesc);
        textDetailAddress = findViewById(R.id.textDetailAddress);
        textDetailCoords = findViewById(R.id.textDetailCoords);
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
        
        imageDetailPlace.setImageResource(android.R.drawable.ic_menu_gallery);
    }
}