package com.fabianoanticona.itanes.data.repository;

import android.content.Context;

import com.fabianoanticona.itanes.data.local.dao.FavoriteDao;
import com.fabianoanticona.itanes.data.local.database.AppDatabase;
import com.fabianoanticona.itanes.data.local.entity.FavoriteEntity;

import java.util.List;

public class FavoriteRepository {

    private final FavoriteDao favoriteDao;

    public FavoriteRepository(Context context) {
        AppDatabase db = AppDatabase.getInstance(context);
        this.favoriteDao = db.favoriteDao();
    }

    public void addFavorite(FavoriteEntity favorite) {
        favoriteDao.insertFavorite(favorite);
    }

    public void removeFavorite(int placeId) {
        favoriteDao.deleteFavorite(placeId);
    }

    public boolean isFavorite(int placeId) {
        return favoriteDao.isFavorite(placeId);
    }

    public List<Integer> getAllFavoriteIds() {
        return favoriteDao.getAllFavoriteIds();
    }
}