package com.training.ordering.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.training.ordering.data.model.ProductEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ProductDao {
    @Query("SELECT * FROM products")
    fun getAllProducts(): Flow<List<ProductEntity>>

    @Query("SELECT * FROM products WHERE categoryId = :categoryId")
    fun getProductsByCategory(categoryId: String): Flow<List<ProductEntity>>

    @Query("SELECT * FROM products WHERE name LIKE '%' || :searchQuery || '%'")
    fun searchProducts(searchQuery: String): Flow<List<ProductEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertProducts(products: List<ProductEntity>)
}