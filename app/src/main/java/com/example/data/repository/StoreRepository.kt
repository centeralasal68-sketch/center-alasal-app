package com.example.data.repository

import android.content.Context
import com.example.R
import com.example.data.model.StoreSettings
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

class StoreRepository(private val db: FirebaseFirestore) {

    constructor(context: Context) : this(
        FirebaseFirestore.getInstance(
            context.applicationContext.getString(R.string.firestore_database_id)
        )
    )

    private val settingsDoc = db.collection("store_settings").document("general")
    private val adminsCol = db.collection("admins")

    fun observeStoreSettings(): Flow<StoreSettings> {
        return settingsDoc.snapshots()
            .map { snapshot ->
                if (snapshot.exists()) {
                    snapshot.toObject(StoreSettings::class.java) ?: StoreSettings()
                } else {
                    StoreSettings()
                }
            }
            .catch { error ->
                if (error !is CancellationException) {
                    handleFirestoreError(error, OperationType.GET, settingsDoc.path)
                }
                emit(StoreSettings())
            }
    }

    suspend fun updateStoreSettings(settings: StoreSettings): Result<Unit> {
        return try {
            val branchesMap = settings.branches.map { branch ->
                mapOf(
                    "name" to branch.name,
                    "address" to branch.address,
                    "phone" to branch.phone,
                    "latitude" to branch.latitude,
                    "longitude" to branch.longitude
                )
            }

            val payload = hashMapOf<String, Any?>(
                "storeName" to settings.storeName,
                "storeSubtitle" to settings.storeSubtitle,
                "phones" to settings.phones,
                "whatsappNumber" to settings.whatsappNumber,
                "branches" to branchesMap,
                "instapayInfo" to settings.instapayInfo,
                "walletInfo" to settings.walletInfo,
                "shippingInfo" to settings.shippingInfo,
                "returnPolicy" to settings.returnPolicy,
                "aiSystemPrompt" to settings.aiSystemPrompt,
                "updatedAt" to FieldValue.serverTimestamp()
            ).filterValues { it != null }

            settingsDoc.set(payload, com.google.firebase.firestore.SetOptions.merge()).await()
            Result.success(Unit)
        } catch (e: Exception) {
            handleFirestoreError(e, OperationType.UPDATE, settingsDoc.path)
            Result.failure(e)
        }
    }

    suspend fun isCurrentUserAdmin(): Boolean {
        val user = Firebase.auth.currentUser ?: return false
        val email = user.email?.trim()?.lowercase().orEmpty()
        if (email == "centeralasal68@gmail.com") {
            return true
        }
        return try {
            val adminDoc = adminsCol.document(user.uid).get().await()
            adminDoc.exists()
        } catch (e: Exception) {
            false
        }
    }
}
