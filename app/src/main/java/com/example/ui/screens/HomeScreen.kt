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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.LocalOffer
import androidx.compose.material.icons.filled.LocalShipping
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.PinDrop
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.ScrollableTabRow
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.Category
import com.example.data.model.Product
import com.example.data.model.StoreSettings
import com.example.ui.components.ProductCard
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
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.ui.theme.TextTertiary

@Composable
fun HomeScreen(
    products: List<Product>,
    categories: List<Category>,
    storeSettings: StoreSettings,
    wishlistIds: Set<String>,
    onProductClick: (Product) -> Unit,
    onAddToCart: (Product) -> Unit,
    onToggleWishlist: (Product) -> Unit,
    onCategoryClick: (String) -> Unit,
    onSearchClick: () -> Unit,
    onOpenAiChat: () -> Unit,
    onViewAllProducts: () -> Unit,
    modifier: Modifier = Modifier
) {
    var selectedFilterIndex by remember { mutableStateOf(0) }
    val filterTabs = listOf("الكل", "الأكثر مبيعاً", "أحدث المفروشات", "العروض والخصومات")

    val filteredProducts = remember(products, selectedFilterIndex) {
        when (selectedFilterIndex) {
            1 -> products.filter { it.badge == "الأكثر مبيعاً" || it.rating >= 4.8 }
            2 -> products.filter { it.badge == "جديد" || (it.createdAt != null) }
            3 -> products.filter { it.hasDiscount }
            else -> products
        }
    }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(IvoryWhite),
        contentPadding = PaddingValues(bottom = 100.dp)
    ) {
        // 1. Search Bar Header Trigger
        item {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(Color.White)
                    .border(1.dp, FrostBlue, RoundedCornerShape(16.dp))
                    .clickable { onSearchClick() }
                    .padding(horizontal = 16.dp, vertical = 14.dp)
                    .testTag("home_search_bar_trigger")
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = "بحث",
                        tint = SkyBlue,
                        modifier = Modifier.size(22.dp)
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(
                        text = "ابحث عن ملايات، بطاطين، لحاف، مفارش...",
                        style = MaterialTheme.typography.bodyMedium,
                        color = TextTertiary
                    )
                }
            }
        }

        // 2. Luxury Hero Banner
        item {
            Card(
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = Color.Transparent),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 6.dp)
                    .border(1.dp, ChampagneGold.copy(alpha = 0.3f), RoundedCornerShape(20.dp))
                    .testTag("hero_banner_card")
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            Brush.linearGradient(
                                listOf(SkyBlueLight, ChampagneGoldLight, FrostBlue)
                            )
                        )
                        .padding(20.dp)
                ) {
                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .background(ChampagneGold, CircleShape)
                                    .padding(horizontal = 10.dp, vertical = 4.dp)
                            ) {
                                Text(
                                    text = "تشكيلة 2026 الفاخرة",
                                    color = Color.White,
                                    style = MaterialTheme.typography.labelSmall,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                            Spacer(modifier = Modifier.weight(1f))
                            Icon(
                                imageVector = Icons.Default.Star,
                                contentDescription = null,
                                tint = ChampagneGold,
                                modifier = Modifier.size(18.dp)
                            )
                            Text(
                                text = " قطن مصري 100%",
                                style = MaterialTheme.typography.labelSmall,
                                color = ChampagneGoldDark,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        Text(
                            text = "سنتر العسال لتجارة المفروشات",
                            style = MaterialTheme.typography.headlineLarge,
                            fontWeight = FontWeight.Bold,
                            color = SkyBlue
                        )

                        Text(
                            text = "أفخم مفروشات العرائس والمنزل العصري بالإسكندرية بأعلى جودة وخامات ملكية.",
                            style = MaterialTheme.typography.bodyMedium,
                            color = TextSecondary,
                            modifier = Modifier.padding(top = 4.dp, bottom = 12.dp)
                        )

                        Row(
                            horizontalArrangement = Arrangement.spacedBy(10.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(SkyBlue)
                                    .clickable { onViewAllProducts() }
                                    .padding(horizontal = 16.dp, vertical = 8.dp)
                            ) {
                                Text(
                                    text = "تسوق الآن",
                                    color = Color.White,
                                    style = MaterialTheme.typography.labelLarge,
                                    fontWeight = FontWeight.Bold
                                )
                            }

                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(Color.White.copy(alpha = 0.85f))
                                    .border(1.dp, ChampagneGold, RoundedCornerShape(12.dp))
                                    .clickable { onOpenAiChat() }
                                    .padding(horizontal = 14.dp, vertical = 8.dp)
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        imageVector = Icons.Default.AutoAwesome,
                                        contentDescription = null,
                                        tint = ChampagneGold,
                                        modifier = Modifier.size(16.dp)
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = "اسأل ASSAL AI",
                                        color = ChampagneGoldDark,
                                        style = MaterialTheme.typography.labelMedium,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        // 3. Categories Rail
        item {
            Column(modifier = Modifier.padding(top = 16.dp)) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "أقسام المفروشات",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )
                    Text(
                        text = "عرض الكل",
                        style = MaterialTheme.typography.labelMedium,
                        color = SkyBlue,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.clickable { onViewAllProducts() }
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                LazyRow(
                    contentPadding = PaddingValues(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    items(categories) { category ->
                        CategoryHomeChip(
                            category = category,
                            onClick = { onCategoryClick(category.name) }
                        )
                    }
                }
            }
        }

        // 4. Trust Badges (Shipping, Quality, Branches)
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 16.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                FeaturePill(icon = Icons.Default.LocalShipping, text = "توصيل سريع", subtext = "لجميع المحافظات")
                FeaturePill(icon = Icons.Default.CheckCircle, text = "قطن مصري", subtext = "أعلى درجات النقاء")
                FeaturePill(icon = Icons.Default.PinDrop, text = "فرعان بالإسكندرية", subtext = "معاينة واستلام")
            }
        }

        // 5. Filter Tabs (الأكثر مبيعاً، أحدث المنتجات، العروض)
        item {
            ScrollableTabRow(
                selectedTabIndex = selectedFilterIndex,
                containerColor = IvoryWhite,
                edgePadding = 16.dp,
                indicator = { tabPositions ->
                    TabRowDefaults.Indicator(
                        modifier = Modifier.tabIndicatorOffset(tabPositions[selectedFilterIndex]),
                        color = SkyBlue,
                        height = 3.dp
                    )
                },
                divider = {}
            ) {
                filterTabs.forEachIndexed { index, title ->
                    val isSelected = selectedFilterIndex == index
                    Tab(
                        selected = isSelected,
                        onClick = { selectedFilterIndex = index },
                        text = {
                            Text(
                                text = title,
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                color = if (isSelected) SkyBlue else TextSecondary
                            )
                        }
                    )
                }
            }
        }

        // 6. Products Grid / List
        if (filteredProducts.isEmpty()) {
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 40.dp, horizontal = 20.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            imageVector = Icons.Default.LocalOffer,
                            contentDescription = null,
                            tint = SkyBlue.copy(alpha = 0.4f),
                            modifier = Modifier.size(56.dp)
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = "المتجر جاهز لاستقبال منتجاتك الفاخرة",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary
                        )
                        Text(
                            text = "يمكنك إضافة المنتجات والأقسام مباشرة من لوحة الإدارة.",
                            style = MaterialTheme.typography.bodyMedium,
                            color = TextSecondary,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.padding(top = 4.dp)
                        )
                    }
                }
            }
        } else {
            items(filteredProducts.chunked(2)) { pair ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 6.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    ProductCard(
                        product = pair[0],
                        isFavorite = wishlistIds.contains(pair[0].id),
                        onProductClick = { onProductClick(pair[0]) },
                        onAddToCartClick = { onAddToCart(pair[0]) },
                        onFavoriteToggle = { onToggleWishlist(pair[0]) },
                        modifier = Modifier.weight(1f)
                    )
                    if (pair.size > 1) {
                        ProductCard(
                            product = pair[1],
                            isFavorite = wishlistIds.contains(pair[1].id),
                            onProductClick = { onProductClick(pair[1]) },
                            onAddToCartClick = { onAddToCart(pair[1]) },
                            onFavoriteToggle = { onToggleWishlist(pair[1]) },
                            modifier = Modifier.weight(1f)
                        )
                    } else {
                        Spacer(modifier = Modifier.weight(1f))
                    }
                }
            }
        }

        // 7. Branch & Contact Card
        item {
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 16.dp)
                    .border(1.dp, FrostBlue, RoundedCornerShape(16.dp))
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.PinDrop,
                            contentDescription = null,
                            tint = SkyBlue,
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "فروع سنتر العسال بالإسكندرية",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary
                        )
                    }
                    Spacer(modifier = Modifier.height(10.dp))
                    Text(
                        text = "• الفرع الأول (المعرض الرئيسي): 8 شارع أنور السادات – الإسكندرية",
                        style = MaterialTheme.typography.bodyMedium,
                        color = TextSecondary
                    )
                    Text(
                        text = "• الفرع الثاني: 95 عمود السواري – كرموز – الإسكندرية",
                        style = MaterialTheme.typography.bodyMedium,
                        color = TextSecondary,
                        modifier = Modifier.padding(top = 4.dp)
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Phone,
                            contentDescription = null,
                            tint = ChampagneGold,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "01277714100  |  01279207793",
                            style = MaterialTheme.typography.labelLarge,
                            fontWeight = FontWeight.Bold,
                            color = ChampagneGoldDark
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun CategoryHomeChip(category: Category, onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(14.dp))
            .background(Color.White)
            .border(1.dp, FrostBlue, RoundedCornerShape(14.dp))
            .clickable { onClick() }
            .padding(horizontal = 14.dp, vertical = 10.dp)
            .testTag("cat_chip_${category.name}")
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .size(8.dp)
                    .background(SkyBlue, CircleShape)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = category.name,
                style = MaterialTheme.typography.labelLarge,
                fontWeight = FontWeight.SemiBold,
                color = TextPrimary
            )
        }
    }
}

@Composable
fun FeaturePill(icon: ImageVector, text: String, subtext: String) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .background(Color.White, RoundedCornerShape(12.dp))
            .border(1.dp, FrostBlue, RoundedCornerShape(12.dp))
            .padding(horizontal = 8.dp, vertical = 10.dp)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = SkyBlue,
            modifier = Modifier.size(20.dp)
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = text,
            style = MaterialTheme.typography.labelSmall,
            fontWeight = FontWeight.Bold,
            color = TextPrimary
        )
        Text(
            text = subtext,
            style = MaterialTheme.typography.labelSmall,
            color = TextTertiary,
            fontSize = 9.sp
        )
    }
}
