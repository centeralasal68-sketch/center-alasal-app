package com.example

import com.example.base.FirestoreEmulatorTestBase
import com.example.data.model.Order
import com.example.data.model.OrderItem
import com.example.data.repository.OrderRepository
import com.google.firebase.firestore.FirebaseFirestoreException
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withTimeout
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Assert.fail
import org.junit.Test

class OrderRepositoryRuleTest : FirestoreEmulatorTestBase() {

    @Test
    fun createOrder_authenticatedUser_success() = runBlocking {
        val uid = signInTestUser("customer@test.com")
        val repository = OrderRepository(firestore)

        val order = Order(
            id = "CAA-2026-${(10000..99999).random()}",
            userId = uid,
            customerName = "أحمد محمد",
            customerPhone = "01234567890",
            governorate = "الإسكندرية",
            address = "8 شارع الإقبال، لوران",
            items = listOf(
                OrderItem(
                    productId = "prod_1",
                    title = "طقم ملاية قطن مصري ملكي",
                    price = 850.0,
                    quantity = 1,
                    selectedSize = "240x260"
                )
            ),
            subtotal = 850.0,
            shippingFee = 50.0,
            total = 900.0,
            paymentMethod = "cash_on_delivery",
            paymentStatus = "pending",
            status = "جديد"
        )

        val result = withTimeout(5000L) {
            repository.createOrder(order)
        }

        assertTrue("Order creation should succeed", result.isSuccess)
        val createdCode = result.getOrThrow()
        assertTrue("Created code should follow CAA-2026 format", createdCode.startsWith("CAA-2026-"))
    }

    @Test
    fun observeUserOrders_unauthenticated_fails() = runBlocking {
        auth.signOut()
        val repository = OrderRepository(firestore)

        try {
            withTimeout(3000L) {
                repository.observeUserOrders().first()
            }
            fail("Should fail when unauthenticated")
        } catch (e: Exception) {
            // Expected failure because user is not authenticated
            assertTrue(e is IllegalStateException || e.unwrapFirestoreException()?.code == FirebaseFirestoreException.Code.PERMISSION_DENIED)
        }
    }
}
