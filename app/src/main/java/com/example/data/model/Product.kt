package com.example.data.model

import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentId
import com.google.firebase.firestore.IgnoreExtraProperties
import com.google.firebase.firestore.ServerTimestamp

@IgnoreExtraProperties
data class Product(
    @DocumentId
    val id: String = "",
    val name: String = "",
    val description: String = "",
    val category: String = "",
    val basePrice: Double = 0.0,
    val originalPrice: Double = 0.0,
    val discountPercent: Int = 0,
    val images: List<String> = emptyList(),
    val sizes: List<String> = emptyList(),
    val colors: List<String> = emptyList(),
    val materials: String = "",
    val stock: Int = 0,
    val isAvailable: Boolean = true,
    val sku: String = "",
    val rating: Double = 5.0,
    val reviewsCount: Int = 0,
    val badge: String = "جديد", // جديد، مميز، الأكثر مبيعاً
    @ServerTimestamp
    val createdAt: Timestamp? = null,
    @ServerTimestamp
    val updatedAt: Timestamp? = null
) {
    val finalPrice: Double
        get() = if (originalPrice > basePrice && discountPercent > 0) basePrice else if (originalPrice > 0.0) basePrice else basePrice

    val hasDiscount: Boolean
        get() = originalPrice > basePrice || discountPercent > 0

    val primaryImage: String
        get() = images.firstOrNull().orEmpty()
}
