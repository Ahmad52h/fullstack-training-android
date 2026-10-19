package com.training.ordering.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.serialization.Serializable

@Serializable
@Entity(tableName = "products")
data class ProductEntity(
    @PrimaryKey
    val id: String,
    val categoryId: String,
    val name: String,
    val description: String? = null,
    val image: String,
    val price: String // بنخزنه String عشان يسهل حفظه وقراءته مع BigDecimal/Ktor
)