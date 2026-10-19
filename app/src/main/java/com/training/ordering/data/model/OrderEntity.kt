package com.training.ordering.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "orders")
data class OrderEntity(
    @PrimaryKey
    val id: String,
    val status: String,
    val totalPrice: String,
    val createdAt: String,
    val itemsSummary: String
)