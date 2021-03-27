package com.test.alltrailstest

import com.dropbox.android.external.store4.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.mapLatest
import kotlinx.coroutines.withContext
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import timber.log.Timber
import java.lang.Exception
import javax.inject.Inject

class RestaurantRepository
@Inject constructor(
    private val restaurantLocal: RestaurantLocalDataSource,
){

    var lastKey: RestaurantKey? = null
    private val restaurantStore = StoreBuilder.from<RestaurantKey, RestaurantsDto, List<RestaurantInfo>>(
        fetcher = Fetcher.ofResult {key -> fetchRestaurants(key.search, key.latitude, key.longitude) },
        sourceOfTruth = SourceOfTruth.Companion.of(
            reader = {getRestaurantsFromDB()},
            writer = {_, restaurantsDto -> writeRestaurantsForStore(restaurantsDto.results)}
        )
    ).build()

    private suspend fun fetchRestaurants(search: String, latitude: Double, longitude: Double): FetcherResult<RestaurantsDto> =
        withContext(Dispatchers.IO) {
            try {
                val location = "$latitude, $longitude"
                Timber.d("Searching for $location $search")
                val response = provideGoogleApi().fetchRestaurants(search, location)
                if (response.isSuccessful) {
                    response.body()?.let {
                        return@withContext FetcherResult.Data(it)
                    }
                }
                Timber.w("Failed to get Restaurants | ${response.code()} | ${response.message()}")
            } catch (e: Exception) {
                Timber.e(e, "Failed to get Restaurants")
                return@withContext FetcherResult.Error.Exception(e)
            }
            return@withContext FetcherResult.Error.Message("Failed to get Restaurants")
        }

    fun getRestaurants(search: String, latitude: Double, longitude: Double, force: Boolean): Flow<StoreResponse<List<RestaurantInfo>>> {
        val key = RestaurantKey(search, latitude, longitude)
        val shouldForce = (force || lastKey == null || key != lastKey)
        lastKey = key
        Timber.d("Getting restaurants with key ${key.search}")
        return restaurantStore.stream(StoreRequest.cached(key, shouldForce))
    }

    private fun getRestaurantsFromDB(): Flow<List<RestaurantInfo>> {
        return restaurantLocal.getAllRestaurants().mapLatest {
            it.map {restaurant ->
                Timber.d("Mapping Restaurant ${restaurant.name}")
                RestaurantInfo(restaurant.name, restaurant.rating.toString(), getPriceLevelString(restaurant.priceLevel), restaurant.vicinity)
            }
        }
    }

    private suspend fun writeRestaurantsForStore(RestaurantsDto: List<RestaurantDto>) {
        restaurantLocal.deleteAllRestaurants()
        val restaurant = RestaurantsDto.mapNotNull {
            Timber.d("Writing Restaurant ${it.name}")
                Restaurant(name = it.name, rating = it.rating, priceLevel = it.price_level, vicinity = it.vicinity)
        }
        restaurantLocal.insertRestaurants(restaurant)
    }

    private fun getPriceLevelString(priceLevel: Int): String {
        var dollars = ""
        for (i in 1..priceLevel) {
            dollars += "$"
        }

        return dollars
    }

    private fun provideGoogleApi(): Api {
        return Retrofit.Builder()
            .baseUrl("https://maps.googleapis.com/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(Api::class.java)
    }

}

data class RestaurantKey(val search: String, val latitude: Double, val longitude: Double)
