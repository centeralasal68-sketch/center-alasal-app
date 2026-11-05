package com.example.ui.screens

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AttachFile
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.CreditCard
import androidx.compose.material.icons.filled.DeleteOutline
import androidx.compose.material.icons.filled.LocalShipping
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material.icons.filled.ShoppingBag
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Divider
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableDoubleStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.data.model.CartItem
import com.example.data.model.Order
import com.example.data.model.OrderItem
import com.example.data.model.StoreSettings
import com.example.ui.theme.ChampagneGold
import com.example.ui.theme.ChampagneGoldDark
import com.example.ui.theme.ChampagneGoldLight
import com.example.ui.theme.FrostBlue
import com.example.ui.theme.IvoryWhite
import com.example.ui.theme.SageGreen
import com.example.ui.theme.SageGreenLight
import com.example.ui.theme.SkyBlue
import com.example.ui.theme.SkyBlueLight
import com.example.ui.theme.StatusGreen
import com.example.ui.theme.StatusRed
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.ui.theme.TextTertiary

@Composable
fun CartScreen(
    cartItems: List<CartItem>,
    onUpdateQuantity: (String, Int) -> Unit,
    onRemoveItem: (String) -> Unit,
    onCheckoutClick: () -> Unit,
    onStartShoppingClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val subtotal = remember(cartItems) { cartItems.sumOf { it.totalPrice } }
    val shippingFee = if (subtotal > 2000 || subtotal == 0.0) 0.0 else 50.0
    val total = subtotal + shippingFee

    if (cartItems.isEmpty()) {
        Box(
            modifier = modifier
                .fillMaxSize()
                .background(IvoryWhite)
                .padding(24.dp),
            contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Icon(
                    imageVector = Icons.Default.ShoppingBag,
                    contentDescription = null,
                    tint = SkyBlue.copy(alpha = 0.4f),
                    modifier = Modifier.size(72.dp)
                )
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = "سلة المشتريات فارغة",
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary
                )
                Text(
                    text = "تصفح أحدث مفروشات سنتر العسال وأضف منتجاتك المفضلة.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = TextSecondary,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(top = 4.dp, bottom = 20.dp)
                )
                Button(
                    onClick = onStartShoppingClick,
                    colors = ButtonDefaults.buttonColors(containerColor = SkyBlue),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.testTag("start_shopping_from_cart_btn")
                ) {
                    Text("ابدأ التسوق الآن", fontWeight = FontWeight.Bold)
                }
            }
        }
    } else {
        Column(
            modifier = modifier
                .fillMaxSize()
                .background(IvoryWhite)
        ) {
            // Cart Items List
            LazyColumn(
                modifier = Modifier
                    .weight(1f)
                    .padding(horizontal = 16.dp),
                contentPadding = PaddingValues(top = 16.dp, bottom = 16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                item {
                    Text(
                        text = "منتجات السلة (${cartItems.size})",
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )
                }

                items(cartItems, key = { it.id }) { item ->
                    Card(
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        modifier = Modifier
                            .fillMaxWidth()
                            .border(1.dp, FrostBlue, RoundedCornerShape(16.dp))
                    ) {
                        Row(
                            modifier = Modifier.padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            // Thumbnail
                            Box(
                                modifier = Modifier
                                    .size(70.dp)
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(SkyBlueLight)
                            ) {
                                if (item.imageUrl.isNotBlank()) {
                                    AsyncImage(
                                        model = item.imageUrl,
                                        contentDescription = item.title,
                                        contentScale = ContentScale.Crop,
                                        modifier = Modifier.fillMaxSize()
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.width(12.dp))

                            // Details
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = item.title,
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = TextPrimary,
                                    maxLines = 1
                                )
                                if (item.selectedSize.isNotBlank() || item.selectedColor.isNotBlank()) {
                                    Text(
                                        text = listOf(item.selectedSize, item.selectedColor).filter { it.isNotBlank() }.joinToString(" - "),
                                        style = MaterialTheme.typography.labelSmall,
                                        color = TextSecondary
                                    )
                                }
                                Text(
                                    text = "${item.price.toInt()} ج.م",
                                    style = MaterialTheme.typography.labelLarge,
                                    fontWeight = FontWeight.Bold,
                                    color = ChampagneGold
                                )
                            }

                            // Quantity Adjuster
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier
                                    .background(SkyBlueLight, RoundedCornerShape(10.dp))
                                    .padding(2.dp)
                            ) {
                                IconButton(
                                    onClick = { onUpdateQuantity(item.id, item.quantity - 1) },
                                    modifier = Modifier.size(28.dp)
                                ) {
                                    Icon(
                                        imageVector = if (item.quantity == 1) Icons.Default.DeleteOutline else Icons.Default.Remove,
                                        contentDescription = null,
                                        tint = if (item.quantity == 1) StatusRed else SkyBlue,
                                        modifier = Modifier.size(16.dp)
                                    )
                                }
                                Text(
                                    text = "${item.quantity}",
                                    style = MaterialTheme.typography.labelMedium,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(horizontal = 6.dp)
                                )
                                IconButton(
                                    onClick = { onUpdateQuantity(item.id, item.quantity + 1) },
                                    modifier = Modifier.size(28.dp)
                                ) {
                                    Icon(Icons.Default.Add, contentDescription = null, tint = SkyBlue, modifier = Modifier.size(16.dp))
                                }
                            }
                        }
                    }
                }
            }

            // Summary Card
            Card(
                shape = RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("المجموع الفرعي:", style = MaterialTheme.typography.bodyMedium, color = TextSecondary)
                        Text("${subtotal.toInt()} ج.م", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                    }
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 6.dp),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("تكلفة الشحن:", style = MaterialTheme.typography.bodyMedium, color = TextSecondary)
                        Text(
                            text = if (shippingFee == 0.0) "شحن مجاني" else "${shippingFee.toInt()} ج.م",
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.Bold,
                            color = if (shippingFee == 0.0) StatusGreen else TextPrimary
                        )
                    }
                    Divider(color = FrostBlue, modifier = Modifier.padding(vertical = 8.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("الإجمالي الكلي:", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold, color = TextPrimary)
                        Text("${total.toInt()} ج.م", style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold, color = ChampagneGold)
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Button(
                        onClick = onCheckoutClick,
                        colors = ButtonDefaults.buttonColors(containerColor = SkyBlue),
                        shape = RoundedCornerShape(14.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(52.dp)
                            .testTag("cart_proceed_to_checkout_btn")
                    ) {
                        Text("متابعة إتمام الطلب (Checkout)", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CheckoutScreen(
    cartItems: List<CartItem>,
    storeSettings: StoreSettings,
    onBackClick: () -> Unit,
    onConfirmOrder: (Order) -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    var customerName by remember { mutableStateOf("") }
    var customerPhone by remember { mutableStateOf("") }
    var selectedGovernorate by remember { mutableStateOf("الإسكندرية") }
    var cityDistrict by remember { mutableStateOf("") }
    var addressDetails by remember { mutableStateOf("") }
    var deliveryNotes by remember { mutableStateOf("") }

    // Payment methods: cash_on_delivery, instapay, wallet
    var paymentMethod by remember { mutableStateOf("cash_on_delivery") }
    var paymentProofUrl by remember { mutableStateOf("") }
    var isSubmitting by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf("") }

    val governorates = listOf(
        "الإسكندرية", "القاهرة", "الجيزة", "البحيرة", "الغربية", "الدقهلية",
        "الشرقية", "القليوبية", "المنوفية", "كفر الشيخ", "دمياط", "بورسعيد",
        "الإسماعيلية", "السويس", "مطروح", "بني سويف", "الفيوم", "المنيا", "أسيوط", "سوهاج"
    )
    var isGovDropdownExpanded by remember { mutableStateOf(false) }

    val photoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        if (uri != null) {
            paymentProofUrl = uri.toString()
        }
    }

    val subtotal = remember(cartItems) { cartItems.sumOf { it.totalPrice } }
    val shippingFee = remember(selectedGovernorate, subtotal) {
        if (subtotal >= 2000) 0.0 else if (selectedGovernorate == "الإسكندرية") 50.0 else 75.0
    }
    val total = subtotal + shippingFee

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("إتمام الطلب - Checkout", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold) },
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
                .verticalScroll(rememberScrollState())
                .padding(16.dp)
        ) {
            // Customer Info Card
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, FrostBlue, RoundedCornerShape(16.dp))
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "1. بيانات العميل والشحن",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = SkyBlue
                    )
                    Spacer(modifier = Modifier.height(12.dp))

                    OutlinedTextField(
                        value = customerName,
                        onValueChange = { customerName = it },
                        label = { Text("الاسم بالكامل *") },
                        singleLine = true,
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth().testTag("checkout_name_input")
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    OutlinedTextField(
                        value = customerPhone,
                        onValueChange = { customerPhone = it },
                        label = { Text("رقم الهاتف للتواصل والمتابعة *") },
                        singleLine = true,
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth().testTag("checkout_phone_input")
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    // Governorate Selector
                    ExposedDropdownMenuBox(
                        expanded = isGovDropdownExpanded,
                        onExpandedChange = { isGovDropdownExpanded = !isGovDropdownExpanded },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        OutlinedTextField(
                            value = selectedGovernorate,
                            onValueChange = {},
                            readOnly = true,
                            label = { Text("المحافظة *") },
                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = isGovDropdownExpanded) },
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.menuAnchor().fillMaxWidth()
                        )
                        ExposedDropdownMenu(
                            expanded = isGovDropdownExpanded,
                            onDismissRequest = { isGovDropdownExpanded = false }
                        ) {
                            governorates.forEach { gov ->
                                DropdownMenuItem(
                                    text = { Text(gov) },
                                    onClick = {
                                        selectedGovernorate = gov
                                        isGovDropdownExpanded = false
                                    }
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    OutlinedTextField(
                        value = cityDistrict,
                        onValueChange = { cityDistrict = it },
                        label = { Text("المنطقة / الحي (مثال: سموحة، كرموز، العصافرة)") },
                        singleLine = true,
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    OutlinedTextField(
                        value = addressDetails,
                        onValueChange = { addressDetails = it },
                        label = { Text("العنوان بالتفصيل (اسم الشارع، رقم العمارة، الشقة) *") },
                        minLines = 2,
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth().testTag("checkout_address_input")
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    OutlinedTextField(
                        value = deliveryNotes,
                        onValueChange = { deliveryNotes = it },
                        label = { Text("ملاحظات خاصة بالتسليم (اختياري)") },
                        singleLine = true,
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Payment Method Selection
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, FrostBlue, RoundedCornerShape(16.dp))
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "2. طريقة الدفع",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = SkyBlue
                    )
                    Spacer(modifier = Modifier.height(10.dp))

                    // Option 1: Cash on Delivery
                    PaymentOptionRow(
                        title = "الدفع عند الاستلام (كاش)",
                        subtitle = "ادفع نقداً عند استلام ومعاينة المفروشات من مندوب الشحن",
                        selected = paymentMethod == "cash_on_delivery",
                        onSelect = { paymentMethod = "cash_on_delivery" }
                    )

                    Divider(color = FrostBlue, modifier = Modifier.padding(vertical = 8.dp))

                    // Option 2: InstaPay
                    PaymentOptionRow(
                        title = "تحويل إنستاباي (InstaPay)",
                        subtitle = storeSettings.instapayInfo,
                        selected = paymentMethod == "instapay",
                        onSelect = { paymentMethod = "instapay" }
                    )

                    // Option 3: Mobile Wallets
                    Divider(color = FrostBlue, modifier = Modifier.padding(vertical = 8.dp))
                    PaymentOptionRow(
                        title = "المحافظ الإلكترونية (فودافون/أورنج/اتصالات كاش)",
                        subtitle = storeSettings.walletInfo,
                        selected = paymentMethod == "wallet",
                        onSelect = { paymentMethod = "wallet" }
                    )

                    // Receipt Upload Box for InstaPay / Wallet
                    AnimatedVisibility(visible = paymentMethod in listOf("instapay", "wallet")) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 12.dp)
                                .background(ChampagneGoldLight, RoundedCornerShape(12.dp))
                                .border(1.dp, ChampagneGold.copy(alpha = 0.5f), RoundedCornerShape(12.dp))
                                .padding(12.dp)
                        ) {
                            Text(
                                text = "إثبات التحويل مطلوب:",
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.Bold,
                                color = ChampagneGoldDark
                            )
                            Text(
                                text = "يرجى تحويل المبلغ الإجمالي (${total.toInt()} ج.م) ثم إرفاق صورة إيصال التحويل ليتم تأكيد طلبك فوراً من الإدارة.",
                                style = MaterialTheme.typography.bodySmall,
                                color = TextSecondary,
                                modifier = Modifier.padding(top = 2.dp, bottom = 8.dp)
                            )
                            Button(
                                onClick = { photoPickerLauncher.launch("image/*") },
                                colors = ButtonDefaults.buttonColors(containerColor = ChampagneGold),
                                shape = RoundedCornerShape(10.dp)
                            ) {
                                Icon(Icons.Default.AttachFile, contentDescription = null, tint = Color.White)
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = if (paymentProofUrl.isNotBlank()) "تم اختيار الإيصال بنجاح ✓" else "إرفاق صورة إيصال التحويل",
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Order Breakdown Card
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, FrostBlue, RoundedCornerShape(16.dp))
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "3. ملخص الحساب والتأكيد",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = SkyBlue
                    )
                    Spacer(modifier = Modifier.height(8.dp))

                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("إجمالي المفروشات (${cartItems.sumOf { it.quantity }} قطعة):", style = MaterialTheme.typography.bodyMedium, color = TextSecondary)
                        Text("${subtotal.toInt()} ج.م", style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Bold)
                    }
                    Row(modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("شحن إلى $selectedGovernorate:", style = MaterialTheme.typography.bodyMedium, color = TextSecondary)
                        Text(
                            text = if (shippingFee == 0.0) "مجاني" else "${shippingFee.toInt()} ج.م",
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.Bold,
                            color = if (shippingFee == 0.0) StatusGreen else TextPrimary
                        )
                    }
                    Divider(color = FrostBlue, modifier = Modifier.padding(vertical = 8.dp))
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("المبلغ المطلوب دفعه:", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                        Text("${total.toInt()} ج.م", style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold, color = ChampagneGold)
                    }

                    if (errorMessage.isNotBlank()) {
                        Text(
                            text = errorMessage,
                            color = StatusRed,
                            style = MaterialTheme.typography.bodySmall,
                            modifier = Modifier.padding(top = 8.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Button(
                        onClick = {
                            if (customerName.isBlank() || customerPhone.isBlank() || addressDetails.isBlank()) {
                                errorMessage = "يرجى إكمال الاسم ورقم الهاتف والعنوان بالتفصيل"
                                return@Button
                            }
                            if (paymentMethod in listOf("instapay", "wallet") && paymentProofUrl.isBlank()) {
                                errorMessage = "يرجى إرفاق صورة إيصال التحويل أو اختيار الدفع عند الاستلام"
                                return@Button
                            }

                            isSubmitting = true
                            errorMessage = ""

                            val orderItems = cartItems.map {
                                OrderItem(
                                    productId = it.productId,
                                    title = it.title,
                                    price = it.price,
                                    quantity = it.quantity,
                                    imageUrl = it.imageUrl,
                                    selectedSize = it.selectedSize,
                                    selectedColor = it.selectedColor
                                )
                            }

                            val order = Order(
                                customerName = customerName.trim(),
                                customerPhone = customerPhone.trim(),
                                governorate = selectedGovernorate,
                                city = cityDistrict.trim(),
                                address = addressDetails.trim(),
                                notes = deliveryNotes.trim(),
                                items = orderItems,
                                subtotal = subtotal,
                                shippingFee = shippingFee,
                                total = total,
                                paymentMethod = paymentMethod,
                                paymentProofUrl = paymentProofUrl,
                                paymentStatus = if (paymentMethod == "cash_on_delivery") "pending" else "pending",
                                status = "جديد"
                            )

                            onConfirmOrder(order)
                        },
                        enabled = !isSubmitting,
                        colors = ButtonDefaults.buttonColors(containerColor = SkyBlue),
                        shape = RoundedCornerShape(14.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(52.dp)
                            .testTag("confirm_order_btn")
                    ) {
                        if (isSubmitting) {
                            CircularProgressIndicator(color = Color.White, modifier = Modifier.size(24.dp))
                        } else {
                            Text("تأكيد الطلب الآن", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun PaymentOptionRow(
    title: String,
    subtitle: String,
    selected: Boolean,
    onSelect: () -> Unit
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onSelect() }
            .padding(vertical = 4.dp)
    ) {
        RadioButton(
            selected = selected,
            onClick = onSelect,
            colors = RadioButtonDefaults.colors(selectedColor = SkyBlue)
        )
        Spacer(modifier = Modifier.width(8.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(title, style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold, color = TextPrimary)
            Text(subtitle, style = MaterialTheme.typography.bodySmall, color = TextSecondary)
        }
    }
}

@Composable
fun OrderSuccessScreen(
    orderCode: String,
    totalAmount: Double,
    customerPhone: String,
    onTrackOrder: (String) -> Unit,
    onBackToHome: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(IvoryWhite)
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Box(
            modifier = Modifier
                .size(90.dp)
                .background(SageGreenLight, CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.CheckCircle,
                contentDescription = null,
                tint = StatusGreen,
                modifier = Modifier.size(54.dp)
            )
        }

        Spacer(modifier = Modifier.height(20.dp))

        Text(
            text = "تم تأكيد طلبك بنجاح!",
            style = MaterialTheme.typography.headlineLarge,
            fontWeight = FontWeight.Bold,
            color = TextPrimary
        )

        Text(
            text = "شكراً لثقتكم بسنتر العسال لتجارة المفروشات بالإسكندرية.",
            style = MaterialTheme.typography.bodyMedium,
            color = TextSecondary,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(top = 4.dp, bottom = 20.dp)
        )

        // Code Box
        Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            modifier = Modifier
                .fillMaxWidth()
                .border(2.dp, ChampagneGold, RoundedCornerShape(16.dp))
        ) {
            Column(
                modifier = Modifier.padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "كود الطلب الخاص بك:",
                    style = MaterialTheme.typography.labelMedium,
                    color = TextTertiary
                )
                Spacer(modifier = Modifier.height(6.dp))
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = orderCode,
                        style = MaterialTheme.typography.headlineLarge,
                        fontWeight = FontWeight.Bold,
                        color = SkyBlue,
                        letterSpacing = 1.sp
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    IconButton(
                        onClick = {
                            val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                            clipboard.setPrimaryClip(ClipData.newPlainText("Order Code", orderCode))
                        }
                    ) {
                        Icon(Icons.Default.ContentCopy, contentDescription = "نسخ الكود", tint = SkyBlue)
                    }
                }
                Text(
                    text = "الإجمالي: ${totalAmount.toInt()} ج.م",
                    style = MaterialTheme.typography.titleMedium,
                    color = ChampagneGoldDark,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(top = 6.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        Button(
            onClick = { onTrackOrder(orderCode) },
            colors = ButtonDefaults.buttonColors(containerColor = SkyBlue),
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp)
                .testTag("track_confirmed_order_btn")
        ) {
            Icon(Icons.Default.LocalShipping, contentDescription = null, tint = Color.White)
            Spacer(modifier = Modifier.width(8.dp))
            Text("متابعة وتتبع حالة الطلب", fontWeight = FontWeight.Bold)
        }

        Spacer(modifier = Modifier.height(10.dp))

        OutlinedButton(
            onClick = {
                val whatsappMsg = "مرحباً سنتر العسال، أود المتابعة بخصوص طلبي كود: $orderCode"
                val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://wa.me/201277714100?text=${Uri.encode(whatsappMsg)}"))
                context.startActivity(intent)
            },
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp)
        ) {
            Icon(Icons.Default.Phone, contentDescription = null, tint = SkyBlue)
            Spacer(modifier = Modifier.width(8.dp))
            Text("تواصل عبر الواتساب لتأكيد الشحن", color = SkyBlue, fontWeight = FontWeight.Bold)
        }

        Spacer(modifier = Modifier.height(10.dp))

        Button(
            onClick = onBackToHome,
            colors = ButtonDefaults.buttonColors(containerColor = FrostBlue, contentColor = TextPrimary),
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp)
        ) {
            Text("العودة إلى الصفحة الرئيسية", fontWeight = FontWeight.Bold)
        }
    }
}
