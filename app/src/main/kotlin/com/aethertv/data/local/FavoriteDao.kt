package com.aethertv.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.aethertv.data.local.entity.FavoriteEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface FavoriteDao {

    @Query("SELECT * FROM favorites ORDER BY sortOrder ASC, addedAt DESC")
    fun observeAll(): Flow<List<FavoriteEntity>>

    @Query("SELECT EXISTS(SELECT 1 FROM favorites WHERE infohash = :infohash)")
    fun observeIsFavorite(infohash: String): Flow<Boolean>

    @Query("SELECT EXISTS(SELECT 1 FROM favorites WHERE infohash = :infohash)")
    suspend fun isFavorite(infohash: String): Boolean

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(favorite: FavoriteEntity)

    @Query("DELETE FROM favorites WHERE infohash = :infohash")
    suspend fun delete(infohash: String)

    @Query("SELECT infohash FROM favorites ORDER BY sortOrder ASC, addedAt DESC")
    suspend fun getAllInfohashes(): List<String>
}
