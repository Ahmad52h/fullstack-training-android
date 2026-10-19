package com.training.ordering.data.model

import kotlinx.serialization.Serializable

@Serializable
data class OrderItemRequest(
    val productId: String,
    val quantity: Int
)

@Serializable
data class OrderRequest(
    val items: List<OrderItemRequest>
)

@Serializable
data class OrderItemResponse(
    val id: String,
    val productId: String,
    val productName: String,
    val quantity: Int,
    val unitPrice: String
)

@Serializable
data class OrderResponse(
    val id: String,
    val status: String,
    val totalPrice: String,
    val createdAt: String,
    val items: List<OrderItemResponse>
)