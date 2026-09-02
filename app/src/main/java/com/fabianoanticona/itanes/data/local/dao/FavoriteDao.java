package com.fabianoanticona.itanes.data.local.dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.fabianoanticona.itanes.data.local.entity.FavoriteEntity;

import java.util.List;

@Dao
public interface FavoriteDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertFavorite(FavoriteEntity favorite);

    @Query("DELETE FROM favorites WHERE placeId = :placeId")
    void deleteFavorite(int placeId);

    @Query("SELECT EXISTS(SELECT 1 FROM favorites WHERE placeId = :placeId)")
    boolean isFavorite(int placeId);

    @Query("SELECT placeId FROM favorites")
    List<Integer> getAllFavoriteIds();
}