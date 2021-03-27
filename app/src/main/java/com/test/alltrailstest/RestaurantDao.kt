package com.test.alltrailstest

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface RestaurantDao {
    @Insert
    suspend fun insert(question: Restaurant)

    @Query("DELETE FROM Restaurant")
    suspend fun deleteAll()

    @Query("SELECT * FROM Restaurant")
    fun findAll(): Flow<List<Restaurant>>
}