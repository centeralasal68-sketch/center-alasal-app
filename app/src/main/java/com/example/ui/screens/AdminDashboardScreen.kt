package com.example.ui.screens

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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Category
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.LocalShipping
import androidx.compose.material.icons.filled.MonetizationOn
import androidx.compose.material.icons.filled.Save
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.ShoppingBag
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
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
import androidx.compose.material3.Scaffold
import androidx.compose.material3.ScrollableTabRow
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.data.model.Category
import com.example.data.model.Order
import com.example.data.model.Product
import com.example.data.model.StoreSettings
import com.example.ui.components.StatusBadge
import com.example.ui.theme.ChampagneGold
import com.example.ui.theme.ChampagneGoldDark
import com.example.ui.theme.ChampagneGoldLight
import com.example.ui.theme.FrostBlue
import com.example.ui.theme.IvoryWhite
import com.example.ui.theme.SageGreen
import com.example.ui.theme.SageGreenLight
import com.example.ui.theme.SkyBlue
import com.example.ui.theme.SkyBlueLight
import com.example.ui.theme.StatusAmber
import com.example.ui.theme.StatusGreen
import com.example.ui.theme.StatusRed
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.ui.theme.TextTertiary
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminDashboardScreen(
    orders: List<Order>,
    products: List<Product>,
    categories: List<Category>,
    storeSettings: StoreSettings,
    onBackClick: () -> Unit,
    onUpdateOrderStatus: (String, String) -> Unit,
    onUpdatePaymentStatus: (String, String) -> Unit,
    onAddOrUpdateProduct: (Product) -> Unit,
    onDeleteProduct: (String) -> Unit,
    onAddCategory: (String) -> Unit,
    onDeleteCategory: (String) -> Unit,
    onUpdateStoreSettings: (StoreSettings) -> Unit,
    modifier: Modifier = Modifier
) {
    var selectedTab by remember { mutableIntStateOf(0) }
    val tabs = listOf("الإحصائيات", "الطلبات (${orders.size})", "المنتجات (${products.size})", "الأقسام", "إعدادات المتجر و AI")

    // Modals
    var showAddProductDialog by remember { mutableStateOf(false) }
    var editingProduct by remember { mutableStateOf<Product?>(null) }
    var showAddCategoryDialog by remember { mutableStateOf(false) }
    var newCategoryName by remember { mutableStateOf("") }
    var selectedOrderForDetail by remember { mutableStateOf<Order?>(null) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text("CENTER AL ASSAL ADMIN", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = SkyBlue)
                        Text("لوحة إدارة متجر ومفروشات سنتر العسال", style = MaterialTheme.typography.labelSmall, color = TextTertiary)
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "رجوع", tint = TextPrimary)
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
        ) {
            // Tabs Row
            ScrollableTabRow(
                selectedTabIndex = selectedTab,
                containerColor = Color.White,
                edgePadding = 16.dp,
                indicator = { tabPositions ->
                    TabRowDefaults.Indicator(
                        modifier = Modifier.tabIndicatorOffset(tabPositions[selectedTab]),
                        color = SkyBlue,
                        height = 3.dp
                    )
                }
            ) {
                tabs.forEachIndexed { index, title ->
                    Tab(
                        selected = selectedTab == index,
                        onClick = { selectedTab = index },
                        text = {
                            Text(
                                text = title,
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = if (selectedTab == index) FontWeight.Bold else FontWeight.Normal,
                                color = if (selectedTab == index) SkyBlue else TextSecondary
                            )
                        }
                    )
                }
            }

            // Tab Content
            when (selectedTab) {
                0 -> AdminStatsTab(orders = orders, products = products)
                1 -> AdminOrdersTab(
                    orders = orders,
                    onOrderClick = { selectedOrderForDetail = it },
                    onStatusChange = onUpdateOrderStatus,
                    onPaymentStatusChange = onUpdatePaymentStatus
                )
                2 -> AdminProductsTab(
                    products = products,
                    categories = categories,
                    onAddClick = {
                        editingProduct = null
                        showAddProductDialog = true
                    },
                    onEditClick = {
                        editingProduct = it
                        showAddProductDialog = true
                    },
                    onDeleteClick = onDeleteProduct
                )
                3 -> AdminCategoriesTab(
                    categories = categories,
                    onAddCategoryClick = { showAddCategoryDialog = true },
                    onDeleteCategory = onDeleteCategory
                )
                4 -> AdminSettingsTab(
                    storeSettings = storeSettings,
                    onSave = onUpdateStoreSettings
                )
            }
        }
    }

    // Modal: Add/Edit Product
    if (showAddProductDialog) {
        ProductFormDialog(
            existingProduct = editingProduct,
            categories = categories,
            onDismiss = {
                showAddProductDialog = false
                editingProduct = null
            },
            onSave = { productToSave ->
                onAddOrUpdateProduct(productToSave)
                showAddProductDialog = false
                editingProduct = null
            }
        )
    }

    // Modal: Add Category
    if (showAddCategoryDialog) {
        AlertDialog(
            onDismissRequest = { showAddCategoryDialog = false },
            title = { Text("إضافة قسم مفروشات جديد") },
            text = {
                OutlinedTextField(
                    value = newCategoryName,
                    onValueChange = { newCategoryName = it },
                    label = { Text("اسم القسم (مثال: أطقم الملايات، البطاطين)") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth().testTag("new_category_name_input")
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (newCategoryName.isNotBlank()) {
                            onAddCategory(newCategoryName.trim())
                            newCategoryName = ""
                            showAddCategoryDialog = false
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = SkyBlue)
                ) {
                    Text("حفظ القسم")
                }
            },
            dismissButton = {
                TextButton(onClick = { showAddCategoryDialog = false }) {
                    Text("إلغاء")
                }
            }
        )
    }

    // Modal: Order Details & Payment Verification
    if (selectedOrderForDetail != null) {
        val order = selectedOrderForDetail!!
        OrderDetailsAdminDialog(
            order = order,
            onDismiss = { selectedOrderForDetail = null },
            onStatusChange = { newStatus ->
                onUpdateOrderStatus(order.id, newStatus)
                selectedOrderForDetail = order.copy(status = newStatus)
            },
            onPaymentStatusChange = { newPayStatus ->
                onUpdatePaymentStatus(order.id, newPayStatus)
                selectedOrderForDetail = order.copy(paymentStatus = newPayStatus)
            }
        )
    }
}

@Composable
fun AdminStatsTab(orders: List<Order>, products: List<Product>) {
    val totalRevenue = remember(orders) { orders.filter { it.status != "ملغي" }.sumOf { it.total } }
    val newOrdersCount = remember(orders) { orders.count { it.status == "جديد" } }
    val inProgressOrders = remember(orders) { orders.count { it.status in listOf("تم التأكيد", "قيد التجهيز", "تم الشحن") } }
    val completedOrders = remember(orders) { orders.count { it.status == "تم التسليم" } }
    val lowStockCount = remember(products) { products.count { it.stock < 5 } }

    LazyColumn(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            Text("ملخص النشاط التجاري", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold, color = TextPrimary)
        }

        item {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                AdminStatCard(
                    title = "إجمالي المبيعات",
                    value = "${totalRevenue.toInt()} ج.م",
                    subtitle = "كل الطلبات المؤكدة",
                    icon = Icons.Default.MonetizationOn,
                    color = SkyBlue,
                    modifier = Modifier.weight(1f)
                )
                AdminStatCard(
                    title = "عدد الطلبات",
                    value = "${orders.size}",
                    subtitle = "$newOrdersCount طلب جديد",
                    icon = Icons.Default.LocalShipping,
                    color = ChampagneGoldDark,
                    modifier = Modifier.weight(1f)
                )
            }
        }

        item {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                AdminStatCard(
                    title = "قيد التجهيز والشحن",
                    value = "$inProgressOrders",
                    subtitle = "تحتاج متابعة",
                    icon = Icons.Default.LocalShipping,
                    color = StatusAmber,
                    modifier = Modifier.weight(1f)
                )
                AdminStatCard(
                    title = "تم تسليمها",
                    value = "$completedOrders",
                    subtitle = "مكتملة بنجاح",
                    icon = Icons.Default.Check,
                    color = StatusGreen,
                    modifier = Modifier.weight(1f)
                )
            }
        }

        item {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                AdminStatCard(
                    title = "إجمالي المفروشات",
                    value = "${products.size}",
                    subtitle = "في الكتالوج",
                    icon = Icons.Default.ShoppingBag,
                    color = SkyBlue,
                    modifier = Modifier.weight(1f)
                )
                AdminStatCard(
                    title = "منخفض المخزون",
                    value = "$lowStockCount",
                    subtitle = "أقل من 5 قطع",
                    icon = Icons.Default.Warning,
                    color = if (lowStockCount > 0) StatusRed else StatusGreen,
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}

@Composable
fun AdminStatCard(
    title: String,
    value: String,
    subtitle: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    color: Color,
    modifier: Modifier = Modifier
) {
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        modifier = modifier.border(1.dp, FrostBlue, RoundedCornerShape(16.dp))
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(title, style = MaterialTheme.typography.labelMedium, color = TextSecondary)
                Icon(icon, contentDescription = null, tint = color, modifier = Modifier.size(20.dp))
            }
            Spacer(modifier = Modifier.height(6.dp))
            Text(value, style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold, color = color)
            Text(subtitle, style = MaterialTheme.typography.labelSmall, color = TextTertiary)
        }
    }
}

@Composable
fun AdminOrdersTab(
    orders: List<Order>,
    onOrderClick: (Order) -> Unit,
    onStatusChange: (String, String) -> Unit,
    onPaymentStatusChange: (String, String) -> Unit
) {
    var searchCode by remember { mutableStateOf("") }
    val filteredOrders = remember(orders, searchCode) {
        if (searchCode.isBlank()) orders else orders.filter {
            it.id.contains(searchCode.trim(), ignoreCase = true) ||
            it.customerName.contains(searchCode.trim(), ignoreCase = true) ||
            it.customerPhone.contains(searchCode.trim())
        }
    }

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        OutlinedTextField(
            value = searchCode,
            onValueChange = { searchCode = it },
            placeholder = { Text("بحث برقم الطلب (CAA-2026-...) أو هاتف العميل") },
            leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = SkyBlue) },
            singleLine = true,
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(12.dp))

        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(10.dp),
            contentPadding = PaddingValues(bottom = 80.dp)
        ) {
            items(filteredOrders, key = { it.id }) { order ->
                Card(
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(1.dp, FrostBlue, RoundedCornerShape(14.dp))
                        .clickable { onOrderClick(order) }
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(order.id, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = SkyBlue)
                            StatusBadge(status = order.status)
                        }
                        Text("${order.customerName} - ${order.customerPhone}", style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.SemiBold, color = TextPrimary)
                        Text("المحافظة: ${order.governorate} | الإجمالي: ${order.total.toInt()} ج.م", style = MaterialTheme.typography.labelMedium, color = ChampagneGoldDark, fontWeight = FontWeight.Bold)

                        Spacer(modifier = Modifier.height(8.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "طريقة الدفع: ${if (order.paymentMethod == "cash_on_delivery") "كاش" else order.paymentMethod} (${order.paymentStatus})",
                                style = MaterialTheme.typography.labelSmall,
                                color = TextSecondary
                            )
                            Button(
                                onClick = { onOrderClick(order) },
                                colors = ButtonDefaults.buttonColors(containerColor = SkyBlueLight, contentColor = SkyBlue),
                                contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                Text("مراجعة وتعديل", style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun AdminProductsTab(
    products: List<Product>,
    categories: List<Category>,
    onAddClick: () -> Unit,
    onEditClick: (Product) -> Unit,
    onDeleteClick: (String) -> Unit
) {
    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("إدارة كتالوج المفروشات", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = TextPrimary)
            Button(
                onClick = onAddClick,
                colors = ButtonDefaults.buttonColors(containerColor = SkyBlue),
                shape = RoundedCornerShape(10.dp),
                modifier = Modifier.testTag("admin_add_product_btn")
            ) {
                Icon(Icons.Default.Add, contentDescription = null, tint = Color.White, modifier = Modifier.size(18.dp))
                Spacer(modifier = Modifier.width(4.dp))
                Text("إضافة منتج جديد")
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        if (products.isEmpty()) {
            Box(
                modifier = Modifier.fillMaxWidth().padding(40.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("المتجر لا يحتوي على منتجات بعد.", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                    Text("اضغط على زر إضافة منتج جديد لبدء إضافة بضائعك.", color = TextSecondary, style = MaterialTheme.typography.bodyMedium)
                }
            }
        } else {
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(10.dp),
                contentPadding = PaddingValues(bottom = 80.dp)
            ) {
                items(products, key = { it.id }) { product ->
                    Card(
                        shape = RoundedCornerShape(14.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        modifier = Modifier
                            .fillMaxWidth()
                            .border(1.dp, FrostBlue, RoundedCornerShape(14.dp))
                    ) {
                        Row(
                            modifier = Modifier.padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            if (product.primaryImage.isNotBlank()) {
                                AsyncImage(
                                    model = product.primaryImage,
                                    contentDescription = null,
                                    contentScale = ContentScale.Crop,
                                    modifier = Modifier.size(60.dp).clip(RoundedCornerShape(8.dp))
                                )
                                Spacer(modifier = Modifier.width(10.dp))
                            }

                            Column(modifier = Modifier.weight(1f)) {
                                Text(product.name, style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold, color = TextPrimary, maxLines = 1)
                                Text("القسم: ${product.category} | SKU: ${product.sku}", style = MaterialTheme.typography.labelSmall, color = TextTertiary)
                                Text("السعر: ${product.basePrice.toInt()} ج.م | المخزون: ${product.stock} قطعة", style = MaterialTheme.typography.labelMedium, color = ChampagneGoldDark, fontWeight = FontWeight.Bold)
                            }

                            IconButton(onClick = { onEditClick(product) }) {
                                Icon(Icons.Default.Edit, contentDescription = "تعديل", tint = SkyBlue)
                            }
                            IconButton(onClick = { onDeleteClick(product.id) }) {
                                Icon(Icons.Default.Delete, contentDescription = "حذف", tint = StatusRed)
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun AdminCategoriesTab(
    categories: List<Category>,
    onAddCategoryClick: () -> Unit,
    onDeleteCategory: (String) -> Unit
) {
    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("أقسام المفروشات", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = TextPrimary)
            Button(
                onClick = onAddCategoryClick,
                colors = ButtonDefaults.buttonColors(containerColor = SkyBlue),
                shape = RoundedCornerShape(10.dp)
            ) {
                Icon(Icons.Default.Add, contentDescription = null, tint = Color.White)
                Spacer(modifier = Modifier.width(4.dp))
                Text("إضافة قسم")
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(8.dp),
            contentPadding = PaddingValues(bottom = 80.dp)
        ) {
            items(categories) { cat ->
                Card(
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    modifier = Modifier.fillMaxWidth().border(1.dp, FrostBlue, RoundedCornerShape(12.dp))
                ) {
                    Row(
                        modifier = Modifier.padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(cat.name, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = TextPrimary)
                        IconButton(onClick = { onDeleteCategory(cat.id) }) {
                            Icon(Icons.Default.Delete, contentDescription = "حذف", tint = StatusRed)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun AdminSettingsTab(
    storeSettings: StoreSettings,
    onSave: (StoreSettings) -> Unit
) {
    var storeName by remember(storeSettings) { mutableStateOf(storeSettings.storeName) }
    var instapayInfo by remember(storeSettings) { mutableStateOf(storeSettings.instapayInfo) }
    var walletInfo by remember(storeSettings) { mutableStateOf(storeSettings.walletInfo) }
    var shippingInfo by remember(storeSettings) { mutableStateOf(storeSettings.shippingInfo) }
    var returnPolicy by remember(storeSettings) { mutableStateOf(storeSettings.returnPolicy) }
    var aiSystemPrompt by remember(storeSettings) { mutableStateOf(storeSettings.aiSystemPrompt) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
            .padding(bottom = 80.dp)
    ) {
        Text("بيانات المتجر وطرق التحويل والذكاء الاصطناعي", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = SkyBlue)
        Spacer(modifier = Modifier.height(10.dp))

        OutlinedTextField(
            value = storeName,
            onValueChange = { storeName = it },
            label = { Text("اسم المتجر") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = instapayInfo,
            onValueChange = { instapayInfo = it },
            label = { Text("بيانات تحويل إنستاباي (InstaPay)") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = walletInfo,
            onValueChange = { walletInfo = it },
            label = { Text("أرقام المحافظ الإلكترونية (فودافون/أورنج كاش)") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = shippingInfo,
            onValueChange = { shippingInfo = it },
            label = { Text("معلومات الشحن والتوصيل") },
            minLines = 2,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = returnPolicy,
            onValueChange = { returnPolicy = it },
            label = { Text("سياسة الاستبدال والاسترجاع") },
            minLines = 2,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = aiSystemPrompt,
            onValueChange = { aiSystemPrompt = it },
            label = { Text("تعليمات مساعد الذكاء الاصطناعي (ASSAL AI System Prompt)") },
            minLines = 3,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = {
                onSave(
                    storeSettings.copy(
                        storeName = storeName.trim(),
                        instapayInfo = instapayInfo.trim(),
                        walletInfo = walletInfo.trim(),
                        shippingInfo = shippingInfo.trim(),
                        returnPolicy = returnPolicy.trim(),
                        aiSystemPrompt = aiSystemPrompt.trim()
                    )
                )
            },
            colors = ButtonDefaults.buttonColors(containerColor = SkyBlue),
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier.fillMaxWidth().height(48.dp)
        ) {
            Icon(Icons.Default.Save, contentDescription = null, tint = Color.White)
            Spacer(modifier = Modifier.width(6.dp))
            Text("حفظ التغييرات في المتجر")
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProductFormDialog(
    existingProduct: Product?,
    categories: List<Category>,
    onDismiss: () -> Unit,
    onSave: (Product) -> Unit
) {
    var name by remember { mutableStateOf(existingProduct?.name.orEmpty()) }
    var category by remember { mutableStateOf(existingProduct?.category?.ifBlank { categories.firstOrNull()?.name.orEmpty() } ?: categories.firstOrNull()?.name.orEmpty()) }
    var basePrice by remember { mutableStateOf(existingProduct?.basePrice?.toInt()?.toString().orEmpty()) }
    var originalPrice by remember { mutableStateOf(existingProduct?.originalPrice?.toInt()?.toString().orEmpty()) }
    var discountPercent by remember { mutableStateOf(existingProduct?.discountPercent?.toString().orEmpty()) }
    var stock by remember { mutableStateOf(existingProduct?.stock?.toString() ?: "10") }
    var sku by remember { mutableStateOf(existingProduct?.sku.orEmpty()) }
    var materials by remember { mutableStateOf(existingProduct?.materials.orEmpty()) }
    var sizesText by remember { mutableStateOf(existingProduct?.sizes?.joinToString("، ").orEmpty()) }
    var colorsText by remember { mutableStateOf(existingProduct?.colors?.joinToString("، ").orEmpty()) }
    var imageUrlsText by remember { mutableStateOf(existingProduct?.images?.joinToString("\n").orEmpty()) }
    var description by remember { mutableStateOf(existingProduct?.description.orEmpty()) }
    var badge by remember { mutableStateOf(existingProduct?.badge ?: "جديد") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(if (existingProduct == null) "إضافة منتج مفروشات جديد" else "تعديل بيانات المنتج") },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState())
            ) {
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("اسم المنتج * (مثال: طقم ملاية قطن مصري ملكي)") },
                    modifier = Modifier.fillMaxWidth().testTag("product_name_input")
                )

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = category,
                    onValueChange = { category = it },
                    label = { Text("القسم *") },
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(8.dp))

                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = basePrice,
                        onValueChange = { basePrice = it },
                        label = { Text("السعر الحالي *") },
                        modifier = Modifier.weight(1f).testTag("product_price_input")
                    )
                    OutlinedTextField(
                        value = originalPrice,
                        onValueChange = { originalPrice = it },
                        label = { Text("السعر قبل الخصم") },
                        modifier = Modifier.weight(1f)
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = stock,
                        onValueChange = { stock = it },
                        label = { Text("الكمية بالمخزن *") },
                        modifier = Modifier.weight(1f)
                    )
                    OutlinedTextField(
                        value = sku,
                        onValueChange = { sku = it },
                        label = { Text("كود SKU") },
                        modifier = Modifier.weight(1f)
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = materials,
                    onValueChange = { materials = it },
                    label = { Text("الخامات (مثال: قطن مصري 100% تمشيط فائق)") },
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = sizesText,
                    onValueChange = { sizesText = it },
                    label = { Text("المقاسات (مفصولة بفاصلة: 240x260, 200x220)") },
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = colorsText,
                    onValueChange = { colorsText = it },
                    label = { Text("الألوان (مفصولة بفاصلة: سكري، كشمير، رمادي)") },
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = imageUrlsText,
                    onValueChange = { imageUrlsText = it },
                    label = { Text("روابط الصور (رابط في كل سطر)") },
                    minLines = 2,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = description,
                    onValueChange = { description = it },
                    label = { Text("الوصف التفصيلي") },
                    minLines = 2,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (name.isNotBlank() && basePrice.toDoubleOrNull() != null) {
                        val sizesList = sizesText.split(Regex("[,،\n]")).map { it.trim() }.filter { it.isNotBlank() }
                        val colorsList = colorsText.split(Regex("[,،\n]")).map { it.trim() }.filter { it.isNotBlank() }
                        val imagesList = imageUrlsText.split("\n").map { it.trim() }.filter { it.isNotBlank() }
                        val bPrice = basePrice.toDoubleOrNull() ?: 0.0
                        val origPrice = originalPrice.toDoubleOrNull() ?: bPrice
                        val disc = if (origPrice > bPrice) (((origPrice - bPrice) / origPrice) * 100).toInt() else 0

                        val product = Product(
                            id = existingProduct?.id.orEmpty(),
                            name = name.trim(),
                            category = category.trim(),
                            basePrice = bPrice,
                            originalPrice = origPrice,
                            discountPercent = disc,
                            stock = stock.toIntOrNull() ?: 10,
                            sku = sku.ifBlank { "CAA-${System.currentTimeMillis().toString().takeLast(6)}" },
                            materials = materials.trim(),
                            sizes = sizesList,
                            colors = colorsList,
                            images = imagesList,
                            description = description.trim(),
                            badge = badge,
                            isAvailable = true
                        )
                        onSave(product)
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = SkyBlue),
                modifier = Modifier.testTag("save_product_form_btn")
            ) {
                Text("حفظ")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("إلغاء")
            }
        }
    )
}

@Composable
fun OrderDetailsAdminDialog(
    order: Order,
    onDismiss: () -> Unit,
    onStatusChange: (String) -> Unit,
    onPaymentStatusChange: (String) -> Unit
) {
    val statuses = listOf("جديد", "قيد المراجعة", "تم التأكيد", "قيد التجهيز", "تم الشحن", "تم التسليم", "ملغي", "مرتجع")
    val payStatuses = listOf("pending", "verified", "rejected")

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("تفاصيل الطلب: ${order.id}") },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState())
            ) {
                Text("العميل: ${order.customerName}", fontWeight = FontWeight.Bold, color = TextPrimary)
                Text("الهاتف: ${order.customerPhone}", color = TextSecondary)
                Text("العنوان: ${order.governorate} - ${order.city} - ${order.address}", color = TextSecondary)
                if (order.notes.isNotBlank()) {
                    Text("ملاحظات: ${order.notes}", color = TextTertiary)
                }

                Divider(color = FrostBlue, modifier = Modifier.padding(vertical = 8.dp))

                Text("المنتجات المطلوبة:", fontWeight = FontWeight.Bold)
                order.items.forEach { item ->
                    Text("• ${item.title} (كمية ${item.quantity}) - ${item.selectedSize} ${item.selectedColor} - ${(item.price * item.quantity).toInt()} ج.م")
                }

                Spacer(modifier = Modifier.height(6.dp))
                Text("الإجمالي الكلي: ${order.total.toInt()} ج.م", fontWeight = FontWeight.Bold, color = ChampagneGoldDark)

                Divider(color = FrostBlue, modifier = Modifier.padding(vertical = 8.dp))

                // Payment verification
                Text("طريقة الدفع: ${order.paymentMethod} (الحالة: ${order.paymentStatus})", fontWeight = FontWeight.Bold)
                if (order.paymentProofUrl.isNotBlank()) {
                    Text("صورة إيصال التحويل مرفقة من العميل ✓", color = StatusGreen, fontWeight = FontWeight.Bold)
                }

                Row(horizontalArrangement = Arrangement.spacedBy(6.dp), modifier = Modifier.padding(top = 4.dp)) {
                    Button(
                        onClick = { onPaymentStatusChange("verified") },
                        colors = ButtonDefaults.buttonColors(containerColor = StatusGreen),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text("قبول وتأكيد الدفع", fontSize = 11.sp)
                    }
                    OutlinedButton(
                        onClick = { onPaymentStatusChange("rejected") },
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text("رفض الدفع", color = StatusRed, fontSize = 11.sp)
                    }
                }

                Divider(color = FrostBlue, modifier = Modifier.padding(vertical = 8.dp))

                Text("تغيير مرحلة الطلب:", fontWeight = FontWeight.Bold)
                LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp), modifier = Modifier.padding(top = 4.dp)) {
                    items(statuses) { st ->
                        val isCurrent = order.status == st
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .background(if (isCurrent) SkyBlue else FrostBlue)
                                .clickable { onStatusChange(st) }
                                .padding(horizontal = 10.dp, vertical = 6.dp)
                        ) {
                            Text(st, color = if (isCurrent) Color.White else TextPrimary, fontSize = 12.sp, fontWeight = if (isCurrent) FontWeight.Bold else FontWeight.Normal)
                        }
                    }
                }
            }
        },
        confirmButton = {
            Button(onClick = onDismiss) {
                Text("إغلاق")
            }
        }
    )
}
