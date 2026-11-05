package com.example.data.repository

import android.content.Context
import com.example.R
import com.example.data.model.Order
import com.example.util.OperationType
import com.example.util.handleFirestoreError
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.snapshots
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.tasks.await
import java.util.Locale

class OrderRepository(private val db: FirebaseFirestore) {

    constructor(context: Context) : this(
        FirebaseFirestore.getInstance(
            context.applicationContext.getString(R.string.firestore_database_id)
        )
    )

    private val ordersCol = db.collection("orders")
    private val metaDoc = db.collection("store_settings").document("order_counter")

    private fun requireUserId(): String {
        return Firebase.auth.currentUser?.uid
            ?: throw IllegalStateException("User must be signed in with Google before placing or querying orders.")
    }

    /**
     * Generates atomic order sequence code: CAA-2026-00001
     */
    private suspend fun generateOrderCode(): String {
        return try {
            val nextNumber = db.runTransaction { transaction ->
                val snapshot = transaction.get(metaDoc)
                val current = if (snapshot.exists()) {
                    snapshot.getLong("lastOrderNumber") ?: 0L
                } else {
                    0L
                }
                val next = current + 1L
                transaction.set(metaDoc, mapOf("lastOrderNumber" to next), com.google.firebase.firestore.SetOptions.merge())
                next
            }.await()
            String.format(Locale.US, "CAA-2026-%05d", nextNumber)
        } catch (e: Exception) {
            // High-entropy fallback if transaction is constrained
            val randomNum = (10000..99999).random()
            "CAA-2026-$randomNum"
        }
    }

    suspend fun createOrder(order: Order): Result<String> {
        val uid = requireUserId()
        return try {
            val orderCode = if (order.id.isNotBlank() && order.id.startsWith("CAA-2026-")) {
                order.id
            } else {
                generateOrderCode()
            }

            val docRef = ordersCol.document(orderCode)
            val itemsPayload = order.items.map { item ->
                mapOf(
                    "productId" to item.productId,
                    "title" to item.title,
                    "price" to item.price,
                    "quantity" to item.quantity,
                    "imageUrl" to item.imageUrl,
                    "selectedSize" to item.selectedSize,
                    "selectedColor" to item.selectedColor
                )
            }

            val payload = hashMapOf<String, Any?>(
                "userId" to uid,
                "customerName" to order.customerName,
                "customerPhone" to order.customerPhone,
                "governorate" to order.governorate,
                "city" to order.city,
                "address" to order.address,
                "notes" to order.notes,
                "items" to itemsPayload,
                "subtotal" to order.subtotal,
                "shippingFee" to order.shippingFee,
                "total" to order.total,
                "paymentMethod" to order.paymentMethod,
                "paymentProofUrl" to order.paymentProofUrl,
                "paymentStatus" to order.paymentStatus,
                "status" to order.status,
                "internalNotes" to order.internalNotes,
                "createdAt" to FieldValue.serverTimestamp(),
                "updatedAt" to FieldValue.serverTimestamp()
            ).filterValues { it != null }

            docRef.set(payload).await()
            Result.success(orderCode)
        } catch (e: Exception) {
            handleFirestoreError(e, OperationType.CREATE, ordersCol.path)
            Result.failure(e)
        }
    }

    fun observeUserOrders(): Flow<List<Order>> {
        val uid = requireUserId()
        return ordersCol
            .whereEqualTo("userId", uid)
            .snapshots()
            .map { snapshot ->
                snapshot.toObjects(Order::class.java).sortedByDescending { it.createdAt?.toDate()?.time ?: 0L }
            }
            .catch { error ->
                if (error !is CancellationException) {
                    handleFirestoreError(error, OperationType.LIST, ordersCol.path)
                }
                throw error
            }
    }

    fun observeAllOrdersForAdmin(): Flow<List<Order>> {
        return ordersCol
            .snapshots()
            .map { snapshot ->
                snapshot.toObjects(Order::class.java).sortedByDescending { it.createdAt?.toDate()?.time ?: 0L }
            }
            .catch { error ->
                if (error !is CancellationException) {
                    handleFirestoreError(error, OperationType.LIST, ordersCol.path)
                }
                throw error
            }
    }

    suspend fun getOrderByCodeAndPhone(code: String, phone: String): Order? {
        return try {
            val doc = ordersCol.document(code.trim().uppercase()).get().await()
            if (doc.exists()) {
                val order = doc.toObject(Order::class.java)
                if (order != null && (order.customerPhone.trim() == phone.trim() || phone.isBlank())) {
                    return order
                }
            }
            null
        } catch (e: Exception) {
            handleFirestoreError(e, OperationType.GET, "${ordersCol.path}/$code")
            null
        }
    }

    suspend fun updateOrderStatus(orderId: String, newStatus: String): Result<Unit> {
        val docRef = ordersCol.document(orderId)
        return try {
            docRef.update(
                mapOf(
                    "status" to newStatus,
                    "updatedAt" to FieldValue.serverTimestamp()
                )
            ).await()
            Result.success(Unit)
        } catch (e: Exception) {
            handleFirestoreError(e, OperationType.UPDATE, docRef.path)
            Result.failure(e)
        }
    }

    suspend fun updatePaymentStatus(orderId: String, newPaymentStatus: String, internalNotes: String = ""): Result<Unit> {
        val docRef = ordersCol.document(orderId)
        return try {
            val updates = mutableMapOf<String, Any>(
                "paymentStatus" to newPaymentStatus,
                "updatedAt" to FieldValue.serverTimestamp()
            )
            if (internalNotes.isNotBlank()) {
                updates["internalNotes"] = internalNotes
            }
            docRef.update(updates).await()
            Result.success(Unit)
        } catch (e: Exception) {
            handleFirestoreError(e, OperationType.UPDATE, docRef.path)
            Result.failure(e)
        }
    }

    suspend fun updatePaymentProof(orderId: String, proofUrl: String): Result<Unit> {
        val docRef = ordersCol.document(orderId)
        return try {
            docRef.update(
                mapOf(
                    "paymentProofUrl" to proofUrl,
                    "updatedAt" to FieldValue.serverTimestamp()
                )
            ).await()
            Result.success(Unit)
        } catch (e: Exception) {
            handleFirestoreError(e, OperationType.UPDATE, docRef.path)
            Result.failure(e)
        }
    }
}
