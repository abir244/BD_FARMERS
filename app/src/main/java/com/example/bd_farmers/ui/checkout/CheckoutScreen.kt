package com.example.bd_farmers.ui.checkout

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.location.Geocoder
import android.location.Location
import android.os.Build
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import coil.compose.AsyncImage
import com.example.bd_farmers.ui.theme.ThemeManager
import com.example.bd_farmers.viewmodel.ProductViewModel
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import java.util.*

@Composable
fun CheckoutScreen(
    viewModel: ProductViewModel,
    onBack: () -> Unit,
    onOrderPlaced: () -> Unit
) {
    val cartItems by viewModel.cartItems.collectAsState()
    val isDark = ThemeManager.isDarkTheme
    val colors = MaterialTheme.colorScheme
    val context = LocalContext.current

    var locationAddress by remember { mutableStateOf("70 Washington Square South, NY") }
    var isEditingAddress by remember { mutableStateOf(false) }
    var manualAddress by remember { mutableStateOf("") }

    val forestDeep = if (isDark) Color(0xFF00150A) else Color(0xFF0D2B1A)
    val forestMid = if (isDark) Color(0xFF1B5E38) else Color(0xFF1B5E38)
    val vibrantGreen = Color(0xFF2ECC71)
    val mossGreen = if (isDark) Color(0xFF6B8E7B) else Color(0xFF4A8C5C)
    val sageLight = if (isDark) Color(0xFF4A5D4F) else Color(0xFF8FB99A)
    val ivoryWarm = if (isDark) Color(0xFF121212) else Color(0xFFF6F3EE)
    val creamDeep = if (isDark) Color(0xFF1A1A1A) else Color(0xFFEDE8DF)
    val goldAccent = Color(0xFFD4A853)
    val goldLight = Color(0xFFF0C96E)
    val errorRed = Color(0xFFE53935)

    val fusedLocationClient = remember { LocationServices.getFusedLocationProviderClient(context) }

    val fetchLocation = {
        fusedLocationClient.getCurrentLocation(Priority.PRIORITY_HIGH_ACCURACY, null)
            .addOnSuccessListener { loc: Location? ->
                if (loc != null) {
                    val geocoder = Geocoder(context, Locale.getDefault())
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                        geocoder.getFromLocation(loc.latitude, loc.longitude, 1) { addresses ->
                            if (addresses.isNotEmpty()) {
                                val address = addresses[0]
                                locationAddress = "${address.thoroughfare ?: ""}, ${address.locality ?: ""}, ${address.adminArea ?: ""}"
                            }
                        }
                    } else {
                        @Suppress("DEPRECATION")
                        val addresses = geocoder.getFromLocation(loc.latitude, loc.longitude, 1)
                        if (!addresses.isNullOrEmpty()) {
                            val address = addresses[0]
                            locationAddress = "${address.thoroughfare ?: ""}, ${address.locality ?: ""}, ${address.adminArea ?: ""}"
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

    Box(
        Modifier.fillMaxSize().background(colors.background).drawBehind {
            drawRect(Brush.verticalGradient(listOf(ivoryWarm, ivoryWarm, creamDeep)))
            drawCircle(
                Brush.radialGradient(
                    listOf(vibrantGreen.copy(0.10f), Color.Transparent),
                    center = Offset(size.width * 1.05f, 0f), radius = size.width * 0.7f
                ), radius = size.width * 0.7f, center = Offset(size.width * 1.05f, 0f)
            )
        }
    ) {
        if (cartItems.isEmpty()) {
            EmptyCheckout(onBack, forestDeep, forestMid, sageLight)
            return@Box
        }

        Column(Modifier.fillMaxSize()) {
            Row(
                Modifier.fillMaxWidth().padding(horizontal = 22.dp).padding(top = 52.dp, bottom = 16.dp),
                Arrangement.SpaceBetween, Alignment.CenterVertically
            ) {
                Column {
                    Text("Review & Pay 🛒", fontSize = 12.sp, color = mossGreen, fontStyle = FontStyle.Italic, letterSpacing = 0.4.sp)
                    Text("Checkout", fontSize = 30.sp, fontWeight = FontWeight.ExtraBold, color = colors.onBackground)
                }
                Box(Modifier.size(44.dp).clip(RoundedCornerShape(14.dp)).background(colors.surface).clickable { onBack() }, Alignment.Center) {
                    Icon(imageVector = Icons.AutoMirrored.Filled.ArrowBack, contentDescription = null, tint = colors.onSurface, modifier = Modifier.size(20.dp))
                }
            }

            // DELIVERY ADDRESS CARD
            Surface(Modifier.fillMaxWidth().padding(horizontal = 22.dp), shape = RoundedCornerShape(20.dp), color = colors.surface, shadowElevation = 6.dp) {
                Column(Modifier.padding(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(Modifier.size(44.dp).clip(CircleShape).background(forestDeep), Alignment.Center) {
                            Icon(imageVector = Icons.Default.LocationOn, contentDescription = null, tint = Color.White, modifier = Modifier.size(20.dp))
                        }
                        Spacer(Modifier.width(14.dp))
                        Column(Modifier.weight(1f)) {
                            Text("Deliver to", fontSize = 11.sp, color = sageLight)
                            Text("Home Address", fontWeight = FontWeight.ExtraBold, fontSize = 14.sp, color = colors.onSurface)
                            if (isEditingAddress) {
                                OutlinedTextField(
                                    value = manualAddress,
                                    onValueChange = { manualAddress = it },
                                    placeholder = { Text("Enter address...", fontSize = 12.sp) },
                                    modifier = Modifier.fillMaxWidth().height(50.dp),
                                    textStyle = LocalTextStyle.current.copy(fontSize = 12.sp),
                                    singleLine = true
                                )
                            } else {
                                Text(locationAddress, fontSize = 12.sp, color = sageLight, modifier = Modifier.padding(top = 1.dp), maxLines = 1, overflow = TextOverflow.Ellipsis)
                            }
                        }
                        
                        // Toggle between manual entry and automatic fetch
                        IconButton(onClick = {
                            if (isEditingAddress) {
                                if (manualAddress.isNotEmpty()) locationAddress = manualAddress
                                isEditingAddress = false
                            } else {
                                isEditingAddress = true
                                manualAddress = locationAddress
                            }
                        }) {
                            Icon(
                                imageVector = if (isEditingAddress) Icons.Default.Check else Icons.Default.Edit,
                                contentDescription = null,
                                tint = forestMid,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }
                    
                    if (!isEditingAddress) {
                        Spacer(Modifier.height(8.dp))
                        Box(
                            Modifier.fillMaxWidth().clip(RoundedCornerShape(8.dp)).background(vibrantGreen.copy(0.10f))
                                .clickable {
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
                                .padding(vertical = 8.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text("Use Current Location", color = forestMid, fontWeight = FontWeight.Bold, fontSize = 11.sp)
                        }
                    }
                }
            }

            Spacer(Modifier.height(20.dp))

            Row(Modifier.fillMaxWidth().padding(horizontal = 22.dp), Arrangement.SpaceBetween, Alignment.CenterVertically) {
                Text("Order Items", fontWeight = FontWeight.ExtraBold, fontSize = 18.sp, color = colors.onBackground)
                Box(Modifier.clip(RoundedCornerShape(8.dp)).background(vibrantGreen.copy(0.10f)).padding(horizontal = 10.dp, vertical = 4.dp)) {
                    Text("${cartItems.size} items", color = forestMid, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                }
            }

            Spacer(Modifier.height(12.dp))

            LazyColumn(Modifier.weight(1f), contentPadding = PaddingValues(horizontal = 22.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                items(cartItems, key = { it.product.id }) { item ->
                    CheckoutItemCard(item.product.imageUrl, item.product.name, item.product.price.toDouble(), item.quantity, { viewModel.deleteFromCart(item.product.id) }, colors, forestDeep, forestMid, sageLight, goldAccent, ivoryWarm)
                }
                item { Spacer(Modifier.height(8.dp)) }
            }

            OrderSummaryPanel(viewModel, onOrderPlaced, colors, forestDeep, forestMid, mossGreen, sageLight, goldAccent, goldLight, vibrantGreen, errorRed)
        }
    }
}

@Composable
private fun CheckoutItemCard(imageUrl: String, name: String, price: Double, quantity: Int, onDelete: () -> Unit, colors: ColorScheme, forestDeep: Color, forestMid: Color, sageLight: Color, goldAccent: Color, ivoryWarm: Color) {
    Surface(Modifier.fillMaxWidth(), shape = RoundedCornerShape(18.dp), color = colors.surface, shadowElevation = 5.dp) {
        Row(Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
            Box(Modifier.size(70.dp).clip(RoundedCornerShape(14.dp)).background(ivoryWarm)) {
                AsyncImage(model = imageUrl, contentDescription = name, modifier = Modifier.fillMaxSize(), contentScale = ContentScale.Crop)
            }
            Spacer(Modifier.width(14.dp))
            Column(Modifier.weight(1f)) {
                Text(name, fontWeight = FontWeight.ExtraBold, fontSize = 14.sp, color = colors.onSurface, maxLines = 1, overflow = TextOverflow.Ellipsis)
                Text("Qty: $quantity", fontSize = 12.sp, color = sageLight)
                Row(verticalAlignment = Alignment.Bottom) {
                    Text("₹", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = goldAccent)
                    Text("${price * quantity}", fontSize = 17.sp, fontWeight = FontWeight.ExtraBold, color = forestMid)
                }
            }
            Box(Modifier.size(36.dp).clip(RoundedCornerShape(10.dp)).background(Color(0xFFFFEBEE)).clickable { onDelete() }, Alignment.Center) {
                Icon(imageVector = Icons.Default.Delete, contentDescription = null, tint = Color(0xFFE53935), modifier = Modifier.size(17.dp))
            }
        }
    }
}

@Composable
private fun OrderSummaryPanel(viewModel: ProductViewModel, onOrderPlaced: () -> Unit, colors: ColorScheme, forestDeep: Color, forestMid: Color, mossGreen: Color, sageLight: Color, goldAccent: Color, goldLight: Color, vibrantGreen: Color, errorRed: Color) {
    Surface(Modifier.fillMaxWidth(), shape = RoundedCornerShape(topStart = 32.dp, topEnd = 32.dp), color = colors.surface, shadowElevation = 20.dp) {
        Column(Modifier.padding(horizontal = 26.dp, vertical = 24.dp)) {
            Box(Modifier.width(40.dp).height(4.dp).background(sageLight.copy(0.4f), CircleShape).align(Alignment.CenterHorizontally))
            Spacer(Modifier.height(20.dp))
            Text("Order Summary", fontWeight = FontWeight.ExtraBold, fontSize = 16.sp, color = colors.onSurface)
            Spacer(Modifier.height(16.dp))
            SummaryLine("Subtotal", "₹${viewModel.cartSubtotal()}", sageLight, colors.onSurface)
            SummaryLine("Delivery", "+₹${viewModel.deliveryFee}", sageLight, errorRed)
            SummaryLine("GST (5%)", "+₹${viewModel.cartGst()}", sageLight, errorRed)
            Box(Modifier.fillMaxWidth().padding(vertical = 14.dp).height(1.dp).background(Brush.horizontalGradient(listOf(vibrantGreen.copy(0.4f), goldAccent.copy(0.2f), Color.Transparent))))
            Row(Modifier.fillMaxWidth(), Arrangement.SpaceBetween, Alignment.CenterVertically) {
                Text("Total", fontWeight = FontWeight.ExtraBold, fontSize = 16.sp, color = colors.onSurface)
                Row(verticalAlignment = Alignment.Bottom) {
                    Text("₹", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = goldAccent, modifier = Modifier.padding(bottom = 2.dp))
                    Text("${viewModel.cartTotal()}", fontSize = 26.sp, fontWeight = FontWeight.ExtraBold, color = forestMid)
                }
            }
            Spacer(Modifier.height(20.dp))
            Box(Modifier.fillMaxWidth().height(56.dp).clip(RoundedCornerShape(18.dp)).background(Brush.horizontalGradient(listOf(forestDeep, forestMid, mossGreen)))
                .clickable { onOrderPlaced() }, Alignment.Center
            ) {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    Icon(imageVector = Icons.Default.ShoppingBag, contentDescription = null, tint = goldLight, modifier = Modifier.size(18.dp))
                    Text("Place Order", fontSize = 16.sp, fontWeight = FontWeight.ExtraBold, color = Color.White)
                    Icon(imageVector = Icons.AutoMirrored.Filled.ArrowForward, contentDescription = null, tint = Color.White.copy(0.7f), modifier = Modifier.size(16.dp))
                }
            }
        }
    }
}

@Composable
private fun SummaryLine(label: String, value: String, labelColor: Color, valueColor: Color) {
    Row(Modifier.fillMaxWidth(), Arrangement.SpaceBetween, Alignment.CenterVertically) {
        Text(label, fontSize = 14.sp, color = labelColor, fontWeight = FontWeight.Medium)
        Text(value, fontSize = 14.sp, color = valueColor, fontWeight = FontWeight.SemiBold)
    }
}

@Composable
private fun EmptyCheckout(onBack: () -> Unit, forestDeep: Color, forestMid: Color, sageLight: Color) {
    Box(Modifier.fillMaxSize(), Alignment.Center) {
        Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.padding(40.dp)) {
            Surface(shape = CircleShape, color = forestDeep.copy(0.07f), modifier = Modifier.size(140.dp)) {
                Box(contentAlignment = Alignment.Center) { Text("🛒", fontSize = 60.sp) }
            }
            Spacer(Modifier.height(24.dp))
            Text("Nothing to check out", fontSize = 22.sp, fontWeight = FontWeight.ExtraBold, color = forestDeep)
            Text("Add some fresh produce first!", fontSize = 13.sp, color = sageLight)
            Spacer(Modifier.height(28.dp))
            Box(Modifier.clip(RoundedCornerShape(14.dp)).background(Brush.horizontalGradient(listOf(forestDeep, forestMid))).clickable { onBack() }.padding(horizontal = 28.dp, vertical = 14.dp)) {
                Text("← Go Back", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = Color.White)
            }
        }
    }
}
