package com.example.bd_farmers.ui.home

import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
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
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.bd_farmers.ui.components.CategoryItem
import com.example.bd_farmers.ui.components.ProductCard
import com.example.bd_farmers.ui.theme.ThemeManager
import com.example.bd_farmers.viewmodel.ProductViewModel

@Composable
private fun getThemeColors(): Map<String, Color> {
    val isDark = ThemeManager.isDarkTheme
    return mapOf(
        // High visibility greens for Dark Mode - brighter and more minty
        "ForestDeep"   to if (isDark) Color(0xFFE8F5E9) else Color(0xFF0D2B1A),
        "ForestMid"    to if (isDark) Color(0xFFB9F6CA) else Color(0xFF1B5E38),
        "VibrantGreen" to Color(0xFF2ECC71),
        "MossGreen"    to if (isDark) Color(0xFF81C784) else Color(0xFF4A8C5C),
        "SageLight"    to if (isDark) Color(0xFFA5D6A7) else Color(0xFF8FB99A),
        "IvoryWarm"    to if (isDark) Color(0xFF121212) else Color(0xFFF6F3EE),
        "GoldAccent"   to Color(0xFFD4A853),
        "GoldLight"    to Color(0xFFF0C96E),
        "Surface"      to if (isDark) Color(0xFF1E1E1E) else Color.White,
        "OnSurface"    to if (isDark) Color.White else Color(0xFF0D2B1A)
    )
}

private fun DrawScope.drawBg(pulse: Float, colors: Map<String, Color>) {
    val forestDeep = colors["ForestDeep"]!!
    val vibrantGreen = colors["VibrantGreen"]!!
    val mossGreen = colors["MossGreen"]!!
    val ivoryWarm = colors["IvoryWarm"]!!

    drawRect(Brush.verticalGradient(listOf(ivoryWarm, ivoryWarm, forestDeep.copy(alpha = 0.05f))))
    drawCircle(
        Brush.radialGradient(
            listOf(vibrantGreen.copy(0.13f * pulse), Color.Transparent),
            center = Offset(size.width * 1.05f, size.height * 0.02f),
            radius = size.width * 0.85f
        ),
        radius = size.width * 0.85f,
        center = Offset(size.width * 1.05f, size.height * 0.02f)
    )
    drawCircle(
        Brush.radialGradient(
            listOf(mossGreen.copy(0.10f), Color.Transparent),
            center = Offset(-size.width * 0.05f, size.height * 0.88f),
            radius = size.width * 0.65f
        ),
        radius = size.width * 0.65f,
        center = Offset(-size.width * 0.05f, size.height * 0.88f)
    )
    val dotColor = if (ThemeManager.isDarkTheme) Color.White.copy(0.05f) else Color(0xFF0D2B1A).copy(0.032f)
    val step = 32f
    var x = step; while (x < size.width) {
        var y = step; while (y < size.height) {
            drawCircle(dotColor, 1.4f, Offset(x, y)); y += step
        }; x += step
    }
}

@Composable
fun HomeScreen(
    viewModel: ProductViewModel,
    onNavigateToExplore: () -> Unit
) {
    val categories       by viewModel.categories.collectAsState()
    val searchQuery      by viewModel.searchQuery.collectAsState()
    val selectedCategory by viewModel.selectedCategory.collectAsState()
    val scrollState      = rememberScrollState()
    val colors           = getThemeColors()

    val inf = rememberInfiniteTransition(label = "bg")
    val pulse by inf.animateFloat(
        1f, 1.2f,
        infiniteRepeatable(tween(5000, easing = EaseInOutSine), RepeatMode.Reverse),
        "pulse"
    )

    Box(Modifier.fillMaxSize().drawBehind { drawBg(pulse, colors) }) {
        Scaffold(containerColor = Color.Transparent) { pad ->
            Column(Modifier.fillMaxSize().padding(pad).verticalScroll(scrollState)) {
                HomeTopBar(colors)
                Spacer(Modifier.height(4.dp))
                SearchBar(searchQuery, viewModel::onSearchQueryChange, colors)
                Spacer(Modifier.height(20.dp))
                HeroBanner(colors)
                Spacer(Modifier.height(24.dp))
                SectionHeader("Categories", onNavigateToExplore, colors)
                Spacer(Modifier.height(10.dp))
                LazyRow(
                    contentPadding = PaddingValues(horizontal = 20.dp),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    items(categories) { cat ->
                        CategoryItem(
                            category = cat,
                            isSelected = selectedCategory == cat,
                            onClick = { viewModel.onCategorySelected(cat) }
                        )
                    }
                }
                Spacer(Modifier.height(24.dp))
                FlashBanner(colors)
                Spacer(Modifier.height(24.dp))
                SectionHeader("Fresh Picks", onNavigateToExplore, colors)
                Spacer(Modifier.height(12.dp))
                ProductGrid(viewModel)
                Spacer(Modifier.height(120.dp))
            }
        }
    }
}

@Composable
private fun HomeTopBar(colors: Map<String, Color>) {
    val forestDeep = colors["ForestDeep"]!!
    val mossGreen = colors["MossGreen"]!!
    val goldAccent = colors["GoldAccent"]!!
    val surface = colors["Surface"]!!

    Row(
        Modifier.fillMaxWidth().padding(horizontal = 22.dp).padding(top = 16.dp, bottom = 12.dp), // Moved UP
        Arrangement.SpaceBetween,
        Alignment.CenterVertically
    ) {
        Column {
            Text("Good Morning 🌿", fontSize = 12.sp, color = mossGreen, fontStyle = FontStyle.Italic, letterSpacing = 0.4.sp)
            Text("BD Farmers", fontSize = 28.sp, fontWeight = FontWeight.ExtraBold, color = forestDeep)
        }
        Row(horizontalArrangement = Arrangement.spacedBy(10.dp), verticalAlignment = Alignment.CenterVertically) {
            Surface(shape = RoundedCornerShape(14.dp), color = surface, shadowElevation = 6.dp) {
                Row(Modifier.padding(horizontal = 12.dp, vertical = 8.dp), verticalAlignment = Alignment.CenterVertically) {
                    Text("☀️", fontSize = 14.sp)
                    Spacer(Modifier.width(5.dp))
                    Text("50", fontWeight = FontWeight.ExtraBold, fontSize = 14.sp, color = goldAccent)
                }
            }
            Box(Modifier.size(42.dp).clip(RoundedCornerShape(14.dp)).background(if (ThemeManager.isDarkTheme) Color(0xFF2E7D32) else Color(0xFF0D2B1A)), Alignment.Center) {
                Icon(imageVector = Icons.Outlined.Notifications, contentDescription = null, tint = Color.White, modifier = Modifier.size(20.dp))
                Box(Modifier.size(8.dp).background(Color(0xFFFF5252), CircleShape).align(Alignment.TopEnd).offset((-4).dp, 4.dp))
            }
        }
    }
}

@Composable
private fun SearchBar(query: String, onQueryChange: (String) -> Unit, colors: Map<String, Color>) {
    val mossGreen = colors["MossGreen"]!!
    val sageLight = colors["SageLight"]!!
    val surface = colors["Surface"]!!
    val vibrantGreen = colors["VibrantGreen"]!!
    val goldLight = colors["GoldLight"]!!
    val forestMid = colors["ForestMid"]!!

    Row(Modifier.fillMaxWidth().padding(horizontal = 22.dp), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
        Surface(Modifier.weight(1f), shape = RoundedCornerShape(18.dp), color = surface, shadowElevation = 8.dp) {
            OutlinedTextField(
                value = query,
                onValueChange = onQueryChange,
                placeholder = { Text("Search fresh produce…", color = sageLight, fontSize = 13.sp) },
                leadingIcon = { Icon(imageVector = Icons.Default.Search, contentDescription = null, tint = mossGreen, modifier = Modifier.size(20.dp)) },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                shape = RoundedCornerShape(18.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    unfocusedBorderColor    = Color.Transparent,
                    focusedBorderColor      = Color.Transparent,
                    unfocusedContainerColor = Color.Transparent,
                    focusedContainerColor   = Color.Transparent,
                    focusedTextColor        = if (ThemeManager.isDarkTheme) Color.White else Color.Black,
                    unfocusedTextColor      = if (ThemeManager.isDarkTheme) Color.White else Color.Black,
                    cursorColor             = vibrantGreen
                ),
                textStyle = LocalTextStyle.current.copy(fontSize = 13.sp)
            )
        }
        Box(Modifier.size(52.dp).clip(RoundedCornerShape(18.dp)).background(Brush.linearGradient(listOf(forestMid, mossGreen))).clickable { }, Alignment.Center) {
            Icon(imageVector = Icons.Default.Tune, contentDescription = null, tint = goldLight, modifier = Modifier.size(20.dp))
        }
    }
}

@Composable
private fun HeroBanner(colors: Map<String, Color>) {
    val mossGreen = colors["MossGreen"]!!
    val vibrantGreen = colors["VibrantGreen"]!!
    val goldAccent = colors["GoldAccent"]!!
    val goldLight = colors["GoldLight"]!!

    Box(Modifier.fillMaxWidth().padding(horizontal = 22.dp).height(160.dp).clip(RoundedCornerShape(28.dp)).background(Brush.linearGradient(listOf(if (ThemeManager.isDarkTheme) Color(0xFF003300) else Color(0xFF0D2B1A), Color(0xFF1A5C35), mossGreen)))
        .drawBehind {
            drawCircle(Brush.radialGradient(listOf(vibrantGreen.copy(0.20f), Color.Transparent), center = Offset(size.width * 0.75f, 0f), radius = size.height * 1.5f), radius = size.height * 1.5f, center = Offset(size.width * 0.75f, 0f))
        }
    ) {
        Row(Modifier.fillMaxSize().padding(horizontal = 26.dp, vertical = 20.dp), verticalAlignment = Alignment.CenterVertically) {
            Column(Modifier.weight(1f)) {
                Surface(color = goldAccent.copy(0.22f), shape = RoundedCornerShape(8.dp)) {
                    Text("🌾 For Farmers", fontSize = 10.sp, color = goldLight, fontWeight = FontWeight.Bold, modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp))
                }
                Spacer(Modifier.height(10.dp))
                Text("Sell Direct,\nEarn More", fontSize = 22.sp, fontWeight = FontWeight.ExtraBold, color = Color.White, lineHeight = 28.sp)
                Spacer(Modifier.height(8.dp))
                Box(Modifier.clip(RoundedCornerShape(10.dp)).background(goldAccent).clickable { }.padding(horizontal = 16.dp, vertical = 7.dp)) {
                    Text("Join Now →", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color.White)
                }
            }
            Text("🧑‍🌾", fontSize = 70.sp)
        }
    }
}

@Composable
private fun FlashBanner(colors: Map<String, Color>) {
    val surface = colors["Surface"]!!
    val vibrantGreen = colors["VibrantGreen"]!!
    val goldAccent = colors["GoldAccent"]!!
    val forestDeep = colors["ForestDeep"]!!
    val goldLight = colors["GoldLight"]!!
    val sageLight = colors["SageLight"]!!

    Surface(Modifier.fillMaxWidth().padding(horizontal = 22.dp), shape = RoundedCornerShape(20.dp), color = surface, shadowElevation = 8.dp) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(Modifier.width(6.dp).height(72.dp).background(Brush.verticalGradient(listOf(vibrantGreen, goldAccent)), RoundedCornerShape(topStart = 20.dp, bottomStart = 20.dp)))
            Spacer(Modifier.width(16.dp))
            Row(Modifier.fillMaxWidth().padding(end = 16.dp, top = 14.dp, bottom = 14.dp), verticalAlignment = Alignment.CenterVertically) {
                Column(Modifier.weight(1f)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text("⚡", fontSize = 16.sp)
                        Spacer(Modifier.width(6.dp))
                        Text("Flash Deals", fontWeight = FontWeight.ExtraBold, fontSize = 15.sp, color = if (ThemeManager.isDarkTheme) Color.White else Color(0xFF0D2B1A))
                    }
                    Text("Up to 40% off · Ends tonight", fontSize = 11.sp, color = sageLight, modifier = Modifier.padding(top = 2.dp))
                }
                Box(Modifier.clip(RoundedCornerShape(10.dp)).background(if (ThemeManager.isDarkTheme) Color(0xFF2E7D32) else Color(0xFF0D2B1A)).padding(horizontal = 12.dp, vertical = 7.dp)) {
                    Text("03:42:11", fontSize = 13.sp, fontWeight = FontWeight.ExtraBold, color = goldLight, letterSpacing = 1.sp)
                }
            }
        }
    }
}

@Composable
fun SectionHeader(title: String, onSeeAll: () -> Unit, colors: Map<String, Color>) {
    val forestMid = colors["ForestMid"]!!
    val vibrantGreen = colors["VibrantGreen"]!!

    Row(Modifier.fillMaxWidth().padding(horizontal = 22.dp), Arrangement.SpaceBetween, Alignment.CenterVertically) {
        Text(title, fontWeight = FontWeight.ExtraBold, fontSize = 20.sp, color = if (ThemeManager.isDarkTheme) Color.White else Color(0xFF0D2B1A))
        Row(Modifier.clip(RoundedCornerShape(8.dp)).background(vibrantGreen.copy(0.10f)).clickable { onSeeAll() }.padding(horizontal = 10.dp, vertical = 5.dp), verticalAlignment = Alignment.CenterVertically) {
            Text("View all", color = forestMid, fontWeight = FontWeight.Bold, fontSize = 12.sp)
            Icon(imageVector = Icons.Default.ChevronRight, contentDescription = null, tint = forestMid, modifier = Modifier.size(15.dp))
        }
    }
}

@Composable
fun ProductGrid(viewModel: ProductViewModel) {
    val products = viewModel.filteredProducts()
    Column(Modifier.padding(horizontal = 16.dp)) {
        products.chunked(2).forEach { row ->
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
}