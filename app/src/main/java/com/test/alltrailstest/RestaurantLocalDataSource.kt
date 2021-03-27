package com.test.alltrailstest

import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class RestaurantLocalDataSource
@Inject constructor(
    databaseWrapper: RestaurantDatabaseWrapper
) {
    private val restaurantDao = databaseWrapper.getRestaurantDao()
    fun getAllRestaurants(): Flow<List<Restaurant>> {
        return restaurantDao.findAll()
    }

    suspend fun deleteAllRestaurants() {
        restaurantDao.deleteAll()
    }

    suspend fun insertRestaurants(questions: List<Restaurant>) {
        questions.forEach {
            restaurantDao.insert(it)
        }
    }
}
