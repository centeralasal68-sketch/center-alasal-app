package com.example.data.model

import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentId
import com.google.firebase.firestore.IgnoreExtraProperties
import com.google.firebase.firestore.ServerTimestamp

data class Branch(
    val name: String = "",
    val address: String = "",
    val phone: String = "",
    val latitude: Double = 31.2001,
    val longitude: Double = 29.9187
)

@IgnoreExtraProperties
data class StoreSettings(
    @DocumentId
    val id: String = "general",
    val storeName: String = "سنتر العسال لتجارة المفروشات",
    val storeSubtitle: String = "CENTER AL ASSAL LUXURY HOME TEXTILES",
    val phones: List<String> = listOf("01277714100", "01279207793"),
    val whatsappNumber: String = "201277714100",
    val branches: List<Branch> = listOf(
        Branch(
            name = "المعرض الرئيسي",
            address = "8 شارع أنور السادات – الإسكندرية",
            phone = "01277714100"
        ),
        Branch(
            name = "فرع عمود السواري",
            address = "95 عمود السواري – كرموز – الإسكندرية",
            phone = "01279207793"
        )
    ),
    val instapayInfo: String = "حساب إنستاباي: centeralassal@instapay (هاتف: 01277714100)",
    val walletInfo: String = "فودافون كاش: 01277714100 | أورنج كاش: 01279207793",
    val shippingInfo: String = "توصيل سريع بالإسكندرية (50 ج.م) وباقي المحافظات (75 ج.م). شحن مجاني للطلبات فوق 2000 ج.م.",
    val returnPolicy: String = "استبدال واسترجاع خلال 14 يوماً من تاريخ الاستلام بشرط أن تكون المفروشات بحالتها الأصلية وبغلاف المصنع.",
    val aiSystemPrompt: String = "أنت ASSAL AI المساعد الذكي لمبيعات وخدمة عملاء سنتر العسال لتجارة المفروشات بالإسكندرية. تحدث باللهجة المصرية الودودة والمهنية.",
    @ServerTimestamp
    val updatedAt: Timestamp? = null
)
