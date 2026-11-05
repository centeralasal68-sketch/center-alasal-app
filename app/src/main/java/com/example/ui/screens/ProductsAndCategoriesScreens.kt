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
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Category
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
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
import com.example.data.model.Category
import com.example.data.model.Product
import com.example.ui.components.ProductCard
import com.example.ui.theme.ChampagneGold
import com.example.ui.theme.FrostBlue
import com.example.ui.theme.IvoryWhite
import com.example.ui.theme.SkyBlue
import com.example.ui.theme.SkyBlueLight
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.ui.theme.TextTertiary

@Composable
fun ProductsScreen(
    products: List<Product>,
    categories: List<Category>,
    selectedCategoryInitial: String = "الكل",
    wishlistIds: Set<String>,
    onProductClick: (Product) -> Unit,
    onAddToCart: (Product) -> Unit,
    onToggleWishlist: (Product) -> Unit,
    modifier: Modifier = Modifier
) {
    var selectedCategory by remember(selectedCategoryInitial) { mutableStateOf(selectedCategoryInitial) }
    var searchQuery by remember { mutableStateOf("") }
    var selectedSort by remember { mutableStateOf("الأحدث") }

    val sortOptions = listOf("الأحدث", "السعر: الأقل للأعلى", "السعر: الأعلى للأقل", "الأكثر مبيعاً")

    val displayedProducts = remember(products, selectedCategory, searchQuery, selectedSort) {
        var list = products

        if (selectedCategory != "الكل") {
            list = list.filter { it.category.trim() == selectedCategory.trim() }
        }

        if (searchQuery.isNotBlank()) {
            val q = searchQuery.trim().lowercase()
            list = list.filter {
                it.name.lowercase().contains(q) ||
                it.description.lowercase().contains(q) ||
                it.category.lowercase().contains(q) ||
                it.materials.lowercase().contains(q) ||
                it.sku.lowercase().contains(q)
            }
        }

        when (selectedSort) {
            "السعر: الأقل للأعلى" -> list.sortedBy { it.finalPrice }
            "السعر: الأعلى للأقل" -> list.sortedByDescending { it.finalPrice }
            "الأكثر مبيعاً" -> list.sortedByDescending { it.reviewsCount }
            else -> list.sortedByDescending { it.createdAt?.toDate()?.time ?: 0L }
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(IvoryWhite)
    ) {
        // Search & Filter Header
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color.White)
                .padding(16.dp)
        ) {
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                placeholder = { Text("بحث في المنتجات بالاسم أو الخامة أو SKU...", style = MaterialTheme.typography.bodyMedium) },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = SkyBlue) },
                singleLine = true,
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = SkyBlue,
                    unfocusedBorderColor = FrostBlue
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("products_search_field")
            )

            Spacer(modifier = Modifier.height(10.dp))

            // Categories chips
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                item {
                    CategoryFilterChip(
                        name = "الكل",
                        isSelected = selectedCategory == "الكل",
                        onSelect = { selectedCategory = "الكل" }
                    )
                }
                items(categories) { cat ->
                    CategoryFilterChip(
                        name = cat.name,
                        isSelected = selectedCategory == cat.name,
                        onSelect = { selectedCategory = cat.name }
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Sort Options Row
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Icon(Icons.Default.FilterList, contentDescription = null, tint = SkyBlue, modifier = Modifier.size(16.dp))
                LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    items(sortOptions) { sort ->
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .background(if (selectedSort == sort) SkyBlueLight else Color.Transparent)
                                .clickable { selectedSort = sort }
                                .padding(horizontal = 8.dp, vertical = 4.dp)
                        ) {
                            Text(
                                text = sort,
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = if (selectedSort == sort) FontWeight.Bold else FontWeight.Normal,
                                color = if (selectedSort == sort) SkyBlue else TextSecondary
                            )
                        }
                    }
                }
            }
        }

        // Product Count Bar
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "المنتجات المعروضة (${displayedProducts.size})",
                style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.Bold,
                color = TextSecondary
            )
            if (selectedCategory != "الكل") {
                Text(
                    text = "قسم: $selectedCategory",
                    style = MaterialTheme.typography.labelSmall,
                    color = SkyBlue,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }

        // Products Grid
        if (displayedProducts.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(32.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(
                        imageVector = Icons.Default.Category,
                        contentDescription = null,
                        tint = SkyBlue.copy(alpha = 0.4f),
                        modifier = Modifier.size(56.dp)
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = "لا توجد منتجات مطابقة للبحث",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )
                    Text(
                        text = "جرب تغيير معايير البحث أو اختيار قسم آخر.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = TextSecondary,
                        textAlign = TextAlign.Center
                    )
                }
            }
        } else {
            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 4.dp, bottom = 100.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.fillMaxSize()
            ) {
                items(displayedProducts, key = { it.id }) { product ->
                    ProductCard(
                        product = product,
                        isFavorite = wishlistIds.contains(product.id),
                        onProductClick = { onProductClick(product) },
                        onAddToCartClick = { onAddToCart(product) },
                        onFavoriteToggle = { onToggleWishlist(product) }
                    )
                }
            }
        }
    }
}

@Composable
fun CategoryFilterChip(
    name: String,
    isSelected: Boolean,
    onSelect: () -> Unit
) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(10.dp))
            .background(if (isSelected) SkyBlue else Color.White)
            .border(1.dp, if (isSelected) SkyBlue else FrostBlue, RoundedCornerShape(10.dp))
            .clickable { onSelect() }
            .padding(horizontal = 12.dp, vertical = 6.dp)
    ) {
        Text(
            text = name,
            style = MaterialTheme.typography.labelMedium,
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
            color = if (isSelected) Color.White else TextPrimary
        )
    }
}

@Composable
fun CategoriesScreen(
    categories: List<Category>,
    products: List<Product>,
    onCategoryClick: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(IvoryWhite)
            .padding(horizontal = 16.dp, vertical = 12.dp)
    ) {
        Text(
            text = "أقسام سنتر العسال للمفروشات",
            style = MaterialTheme.typography.headlineLarge,
            fontWeight = FontWeight.Bold,
            color = TextPrimary
        )
        Text(
            text = "اختر القسم المناسب لتصفح أرقى الموديلات والخامات المنزلية",
            style = MaterialTheme.typography.bodyMedium,
            color = TextSecondary,
            modifier = Modifier.padding(top = 4.dp, bottom = 16.dp)
        )

        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            contentPadding = PaddingValues(bottom = 100.dp),
            modifier = Modifier.fillMaxSize()
        ) {
            items(categories) { category ->
                val itemCount = products.count { it.category.trim() == category.name.trim() }
                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(130.dp)
                        .border(1.dp, FrostBlue, RoundedCornerShape(16.dp))
                        .clickable { onCategoryClick(category.name) }
                        .testTag("category_grid_${category.name}")
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(14.dp),
                        verticalArrangement = Arrangement.SpaceBetween
                    ) {
                        Box(
                            modifier = Modifier
                                .size(38.dp)
                                .background(SkyBlueLight, CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Category,
                                contentDescription = null,
                                tint = SkyBlue,
                                modifier = Modifier.size(20.dp)
                            )
                        }

                        Column {
                            Text(
                                text = category.name,
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = TextPrimary
                            )
                            Text(
                                text = "$itemCount منتج متاح",
                                style = MaterialTheme.typography.labelSmall,
                                color = ChampagneGold,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }
                }
            }
        }
    }
}
