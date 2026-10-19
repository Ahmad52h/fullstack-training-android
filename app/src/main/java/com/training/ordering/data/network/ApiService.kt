package com.training.ordering.data.network

import com.training.ordering.data.model.CategoryEntity
import com.training.ordering.data.model.OrderPayloads
import com.training.ordering.data.model.OrderRequest
import com.training.ordering.data.model.OrderResponse
import com.training.ordering.data.model.ProductEntity
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.request.parameter
import io.ktor.http.ContentType
import io.ktor.http.contentType

class ApiService(private val client: HttpClient) {

    // ملاحظة: في محاكي أندرويد (Android Emulator)، عنوان localhost يتم تمثيله بـ 10.0.2.2 للوصول للكمبيوتر المضيف
    private val baseUrl = "http://10.0.2.2:8080/api"

    // 1. جلب التصنيفات
    suspend fun getCategories(): List<CategoryEntity> {
        return client.get("$baseUrl/categories").body()
    }

    // 2. جلب المنتجات (مع إمكانية الفلترة والبحث)
    suspend fun getProducts(categoryId: String? = null, search: String? = null): List<ProductEntity> {
        return client.get("$baseUrl/products") {
            if (!categoryId.isNullOrBlank()) {
                parameter("categoryId", categoryId)
            }
            if (!search.isNullOrBlank()) {
                parameter("search", search)
            }
        }.body()
    }

    // 3. إرسال طلب شراء جديد
    suspend fun createOrder(request: OrderRequest): OrderResponse {
        return client.post("$baseUrl/orders") {
            contentType(ContentType.Application.Json)
            setBody(request)
        }.body()
    }

    // 4. جلب الطلبات السابقة
    suspend fun getOrders(): List<OrderResponse> {
        return client.get("$baseUrl/orders").body()
    }
}