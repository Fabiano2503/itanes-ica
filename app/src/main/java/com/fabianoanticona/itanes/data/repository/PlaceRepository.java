package com.fabianoanticona.itanes.data.repository;

import android.content.Context;

import com.fabianoanticona.itanes.data.local.dao.PlaceDao;
import com.fabianoanticona.itanes.data.local.database.AppDatabase;
import com.fabianoanticona.itanes.data.local.entity.PlaceEntity;

import java.util.List;

public class PlaceRepository {

    private final PlaceDao placeDao;

    public PlaceRepository(Context context) {
        AppDatabase db = AppDatabase.getInstance(context);
        this.placeDao = db.placeDao();
    }

    public List<PlaceEntity> getAllPlaces() {
        return placeDao.getAllPlaces();
    }

    public PlaceEntity getPlaceById(int id) {
        return placeDao.getPlaceById(id);
    }

    public void insertPlaces(List<PlaceEntity> places) {
        placeDao.insertAll(places);
    }

    public void deleteAllPlaces() {
        placeDao.deleteAll();
    }
}