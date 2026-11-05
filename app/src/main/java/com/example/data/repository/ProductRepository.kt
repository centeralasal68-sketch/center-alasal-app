package com.example.data.repository

import android.content.Context
import com.example.R
import com.example.data.model.Category
import com.example.data.model.Product
import com.example.util.OperationType
import com.example.util.handleFirestoreError
import com.google.firebase.Firebase
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.snapshots
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.tasks.await

class ProductRepository(private val db: FirebaseFirestore) {

    constructor(context: Context) : this(
        FirebaseFirestore.getInstance(
            context.applicationContext.getString(R.string.firestore_database_id)
        )
    )

    private val productsCol = db.collection("products")
    private val categoriesCol = db.collection("categories")

    // Default categories specified by user
    val defaultCategoryNames = listOf(
        "أطقم الملايات",
        "البطاطين",
        "اللحاف",
        "المفارش",
        "الفوط والبشاكير",
        "المخدات",
        "السجاد",
        "الستائر",
        "منتجات أخرى"
    )

    fun observeProducts(): Flow<List<Product>> {
        return productsCol.snapshots()
            .map { snapshot -> snapshot.toObjects(Product::class.java) }
            .catch { error ->
                if (error !is CancellationException) {
                    handleFirestoreError(error, OperationType.LIST, productsCol.path)
                }
                throw error
            }
    }

    fun observeCategories(): Flow<List<Category>> {
        return categoriesCol.orderBy("sortOrder").snapshots()
            .map { snapshot -> snapshot.toObjects(Category::class.java) }
            .catch { error ->
                if (error !is CancellationException) {
                    handleFirestoreError(error, OperationType.LIST, categoriesCol.path)
                }
                throw error
            }
    }

    suspend fun getProductById(id: String): Product? {
        return try {
            val doc = productsCol.document(id).get().await()
            doc.toObject(Product::class.java)
        } catch (e: Exception) {
            handleFirestoreError(e, OperationType.GET, "${productsCol.path}/$id")
            null
        }
    }

    suspend fun addProduct(product: Product): Result<String> {
        return try {
            val docRef = if (product.id.isNotBlank()) productsCol.document(product.id) else productsCol.document()
            val payload = hashMapOf<String, Any?>(
                "name" to product.name,
                "description" to product.description,
                "category" to product.category,
                "basePrice" to product.basePrice,
                "originalPrice" to product.originalPrice,
                "discountPercent" to product.discountPercent,
                "images" to product.images,
                "sizes" to product.sizes,
                "colors" to product.colors,
                "materials" to product.materials,
                "stock" to product.stock,
                "isAvailable" to product.isAvailable,
                "sku" to product.sku,
                "rating" to product.rating,
                "reviewsCount" to product.reviewsCount,
                "badge" to product.badge,
                "createdAt" to FieldValue.serverTimestamp(),
                "updatedAt" to FieldValue.serverTimestamp()
            ).filterValues { it != null }

            docRef.set(payload).await()
            Result.success(docRef.id)
        } catch (e: Exception) {
            handleFirestoreError(e, OperationType.CREATE, productsCol.path)
            Result.failure(e)
        }
    }

    suspend fun updateProduct(product: Product): Result<Unit> {
        require(product.id.isNotBlank()) { "Product ID cannot be empty for update" }
        val docRef = productsCol.document(product.id)
        return try {
            val payload = hashMapOf<String, Any?>(
                "name" to product.name,
                "description" to product.description,
                "category" to product.category,
                "basePrice" to product.basePrice,
                "originalPrice" to product.originalPrice,
                "discountPercent" to product.discountPercent,
                "images" to product.images,
                "sizes" to product.sizes,
                "colors" to product.colors,
                "materials" to product.materials,
                "stock" to product.stock,
                "isAvailable" to product.isAvailable,
                "sku" to product.sku,
                "rating" to product.rating,
                "reviewsCount" to product.reviewsCount,
                "badge" to product.badge,
                "updatedAt" to FieldValue.serverTimestamp()
            ).filterValues { it != null }

            docRef.update(payload).await()
            Result.success(Unit)
        } catch (e: Exception) {
            handleFirestoreError(e, OperationType.UPDATE, docRef.path)
            Result.failure(e)
        }
    }

    suspend fun deleteProduct(productId: String): Result<Unit> {
        val docRef = productsCol.document(productId)
        return try {
            docRef.delete().await()
            Result.success(Unit)
        } catch (e: Exception) {
            handleFirestoreError(e, OperationType.DELETE, docRef.path)
            Result.failure(e)
        }
    }

    suspend fun addCategory(category: Category): Result<String> {
        return try {
            val docRef = if (category.id.isNotBlank()) categoriesCol.document(category.id) else categoriesCol.document()
            val payload = hashMapOf(
                "name" to category.name,
                "iconName" to category.iconName,
                "imageUrl" to category.imageUrl,
                "description" to category.description,
                "sortOrder" to category.sortOrder,
                "createdAt" to FieldValue.serverTimestamp(),
                "updatedAt" to FieldValue.serverTimestamp()
            )
            docRef.set(payload).await()
            Result.success(docRef.id)
        } catch (e: Exception) {
            handleFirestoreError(e, OperationType.CREATE, categoriesCol.path)
            Result.failure(e)
        }
    }

    suspend fun deleteCategory(categoryId: String): Result<Unit> {
        val docRef = categoriesCol.document(categoryId)
        return try {
            docRef.delete().await()
            Result.success(Unit)
        } catch (e: Exception) {
            handleFirestoreError(e, OperationType.DELETE, docRef.path)
            Result.failure(e)
        }
    }

    suspend fun initializeDefaultCategoriesIfEmpty() {
        try {
            val existing = categoriesCol.limit(1).get().await()
            if (existing.isEmpty) {
                defaultCategoryNames.forEachIndexed { index, name ->
                    val docRef = categoriesCol.document("cat_$index")
                    docRef.set(
                        mapOf(
                            "name" to name,
                            "iconName" to "bedding_$index",
                            "imageUrl" to "",
                            "description" to "قسم $name في سنتر العسال للمفروشات",
                            "sortOrder" to index,
                            "createdAt" to FieldValue.serverTimestamp(),
                            "updatedAt" to FieldValue.serverTimestamp()
                        )
                    ).await()
                }
            }
        } catch (e: Exception) {
            // Log quietly if initialization cannot proceed due to permissions or network
            handleFirestoreError(e, OperationType.CREATE, categoriesCol.path)
        }
    }
}
