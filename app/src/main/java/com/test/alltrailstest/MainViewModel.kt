package com.test.alltrailstest

import androidx.databinding.ObservableField
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.ReceiveChannel

import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.mapLatest
import javax.inject.Inject
import kotlinx.coroutines.flow.flowOn

@HiltViewModel
class MainViewModel
@Inject constructor(
    private val restaurantRepository: RestaurantRepository,

    ): ViewModel() {
    private val _eventChannel: ViewModelChannel<Event> = ViewModelChannel(this)
    val eventChannel: ReceiveChannel<Event> = _eventChannel

    val searchTerm = ObservableField("")
    var latestLatitude = 0.0
    var latestLongitude = 0.0
    var force = true

    fun queryRestaurants(): Flow<List<RestaurantInfo>> {
        return restaurantRepository.getRestaurants(searchTerm.get() ?: "", latestLatitude, latestLongitude, force)
            .mapLatest {
                val data = it.dataOrNull()
                if (data == null) {
                    emptyList()
                } else {
                    force = false
                    data
                }
            }
            .distinctUntilChanged()
            .flowOn(Dispatchers.IO)
    }

    fun startQuery() {
        _eventChannel.sendAsync(Event.StartQuery)
    }

    fun itemClicked(restaurantInfo: RestaurantInfo) {
        restaurantInfo.toggleExpanded()
    }

    sealed class Event {
        object StartQuery : Event()
    }

}