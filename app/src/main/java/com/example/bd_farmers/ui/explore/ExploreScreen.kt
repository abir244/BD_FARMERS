package com.example.bd_farmers.ui.explore

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.*
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.bd_farmers.data.model.Product
import com.example.bd_farmers.ui.components.ProductCard
import com.example.bd_farmers.ui.theme.ThemeManager
import com.example.bd_farmers.viewmodel.ProductViewModel

@Composable
fun ExploreScreen(viewModel: ProductViewModel) {
    val categories by viewModel.categories.collectAsState()
    val products = viewModel.filteredProducts()
    val scrollState = rememberScrollState()

    var selectedChip by remember { mutableIntStateOf(0) }
    var selectedSort by remember { mutableIntStateOf(0) }
    var searchText by remember { mutableStateOf("") }

    val isDark = ThemeManager.isDarkTheme
    val colors = MaterialTheme.colorScheme
    
    // Standardized high-visibility palette
    val forestDeep = if (isDark) Color(0xFFE8F5E9) else Color(0xFF0D2B1A)
    val forestMid = if (isDark) Color(0xFFB9F6CA) else Color(0xFF1B5E38)
    val vibrantGreen = Color(0xFF2ECC71)
    val sageLight = if (isDark) Color(0xFFA5D6A7) else Color(0xFF8FB99A)
    val ivoryWarm = if (isDark) Color(0xFF121212) else Color(0xFFF6F3EE)
    val goldAccent = Color(0xFFD4A853)
    val goldLight = Color(0xFFF0C96E)

    val filterChips = listOf(
        FilterChip("All", "🌿"),
        FilterChip("Vegetables", "🥦"),
        FilterChip("Fruits", "🍎"),
        FilterChip("Dairy", "🥛"),
        FilterChip("Grains", "🌾"),
        FilterChip("Spices", "🌶️"),
        FilterChip("Organic", "✨"),
        FilterChip("Honey", "🍯"),
    )
    val sortOptions = listOf("Popular", "Newest", "Price ↑", "Price ↓", "Rating")

    val inf = rememberInfiniteTransition(label = "bg")
    val pulse by inf.animateFloat(
        1f, 1.2f, infiniteRepeatable(tween(5000, easing = EaseInOutSine), RepeatMode.Reverse), "pulse"
    )

    Column(Modifier.fillMaxSize().background(colors.background)) {
        // HEADER
        Box(
            Modifier
                .fillMaxWidth()
                .drawBehind {
                    val headerBg = if (isDark) Color(0xFF00150A) else Color(0xFF0D2B1A)
                    drawRect(Brush.verticalGradient(listOf(headerBg, headerBg.copy(alpha = 0.9f))))
                    drawCircle(
                        Brush.radialGradient(
                            listOf(vibrantGreen.copy(0.22f * pulse), Color.Transparent),
                            center = Offset(size.width * 1.05f, 0f), radius = size.width * 0.65f
                        ), radius = size.width * 0.65f, center = Offset(size.width * 1.05f, 0f)
                    )
                }
        ) {
            Column(
                Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 22.dp)
                    .padding(top = 32.dp, bottom = 20.dp) // Adjusted top padding
            ) {
                Row(Modifier.fillMaxWidth(), Arrangement.SpaceBetween, Alignment.CenterVertically) {
                    Column {
                        Text("What are you", fontSize = 13.sp, color = sageLight, fontStyle = FontStyle.Italic)
                        Text("Looking for?", fontSize = 28.sp, fontWeight = FontWeight.ExtraBold, color = Color.White)
                    }
                    Box(
                        Modifier.size(46.dp).clip(RoundedCornerShape(14.dp))
                            .background(Color.White.copy(0.10f))
                            .border(1.dp, Color.White.copy(0.15f), RoundedCornerShape(14.dp))
                            .clickable { },
                        Alignment.Center
                    ) {
                        Icon(imageVector = Icons.Default.Tune, contentDescription = null, tint = goldLight, modifier = Modifier.size(20.dp))
                    }
                }

                Spacer(Modifier.height(18.dp))

                Row(
                    Modifier.fillMaxWidth().clip(RoundedCornerShape(16.dp))
                        .background(Color.White.copy(0.10f))
                        .border(1.dp, Color.White.copy(0.14f), RoundedCornerShape(16.dp))
                        .padding(horizontal = 14.dp, vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(imageVector = Icons.Default.Search, contentDescription = null, tint = sageLight, modifier = Modifier.size(19.dp))
                    Spacer(Modifier.width(10.dp))
                    OutlinedTextField(
                        value = searchText,
                        onValueChange = { searchText = it; viewModel.onSearchQueryChange(it) },
                        placeholder = { Text("Search...", color = sageLight.copy(0.6f), fontSize = 13.sp) },
                        modifier = Modifier.weight(1f),
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            unfocusedBorderColor = Color.Transparent,
                            focusedBorderColor = Color.Transparent,
                            unfocusedContainerColor = Color.Transparent,
                            focusedContainerColor = Color.Transparent,
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White,
                            cursorColor = vibrantGreen
                        ),
                        textStyle = LocalTextStyle.current.copy(fontSize = 13.sp)
                    )
                }

                Spacer(Modifier.height(16.dp))

                LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    itemsIndexed(filterChips) { idx, chip ->
                        ChipFilter(chip, selectedChip == idx, vibrantGreen, if (isDark) Color(0xFF003300) else Color(0xFF0D2B1A)) {
                            selectedChip = idx
                            if (idx == 0) viewModel.onCategorySelected(null)
                            else categories.find { it.name.equals(chip.label, ignoreCase = true) }?.let { viewModel.onCategorySelected(it) }
                        }
                    }
                }
            }
        }

        Column(Modifier.fillMaxSize().verticalScroll(scrollState)) {
            Spacer(Modifier.height(22.dp))
            
            Row(
                Modifier.fillMaxWidth().padding(horizontal = 22.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    "${products.size} results",
                    fontSize = 13.sp,
                    color = sageLight,
                    fontWeight = FontWeight.Medium,
                    modifier = Modifier.padding(end = 12.dp)
                )
                LazyRow(
                    modifier = Modifier.weight(1f),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    itemsIndexed(sortOptions) { idx, opt ->
                        SortChip(opt, selectedSort == idx, colors.onBackground, sageLight) { selectedSort = idx }
                    }
                }
            }

            Spacer(Modifier.height(18.dp))

            if (products.isNotEmpty()) {
                SpotlightCard(products.first(), viewModel, if (isDark) Color(0xFF003300) else Color(0xFF0D2B1A), vibrantGreen, goldAccent, goldLight, sageLight)
                Spacer(Modifier.height(22.dp))
            }

            Row(Modifier.fillMaxWidth().padding(horizontal = 22.dp), Arrangement.SpaceBetween, Alignment.CenterVertically) {
                Text("All Products", fontWeight = FontWeight.ExtraBold, fontSize = 18.sp, color = forestDeep)
                Text("Sorted by: ${sortOptions[selectedSort]}", fontSize = 11.sp, color = sageLight)
            }

            Spacer(Modifier.height(14.dp))

            val rest = if (products.size > 1) products.drop(1) else products
            Column(Modifier.padding(horizontal = 16.dp)) {
                rest.chunked(2).forEach { row ->
                    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                        row.forEach { product ->
                            ProductCard(
                                product = product,
                                onAddToCart = { viewModel.addToCart(product) },
                                onFavoriteToggle = { viewModel.toggleFavorite(product.id) },
                                modifier = Modifier.weight(1f).padding(vertical = 6.dp)
                            )
                        }
                        if (row.size == 1) Spacer(Modifier.weight(1f))
                    }
                }
            }
            Spacer(Modifier.height(110.dp))
        }
    }
}

@Composable
private fun ChipFilter(chip: FilterChip, selected: Boolean, vibrantGreen: Color, forestDeep: Color, onClick: () -> Unit) {
    val bg by animateColorAsState(if (selected) vibrantGreen else Color.White.copy(0.12f), label = "cb")
    val textColor = if (selected) forestDeep else Color.White.copy(0.75f)

    Box(Modifier.clip(CircleShape).background(bg).clickable { onClick() }.padding(horizontal = 14.dp, vertical = 8.dp)) {
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(5.dp)) {
            Text(chip.emoji, fontSize = 13.sp)
            Text(chip.label, fontSize = 12.sp, fontWeight = if (selected) FontWeight.ExtraBold else FontWeight.Medium, color = textColor)
        }
    }
}

@Composable
private fun SortChip(label: String, selected: Boolean, onBg: Color, sageLight: Color, onClick: () -> Unit) {
    val bg by animateColorAsState(if (selected) onBg else Color.Transparent, label = "sb")
    val txtColor = if (selected) MaterialTheme.colorScheme.surface else sageLight

    Box(Modifier.clip(RoundedCornerShape(8.dp)).background(bg).border(1.dp, if (selected) Color.Transparent else sageLight.copy(0.3f), RoundedCornerShape(8.dp))
        .clickable { onClick() }.padding(horizontal = 10.dp, vertical = 5.dp)) {
        Text(label, fontSize = 11.sp, fontWeight = FontWeight.SemiBold, color = txtColor)
    }
}

@Composable
private fun SpotlightCard(product: Product, viewModel: ProductViewModel, forestDeep: Color, vibrantGreen: Color, goldAccent: Color, goldLight: Color, sageLight: Color) {
    Surface(Modifier.fillMaxWidth().padding(horizontal = 22.dp).height(200.dp), shape = RoundedCornerShape(28.dp), color = Color.Transparent, shadowElevation = 14.dp) {
        Box(Modifier.fillMaxSize()) {
            AsyncImage(model = product.imageUrl, contentDescription = null, modifier = Modifier.fillMaxSize().clip(RoundedCornerShape(28.dp)), contentScale = ContentScale.Crop)
            Box(Modifier.fillMaxSize().background(Brush.horizontalGradient(listOf(forestDeep.copy(0.85f), forestDeep.copy(0.3f), Color.Transparent))))
            Column(Modifier.align(Alignment.CenterStart).padding(22.dp)) {
                Surface(color = goldAccent, shape = RoundedCornerShape(6.dp)) {
                    Text("⭐ Spotlight", fontSize = 9.sp, fontWeight = FontWeight.Bold, color = Color.White, modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp))
                }
                Spacer(Modifier.height(8.dp))
                Text(product.name, fontSize = 22.sp, fontWeight = FontWeight.ExtraBold, color = Color.White, maxLines = 1, overflow = TextOverflow.Ellipsis)
                Row(verticalAlignment = Alignment.Bottom) {
                    Text("₹", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = goldLight, modifier = Modifier.padding(bottom = 2.dp))
                    Text("${product.price}", fontSize = 24.sp, fontWeight = FontWeight.ExtraBold, color = Color.White)
                }
                Spacer(Modifier.height(12.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Button(onClick = { viewModel.addToCart(product) }, colors = ButtonDefaults.buttonColors(containerColor = vibrantGreen), shape = RoundedCornerShape(10.dp)) {
                        Text("Add to Cart", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color.White)
                    }
                    IconButton(onClick = { viewModel.toggleFavorite(product.id) }, modifier = Modifier.background(Color.White.copy(0.15f), RoundedCornerShape(10.dp))) {
                        Icon(imageVector = Icons.Outlined.FavoriteBorder, contentDescription = null, tint = Color.White, modifier = Modifier.size(18.dp))
                    }
                }
            }
        }
    }
}

private data class FilterChip(val label: String, val emoji: String)
