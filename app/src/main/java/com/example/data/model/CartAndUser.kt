package com.example.data.model

import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentId
import com.google.firebase.firestore.IgnoreExtraProperties
import com.google.firebase.firestore.ServerTimestamp

@IgnoreExtraProperties
data class CartItem(
    @DocumentId
    val id: String = "",
    val productId: String = "",
    val title: String = "",
    val price: Double = 0.0,
    val imageUrl: String = "",
    val quantity: Int = 1,
    val selectedSize: String = "",
    val selectedColor: String = "",
    @ServerTimestamp
    val createdAt: Timestamp? = null,
    @ServerTimestamp
    val updatedAt: Timestamp? = null
) {
    val totalPrice: Double
        get() = price * quantity
}

@IgnoreExtraProperties
data class WishlistItem(
    @DocumentId
    val id: String = "",
    val productId: String = "",
    val title: String = "",
    val price: Double = 0.0,
    val imageUrl: String = "",
    val category: String = "",
    @ServerTimestamp
    val createdAt: Timestamp? = null
)

@IgnoreExtraProperties
data class UserProfile(
    @DocumentId
    val id: String = "",
    val userId: String = "",
    val displayName: String = "",
    val email: String = "",
    val phoneNumber: String = "",
    val role: String = "customer", // customer, admin, super_admin, orders_manager, products_manager, support
    @ServerTimestamp
    val createdAt: Timestamp? = null,
    @ServerTimestamp
    val updatedAt: Timestamp? = null
)
