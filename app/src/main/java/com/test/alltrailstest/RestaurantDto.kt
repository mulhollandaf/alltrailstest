package com.test.alltrailstest

data class RestaurantDto(val name: String, val rating: Float, val price_level: Int, val vicinity: String, val types: List<String>)

data class RestaurantsDto(val results: List<RestaurantDto>)