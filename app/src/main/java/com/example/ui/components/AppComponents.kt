package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Category
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.LocalShipping
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.ShoppingBag
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.Category
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.LocalShipping
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.data.model.Product
import com.example.ui.theme.*


enum class NavDestination(val route: String, val titleAr: String, val activeIcon: ImageVector, val inactiveIcon: ImageVector) {
    HOME("home", "الرئيسية", Icons.Filled.Home, Icons.Outlined.Home),
    CATEGORIES("categories", "الأقسام", Icons.Filled.Category, Icons.Outlined.Category),
    WISHLIST("wishlist", "المفضلة", Icons.Filled.Favorite, Icons.Outlined.FavoriteBorder),
    ORDERS("orders", "طلباتي", Icons.Filled.LocalShipping, Icons.Outlined.LocalShipping),
    ACCOUNT("account", "حسابي", Icons.Filled.Person, Icons.Outlined.Person)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AssalTopAppBar(
    title: String = "سنتر العسال",
    subtitle: String = "CENTER AL ASSAL",
    cartCount: Int = 0,
    onSearchClick: () -> Unit = {},
    onCartClick: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    TopAppBar(
        title = {
            Column {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = title,
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = SkyBlue
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Box(
                        modifier = Modifier
                            .background(ChampagneGoldLight, RoundedCornerShape(4.dp))
                            .padding(horizontal = 6.dp, vertical = 2.dp)
                    ) {
                        Text(
                            text = "فاخر",
                            style = MaterialTheme.typography.labelSmall,
                            color = ChampagneGold,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.labelSmall,
                    color = TextTertiary,
                    letterSpacing = 1.sp
                )
            }
        },
        actions = {
            IconButton(
                onClick = onSearchClick,
                modifier = Modifier.testTag("top_search_button")
            ) {
                Icon(
                    imageVector = Icons.Default.Search,
                    contentDescription = "بحث",
                    tint = SkyBlue
                )
            }
            IconButton(
                onClick = onCartClick,
                modifier = Modifier.testTag("top_cart_button")
            ) {
                BadgedBox(
                    badge = {
                        if (cartCount > 0) {
                            Badge(
                                containerColor = ChampagneGold,
                                contentColor = Color.White
                            ) {
                                Text("$cartCount", fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                ) {
                    Icon(
                        imageVector = Icons.Default.ShoppingCart,
                        contentDescription = "سلة المشتريات",
                        tint = SkyBlue
                    )
                }
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = IvoryWhite
        ),
        modifier = modifier
    )
}

@Composable
fun AssalBottomNavBar(
    currentRoute: String,
    onNavigate: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    NavigationBar(
        containerColor = IvoryWhite,
        tonalElevation = 4.dp,
        modifier = modifier.navigationBarsPadding()
    ) {
        NavDestination.values().forEach { destination ->
            val isSelected = currentRoute == destination.route
            NavigationBarItem(
                selected = isSelected,
                onClick = { onNavigate(destination.route) },
                icon = {
                    Icon(
                        imageVector = if (isSelected) destination.activeIcon else destination.inactiveIcon,
                        contentDescription = destination.titleAr
                    )
                },
                label = {
                    Text(
                        text = destination.titleAr,
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                    )
                },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = SkyBlue,
                    selectedTextColor = SkyBlue,
                    unselectedIconColor = TextTertiary,
                    unselectedTextColor = TextTertiary,
                    indicatorColor = SkyBlueLight
                ),
                modifier = Modifier.testTag("bottom_nav_${destination.route}")
            )
        }
    }
}

@Composable
fun FloatingAiButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    FloatingActionButton(
        onClick = onClick,
        containerColor = SkyBlue,
        contentColor = Color.White,
        shape = RoundedCornerShape(20.dp),
        modifier = modifier.testTag("floating_ai_assistant_btn")
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp)
        ) {
            Icon(
                imageVector = Icons.Default.AutoAwesome,
                contentDescription = "ASSAL AI",
                tint = ChampagneGoldLight,
                modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = "ASSAL AI",
                fontWeight = FontWeight.Bold,
                fontSize = 13.sp,
                letterSpacing = 0.5.sp
            )
        }
    }
}

@Composable
fun ProductCard(
    product: Product,
    isFavorite: Boolean,
    onProductClick: () -> Unit,
    onAddToCartClick: () -> Unit,
    onFavoriteToggle: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        modifier = modifier
            .border(1.dp, FrostBlue, RoundedCornerShape(16.dp))
            .clickable { onProductClick() }
            .testTag("product_card_${product.id}")
    ) {
        Column {
            // Image Box
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(150.dp)
                    .background(SkyBlueLight)
            ) {
                if (product.primaryImage.isNotBlank()) {
                    AsyncImage(
                        model = product.primaryImage,
                        contentDescription = product.name,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxWidth().height(150.dp)
                    )
                } else {
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier.fillMaxWidth().height(150.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.ShoppingBag,
                            contentDescription = null,
                            tint = SkyBlue.copy(alpha = 0.3f),
                            modifier = Modifier.size(48.dp)
                        )
                    }
                }

                // Badge top-start
                if (product.badge.isNotBlank()) {
                    Box(
                        modifier = Modifier
                            .padding(8.dp)
                            .align(Alignment.TopStart)
                            .background(
                                Brush.horizontalGradient(listOf(ChampagneGold, ChampagneGoldDark)),
                                RoundedCornerShape(8.dp)
                            )
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        Text(
                            text = product.badge,
                            color = Color.White,
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                // Discount tag top-end
                if (product.hasDiscount) {
                    Box(
                        modifier = Modifier
                            .padding(8.dp)
                            .align(Alignment.TopEnd)
                            .background(StatusRed, RoundedCornerShape(8.dp))
                            .padding(horizontal = 6.dp, vertical = 2.dp)
                    ) {
                        Text(
                            text = "-${product.discountPercent}%",
                            color = Color.White,
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                // Favorite button bottom-end
                IconButton(
                    onClick = onFavoriteToggle,
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .padding(4.dp)
                        .background(Color.White.copy(alpha = 0.9f), CircleShape)
                        .size(32.dp)
                        .testTag("fav_btn_${product.id}")
                ) {
                    Icon(
                        imageVector = if (isFavorite) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                        contentDescription = "مفضلة",
                        tint = if (isFavorite) StatusRed else TextTertiary,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }

            // Info Section
            Column(modifier = Modifier.padding(10.dp)) {
                Text(
                    text = product.category,
                    style = MaterialTheme.typography.labelSmall,
                    color = SkyBlue,
                    maxLines = 1
                )
                Text(
                    text = product.name,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = TextPrimary,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.height(42.dp)
                )

                Spacer(modifier = Modifier.height(4.dp))

                // Price and Rating Row
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column {
                        Row(verticalAlignment = Alignment.Bottom) {
                            Text(
                                text = "${product.finalPrice.toInt()} ",
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.Bold,
                                color = ChampagneGold
                            )
                            Text(
                                text = "ج.م",
                                style = MaterialTheme.typography.labelSmall,
                                color = ChampagneGold,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                        if (product.hasDiscount && product.originalPrice > product.finalPrice) {
                            Text(
                                text = "${product.originalPrice.toInt()} ج.م",
                                style = MaterialTheme.typography.labelSmall,
                                textDecoration = TextDecoration.LineThrough,
                                color = TextTertiary
                            )
                        }
                    }

                    // Quick Add to cart icon
                    IconButton(
                        onClick = onAddToCartClick,
                        modifier = Modifier
                            .background(SkyBlueLight, CircleShape)
                            .size(34.dp)
                            .testTag("add_cart_${product.id}")
                    ) {
                        Icon(
                            imageVector = Icons.Default.ShoppingCart,
                            contentDescription = "أضف للسلة",
                            tint = SkyBlue,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun StatusBadge(status: String, modifier: Modifier = Modifier) {
    val pair: Pair<Color, Color> = when (status) {
        "جديد", "قيد المراجعة" -> Pair(SkyBlueLight, SkyBlue)
        "تم التأكيد", "قيد التجهيز" -> Pair(ChampagneGoldLight, ChampagneGoldDark)
        "تم الشحن" -> Pair(FrostBlue, SkyBlue)
        "تم التسليم" -> Pair(SageGreenLight, StatusGreen)
        "ملغي", "مرتجع" -> Pair(Color(0xFFFEE2E2), StatusRed)
        else -> Pair(WarmOffWhite, TextSecondary)
    }
    val bgColor = pair.first
    val textColor = pair.second

    Box(
        modifier = modifier
            .background(bgColor, RoundedCornerShape(8.dp))
            .padding(horizontal = 8.dp, vertical = 4.dp)
    ) {
        Text(
            text = status,
            color = textColor,
            style = MaterialTheme.typography.labelSmall,
            fontWeight = FontWeight.Bold
        )
    }
}
