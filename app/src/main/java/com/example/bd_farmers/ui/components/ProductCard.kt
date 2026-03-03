package com.example.bd_farmers.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
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
import androidx.compose.ui.draw.scale
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
import com.example.bd_farmers.ui.theme.ThemeManager

@Composable
fun ProductCard(
    product: Product,
    onAddToCart: () -> Unit,
    onFavoriteToggle: () -> Unit,
    modifier: Modifier = Modifier
) {
    var heartScale by remember { mutableStateOf(1f) }
    val animatedHeartScale by animateFloatAsState(
        targetValue = heartScale,
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy),
        label = "heart"
    )
    
    val isDark = ThemeManager.isDarkTheme
    val colors = MaterialTheme.colorScheme

    // High visibility greens for Dark Mode
    val forestDeep   = if (isDark) Color(0xFFE8F5E9) else Color(0xFF0D2B1A)
    val forestMid    = if (isDark) Color(0xFFB9F6CA) else Color(0xFF1B5E38)
    val vibrantGreen = Color(0xFF2ECC71)
    val mossGreen    = if (isDark) Color(0xFF81C784) else Color(0xFF4A8C5C)
    val sageLight    = if (isDark) Color(0xFFA5D6A7) else Color(0xFF8FB99A)
    val ivoryWarm    = if (isDark) Color(0xFF2A2A2A) else Color(0xFFF5F2EC)
    val goldAccent   = Color(0xFFD4A853)
    val goldLight    = Color(0xFFF0C96E)

    val heartColor by animateColorAsState(
        targetValue = if (product.isFavorite) Color(0xFFE53935) else forestDeep.copy(alpha = 0.4f),
        animationSpec = tween(280),
        label = "heart_color"
    )

    Box(
        modifier = modifier
            .drawBehind {
                drawCircle(
                    brush = Brush.radialGradient(
                        colors = listOf(mossGreen.copy(alpha = 0.22f), Color.Transparent),
                        center = Offset(size.width * 0.5f, size.height * 0.97f),
                        radius = size.width * 0.58f
                    ),
                    radius = size.width * 0.58f,
                    center = Offset(size.width * 0.5f, size.height * 0.97f)
                )
            }
    ) {
        Surface(
            shape = RoundedCornerShape(22.dp),
            color = colors.surface,
            shadowElevation = 16.dp,
            modifier = Modifier
                .fillMaxWidth()
                .border(
                    width = 1.2.dp,
                    brush = Brush.linearGradient(
                        listOf(
                            vibrantGreen.copy(alpha = 0.38f),
                            goldAccent.copy(alpha = 0.18f),
                            colors.surface.copy(alpha = 0.5f)
                        )
                    ),
                    shape = RoundedCornerShape(22.dp)
                )
        ) {
            Column {
                Box(modifier = Modifier.height(148.dp)) {
                    AsyncImage(
                        model = product.imageUrl,
                        contentDescription = product.name,
                        modifier = Modifier
                            .fillMaxSize()
                            .clip(RoundedCornerShape(topStart = 22.dp, topEnd = 22.dp)),
                        contentScale = ContentScale.Crop
                    )

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(72.dp)
                            .align(Alignment.BottomCenter)
                            .background(
                                Brush.verticalGradient(
                                    listOf(Color.Transparent, Color.Black.copy(alpha = 0.58f))
                                )
                            )
                    )

                    Box(
                        modifier = Modifier
                            .align(Alignment.TopEnd)
                            .padding(10.dp)
                            .size(36.dp)
                            .background(colors.surface, CircleShape)
                            .border(1.dp, sageLight.copy(alpha = 0.5f), CircleShape)
                            .clip(CircleShape)
                            .clickable {
                                heartScale = 1.4f
                                onFavoriteToggle()
                                heartScale = 1f
                            },
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = if (product.isFavorite) Icons.Filled.Favorite
                            else Icons.Outlined.FavoriteBorder,
                            contentDescription = "Favourite",
                            tint = heartColor,
                            modifier = Modifier.size(17.dp).scale(animatedHeartScale)
                        )
                    }

                    Row(
                        modifier = Modifier
                            .align(Alignment.BottomStart)
                            .padding(9.dp),
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Surface(shape = RoundedCornerShape(6.dp), color = goldAccent) {
                            Text(
                                "🌿 Organic",
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp),
                                fontSize = 9.sp, fontWeight = FontWeight.Bold, color = Color.White
                            )
                        }
                        Surface(shape = RoundedCornerShape(6.dp), color = vibrantGreen) {
                            Text(
                                "In Stock",
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp),
                                fontSize = 9.sp, fontWeight = FontWeight.Bold, color = Color.White
                            )
                        }
                    }
                }

                Column(modifier = Modifier.padding(horizontal = 13.dp, vertical = 11.dp)) {
                    Text(
                        text = product.name,
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = 14.sp,
                        color = colors.onSurface,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Text(
                        "Farm fresh · Daily harvest",
                        fontSize = 10.sp,
                        color = sageLight,
                        fontStyle = FontStyle.Italic,
                        modifier = Modifier.padding(top = 2.dp)
                    )

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp)
                            .height(1.dp)
                            .background(
                                Brush.horizontalGradient(
                                    listOf(vibrantGreen.copy(0.5f), goldAccent.copy(0.25f), Color.Transparent)
                                )
                            )
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Row(verticalAlignment = Alignment.Bottom) {
                                Text(
                                    "₹", fontSize = 12.sp, fontWeight = FontWeight.Bold,
                                    color = goldAccent, modifier = Modifier.padding(bottom = 2.dp)
                                )
                                Text(
                                    "${product.price}",
                                    fontSize = 22.sp, fontWeight = FontWeight.ExtraBold, color = forestMid
                                )
                            }
                            Text("per kg", fontSize = 9.sp, color = sageLight)
                        }

                        Surface(
                            shape = RoundedCornerShape(10.dp),
                            color = ivoryWarm,
                            border = BorderStroke(0.8.dp, goldAccent.copy(alpha = 0.5f))
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 5.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text("★", color = goldAccent, fontSize = 12.sp)
                                Spacer(Modifier.width(3.dp))
                                Text(
                                    "${product.rating}",
                                    fontSize = 12.sp, fontWeight = FontWeight.ExtraBold, color = forestDeep
                                )
                            }
                        }
                    }

                    Text(
                        "(${product.reviewCount} reviews)",
                        fontSize = 9.sp, color = sageLight,
                        modifier = Modifier.padding(top = 3.dp)
                    )

                    Spacer(Modifier.height(10.dp))

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(42.dp)
                            .clip(RoundedCornerShape(14.dp))
                            .background(
                                Brush.horizontalGradient(listOf(if (isDark) Color(0xFF003300) else Color(0xFF0D2B1A), forestMid, mossGreen))
                            )
                            .drawBehind {
                                drawCircle(
                                    brush = Brush.radialGradient(
                                        listOf(vibrantGreen.copy(0.28f), Color.Transparent),
                                        center = Offset(size.width * 0.12f, 0f),
                                        radius = size.height * 1.3f
                                    ),
                                    radius = size.height * 1.3f,
                                    center = Offset(size.width * 0.12f, 0f)
                                )
                            }
                            .border(
                                0.8.dp,
                                Brush.linearGradient(listOf(vibrantGreen.copy(0.5f), Color.Transparent)),
                                RoundedCornerShape(14.dp)
                            )
                            .clickable { onAddToCart() },
                        contentAlignment = Alignment.Center
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.ShoppingBasket,
                                contentDescription = null,
                                tint = goldLight,
                                modifier = Modifier.size(14.dp)
                            )
                            Text(
                                "Add to Cart",
                                fontSize = 12.sp, fontWeight = FontWeight.ExtraBold,
                                color = Color.White, letterSpacing = 0.3.sp
                            )
                        }
                    }
                }
            }
        }
    }
}
