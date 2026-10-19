package com.training.ordering.data

import androidx.room.Database
import androidx.room.RoomDatabase
import com.training.ordering.data.dao.CategoryDao
import com.training.ordering.data.dao.OrderDao
import com.training.ordering.data.dao.ProductDao
import com.training.ordering.data.model.CategoryEntity
import com.training.ordering.data.model.OrderEntity
import com.training.ordering.data.model.ProductEntity

@Database(
    entities = [CategoryEntity::class, ProductEntity::class, OrderEntity::class],
    version = 2,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun categoryDao(): CategoryDao
    abstract fun productDao(): ProductDao
    abstract fun orderDao(): OrderDao
}                       