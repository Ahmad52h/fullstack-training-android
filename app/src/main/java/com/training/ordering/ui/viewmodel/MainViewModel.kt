package com.training.ordering.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.training.ordering.data.model.CategoryEntity
import com.training.ordering.data.model.OrderItemRequest
import com.training.ordering.data.model.OrderRequest
import com.training.ordering.data.model.OrderResponse
import com.training.ordering.data.model.ProductEntity
import com.training.ordering.data.repository.AppRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.math.BigDecimal

// تمثيل عناصر سلة الطلبات الحالية
data class CartItem(
    val product: ProductEntity,
    var quantity: Int = 1
)

class MainViewModel(private val repository: AppRepository) : ViewModel() {

    // حالة الواجهة (UI State)
    private val _categories = MutableStateFlow<List<CategoryEntity>>(emptyList())
    val categories: StateFlow<List<CategoryEntity>> = _categories.asStateFlow()

    private val _products = MutableStateFlow<List<ProductEntity>>(emptyList())
    val products: StateFlow<List<ProductEntity>> = _products.asStateFlow()

    private val _cartItems = MutableStateFlow<Map<String, CartItem>>(emptyMap())
    val cartItems: StateFlow<Map<String, CartItem>> = _cartItems.asStateFlow()

    private val _orders = MutableStateFlow<List<OrderResponse>>(emptyList())
    val orders: StateFlow<List<OrderResponse>> = _orders.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _orderSuccessMessage = MutableStateFlow<String?>(null)
    val orderSuccessMessage: StateFlow<String?> = _orderSuccessMessage.asStateFlow()

    init {
        loadCategories()
        loadProducts()
        loadOrders()
    }

    // 1. جلب التصنيفات
    fun loadCategories() {
        viewModelScope.launch {
            repository.getCategories().collect { list ->
                _categories.value = list
            }
        }
    }

    // 2. جلب المنتجات (مع دعم الفلترة والبحث)
    fun loadProducts(categoryId: String? = null, search: String? = null) {
        viewModelScope.launch {
            _isLoading.value = true
            repository.getProducts(categoryId, search).collect { list ->
                _products.value = list
                _isLoading.value = false
            }
        }
    }

    // 3. إضافة منتج للسلة (عند الضغط المباشر على المنتج حسب متطلبات الـ PDF)
    fun addToCart(product: ProductEntity) {
        _cartItems.update { currentMap ->
            val mutableMap = currentMap.toMutableMap()
            if (mutableMap.containsKey(product.id)) {
                val existing = mutableMap[product.id]!!
                mutableMap[product.id] = existing.copy(quantity = existing.quantity + 1)
            } else {
                mutableMap[product.id] = CartItem(product = product, quantity = 1)
            }
            mutableMap
        }
    }

    // تقليل الكمية أو إزالة المنتج من السلة
    fun removeFromCart(productId: String) {
        _cartItems.update { currentMap ->
            val mutableMap = currentMap.toMutableMap()
            val existing = mutableMap[productId]
            if (existing != null) {
                if (existing.quantity > 1) {
                    mutableMap[productId] = existing.copy(quantity = existing.quantity - 1)
                } else {
                    mutableMap.remove(productId)
                }
            }
            mutableMap
        }
    }

    // حساب المجموع الإجمالي للسلة محلياً للعرض فقط
    fun calculateCartTotal(): BigDecimal {
        var total = BigDecimal.ZERO
        for (item in _cartItems.value.values) {
            val price = item.product.price.toBigDecimalOrNull() ?: BigDecimal.ZERO
            val itemTotal = price.multiply(BigDecimal.valueOf(item.quantity.toLong()))
            total = total.add(itemTotal)
        }
        return total
    }

    // 4. إرسال الطلب إلى الـ Order Service عبر الباك إند
    fun submitOrder() {
        val itemsInCart = _cartItems.value.values
        if (itemsInCart.isEmpty()) return

        viewModelScope.launch {
            _isLoading.value = true
            try {
                val orderItems = itemsInCart.map {
                    OrderItemRequest(productId = it.product.id, quantity = it.quantity)
                }
                val request = OrderRequest(items = orderItems)
                val response = repository.createOrder(request)

                // نجاح الطلب: تفريغ السلة وإضافة الطلب للقائمة وتحديث الطلبات السابقة
                _cartItems.value = emptyMap()
                _orderSuccessMessage.value = "Order submitted successfully! ID: ${response.id}"
                loadOrders()
            } catch (e: Exception) {
                _orderSuccessMessage.value = "Failed to submit order: ${e.localizedMessage}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun clearMessage() {
        _orderSuccessMessage.value = null
    }

    // 5. جلب الطلبات السابقة
    fun loadOrders() {
        viewModelScope.launch {
            try {
                _orders.value = repository.getOrders()
            } catch (e: Exception) {
                // التعامل مع الخطأ بصمت أو تخزين مؤقت
            }
        }
    }
}