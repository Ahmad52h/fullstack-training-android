package com.training.ordering.data.repository

import com.training.ordering.data.dao.CategoryDao
import com.training.ordering.data.dao.ProductDao
import com.training.ordering.data.model.OrderRequest
import com.training.ordering.data.model.OrderResponse
import com.training.ordering.data.network.ApiService
import kotlinx.coroutines.flow.flow

class AppRepository(
    private val apiService: ApiService,
    private val categoryDao: CategoryDao,
    private val productDao: ProductDao
) {

    // 1. جلب التصنيفات (مع التخزين المؤقت المحلي)
    fun getCategories() = flow {
        // محاولة جلب البيانات من الشبكة وحفظها محلياً
        try {
            val remoteCategories = apiService.getCategories()
            categoryDao.insertCategories(remoteCategories)
        } catch (e: Exception) {
            // في حال انقطع الإنترنت، نستمر في الاعتماد على الكاش المحلي
        }
        // إصدار البيانات من قاعدة بيانات Room المحلية
        categoryDao.getAllCategories().collect { emit(it) }
    }

    // 2. جلب المنتجات (مع دعم البحث والفلترة)
    fun getProducts(categoryId: String? = null, search: String? = null) = flow {
        try {
            val remoteProducts = apiService.getProducts(categoryId, search)
            productDao.insertProducts(remoteProducts)
        } catch (e: Exception) {
            // استخدام الكاش المحلي عند انقطاع الاتصال
        }

        when {
            !categoryId.isNullOrBlank() -> productDao.getProductsByCategory(categoryId).collect { emit(it) }
            !search.isNullOrBlank() -> productDao.searchProducts(search).collect { emit(it) }
            else -> productDao.getAllProducts().collect { emit(it) }
        }
    }

    // 3. إرسال طلب شراء جديد للباك إند
    suspend fun createOrder(request: OrderRequest): OrderResponse {
        return apiService.createOrder(request)
    }

    // 4. جلب الطلبات السابقة
    suspend fun getOrders(): List<OrderResponse> {
        return apiService.getOrders()
    }
}