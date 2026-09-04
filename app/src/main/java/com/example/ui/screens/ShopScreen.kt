package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.ShoppingBag
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.model.Product
import com.example.ui.theme.CharcoalDark
import com.example.ui.theme.GoldStar
import com.example.ui.theme.GrayBorder
import com.example.ui.theme.GrayText
import com.example.ui.theme.GreenBg
import com.example.ui.theme.GreenSuccess
import com.example.ui.theme.PinkDark
import com.example.ui.theme.PinkLight
import com.example.ui.theme.PinkPrimary
import com.example.viewmodel.ReMartViewModel

data class CategoryItem(
    val id: String,
    val label: String,
    val iconEmoji: String
)

@Composable
fun ShopScreen(
    viewModel: ReMartViewModel,
    onOpenReseller: () -> Unit,
    modifier: Modifier = Modifier
) {
    val products by viewModel.filteredProducts.collectAsState()
    val searchQuery by viewModel.searchQuery.collectAsState()
    val selectedCat by viewModel.selectedCategory.collectAsState()

    val categories = listOf(
        CategoryItem("all", "All", "🏷️"),
        CategoryItem("saree", "Saree", "👗"),
        CategoryItem("kurta", "Kurta", "👕"),
        CategoryItem("shoes", "Shoes", "👟"),
        CategoryItem("watch", "Watch", "⌚"),
        CategoryItem("earbuds", "Earbuds", "🎧"),
        CategoryItem("reseller", "Reseller", "🔥")
    )

    LazyVerticalGrid(
        columns = GridCells.Fixed(2),
        modifier = modifier
            .fillMaxSize()
            .testTag("shop_product_grid"),
        contentPadding = PaddingValues(12.dp),
        horizontalArrangement = Arrangement.spacedBy(10.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        // Search & Category & Hero Header
        item(span = { GridItemSpan(2) }) {
            Column(modifier = Modifier.fillMaxWidth()) {
                // Search Box
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { viewModel.setSearchQuery(it) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("search_product_input"),
                    placeholder = {
                        Text(
                            "Saree, Kurta, Shoes search karo...",
                            fontSize = 14.sp,
                            color = GrayText
                        )
                    },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.Search,
                            contentDescription = "Search",
                            tint = PinkPrimary
                        )
                    },
                    trailingIcon = {
                        if (searchQuery.isNotEmpty()) {
                            IconButton(onClick = { viewModel.setSearchQuery("") }) {
                                Icon(Icons.Default.Clear, contentDescription = "Clear", tint = GrayText)
                            }
                        }
                    },
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = PinkPrimary,
                        unfocusedBorderColor = GrayBorder,
                        focusedContainerColor = Color.White,
                        unfocusedContainerColor = Color.White
                    ),
                    singleLine = true
                )

                Spacer(modifier = Modifier.height(10.dp))

                // Hero Promo Card
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("hero_promo_card"),
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = PinkLight)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(14.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "Lowest Prices\nBest Quality Shopping",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Black,
                                lineHeight = 20.sp,
                                color = CharcoalDark
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "50 Lakh+ Products • Free Delivery • COD",
                                fontSize = 11.sp,
                                color = PinkDark,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                        Box(
                            modifier = Modifier
                                .size(48.dp)
                                .background(Color.White, CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Text("🛍️", fontSize = 26.sp)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Category Filter Row
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    categories.forEach { cat ->
                        val isSelected = selectedCat == cat.id
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier
                                .clickable { viewModel.setCategory(cat.id) }
                                .padding(4.dp)
                                .testTag("cat_chip_${cat.id}")
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(54.dp)
                                    .clip(CircleShape)
                                    .background(
                                        if (isSelected) PinkPrimary else Color.White
                                    )
                                    .border(
                                        width = if (isSelected) 2.dp else 1.dp,
                                        color = if (isSelected) PinkPrimary else GrayBorder,
                                        shape = CircleShape
                                    ),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = cat.iconEmoji,
                                    fontSize = 22.sp
                                )
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = cat.label,
                                fontSize = 11.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                color = if (isSelected) PinkPrimary else CharcoalDark
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Section Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = if (selectedCat == "all") "Products For You" else "${selectedCat.replaceFirstChar { it.uppercase() }} Products",
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp,
                        color = CharcoalDark
                    )
                    Text(
                        text = "COD Available",
                        fontSize = 11.sp,
                        color = GreenSuccess,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }
        }

        // Product Cards
        if (products.isEmpty()) {
            item(span = { GridItemSpan(2) }) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(40.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("🔍", fontSize = 40.sp)
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Koi product nahi mila",
                            fontWeight = FontWeight.Bold,
                            color = CharcoalDark
                        )
                        Text(
                            text = "Dusra keyword search karein ya category badlein",
                            fontSize = 12.sp,
                            color = GrayText
                        )
                    }
                }
            }
        } else {
            items(products, key = { it.id }) { product ->
                ProductCard(
                    product = product,
                    onAddToCart = { viewModel.addToCart(product) }
                )
            }
        }
    }
}

@Composable
fun ProductCard(
    product: Product,
    onAddToCart: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .testTag("product_card_${product.id}"),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(1f)
                    .background(Color(0xFFF3F4F6))
            ) {
                AsyncImage(
                    model = product.imageUrl,
                    contentDescription = product.name,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )

                // Reseller Tag
                if (product.isResellerProduct) {
                    Surface(
                        modifier = Modifier
                            .padding(6.dp)
                            .align(Alignment.TopStart),
                        color = CharcoalDark,
                        shape = RoundedCornerShape(4.dp)
                    ) {
                        Text(
                            text = "🔥 Reseller",
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                    }
                }

                // Rating Badge
                Surface(
                    modifier = Modifier
                        .padding(6.dp)
                        .align(Alignment.BottomStart),
                    color = Color.White.copy(alpha = 0.92f),
                    shape = RoundedCornerShape(4.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(horizontal = 5.dp, vertical = 2.dp)
                    ) {
                        Text(
                            text = String.format("%.1f", product.rating),
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = CharcoalDark
                        )
                        Spacer(modifier = Modifier.width(2.dp))
                        Icon(
                            imageVector = Icons.Default.Star,
                            contentDescription = "Rating",
                            tint = GoldStar,
                            modifier = Modifier.size(10.dp)
                        )
                    }
                }
            }

            Column(modifier = Modifier.padding(10.dp)) {
                Text(
                    text = product.name,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 13.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    color = CharcoalDark
                )

                Spacer(modifier = Modifier.height(4.dp))

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Text(
                        text = "₹${product.sellingPrice}",
                        fontWeight = FontWeight.Black,
                        fontSize = 15.sp,
                        color = CharcoalDark
                    )
                    Text(
                        text = "₹${product.mrpPrice}",
                        fontSize = 11.sp,
                        color = GrayText,
                        textDecoration = TextDecoration.LineThrough
                    )
                    Surface(
                        color = GreenBg,
                        shape = RoundedCornerShape(3.dp)
                    ) {
                        Text(
                            text = "${product.discountPercent}% off",
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Bold,
                            color = GreenSuccess,
                            modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = "Free Delivery • COD",
                    fontSize = 10.sp,
                    color = GrayText
                )

                Spacer(modifier = Modifier.height(8.dp))

                Button(
                    onClick = onAddToCart,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(36.dp)
                        .testTag("add_to_cart_btn_${product.id}"),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = PinkLight,
                        contentColor = PinkPrimary
                    ),
                    shape = RoundedCornerShape(8.dp),
                    contentPadding = PaddingValues(0.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.ShoppingCart,
                        contentDescription = null,
                        modifier = Modifier.size(14.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "Add to Cart",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}
