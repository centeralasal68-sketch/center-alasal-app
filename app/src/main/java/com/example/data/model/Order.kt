package com.example.data.model

import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentId
import com.google.firebase.firestore.IgnoreExtraProperties
import com.google.firebase.firestore.ServerTimestamp

@IgnoreExtraProperties
data class OrderItem(
    val productId: String = "",
    val title: String = "",
    val price: Double = 0.0,
    val quantity: Int = 1,
    val imageUrl: String = "",
    val selectedSize: String = "",
    val selectedColor: String = ""
)

@IgnoreExtraProperties
data class Order(
    @DocumentId
    val id: String = "", // CAA-2026-XXXXX
    val userId: String = "",
    val customerName: String = "",
    val customerPhone: String = "",
    val governorate: String = "الإسكندرية",
    val city: String = "",
    val address: String = "",
    val notes: String = "",
    val items: List<OrderItem> = emptyList(),
    val subtotal: Double = 0.0,
    val shippingFee: Double = 0.0,
    val total: Double = 0.0,
    val paymentMethod: String = "cash_on_delivery", // cash_on_delivery, instapay, wallet
    val paymentProofUrl: String = "",
    val paymentStatus: String = "pending", // pending, verified, rejected
    val status: String = "جديد", // جديد، قيد المراجعة، تم التأكيد، قيد التجهيز، تم الشحن، تم التسليم، ملغي، مرتجع
    val internalNotes: String = "",
    @ServerTimestamp
    val createdAt: Timestamp? = null,
    @ServerTimestamp
    val updatedAt: Timestamp? = null
) {
    val itemsCount: Int
        get() = items.sumOf { it.quantity }
}
