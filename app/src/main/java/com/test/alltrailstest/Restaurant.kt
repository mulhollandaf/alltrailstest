package com.test.alltrailstest

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Restaurant (
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val name: String,
    val rating: Float,
    val priceLevel: Int,
    val vicinity: String,
)