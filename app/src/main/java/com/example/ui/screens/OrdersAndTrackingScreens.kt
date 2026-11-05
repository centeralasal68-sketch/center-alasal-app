package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.LocalShipping
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.Order
import com.example.ui.components.StatusBadge
import com.example.ui.theme.*
import kotlinx.coroutines.launch

@Composable
fun OrdersScreen(
    orders: List<Order>,
    onTrackOrderCode: (String) -> Unit,
    onStartShopping: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(IvoryWhite)
            .padding(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "طلباتي",
                    style = MaterialTheme.typography.headlineLarge,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary
                )
                Text(
                    text = "متابعة سجل طلبات المفروشات وحالات الشحن",
                    style = MaterialTheme.typography.bodyMedium,
                    color = TextSecondary
                )
            }

            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(10.dp))
                    .background(SkyBlueLight)
                    .clickable { onTrackOrderCode("") }
                    .padding(horizontal = 10.dp, vertical = 6.dp)
            ) {
                Text(
                    text = "تتبع كود",
                    style = MaterialTheme.typography.labelSmall,
                    color = SkyBlue,
                    fontWeight = FontWeight.Bold
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        if (orders.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(24.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(
                        imageVector = Icons.Default.LocalShipping,
                        contentDescription = null,
                        tint = SkyBlue.copy(alpha = 0.4f),
                        modifier = Modifier.size(64.dp)
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = "لا توجد طلبات مسجلة بعد",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )
                    Text(
                        text = "يمكنك التسوق واختيار المفروشات الراقية لإنشاء طلبك الأول، أو تتبع طلب ضيف بالكود.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = TextSecondary,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(top = 4.dp, bottom = 16.dp)
                    )
                    Button(
                        onClick = onStartShopping,
                        colors = ButtonDefaults.buttonColors(containerColor = SkyBlue),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text("تصفح المفروشات الآن", fontWeight = FontWeight.Bold)
                    }
                }
            }
        } else {
            LazyColumn(
                contentPadding = PaddingValues(bottom = 100.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                items(orders, key = { it.id }) { order ->
                    OrderCardItem(
                        order = order,
                        onTrackClick = { onTrackOrderCode(order.id) }
                    )
                }
            }
        }
    }
}

@Composable
fun OrderCardItem(
    order: Order,
    onTrackClick: () -> Unit
) {
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        modifier = Modifier
            .fillMaxWidth()
            .border(1.dp, FrostBlue, RoundedCornerShape(16.dp))
            .clickable { onTrackClick() }
            .testTag("order_card_${order.id}")
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = order.id,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = SkyBlue
                    )
                    Text(
                        text = "تاريخ الطلب: ${order.createdAt?.toDate()?.toString()?.take(16) ?: "اليوم"}",
                        style = MaterialTheme.typography.labelSmall,
                        color = TextTertiary
                    )
                }
                StatusBadge(status = order.status)
            }

            Divider(color = FrostBlue, modifier = Modifier.padding(vertical = 10.dp))

            // Items breakdown
            order.items.take(3).forEach { item ->
                Row(
                    modifier = Modifier.fillMaxWidth().padding(vertical = 2.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "• ${item.title} (x${item.quantity})",
                        style = MaterialTheme.typography.bodyMedium,
                        color = TextPrimary,
                        modifier = Modifier.weight(1f)
                    )
                    Text(
                        text = "${(item.price * item.quantity).toInt()} ج.م",
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.SemiBold,
                        color = TextSecondary
                    )
                }
            }

            if (order.items.size > 3) {
                Text(
                    text = "+ ${order.items.size - 3} منتجات أخرى...",
                    style = MaterialTheme.typography.labelSmall,
                    color = SkyBlue
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(text = "الإجمالي:", style = MaterialTheme.typography.labelSmall, color = TextTertiary)
                    Text(
                        text = "${order.total.toInt()} ج.م",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = ChampagneGold
                    )
                }

                Button(
                    onClick = onTrackClick,
                    colors = ButtonDefaults.buttonColors(containerColor = SkyBlueLight, contentColor = SkyBlue),
                    shape = RoundedCornerShape(10.dp),
                    contentPadding = PaddingValues(horizontal = 14.dp, vertical = 6.dp)
                ) {
                    Text("عرض وتتبع", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.labelMedium)
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OrderTrackingScreen(
    initialCode: String = "",
    onBackClick: () -> Unit,
    onFindOrder: suspend (String, String) -> Order?,
    modifier: Modifier = Modifier
) {
    var searchCode by remember(initialCode) { mutableStateOf(initialCode) }
    var searchPhone by remember { mutableStateOf("") }
    var searchedOrder by remember { mutableStateOf<Order?>(null) }
    var isLoading by remember { mutableStateOf(false) }
    var hasSearched by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("تتبع الطلب بالكود", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "رجوع")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = IvoryWhite)
            )
        },
        containerColor = IvoryWhite,
        modifier = modifier
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
        ) {
            // Search Input Box
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, FrostBlue, RoundedCornerShape(16.dp))
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "أدخل كود الطلب CAA-2026-XXXXX",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = searchCode,
                        onValueChange = { searchCode = it.uppercase() },
                        placeholder = { Text("مثال: CAA-2026-00001") },
                        singleLine = true,
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth().testTag("tracking_code_input")
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    OutlinedTextField(
                        value = searchPhone,
                        onValueChange = { searchPhone = it },
                        placeholder = { Text("رقم الهاتف (اختياري للتحقق)") },
                        singleLine = true,
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    Button(
                        onClick = {
                            if (searchCode.isNotBlank()) {
                                isLoading = true
                                hasSearched = true
                                scope.launch {
                                    searchedOrder = onFindOrder(searchCode.trim(), searchPhone.trim())
                                    isLoading = false
                                }
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = SkyBlue),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth().height(48.dp).testTag("search_order_tracking_btn")
                    ) {
                        if (isLoading) {
                            CircularProgressIndicator(color = Color.White, modifier = Modifier.size(20.dp))
                        } else {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.Search, contentDescription = null, tint = Color.White)
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("تتبع حالة الشحن والتسليم", fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Result
            if (searchedOrder != null) {
                val order = searchedOrder!!
                OrderTimelineCard(order = order)
            } else if (hasSearched && !isLoading) {
                Box(
                    modifier = Modifier.fillMaxWidth().padding(32.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "لم يتم العثور على طلب بهذا الكود ورقم الهاتف. يرجى التأكد من كتابة الكود بشكل صحيح (مثل CAA-2026-00001).",
                        color = StatusRed,
                        textAlign = TextAlign.Center,
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }
        }
    }
}

@Composable
fun OrderTimelineCard(order: Order) {
    val statuses = listOf(
        "جديد",
        "تم التأكيد",
        "قيد التجهيز",
        "تم الشحن",
        "تم التسليم"
    )

    val currentStatusIndex = when (order.status) {
        "جديد", "قيد المراجعة" -> 0
        "تم التأكيد" -> 1
        "قيد التجهيز" -> 2
        "تم الشحن" -> 3
        "تم التسليم" -> 4
        "ملغي", "مرتجع" -> -1
        else -> 0
    }

    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        modifier = Modifier
            .fillMaxWidth()
            .border(1.dp, FrostBlue, RoundedCornerShape(16.dp))
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(text = "طلب: ${order.id}", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold, color = SkyBlue)
                    Text(text = "المستلم: ${order.customerName} - ${order.governorate}", style = MaterialTheme.typography.bodyMedium, color = TextSecondary)
                }
                StatusBadge(status = order.status)
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(text = "مراحل الشحن والتجهيز:", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold, color = TextPrimary)
            Spacer(modifier = Modifier.height(8.dp))

            // Step timeline
            statuses.forEachIndexed { index, stepName ->
                val isDone = currentStatusIndex >= index
                val isCurrent = currentStatusIndex == index

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(vertical = 4.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(24.dp)
                            .background(
                                color = if (isDone) StatusGreen else FrostBlue,
                                shape = CircleShape
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        if (isDone) {
                            Icon(Icons.Default.CheckCircle, contentDescription = null, tint = Color.White, modifier = Modifier.size(16.dp))
                        } else {
                            Text("${index + 1}", fontSize = 11.sp, color = TextTertiary, fontWeight = FontWeight.Bold)
                        }
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(
                        text = stepName,
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = if (isCurrent) FontWeight.Bold else FontWeight.Normal,
                        color = if (isDone) TextPrimary else TextTertiary
                    )
                }
            }

            Divider(color = FrostBlue, modifier = Modifier.padding(vertical = 12.dp))

            Text(text = "طريقة الدفع: ${if (order.paymentMethod == "cash_on_delivery") "الدفع عند الاستلام (كاش)" else if (order.paymentMethod == "instapay") "إنستاباي" else "محفظة إلكترونية"}", style = MaterialTheme.typography.bodyMedium, color = TextSecondary)
            Text(text = "العنوان: ${order.address}", style = MaterialTheme.typography.bodyMedium, color = TextSecondary)
            Text(text = "المبلغ الإجمالي: ${order.total.toInt()} ج.م", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = ChampagneGoldDark)
        }
    }
}
