package com.example.data.repository

import android.content.Context
import com.example.R
import com.example.data.model.CartItem
import com.example.data.model.Product
import com.example.data.model.WishlistItem
import com.example.util.OperationType
import com.example.util.handleFirestoreError
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.snapshots
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.tasks.await

class CartRepository(private val db: FirebaseFirestore) {

    constructor(context: Context) : this(
        FirebaseFirestore.getInstance(
            context.applicationContext.getString(R.string.firestore_database_id)
        )
    )

    private fun getCartCol(uid: String) = db.collection("users").document(uid).collection("cart")

    private fun requireUserId(): String {
        return Firebase.auth.currentUser?.uid
            ?: throw IllegalStateException("User must be signed in with Google to manage cart.")
    }

    fun observeCart(): Flow<List<CartItem>> {
        val uid = requireUserId()
        val cartCol = getCartCol(uid)
        return cartCol.snapshots()
            .map { snapshot -> snapshot.toObjects(CartItem::class.java) }
            .catch { error ->
                if (error !is CancellationException) {
                    handleFirestoreError(error, OperationType.LIST, cartCol.path)
                }
                throw error
            }
    }

    suspend fun addToCart(
        product: Product,
        quantity: Int = 1,
        selectedSize: String = "",
        selectedColor: String = ""
    ): Result<Unit> {
        val uid = requireUserId()
        val cartCol = getCartCol(uid)
        val itemId = "${product.id}_${selectedSize.trim()}_${selectedColor.trim()}".replace(Regex("[^a-zA-Z0-9_\\-]"), "_")
        val docRef = cartCol.document(itemId)

        return try {
            val existing = docRef.get().await()
            if (existing.exists()) {
                val currentQty = existing.getLong("quantity")?.toInt() ?: 1
                docRef.update(
                    mapOf(
                        "quantity" to (currentQty + quantity).coerceAtMost(100),
                        "updatedAt" to FieldValue.serverTimestamp()
                    )
                ).await()
            } else {
                val payload = hashMapOf<String, Any?>(
                    "productId" to product.id,
                    "title" to product.name,
                    "price" to product.finalPrice,
                    "imageUrl" to product.primaryImage,
                    "quantity" to quantity.coerceIn(1, 100),
                    "selectedSize" to selectedSize,
                    "selectedColor" to selectedColor,
                    "createdAt" to FieldValue.serverTimestamp(),
                    "updatedAt" to FieldValue.serverTimestamp()
                ).filterValues { it != null }
                docRef.set(payload).await()
            }
            Result.success(Unit)
        } catch (e: Exception) {
            handleFirestoreError(e, OperationType.CREATE, docRef.path)
            Result.failure(e)
        }
    }

    suspend fun updateQuantity(itemId: String, newQuantity: Int): Result<Unit> {
        val uid = requireUserId()
        val docRef = getCartCol(uid).document(itemId)
        return try {
            if (newQuantity <= 0) {
                docRef.delete().await()
            } else {
                docRef.update(
                    mapOf(
                        "quantity" to newQuantity.coerceAtMost(100),
                        "updatedAt" to FieldValue.serverTimestamp()
                    )
                ).await()
            }
            Result.success(Unit)
        } catch (e: Exception) {
            handleFirestoreError(e, OperationType.UPDATE, docRef.path)
            Result.failure(e)
        }
    }

    suspend fun removeItem(itemId: String): Result<Unit> {
        val uid = requireUserId()
        val docRef = getCartCol(uid).document(itemId)
        return try {
            docRef.delete().await()
            Result.success(Unit)
        } catch (e: Exception) {
            handleFirestoreError(e, OperationType.DELETE, docRef.path)
            Result.failure(e)
        }
    }

    suspend fun clearCart(): Result<Unit> {
        val uid = requireUserId()
        val cartCol = getCartCol(uid)
        return try {
            val snapshot = cartCol.get().await()
            for (doc in snapshot.documents) {
                doc.reference.delete().await()
            }
            Result.success(Unit)
        } catch (e: Exception) {
            handleFirestoreError(e, OperationType.DELETE, cartCol.path)
            Result.failure(e)
        }
    }
}

class WishlistRepository(private val db: FirebaseFirestore) {

    constructor(context: Context) : this(
        FirebaseFirestore.getInstance(
            context.applicationContext.getString(R.string.firestore_database_id)
        )
    )

    private fun getWishlistCol(uid: String) = db.collection("users").document(uid).collection("wishlist")

    private fun requireUserId(): String {
        return Firebase.auth.currentUser?.uid
            ?: throw IllegalStateException("User must be signed in with Google to manage wishlist.")
    }

    fun observeWishlist(): Flow<List<WishlistItem>> {
        val uid = requireUserId()
        val wishlistCol = getWishlistCol(uid)
        return wishlistCol.snapshots()
            .map { snapshot -> snapshot.toObjects(WishlistItem::class.java) }
            .catch { error ->
                if (error !is CancellationException) {
                    handleFirestoreError(error, OperationType.LIST, wishlistCol.path)
                }
                throw error
            }
    }

    suspend fun toggleWishlist(product: Product): Result<Boolean> {
        val uid = requireUserId()
        val docRef = getWishlistCol(uid).document(product.id)
        return try {
            val doc = docRef.get().await()
            if (doc.exists()) {
                docRef.delete().await()
                Result.success(false) // removed
            } else {
                val payload = hashMapOf<String, Any?>(
                    "productId" to product.id,
                    "title" to product.name,
                    "price" to product.finalPrice,
                    "imageUrl" to product.primaryImage,
                    "category" to product.category,
                    "createdAt" to FieldValue.serverTimestamp()
                ).filterValues { it != null }
                docRef.set(payload).await()
                Result.success(true) // added
            }
        } catch (e: Exception) {
            handleFirestoreError(e, OperationType.WRITE, docRef.path)
            Result.failure(e)
        }
    }
}
