package com.fabianoanticona.itanes.data.repository;

import android.content.Context;

import com.fabianoanticona.itanes.data.local.dao.FavoriteDao;
import com.fabianoanticona.itanes.data.local.database.AppDatabase;
import com.fabianoanticona.itanes.data.local.entity.FavoriteEntity;

import java.util.List;

public class FavoriteRepository {

    private static volatile FavoriteRepository INSTANCE;
    private final FavoriteDao favoriteDao;

    private FavoriteRepository(Context context) {
        AppDatabase db = AppDatabase.getInstance(context);
        this.favoriteDao = db.favoriteDao();
    }

    public static FavoriteRepository getInstance(final Context context) {
        if (INSTANCE == null) {
            synchronized (FavoriteRepository.class) {
                if (INSTANCE == null) {
                    INSTANCE = new FavoriteRepository(context);
                }
            }
        }
        return INSTANCE;
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