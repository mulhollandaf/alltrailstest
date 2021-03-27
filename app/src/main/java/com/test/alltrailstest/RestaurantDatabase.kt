package com.test.alltrailstest

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(
    entities = [
        Restaurant::class,
    ],
    version = 1
)
abstract class RestaurantDatabase : RoomDatabase() {

    abstract val restaurantDao: RestaurantDao

    companion object {
        const val DATABASE_FILENAME = "restaurant.db"
    }
}
