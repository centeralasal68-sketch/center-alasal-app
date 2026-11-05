package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
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
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.ShoppingBag
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.data.model.Product
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProductDetailsScreen(
    product: Product,
    isFavorite: Boolean,
    onBackClick: () -> Unit,
    onAddToCart: (Product, Int, String, String) -> Unit,
    onToggleWishlist: (Product) -> Unit,
    onAskAiAboutProduct: (Product) -> Unit,
    modifier: Modifier = Modifier
) {
    var selectedImageIndex by remember { mutableIntStateOf(0) }
    var selectedSize by remember { mutableStateOf(product.sizes.firstOrNull().orEmpty()) }
    var selectedColor by remember { mutableStateOf(product.colors.firstOrNull().orEmpty()) }
    var quantity by remember { mutableIntStateOf(1) }

    val imagesList = if (product.images.isNotEmpty()) product.images else listOf("")

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(product.category, style = MaterialTheme.typography.titleMedium, color = TextPrimary) },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "رجوع", tint = TextPrimary)
                    }
                },
                actions = {
                    IconButton(onClick = { onToggleWishlist(product) }) {
                        Icon(
                            imageVector = if (isFavorite) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                            contentDescription = "مفضلة",
                            tint = if (isFavorite) StatusRed else TextPrimary
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = IvoryWhite)
            )
        },
        bottomBar = {
            // Action Bar: Quantity & Add to Cart
            Card(
                colors = CardDefaults.cardColors(containerColor = Color.White),
                shape = RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    // Quantity selector
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .background(SkyBlueLight, RoundedCornerShape(12.dp))
                            .padding(4.dp)
                    ) {
                        IconButton(
                            onClick = { if (quantity > 1) quantity-- },
                            modifier = Modifier.size(32.dp)
                        ) {
                            Icon(Icons.Default.Remove, contentDescription = "تقليل", tint = SkyBlue)
                        }
                        Text(
                            text = "$quantity",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary,
                            modifier = Modifier.padding(horizontal = 12.dp)
                        )
                        IconButton(
                            onClick = { if (quantity < product.stock.coerceAtLeast(10)) quantity++ },
                            modifier = Modifier.size(32.dp)
                        ) {
                            Icon(Icons.Default.Add, contentDescription = "زيادة", tint = SkyBlue)
                        }
                    }

                    Spacer(modifier = Modifier.width(16.dp))

                    // Add to cart button
                    Button(
                        onClick = { onAddToCart(product, quantity, selectedSize, selectedColor) },
                        enabled = product.isAvailable && product.stock > 0,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = SkyBlue,
                            disabledContainerColor = TextTertiary
                        ),
                        shape = RoundedCornerShape(14.dp),
                        modifier = Modifier
                            .weight(1f)
                            .height(50.dp)
                            .testTag("details_add_to_cart_btn")
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.ShoppingCart, contentDescription = null, tint = Color.White)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = if (product.isAvailable && product.stock > 0) "إضافة للسلة (${(product.finalPrice * quantity).toInt()} ج.م)" else "غير متوفر حالياً",
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }
        },
        containerColor = IvoryWhite,
        modifier = modifier
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
        ) {
            // Main Image
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(280.dp)
                    .background(SkyBlueLight)
            ) {
                val currentImage = imagesList.getOrNull(selectedImageIndex).orEmpty()
                if (currentImage.isNotBlank()) {
                    AsyncImage(
                        model = currentImage,
                        contentDescription = product.name,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )
                } else {
                    Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxSize()) {
                        Icon(
                            imageVector = Icons.Default.ShoppingBag,
                            contentDescription = null,
                            tint = SkyBlue.copy(alpha = 0.3f),
                            modifier = Modifier.size(64.dp)
                        )
                    }
                }

                // Discount Badge
                if (product.hasDiscount) {
                    Box(
                        modifier = Modifier
                            .padding(16.dp)
                            .align(Alignment.TopEnd)
                            .background(StatusRed, RoundedCornerShape(8.dp))
                            .padding(horizontal = 10.dp, vertical = 4.dp)
                    ) {
                        Text(
                            text = "خصم ${product.discountPercent}%",
                            color = Color.White,
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            // Image Thumbnails row
            if (imagesList.size > 1) {
                LazyRow(
                    contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(imagesList.indices.toList()) { index ->
                        Box(
                            modifier = Modifier
                                .size(60.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .border(
                                    width = if (selectedImageIndex == index) 2.dp else 1.dp,
                                    color = if (selectedImageIndex == index) SkyBlue else FrostBlue,
                                    shape = RoundedCornerShape(8.dp)
                                )
                                .clickable { selectedImageIndex = index }
                        ) {
                            AsyncImage(
                                model = imagesList[index],
                                contentDescription = null,
                                contentScale = ContentScale.Crop,
                                modifier = Modifier.fillMaxSize()
                            )
                        }
                    }
                }
            }

            // Info Box
            Column(modifier = Modifier.padding(16.dp)) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = "SKU: ${product.sku.ifBlank { "CAA-${product.id.take(6)}" }}",
                        style = MaterialTheme.typography.labelSmall,
                        color = TextTertiary
                    )
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Star, contentDescription = null, tint = ChampagneGold, modifier = Modifier.size(16.dp))
                        Text(
                            text = " ${product.rating} (${product.reviewsCount} تقييم)",
                            style = MaterialTheme.typography.labelSmall,
                            color = TextSecondary,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                Spacer(modifier = Modifier.height(6.dp))

                Text(
                    text = product.name,
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary
                )

                Spacer(modifier = Modifier.height(10.dp))

                // Price Section
                Row(verticalAlignment = Alignment.Bottom) {
                    Text(
                        text = "${product.finalPrice.toInt()} ",
                        style = MaterialTheme.typography.displayMedium,
                        fontWeight = FontWeight.Bold,
                        color = ChampagneGold
                    )
                    Text(
                        text = "جنية مصري",
                        style = MaterialTheme.typography.titleMedium,
                        color = ChampagneGold,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(bottom = 4.dp)
                    )
                    if (product.hasDiscount && product.originalPrice > product.finalPrice) {
                        Spacer(modifier = Modifier.width(12.dp))
                        Text(
                            text = "${product.originalPrice.toInt()} ج.م",
                            style = MaterialTheme.typography.titleMedium,
                            textDecoration = TextDecoration.LineThrough,
                            color = TextTertiary,
                            modifier = Modifier.padding(bottom = 4.dp)
                        )
                    }
                }

                // Ask ASSAL AI Action Card
                Card(
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = ChampagneGoldLight),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 12.dp)
                        .border(1.dp, ChampagneGold.copy(alpha = 0.4f), RoundedCornerShape(14.dp))
                        .clickable { onAskAiAboutProduct(product) }
                        .testTag("ask_ai_about_product_btn")
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(12.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .background(ChampagneGold, CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Default.AutoAwesome, contentDescription = null, tint = Color.White, modifier = Modifier.size(20.dp))
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "اسأل ASSAL AI عن هذا المنتج",
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.Bold,
                                color = ChampagneGoldDark
                            )
                            Text(
                                text = "استفسر عن المقاس الأنسب لسريرك، تناسق الألوان، وطرق العناية بالغسيل.",
                                style = MaterialTheme.typography.bodySmall,
                                color = TextSecondary
                            )
                        }
                    }
                }

                // Available Sizes
                if (product.sizes.isNotEmpty()) {
                    Text(
                        text = "المقاسات المتاحة:",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary,
                        modifier = Modifier.padding(top = 8.dp, bottom = 6.dp)
                    )
                    LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        items(product.sizes) { size ->
                            val isSelected = selectedSize == size
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(10.dp))
                                    .background(if (isSelected) SkyBlue else Color.White)
                                    .border(1.dp, if (isSelected) SkyBlue else FrostBlue, RoundedCornerShape(10.dp))
                                    .clickable { selectedSize = size }
                                    .padding(horizontal = 14.dp, vertical = 8.dp)
                            ) {
                                Text(
                                    text = size,
                                    style = MaterialTheme.typography.labelMedium,
                                    color = if (isSelected) Color.White else TextPrimary,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                                )
                            }
                        }
                    }
                }

                // Available Colors
                if (product.colors.isNotEmpty()) {
                    Text(
                        text = "الألوان المتاحة:",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary,
                        modifier = Modifier.padding(top = 14.dp, bottom = 6.dp)
                    )
                    LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        items(product.colors) { color ->
                            val isSelected = selectedColor == color
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(10.dp))
                                    .background(if (isSelected) ChampagneGold else Color.White)
                                    .border(1.dp, if (isSelected) ChampagneGold else FrostBlue, RoundedCornerShape(10.dp))
                                    .clickable { selectedColor = color }
                                    .padding(horizontal = 14.dp, vertical = 8.dp)
                            ) {
                                Text(
                                    text = color,
                                    style = MaterialTheme.typography.labelMedium,
                                    color = if (isSelected) Color.White else TextPrimary,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                                )
                            }
                        }
                    }
                }

                // Materials & Specs
                if (product.materials.isNotBlank()) {
                    Text(
                        text = "مواصفات الخامات والجودة:",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary,
                        modifier = Modifier.padding(top = 16.dp, bottom = 4.dp)
                    )
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(Color.White, RoundedCornerShape(12.dp))
                            .border(1.dp, FrostBlue, RoundedCornerShape(12.dp))
                            .padding(12.dp)
                    ) {
                        Text(
                            text = product.materials,
                            style = MaterialTheme.typography.bodyMedium,
                            color = TextSecondary
                        )
                    }
                }

                // Description
                if (product.description.isNotBlank()) {
                    Text(
                        text = "تفاصيل المنتج:",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary,
                        modifier = Modifier.padding(top = 16.dp, bottom = 4.dp)
                    )
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(Color.White, RoundedCornerShape(12.dp))
                            .border(1.dp, FrostBlue, RoundedCornerShape(12.dp))
                            .padding(12.dp)
                    ) {
                        Text(
                            text = product.description,
                            style = MaterialTheme.typography.bodyMedium,
                            color = TextSecondary
                        )
                    }
                }

                // Stock status
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(top = 14.dp, bottom = 24.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(10.dp)
                            .background(if (product.stock > 0) StatusGreen else StatusRed, CircleShape)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = if (product.stock > 0) "متوفر في المخزن (${product.stock} قطع)" else "نفدت الكمية مؤقتاً",
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.Bold,
                        color = if (product.stock > 0) StatusGreen else StatusRed
                    )
                }
            }
        }
    }
}
