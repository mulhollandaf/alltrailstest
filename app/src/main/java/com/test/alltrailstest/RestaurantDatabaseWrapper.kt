package com.test.alltrailstest

import android.app.Application
import androidx.room.Room
import org.dbtools.android.room.CloseableDatabaseWrapper
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class RestaurantDatabaseWrapper
@Inject constructor(
    context: Application
) : CloseableDatabaseWrapper<RestaurantDatabase>(context)
{
    override fun createDatabase(): RestaurantDatabase {
        return Room.databaseBuilder(context, RestaurantDatabase::class.java, RestaurantDatabase.DATABASE_FILENAME)
            .build()
    }

    fun getRestaurantDao() = getDatabase().restaurantDao
}
