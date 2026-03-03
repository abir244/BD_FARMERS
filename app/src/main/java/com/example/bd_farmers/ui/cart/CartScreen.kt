package com.example.bd_farmers.ui.cart

import androidx.compose.animation.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.bd_farmers.data.model.CartItem
import com.example.bd_farmers.ui.theme.ThemeManager
import com.example.bd_farmers.viewmodel.ProductViewModel

@Composable
fun CartScreen(
    viewModel: ProductViewModel,
    onProceedToCheckout: () -> Unit,
    onNavigateHome: () -> Unit
) {
    val cartItems by viewModel.cartItems.collectAsState()
    val isDark = ThemeManager.isDarkTheme

    // High-visibility palette for Glassmorphism
    val forestDeep   = if (isDark) Color(0xFFE8F5E9) else Color(0xFF1A3D2B)
    val forestMid    = if (isDark) Color(0xFFB9F6CA) else Color(0xFF2E6844)
    val mossGreen    = if (isDark) Color(0xFF81C784) else Color(0xFF4A8C5C)
    val sageLight    = if (isDark) Color(0xFFA5D6A7) else Color(0xFF8FB99A)
    val ivoryWarm    = if (isDark) Color(0xFF121212) else Color(0xFFF8F4EE)
    val goldAccent   = Color(0xFFD4A853)
    val goldLight    = Color(0xFFF0C96E)
    val surfaceColor = if (isDark) Color(0xFF1E1E1E) else Color.White

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .drawBehind { drawCartBackground(ivoryWarm, sageLight, goldAccent, forestDeep) }
    ) {
        AnimatedContent(
            targetState = cartItems.isEmpty(),
            transitionSpec = { fadeIn() togetherWith fadeOut() },
            label = "cart_state"
        ) { isEmpty ->
            if (isEmpty) {
                EmptyCartState(onNavigateHome, forestDeep, sageLight, goldAccent, goldLight)
            } else {
                // ── FULLY SCROLLABLE CONTENT ────────────────────────────────
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(top = 16.dp, bottom = 40.dp),
                    verticalArrangement = Arrangement.spacedBy(18.dp)
                ) {
                    // 1. Header
                    item {
                        CartHeader(itemCount = cartItems.size, forestDeep, forestMid, mossGreen, goldAccent)
                    }

                    // 2. Cart Items List
                    items(cartItems, key = { it.product.id }) { item ->
                        CartItemCard(item, viewModel, forestDeep, forestMid, mossGreen, sageLight, goldAccent, surfaceColor)
                    }

                    // 3. Promo & Shipping
                    item {
                        Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                            PromoCodeCard(forestDeep, mossGreen, sageLight, goldAccent)
                            ShippingCard(forestDeep, forestMid, mossGreen, sageLight, goldAccent, surfaceColor)
                        }
                    }

                    // 4. Checkout Summary (Scrolled to the end)
                    item {
                        CheckoutPanel(
                            viewModel,
                            onProceedToCheckout,
                            forestDeep, forestMid, mossGreen, sageLight, goldLight, goldAccent
                        )
                    }
                }
            }
        }
    }
}

private fun DrawScope.drawCartBackground(ivoryWarm: Color, sageLight: Color, goldAccent: Color, forestDeep: Color) {
    drawRect(color = ivoryWarm)
    drawCircle(
        brush = Brush.radialGradient(
            colors = listOf(sageLight.copy(alpha = 0.12f), Color.Transparent),
            radius = size.width * 0.75f,
            center = Offset(-size.width * 0.1f, size.height * 0.05f)
        ),
        radius = size.width * 0.75f,
        center = Offset(-size.width * 0.1f, size.height * 0.05f)
    )
    // Subtle Dot Pattern for texture
    val dotColor = forestDeep.copy(alpha = 0.04f)
    val dotSpacing = 32f
    var x = dotSpacing
    while (x < size.width) {
        var y = dotSpacing
        while (y < size.height) {
            drawCircle(color = dotColor, radius = 1.5f, center = Offset(x, y))
            y += dotSpacing
        }
        x += dotSpacing
    }
}

@Composable
private fun CartHeader(itemCount: Int, forestDeep: Color, forestMid: Color, mossGreen: Color, goldAccent: Color) {
    Column(modifier = Modifier.fillMaxWidth().padding(horizontal = 24.dp).padding(top = 16.dp)) {
        Text("YOUR", fontSize = 12.sp, fontWeight = FontWeight.Black, color = mossGreen, letterSpacing = 4.sp)
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.Bottom) {
            Text("Harvest\nBasket", fontSize = 36.sp, fontWeight = FontWeight.ExtraBold, color = forestDeep, lineHeight = 38.sp)
            Surface(color = goldAccent, shape = RoundedCornerShape(50)) {
                Text("$itemCount items", modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp), fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color.White)
            }
        }
        Spacer(Modifier.height(12.dp))
        Box(modifier = Modifier.fillMaxWidth().height(1.dp).background(Brush.horizontalGradient(listOf(forestMid, Color.Transparent))))
    }
}

@Composable
private fun CartItemCard(cartItem: CartItem, viewModel: ProductViewModel, forestDeep: Color, forestMid: Color, mossGreen: Color, sageLight: Color, goldAccent: Color, surface: Color) {
    Surface(
        shape = RoundedCornerShape(24.dp),
        color = surface.copy(alpha = 0.6f), // Glass transparency
        modifier = Modifier
            .padding(horizontal = 20.dp)
            .fillMaxWidth()
            .border(1.dp, Color.White.copy(alpha = 0.5f), RoundedCornerShape(24.dp))
    ) {
        Row(modifier = Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
            AsyncImage(
                model = cartItem.product.imageUrl,
                contentDescription = null,
                modifier = Modifier.size(85.dp).clip(RoundedCornerShape(18.dp)),
                contentScale = ContentScale.Crop
            )
            Spacer(Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(cartItem.product.name, fontWeight = FontWeight.Bold, fontSize = 16.sp, color = forestDeep, maxLines = 1, overflow = TextOverflow.Ellipsis)
                Text("Fresh · Organic", fontSize = 11.sp, color = sageLight, fontStyle = FontStyle.Italic)
                Spacer(Modifier.height(4.dp))
                Text("₹${cartItem.product.price}", color = forestMid, fontWeight = FontWeight.Black, fontSize = 20.sp)
            }
            QuantityStepper(cartItem.quantity, { viewModel.addToCart(cartItem.product) }, { viewModel.removeFromCart(cartItem.product.id) }, forestDeep, forestMid)
        }
    }
}

@Composable
private fun QuantityStepper(quantity: Int, onAdd: () -> Unit, onRemove: () -> Unit, forestDeep: Color, forestMid: Color) {
    Column(
        modifier = Modifier.background(Color.White.copy(alpha = 0.4f), RoundedCornerShape(15.dp)).padding(4.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        IconButton(onClick = onAdd, modifier = Modifier.size(32.dp)) {
            Icon(Icons.Default.Add, null, tint = forestMid, modifier = Modifier.size(18.dp))
        }
        Text("$quantity", fontWeight = FontWeight.Black, fontSize = 16.sp, color = forestDeep)
        IconButton(onClick = onRemove, modifier = Modifier.size(32.dp)) {
            Icon(if (quantity <= 1) Icons.Default.Delete else Icons.Default.Remove, null, tint = if (quantity <= 1) Color.Red.copy(0.7f) else forestMid, modifier = Modifier.size(18.dp))
        }
    }
}

@Composable
private fun PromoCodeCard(forestDeep: Color, mossGreen: Color, sageLight: Color, goldAccent: Color) {
    Surface(
        shape = RoundedCornerShape(20.dp),
        color = forestDeep.copy(alpha = 0.05f),
        modifier = Modifier.padding(horizontal = 20.dp).fillMaxWidth().border(1.dp, mossGreen.copy(alpha = 0.1f), RoundedCornerShape(20.dp))
    ) {
        Row(modifier = Modifier.padding(14.dp), verticalAlignment = Alignment.CenterVertically) {
            Icon(Icons.Default.LocalOffer, null, tint = goldAccent, modifier = Modifier.size(20.dp))
            Spacer(Modifier.width(10.dp))
            Text("Apply Promo Code", color = sageLight, fontSize = 13.sp, modifier = Modifier.weight(1f))
            Text("Apply", color = goldAccent, fontWeight = FontWeight.Bold, fontSize = 14.sp, modifier = Modifier.clickable { })
        }
    }
}

@Composable
private fun ShippingCard(forestDeep: Color, forestMid: Color, mossGreen: Color, sageLight: Color, goldAccent: Color, surface: Color) {
    Column(modifier = Modifier.padding(horizontal = 24.dp)) {
        Text("Shipping Address", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = forestDeep, modifier = Modifier.padding(bottom = 8.dp))
        Surface(
            shape = RoundedCornerShape(22.dp),
            color = surface.copy(alpha = 0.6f),
            modifier = Modifier.fillMaxWidth().border(1.dp, Color.White.copy(alpha = 0.4f), RoundedCornerShape(22.dp))
        ) {
            Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                Box(modifier = Modifier.size(40.dp).background(forestMid.copy(0.1f), CircleShape), contentAlignment = Alignment.Center) {
                    Icon(Icons.Default.LocationOn, null, tint = forestMid, modifier = Modifier.size(20.dp))
                }
                Spacer(Modifier.width(12.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text("Home", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = forestDeep)
                    Text("70 Washington Square South, NY", fontSize = 12.sp, color = sageLight)
                }
                Icon(Icons.Default.Edit, null, tint = goldAccent, modifier = Modifier.size(16.dp))
            }
        }
    }
}

@Composable
private fun CheckoutPanel(viewModel: ProductViewModel, onProceed: () -> Unit, forestDeep: Color, forestMid: Color, mossGreen: Color, sageLight: Color, goldLight: Color, goldAccent: Color) {
    Surface(
        shape = RoundedCornerShape(32.dp),
        color = forestDeep,
        shadowElevation = 12.dp,
        modifier = Modifier.padding(horizontal = 20.dp).fillMaxWidth()
    ) {
        Box(modifier = Modifier.background(Brush.linearGradient(listOf(forestMid.copy(0.3f), Color.Transparent)))) {
            Column(modifier = Modifier.padding(24.dp)) {
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                    Column {
                        Text("TOTAL PRICE", fontSize = 10.sp, color = sageLight, letterSpacing = 2.sp, fontWeight = FontWeight.Bold)
                        Row(verticalAlignment = Alignment.Bottom) {
                            Text("₹", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = goldLight, modifier = Modifier.padding(bottom = 4.dp))
                            Text(viewModel.cartTotal().toString(), fontSize = 34.sp, fontWeight = FontWeight.Black, color = Color.White)
                        }
                    }
                    Button(
                        onClick = onProceed,
                        modifier = Modifier.height(56.dp).width(140.dp),
                        shape = RoundedCornerShape(16.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = goldAccent)
                    ) {
                        Text("Checkout", fontWeight = FontWeight.ExtraBold, fontSize = 16.sp)
                    }
                }
                Spacer(Modifier.height(12.dp))
                Text("* Prices include GST and delivery charges", fontSize = 10.sp, color = sageLight.copy(0.5f), textAlign = TextAlign.Center, modifier = Modifier.fillMaxWidth())
            }
        }
    }
}

@Composable
fun EmptyCartState(onNavigateHome: () -> Unit, forestDeep: Color, sageLight: Color, goldAccent: Color, goldLight: Color) {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.padding(40.dp)) {
            Text("🧺", fontSize = 80.sp)
            Spacer(Modifier.height(24.dp))
            Text("Empty Basket", fontSize = 28.sp, fontWeight = FontWeight.Black, color = forestDeep)
            Text("Looks like you haven't added any fresh harvests yet.", fontSize = 14.sp, color = sageLight, textAlign = TextAlign.Center)
            Spacer(Modifier.height(32.dp))
            Button(
                onClick = onNavigateHome,
                colors = ButtonDefaults.buttonColors(containerColor = forestDeep),
                shape = RoundedCornerShape(14.dp),
                modifier = Modifier.height(50.dp)
            ) {
                Icon(Icons.Default.ShoppingBasket, null, tint = goldLight)
                Spacer(Modifier.width(8.dp))
                Text("Start Shopping", fontWeight = FontWeight.Bold)
            }
        }
    }
}