package com.test.alltrailstest

import android.view.View
import androidx.databinding.ObservableField

data class RestaurantInfo(val name: String, val rating: String, val priceLevel: String, val vicinity: String) {
    var showExpanded = ObservableField(View.GONE)
    fun toggleExpanded() {
        if (showExpanded.get() == View.GONE) {
            showExpanded.set(View.VISIBLE)
        } else {
            showExpanded.set(View.GONE)
        }
    }
}