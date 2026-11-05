package com.example.data.repository

import android.content.Context
import android.graphics.Bitmap
import android.util.Log
import com.example.data.model.ChatMessage
import com.example.data.model.Order
import com.example.data.model.Product
import com.example.data.model.StoreSettings
import com.google.firebase.Firebase
import com.google.firebase.ai.ai
import com.google.firebase.ai.type.content

import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.TimeoutCancellationException
import kotlinx.coroutines.withContext
import kotlinx.coroutines.withTimeout

private const val TAG = "AssalAiRepository"

class AssalAiRepository(
    private val productRepository: ProductRepository,
    private val orderRepository: OrderRepository,
    private val storeRepository: StoreRepository
) {
    // Lazy model initialization as mandated
    private val generativeModel by lazy {
        Firebase.ai.generativeModel(
            modelName = "gemini-3.8-flash"
        )
    }

    suspend fun sendMessage(
        userText: String,
        imageBitmap: Bitmap?,
        conversationHistory: List<ChatMessage>,
        allProducts: List<Product>,
        storeSettings: StoreSettings
    ): Result<ChatMessage> = withContext(Dispatchers.IO) {
        try {
            // Mandated coroutine timeout 60s
            withTimeout(60_000L) {
                // RAG: Build dynamic context from store database
                val productsContext = buildProductsContext(allProducts)
                val branchesContext = storeSettings.branches.joinToString("\n") {
                    "- ${it.name}: ${it.address} (هاتف: ${it.phone})"
                }
                val phonesContext = storeSettings.phones.joinToString(" ، ")

                // Check if user mentioned an order code like CAA-2026-XXXXX
                val orderCodeRegex = Regex("CAA-2026-\\d{5}", RegexOption.IGNORE_CASE)
                val matchedCode = orderCodeRegex.find(userText)?.value
                var orderContext = ""
                if (matchedCode != null) {
                    val order = orderRepository.getOrderByCodeAndPhone(matchedCode, "")
                    if (order != null) {
                        orderContext = """
                        معلومات الطلب الحالي المستعلم عنه:
                        - كود الطلب: ${order.id}
                        - العميل: ${order.customerName}
                        - الحالة: ${order.status}
                        - طريقة الدفع: ${order.paymentMethod} (حالة الدفع: ${order.paymentStatus})
                        - الإجمالي: ${order.total} ج.م
                        - المنتجات: ${order.items.joinToString { "${it.title} (كمية ${it.quantity})" }}
                        """.trimIndent()
                    }
                }

                val systemPrompt = """
                أنت "ASSAL AI" – المساعد الذكي لمبيعات وخدمة عملاء "سنتر العسال لتجارة المفروشات" (CENTER AL ASSAL) بالإسكندرية.
                أسلوبك:
                - تحدث بأسلوب مصري راقٍ، ودود، مهني جداً يرحب بالعميل ويساعده في اختيار أفضل المفروشات.
                - تفهم اللهجة المصرية والعربية الفصحى والأخطاء الإملائية.
                
                بيانات المتجر المحدثة من قاعدة البيانات (RAG):
                - الفروع:
                $branchesContext
                - أرقام التواصل والواتساب: $phonesContext
                - طرق الدفع: الدفع عند الاستلام، إنستاباي (${storeSettings.instapayInfo})، المحافظ الإلكترونية (${storeSettings.walletInfo}).
                - الشحن والتوصيل: ${storeSettings.shippingInfo}
                - سياسة الاستبدال والاسترجاع: ${storeSettings.returnPolicy}
                
                كتالوج المنتجات الفعلي المتاح بالمتجر:
                $productsContext
                
                $orderContext
                
                قواعد هامة جداً:
                1. لا تخترع منتجات أو أسعار أو عروض غير موجودة في الكتالوج أعلاه. إذا كان المتجر فارغاً أو المنتج غير موجود، أخبر العميل بلطف واقترح له التواصل عبر الواتساب أو الفروع.
                2. عند ترشيح منتج موجود بالفعل، اذكر اسمه وسعره ومميزاته، ويمكنك تضمين معرف المنتج في نهاية رسالتك بهذا الشكل: [RECOMMEND:product_id] لكي نعرضه له كبطاقة تفاعلية.
                3. إذا أرسل العميل صورة، حلل الألوان والنمط واقترح ما يناسبها من الكتالوج المتوفر مع توضيح مدى التناسق.
                4. عند الاستفسار عن كود طلب، اعرض حالته بدقة بناءً على البيانات.
                """.trimIndent()

                val promptWithHistory = buildString {
                    append(systemPrompt)
                    append("\n\nسجل المحادثة السابقة:\n")
                    conversationHistory.takeLast(6).forEach { msg ->
                        val role = if (msg.isUser) "العميل" else "ASSAL AI"
                        append("$role: ${msg.text}\n")
                    }
                    append("العميل: $userText\n")
                    append("ASSAL AI: ")
                }

                val response = if (imageBitmap != null) {
                    generativeModel.generateContent(
                        content {
                            image(imageBitmap)
                            text(promptWithHistory)
                        }
                    )
                } else {
                    generativeModel.generateContent(promptWithHistory)
                }

                val responseText = response.text.orEmpty()
                if (responseText.isBlank()) {
                    return@withTimeout Result.failure(IllegalStateException("لم يتم استلام رد من المساعد الذكي"))
                }

                // Extract recommended products tags [RECOMMEND:id]
                val recommendedIds = Regex("\\[RECOMMEND:([^\\]]+)\\]")
                    .findAll(responseText)
                    .map { it.groupValues[1].trim() }
                    .toList()

                val recommendedProducts = allProducts.filter { it.id in recommendedIds }
                val cleanedText = responseText.replace(Regex("\\[RECOMMEND:[^\\]]+\\]"), "").trim()

                // Generate smart action chips
                val suggestedChips = mutableListOf<String>()
                if (recommendedProducts.isNotEmpty()) {
                    suggestedChips.add("أضف للسلة")
                }
                if (!cleanedText.contains("فروع") && !cleanedText.contains("العنوان")) {
                    suggestedChips.add("أين فروعكم؟")
                }
                suggestedChips.add("العروض الحالية")
                suggestedChips.add("طرق الدفع")

                val botMessage = ChatMessage(
                    text = cleanedText,
                    isUser = false,
                    recommendedProducts = recommendedProducts,
                    suggestedActions = suggestedChips
                )

                Result.success(botMessage)
            }
        } catch (e: TimeoutCancellationException) {
            Log.e(TAG, "Assal AI timed out", e)
            Result.failure(Exception("استغرق الرد وقتاً طويلاً. يرجى إعادة المحاولة."))
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            Log.e(TAG, "Assal AI failed", e)
            Result.failure(e)
        }
    }

    private fun buildProductsContext(products: List<Product>): String {
        if (products.isEmpty()) {
            return "المتجر قيد تجهيز المنتجات حالياً. يمكنك الاستفسار عن المفروشات عموماً والفروع وطرق التوصيل."
        }
        return products.take(40).joinToString("\n") { p ->
            "- [ID: ${p.id}] ${p.name} | القسم: ${p.category} | السعر: ${p.finalPrice} ج.م ${if (p.hasDiscount) "(قبل الخصم: ${p.originalPrice})" else ""} | الخامات: ${p.materials} | المقاسات: ${p.sizes.joinToString()} | المخزون: ${p.stock}"
        }
    }
}
