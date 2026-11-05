package com.example

import android.content.Context
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.credentials.CredentialManager
import com.example.data.model.CartItem
import com.example.data.model.ChatMessage
import com.example.data.model.Order
import com.example.data.model.Product
import com.example.data.model.StoreSettings
import com.example.data.repository.AssalAiRepository
import com.example.data.repository.CartRepository
import com.example.data.repository.OrderRepository
import com.example.data.repository.ProductRepository
import com.example.data.repository.StoreRepository
import com.example.data.repository.WishlistRepository
import com.example.ui.components.AssalBottomNavBar
import com.example.ui.components.AssalTopAppBar
import com.example.ui.components.FloatingAiButton
import com.example.ui.screens.AccountScreen
import com.example.ui.screens.AdminDashboardScreen
import com.example.ui.screens.AssalAiScreen
import com.example.ui.screens.BranchesScreen
import com.example.ui.screens.CartScreen
import com.example.ui.screens.CategoriesScreen
import com.example.ui.screens.CheckoutScreen
import com.example.ui.screens.HomeScreen
import com.example.ui.screens.OrderSuccessScreen
import com.example.ui.screens.OrderTrackingScreen
import com.example.ui.screens.OrdersScreen
import com.example.ui.screens.PoliciesScreen
import com.example.ui.screens.ProductDetailsScreen
import com.example.ui.screens.ProductsScreen
import com.example.ui.theme.CenterAlAssalTheme
import com.example.util.attemptSilentGoogleSignIn
import com.example.util.configureAppCheck
import com.example.util.launchGoogleSignIn
import com.example.util.signOutGoogle
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Mandatory App Check initialization with launch intent as first statement
        configureAppCheck(this, intent)

        enableEdgeToEdge()

        setContent {
            // Force RTL layout direction for authentic Arabic experience
            CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
                CenterAlAssalTheme {
                    CenterAlAssalApp()
                }
            }
        }
    }
}

enum class Screen {
    HOME,
    CATEGORIES,
    WISHLIST,
    ORDERS,
    ACCOUNT,
    PRODUCTS_CATALOG,
    PRODUCT_DETAILS,
    CART,
    CHECKOUT,
    ORDER_SUCCESS,
    ORDER_TRACKING,
    ASSAL_AI,
    BRANCHES,
    POLICIES,
    ADMIN_DASHBOARD
}

@Composable
fun CenterAlAssalApp() {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }
    val credentialManager = remember { CredentialManager.create(context) }

    // Repositories
    val productRepo = remember { ProductRepository(context) }
    val orderRepo = remember { OrderRepository(context) }
    val cartRepo = remember { CartRepository(context) }
    val wishlistRepo = remember { WishlistRepository(context) }
    val storeRepo = remember { StoreRepository(context) }
    val aiRepo = remember { AssalAiRepository(productRepo, orderRepo, storeRepo) }

    // Navigation and screen state
    var currentScreen by remember { mutableStateOf(Screen.HOME) }
    var screenStack by remember { mutableStateOf(listOf(Screen.HOME)) }

    fun navigateTo(screen: Screen) {
        screenStack = screenStack + screen
        currentScreen = screen
    }

    fun navigateBack() {
        if (screenStack.size > 1) {
            val newStack = screenStack.dropLast(1)
            screenStack = newStack
            currentScreen = newStack.last()
        } else {
            currentScreen = Screen.HOME
        }
    }

    // Detail targets
    var selectedProductForDetail by remember { mutableStateOf<Product?>(null) }
    var selectedCategoryFilter by remember { mutableStateOf("الكل") }
    var confirmedOrderCode by remember { mutableStateOf("") }
    var confirmedOrderTotal by remember { mutableStateOf(0.0) }
    var confirmedOrderPhone by remember { mutableStateOf("") }
    var trackingCodeTarget by remember { mutableStateOf("") }

    // Realtime observed data
    val products by productRepo.observeProducts().collectAsState(initial = emptyList())
    val categories by productRepo.observeCategories().collectAsState(initial = emptyList())
    val storeSettings by storeRepo.observeStoreSettings().collectAsState(initial = StoreSettings())

    // Auth state
    var authUser by remember { mutableStateOf(Firebase.auth.currentUser) }
    var isAdminUser by remember { mutableStateOf(false) }

    // Silent auth & Category initialization on first launch
    LaunchedEffect(Unit) {
        productRepo.initializeDefaultCategoriesIfEmpty()
        attemptSilentGoogleSignIn(
            context = context,
            credentialManager = credentialManager,
            onAuthSuccess = {
                authUser = Firebase.auth.currentUser
                scope.launch {
                    isAdminUser = storeRepo.isCurrentUserAdmin()
                }
            },
            onUnauthenticated = {
                authUser = null
            },
            scope = scope
        )
    }

    // Auth state listener
    LaunchedEffect(authUser) {
        if (authUser != null) {
            isAdminUser = storeRepo.isCurrentUserAdmin()
        }
    }

    // User cart & wishlist (observed if user signed in, else in-memory guest cart)
    val guestCart = remember { mutableStateListOf<CartItem>() }
    val userCartFlow = remember(authUser) {
        if (authUser != null) cartRepo.observeCart() else null
    }
    val userCart by (userCartFlow?.collectAsState(initial = emptyList()) ?: mutableStateOf(guestCart))

    val activeCartItems = if (authUser != null) userCart else guestCart

    val userWishlistFlow = remember(authUser) {
        if (authUser != null) wishlistRepo.observeWishlist() else null
    }
    val userWishlist by (userWishlistFlow?.collectAsState(initial = emptyList()) ?: mutableStateOf(emptyList()))
    val wishlistIds = remember(userWishlist) { userWishlist.map { it.productId }.toSet() }

    val userOrdersFlow = remember(authUser) {
        if (authUser != null) orderRepo.observeUserOrders() else null
    }
    val userOrders by (userOrdersFlow?.collectAsState(initial = emptyList()) ?: mutableStateOf(emptyList()))

    val adminOrders by orderRepo.observeAllOrdersForAdmin().collectAsState(initial = emptyList())

    // ASSAL AI Chat state
    val chatMessages = remember {
        mutableStateListOf(
            ChatMessage(
                text = "أهلاً بحضرتك في سنتر العسال للمفروشات بالإسكندرية! أنا ASSAL AI مساعد المبيعات الذكي الخاص بك. تحب أساعدك في اختيار أطقم ملايات، بطاطين، لحاف، أو تشكيلة العرايس؟",
                isUser = false,
                suggestedActions = listOf(
                    "عايز أطقم ملايات قطن مصري",
                    "إيه أفضل لحاف للشتا؟",
                    "أين فروعكم بالإسكندرية؟",
                    "العروض والخصومات الحالية"
                )
            )
        )
    }
    var isAiGenerating by remember { mutableStateOf(false) }

    // Android Hardware BackHandler
    BackHandler(enabled = currentScreen != Screen.HOME) {
        navigateBack()
    }

    val isTopLevelScreen = currentScreen in listOf(
        Screen.HOME,
        Screen.CATEGORIES,
        Screen.WISHLIST,
        Screen.ORDERS,
        Screen.ACCOUNT
    )

    Scaffold(
        topBar = {
            if (isTopLevelScreen) {
                AssalTopAppBar(
                    title = storeSettings.storeName.ifBlank { "سنتر العسال" },
                    subtitle = storeSettings.storeSubtitle,
                    cartCount = activeCartItems.sumOf { it.quantity },
                    onSearchClick = { navigateTo(Screen.PRODUCTS_CATALOG) },
                    onCartClick = { navigateTo(Screen.CART) }
                )
            }
        },
        bottomBar = {
            if (isTopLevelScreen) {
                val currentRoute = when (currentScreen) {
                    Screen.HOME -> "home"
                    Screen.CATEGORIES -> "categories"
                    Screen.WISHLIST -> "wishlist"
                    Screen.ORDERS -> "orders"
                    Screen.ACCOUNT -> "account"
                    else -> "home"
                }

                AssalBottomNavBar(
                    currentRoute = currentRoute,
                    onNavigate = { route ->
                        when (route) {
                            "home" -> currentScreen = Screen.HOME
                            "categories" -> currentScreen = Screen.CATEGORIES
                            "wishlist" -> currentScreen = Screen.WISHLIST
                            "orders" -> currentScreen = Screen.ORDERS
                            "account" -> currentScreen = Screen.ACCOUNT
                        }
                    }
                )
            }
        },
        floatingActionButton = {
            if (currentScreen != Screen.ASSAL_AI && currentScreen != Screen.ADMIN_DASHBOARD) {
                FloatingAiButton(
                    onClick = { navigateTo(Screen.ASSAL_AI) }
                )
            }
        },
        snackbarHost = { SnackbarHost(snackbarHostState) },
        containerColor = com.example.ui.theme.IvoryWhite
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            when (currentScreen) {
                Screen.HOME -> {
                    HomeScreen(
                        products = products,
                        categories = categories,
                        storeSettings = storeSettings,
                        wishlistIds = wishlistIds,
                        onProductClick = { prod ->
                            selectedProductForDetail = prod
                            navigateTo(Screen.PRODUCT_DETAILS)
                        },
                        onAddToCart = { prod ->
                            scope.launch {
                                if (authUser != null) {
                                    cartRepo.addToCart(prod)
                                } else {
                                    val existing = guestCart.find { it.productId == prod.id }
                                    if (existing != null) {
                                        val idx = guestCart.indexOf(existing)
                                        guestCart[idx] = existing.copy(quantity = existing.quantity + 1)
                                    } else {
                                        guestCart.add(
                                            CartItem(
                                                id = "guest_${System.currentTimeMillis()}",
                                                productId = prod.id,
                                                title = prod.name,
                                                price = prod.finalPrice,
                                                imageUrl = prod.primaryImage,
                                                quantity = 1
                                            )
                                        )
                                    }
                                }
                                snackbarHostState.showSnackbar("تمت إضافة ${prod.name} إلى السلة")
                            }
                        },
                        onToggleWishlist = { prod ->
                            if (authUser != null) {
                                scope.launch { wishlistRepo.toggleWishlist(prod) }
                            } else {
                                Toast.makeText(context, "سجل دخولك بحساب جوجل لحفظ المفروشات بالمفضلة", Toast.LENGTH_SHORT).show()
                            }
                        },
                        onCategoryClick = { catName ->
                            selectedCategoryFilter = catName
                            navigateTo(Screen.PRODUCTS_CATALOG)
                        },
                        onSearchClick = { navigateTo(Screen.PRODUCTS_CATALOG) },
                        onOpenAiChat = { navigateTo(Screen.ASSAL_AI) },
                        onViewAllProducts = {
                            selectedCategoryFilter = "الكل"
                            navigateTo(Screen.PRODUCTS_CATALOG)
                        }
                    )
                }

                Screen.CATEGORIES -> {
                    CategoriesScreen(
                        categories = categories,
                        products = products,
                        onCategoryClick = { catName ->
                            selectedCategoryFilter = catName
                            navigateTo(Screen.PRODUCTS_CATALOG)
                        }
                    )
                }

                Screen.PRODUCTS_CATALOG -> {
                    ProductsScreen(
                        products = products,
                        categories = categories,
                        selectedCategoryInitial = selectedCategoryFilter,
                        wishlistIds = wishlistIds,
                        onProductClick = { prod ->
                            selectedProductForDetail = prod
                            navigateTo(Screen.PRODUCT_DETAILS)
                        },
                        onAddToCart = { prod ->
                            scope.launch {
                                if (authUser != null) {
                                    cartRepo.addToCart(prod)
                                } else {
                                    guestCart.add(
                                        CartItem(
                                            id = "guest_${System.currentTimeMillis()}",
                                            productId = prod.id,
                                            title = prod.name,
                                            price = prod.finalPrice,
                                            imageUrl = prod.primaryImage,
                                            quantity = 1
                                        )
                                    )
                                }
                                snackbarHostState.showSnackbar("تمت إضافة المنتج للسلة")
                            }
                        },
                        onToggleWishlist = { prod ->
                            if (authUser != null) {
                                scope.launch { wishlistRepo.toggleWishlist(prod) }
                            } else {
                                Toast.makeText(context, "سجل دخولك لحفظ المفروشات بالمفضلة", Toast.LENGTH_SHORT).show()
                            }
                        }
                    )
                }

                Screen.PRODUCT_DETAILS -> {
                    selectedProductForDetail?.let { prod ->
                        ProductDetailsScreen(
                            product = prod,
                            isFavorite = wishlistIds.contains(prod.id),
                            onBackClick = { navigateBack() },
                            onAddToCart = { product, qty, size, color ->
                                scope.launch {
                                    if (authUser != null) {
                                        cartRepo.addToCart(product, qty, size, color)
                                    } else {
                                        guestCart.add(
                                            CartItem(
                                                id = "guest_${System.currentTimeMillis()}",
                                                productId = product.id,
                                                title = product.name,
                                                price = product.finalPrice,
                                                imageUrl = product.primaryImage,
                                                quantity = qty,
                                                selectedSize = size,
                                                selectedColor = color
                                            )
                                        )
                                    }
                                    snackbarHostState.showSnackbar("تمت إضافة $qty قطع إلى السلة بنجاح")
                                }
                            },
                            onToggleWishlist = { product ->
                                if (authUser != null) {
                                    scope.launch { wishlistRepo.toggleWishlist(product) }
                                } else {
                                    Toast.makeText(context, "سجل دخولك لحفظ المفروشات بالمفضلة", Toast.LENGTH_SHORT).show()
                                }
                            },
                            onAskAiAboutProduct = { product ->
                                chatMessages.add(
                                    ChatMessage(
                                        text = "عايز استفسر عن ${product.name} (سعره ${product.finalPrice} ج.م وخامته ${product.materials})",
                                        isUser = true
                                    )
                                )
                                navigateTo(Screen.ASSAL_AI)
                                scope.launch {
                                    isAiGenerating = true
                                    val result = aiRepo.sendMessage(
                                        userText = "عايز استفسر عن ${product.name} بتفصيل أكتر ومقاساته ونصائح العناية به",
                                        imageBitmap = null,
                                        conversationHistory = chatMessages,
                                        allProducts = products,
                                        storeSettings = storeSettings
                                    )
                                    isAiGenerating = false
                                    result.onSuccess { chatMessages.add(it) }
                                }
                            }
                        )
                    } ?: run {
                        currentScreen = Screen.HOME
                    }
                }

                Screen.CART -> {
                    CartScreen(
                        cartItems = activeCartItems,
                        onUpdateQuantity = { itemId, newQty ->
                            scope.launch {
                                if (authUser != null) {
                                    cartRepo.updateQuantity(itemId, newQty)
                                } else {
                                    val item = guestCart.find { it.id == itemId }
                                    if (item != null) {
                                        if (newQty <= 0) guestCart.remove(item)
                                        else {
                                            val idx = guestCart.indexOf(item)
                                            guestCart[idx] = item.copy(quantity = newQty)
                                        }
                                    }
                                }
                            }
                        },
                        onRemoveItem = { itemId ->
                            scope.launch {
                                if (authUser != null) cartRepo.removeItem(itemId)
                                else guestCart.removeAll { it.id == itemId }
                            }
                        },
                        onCheckoutClick = { navigateTo(Screen.CHECKOUT) },
                        onStartShoppingClick = {
                            selectedCategoryFilter = "الكل"
                            navigateTo(Screen.PRODUCTS_CATALOG)
                        }
                    )
                }

                Screen.CHECKOUT -> {
                    CheckoutScreen(
                        cartItems = activeCartItems,
                        storeSettings = storeSettings,
                        onBackClick = { navigateBack() },
                        onConfirmOrder = { order ->
                            scope.launch {
                                val orderResult = orderRepo.createOrder(order)
                                orderResult.onSuccess { code ->
                                    confirmedOrderCode = code
                                    confirmedOrderTotal = order.total
                                    confirmedOrderPhone = order.customerPhone
                                    // Clear cart
                                    if (authUser != null) cartRepo.clearCart()
                                    else guestCart.clear()
                                    navigateTo(Screen.ORDER_SUCCESS)
                                }.onFailure { error ->
                                    Toast.makeText(context, "فشل إنشاء الطلب: ${error.localizedMessage}", Toast.LENGTH_LONG).show()
                                }
                            }
                        }
                    )
                }

                Screen.ORDER_SUCCESS -> {
                    OrderSuccessScreen(
                        orderCode = confirmedOrderCode,
                        totalAmount = confirmedOrderTotal,
                        customerPhone = confirmedOrderPhone,
                        onTrackOrder = { code ->
                            trackingCodeTarget = code
                            navigateTo(Screen.ORDER_TRACKING)
                        },
                        onBackToHome = {
                            screenStack = listOf(Screen.HOME)
                            currentScreen = Screen.HOME
                        }
                    )
                }

                Screen.ORDERS -> {
                    OrdersScreen(
                        orders = userOrders,
                        onTrackOrderCode = { code ->
                            trackingCodeTarget = code
                            navigateTo(Screen.ORDER_TRACKING)
                        },
                        onStartShopping = {
                            selectedCategoryFilter = "الكل"
                            navigateTo(Screen.PRODUCTS_CATALOG)
                        }
                    )
                }

                Screen.ORDER_TRACKING -> {
                    OrderTrackingScreen(
                        initialCode = trackingCodeTarget,
                        onBackClick = { navigateBack() },
                        onFindOrder = { code, phone ->
                            orderRepo.getOrderByCodeAndPhone(code, phone)
                        }
                    )
                }

                Screen.WISHLIST -> {
                    // Wishlist screen displays saved items
                    ProductsScreen(
                        products = products.filter { wishlistIds.contains(it.id) },
                        categories = categories,
                        selectedCategoryInitial = "الكل",
                        wishlistIds = wishlistIds,
                        onProductClick = { prod ->
                            selectedProductForDetail = prod
                            navigateTo(Screen.PRODUCT_DETAILS)
                        },
                        onAddToCart = { prod ->
                            scope.launch {
                                if (authUser != null) cartRepo.addToCart(prod)
                                else guestCart.add(CartItem(productId = prod.id, title = prod.name, price = prod.finalPrice, quantity = 1))
                                snackbarHostState.showSnackbar("تمت إضافة المفروشات إلى السلة")
                            }
                        },
                        onToggleWishlist = { prod ->
                            if (authUser != null) scope.launch { wishlistRepo.toggleWishlist(prod) }
                        }
                    )
                }

                Screen.ACCOUNT -> {
                    AccountScreen(
                        storeSettings = storeSettings,
                        isAdminUser = isAdminUser,
                        onSignInClick = {
                            launchGoogleSignIn(
                                context = context,
                                credentialManager = credentialManager,
                                onAuthSuccess = {
                                    authUser = Firebase.auth.currentUser
                                    scope.launch {
                                        isAdminUser = storeRepo.isCurrentUserAdmin()
                                        snackbarHostState.showSnackbar("مرحباً بك ${authUser?.displayName ?: ""}")
                                    }
                                },
                                onAuthError = { err ->
                                    Toast.makeText(context, err, Toast.LENGTH_SHORT).show()
                                },
                                scope = scope
                            )
                        },
                        onSignOutClick = {
                            signOutGoogle(
                                context = context,
                                credentialManager = credentialManager,
                                onSignOutComplete = {
                                    authUser = null
                                    isAdminUser = false
                                    Toast.makeText(context, "تم تسجيل الخروج", Toast.LENGTH_SHORT).show()
                                },
                                scope = scope
                            )
                        },
                        onNavigateToOrders = { navigateTo(Screen.ORDERS) },
                        onNavigateToWishlist = { navigateTo(Screen.WISHLIST) },
                        onNavigateToBranches = { navigateTo(Screen.BRANCHES) },
                        onNavigateToPolicies = { navigateTo(Screen.POLICIES) },
                        onOpenAdminDashboard = { navigateTo(Screen.ADMIN_DASHBOARD) }
                    )
                }

                Screen.BRANCHES -> {
                    BranchesScreen(
                        storeSettings = storeSettings,
                        onBackClick = { navigateBack() }
                    )
                }

                Screen.POLICIES -> {
                    PoliciesScreen(
                        storeSettings = storeSettings,
                        onBackClick = { navigateBack() }
                    )
                }

                Screen.ASSAL_AI -> {
                    AssalAiScreen(
                        chatMessages = chatMessages,
                        isLoading = isAiGenerating,
                        onSendMessage = { text, bitmap ->
                            chatMessages.add(
                                ChatMessage(
                                    text = text,
                                    isUser = true,
                                    imageBitmap = bitmap
                                )
                            )
                            scope.launch {
                                isAiGenerating = true
                                val result = aiRepo.sendMessage(
                                    userText = text,
                                    imageBitmap = bitmap,
                                    conversationHistory = chatMessages,
                                    allProducts = products,
                                    storeSettings = storeSettings
                                )
                                isAiGenerating = false
                                result.onSuccess { responseMsg ->
                                    chatMessages.add(responseMsg)
                                }.onFailure { err ->
                                    chatMessages.add(
                                        ChatMessage(
                                            text = "عذراً يا فندم: ${err.localizedMessage ?: "حدث خطأ غير متوقع. يمكنك التواصل معنا عبر الواتساب على 01277714100"}",
                                            isUser = false
                                        )
                                    )
                                }
                            }
                        },
                        onProductClick = { prod ->
                            selectedProductForDetail = prod
                            navigateTo(Screen.PRODUCT_DETAILS)
                        },
                        onAddToCart = { prod ->
                            scope.launch {
                                if (authUser != null) cartRepo.addToCart(prod)
                                else guestCart.add(CartItem(productId = prod.id, title = prod.name, price = prod.finalPrice, quantity = 1))
                                snackbarHostState.showSnackbar("تمت إضافة ${prod.name} إلى السلة")
                            }
                        },
                        onClearChat = {
                            chatMessages.clear()
                            chatMessages.add(
                                ChatMessage(
                                    text = "أهلاً بحضرتك مرة أخرى في سنتر العسال! كيف أستطيع مساعدتك اليوم؟",
                                    isUser = false
                                )
                            )
                        },
                        onBackClick = { navigateBack() }
                    )
                }

                Screen.ADMIN_DASHBOARD -> {
                    AdminDashboardScreen(
                        orders = adminOrders,
                        products = products,
                        categories = categories,
                        storeSettings = storeSettings,
                        onBackClick = { navigateBack() },
                        onUpdateOrderStatus = { orderId, newStatus ->
                            scope.launch {
                                orderRepo.updateOrderStatus(orderId, newStatus)
                                snackbarHostState.showSnackbar("تم تحديث حالة الطلب $orderId إلى $newStatus")
                            }
                        },
                        onUpdatePaymentStatus = { orderId, newPayStatus ->
                            scope.launch {
                                orderRepo.updatePaymentStatus(orderId, newPayStatus)
                                snackbarHostState.showSnackbar("تم تحديث حالة الدفع إلى $newPayStatus")
                            }
                        },
                        onAddOrUpdateProduct = { prod ->
                            scope.launch {
                                if (prod.id.isBlank()) {
                                    productRepo.addProduct(prod)
                                    snackbarHostState.showSnackbar("تمت إضافة المنتج بنجاح")
                                } else {
                                    productRepo.updateProduct(prod)
                                    snackbarHostState.showSnackbar("تم تحديث المنتج بنجاح")
                                }
                            }
                        },
                        onDeleteProduct = { id ->
                            scope.launch {
                                productRepo.deleteProduct(id)
                                snackbarHostState.showSnackbar("تم حذف المنتج")
                            }
                        },
                        onAddCategory = { catName ->
                            scope.launch {
                                productRepo.addCategory(
                                    com.example.data.model.Category(
                                        name = catName,
                                        sortOrder = categories.size
                                    )
                                )
                                snackbarHostState.showSnackbar("تمت إضافة القسم")
                            }
                        },
                        onDeleteCategory = { id ->
                            scope.launch {
                                productRepo.deleteCategory(id)
                                snackbarHostState.showSnackbar("تم حذف القسم")
                            }
                        },
                        onUpdateStoreSettings = { newSettings ->
                            scope.launch {
                                storeRepo.updateStoreSettings(newSettings)
                                snackbarHostState.showSnackbar("تم تحديث إعدادات المتجر وبيانات AI")
                            }
                        }
                    )
                }
            }
        }
    }
}
