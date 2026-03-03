package com.example.bd_farmers.ui.home

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import android.location.Geocoder
import android.location.Location
import android.os.Build
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import com.example.bd_farmers.ui.components.CategoryItem
import com.example.bd_farmers.ui.components.ProductCard
import com.example.bd_farmers.ui.theme.ThemeManager
import com.example.bd_farmers.viewmodel.ProductViewModel
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import kotlinx.coroutines.delay
import java.util.*

@Composable
private fun getThemeColors(): Map<String, Color> {
    val isDark = ThemeManager.isDarkTheme
    return mapOf(
        "ForestDeep"   to if (isDark) Color.White else Color(0xFF0D2B1A),
        "ForestMid"    to if (isDark) Color(0xFFB9F6CA) else Color(0xFF1B5E38),
        "VibrantGreen" to Color(0xFF2ECC71),
        "MossGreen"    to if (isDark) Color(0xFFB9F6CA) else Color(0xFF4A8C5C),
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

    drawRect(Brush.verticalGradient(listOf(ivoryWarm, ivoryWarm, if (ThemeManager.isDarkTheme) Color.Black else forestDeep.copy(alpha = 0.05f))))
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
                SearchSection(searchQuery, viewModel::onSearchQueryChange, colors)
                Spacer(Modifier.height(20.dp))
                RotatingBanner(colors)
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

@SuppressLint("MissingPermission")
@Composable
private fun HomeTopBar(colors: Map<String, Color>) {
    val forestDeep = colors["ForestDeep"]!!
    val mossGreen = colors["MossGreen"]!!
    val goldAccent = colors["GoldAccent"]!!
    val surface = colors["Surface"]!!
    val context = LocalContext.current

    var locationText by remember { mutableStateOf("") }
    var temperature by remember { mutableStateOf("26") }
    
    val hour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY)
    
    val greeting = when {
        hour in 5..11 -> "Good Morning 🌿"
        hour in 12..16 -> "Good Afternoon ☀️"
        hour in 17..20 -> "Good Evening 🌆"
        else -> "Good Night 🌙"
    }

    val fusedLocationClient = remember { LocationServices.getFusedLocationProviderClient(context) }

    val fetchLocation = {
        fusedLocationClient.getCurrentLocation(Priority.PRIORITY_HIGH_ACCURACY, null)
            .addOnSuccessListener { loc: Location? ->
                if (loc != null) {
                    val geocoder = Geocoder(context, Locale.getDefault())
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                        geocoder.getFromLocation(loc.latitude, loc.longitude, 1) { addresses ->
                            if (addresses.isNotEmpty()) {
                                locationText = addresses[0].locality ?: addresses[0].subAdminArea ?: ""
                                temperature = (22 + (locationText.length % 8)).toString()
                            }
                        }
                    } else {
                        @Suppress("DEPRECATION")
                        val addresses = geocoder.getFromLocation(loc.latitude, loc.longitude, 1)
                        if (!addresses.isNullOrEmpty()) {
                            locationText = addresses[0].locality ?: addresses[0].subAdminArea ?: ""
                            temperature = (22 + (locationText.length % 8)).toString()
                        }
                    }
                } else {
                    Toast.makeText(context, "Please turn on GPS / Location services", Toast.LENGTH_SHORT).show()
                }
            }
    }

    val locationLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        if (permissions[Manifest.permission.ACCESS_FINE_LOCATION] == true ||
            permissions[Manifest.permission.ACCESS_COARSE_LOCATION] == true) {
            fetchLocation()
        } else {
            Toast.makeText(context, "Location permission denied", Toast.LENGTH_SHORT).show()
        }
    }

    Row(
        Modifier
            .fillMaxWidth()
            .padding(horizontal = 22.dp)
            .padding(top = 0.dp, bottom = 12.dp),
        Arrangement.SpaceBetween,
        Alignment.CenterVertically
    ) {
        Column {
            Text("Farmer", fontSize = 28.sp, fontWeight = FontWeight.ExtraBold, color = forestDeep)
            Text(
                text = if (locationText.isEmpty()) greeting else "$greeting, $locationText", 
                fontSize = 12.sp, color = mossGreen, fontStyle = FontStyle.Italic, letterSpacing = 0.4.sp
            )
        }
        Row(horizontalArrangement = Arrangement.spacedBy(10.dp), verticalAlignment = Alignment.CenterVertically) {
            Surface(
                shape = RoundedCornerShape(14.dp), 
                color = surface, 
                shadowElevation = 4.dp,
                onClick = {
                    val hasPermission = ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
                    if (hasPermission) {
                        fetchLocation()
                    } else {
                        locationLauncher.launch(arrayOf(
                            Manifest.permission.ACCESS_FINE_LOCATION,
                            Manifest.permission.ACCESS_COARSE_LOCATION
                        ))
                    }
                }
            ) {
                Row(Modifier.padding(horizontal = 12.dp, vertical = 8.dp), verticalAlignment = Alignment.CenterVertically) {
                    val isDay = hour in 6..18
                    val timeIcon = if (isDay) Icons.Default.WbSunny else Icons.Default.NightlightRound
                    Icon(imageVector = timeIcon, contentDescription = null, tint = goldAccent, modifier = Modifier.size(16.dp))
                    Spacer(Modifier.width(5.dp))
                    Text(text = "$temperature°", fontWeight = FontWeight.ExtraBold, fontSize = 14.sp, color = goldAccent)
                }
            }
            
            Surface(
                shape = RoundedCornerShape(14.dp),
                color = surface,
                shadowElevation = 4.dp
            ) {
                Box(Modifier.size(42.dp), contentAlignment = Alignment.Center) {
                    Icon(
                        imageVector = Icons.Outlined.Notifications, 
                        contentDescription = null, 
                        tint = if (ThemeManager.isDarkTheme) Color.White else Color(0xFF424242), 
                        modifier = Modifier.size(22.dp)
                    )
                    Box(
                        Modifier
                            .size(8.dp)
                            .background(Color(0xFFFF5252), CircleShape)
                            .align(Alignment.TopEnd)
                            .offset((-6).dp, 6.dp)
                    )
                }
            }
        }
    }
}

@Composable
private fun RotatingBanner(colors: Map<String, Color>) {
    val banners = listOf(
        BannerData("🌾 Direct from Farm", "Sell Direct, Earn More", "Join our farmer community today.", Color(0xFFFFF9C4), "🧑‍🌾"),
        BannerData("🥦 Fresh Vegetables", "Quality Guaranteed", "Daily harvest from local farms.", Color(0xFFE8F5E9), "🥦"),
        BannerData("🍎 Healthy Fruits", "Organic & Pure", "Sweetest picks of the season.", Color(0xFFFBE9E7), "🍎")
    )
    
    var currentIndex by remember { mutableIntStateOf(0) }
    
    LaunchedEffect(Unit) {
        while (true) {
            delay(4000)
            currentIndex = (currentIndex + 1) % banners.size
        }
    }
    
    val currentBanner = banners[currentIndex]
    
    Box(
        Modifier
            .fillMaxWidth()
            .padding(horizontal = 22.dp)
            .height(160.dp)
            .clip(RoundedCornerShape(28.dp))
            .background(currentBanner.bgColor)
    ) {
        Row(Modifier.fillMaxSize().padding(horizontal = 26.dp, vertical = 20.dp), verticalAlignment = Alignment.CenterVertically) {
            Column(Modifier.weight(1f)) {
                Surface(color = Color(0xFF2E7D32).copy(0.15f), shape = RoundedCornerShape(8.dp)) {
                    Text(currentBanner.tag, fontSize = 10.sp, color = Color(0xFF2E7D32), fontWeight = FontWeight.Bold, modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp))
                }
                Spacer(Modifier.height(10.dp))
                Text(currentBanner.title, fontSize = 22.sp, fontWeight = FontWeight.ExtraBold, color = Color(0xFF0D2B1A), lineHeight = 28.sp)
                Spacer(Modifier.height(4.dp))
                Text(currentBanner.desc, fontSize = 12.sp, color = Color.Gray)
            }
            Text(currentBanner.emoji, fontSize = 70.sp)
        }
    }
}

private data class BannerData(val tag: String, val title: String, val desc: String, val bgColor: Color, val emoji: String)

@Composable
private fun SearchSection(query: String, onQueryChange: (String) -> Unit, colors: Map<String, Color>) {
    val mossGreen = colors["MossGreen"]!!
    val sageLight = colors["SageLight"]!!
    val surface = colors["Surface"]!!
    val vibrantGreen = colors["VibrantGreen"]!!
    val forestMid = colors["ForestMid"]!!
    val onSurface = colors["OnSurface"]!!

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
                    focusedTextColor        = onSurface,
                    unfocusedTextColor      = onSurface,
                    cursorColor             = vibrantGreen
                ),
                textStyle = LocalTextStyle.current.copy(fontSize = 13.sp)
            )
        }
        Box(
            modifier = Modifier
                .size(52.dp)
                .clip(RoundedCornerShape(18.dp))
                .background(Brush.linearGradient(listOf(forestMid, mossGreen)))
                .clickable { },
            contentAlignment = Alignment.Center
        ) {
            Icon(imageVector = Icons.Default.Tune, contentDescription = null, tint = Color.White, modifier = Modifier.size(20.dp))
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
                        Text("Flash Deals", fontWeight = FontWeight.ExtraBold, fontSize = 15.sp, color = forestDeep)
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
    val forestDeep = colors["ForestDeep"]!!
    val forestMid = colors["ForestMid"]!!
    val vibrantGreen = colors["VibrantGreen"]!!

    Row(Modifier.fillMaxWidth().padding(horizontal = 22.dp), Arrangement.SpaceBetween, Alignment.CenterVertically) {
        Text(title, fontWeight = FontWeight.ExtraBold, fontSize = 20.sp, color = forestDeep)
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
