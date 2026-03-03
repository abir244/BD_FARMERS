package com.example.bd_farmers.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.bd_farmers.R
import com.example.bd_farmers.ui.theme.ThemeManager

sealed class BottomNavItem(val route: String, val label: String, val iconRes: Int) {
    object Home : BottomNavItem("home", "Home", R.drawable.ic_home)
    object Explore : BottomNavItem("explore", "Explore", R.drawable.ic_explore)
    object Cart : BottomNavItem("cart", "Cart", R.drawable.ic_cart)
    object Profile : BottomNavItem("profile", "Profile", R.drawable.ic_profile)
}

@Composable
fun BottomNavBar(
    currentRoute: String,
    onNavigate: (String) -> Unit,
    cartCount: Int = 0
) {
    val items = listOf(
        BottomNavItem.Home,
        BottomNavItem.Explore,
        BottomNavItem.Cart,
        BottomNavItem.Profile
    )

    val isDark = ThemeManager.isDarkTheme
    
    // Glassmorphism Colors
    val glassColor = if (isDark) {
        Color(0xFF1E1E1E).copy(alpha = 0.85f)
    } else {
        Color.White.copy(alpha = 0.95f)
    }
    
    val borderColor = if (isDark) {
        Color.White.copy(alpha = 0.15f)
    } else {
        Color.Black.copy(alpha = 0.08f)
    }

    val itemActiveColor = if (isDark) {
        Color(0xFF81C784).copy(alpha = 0.25f)
    } else {
        Color(0xFFE8F5E9).copy(alpha = 0.8f)
    }

    val glowColor = if (isDark) Color(0xFF81C784).copy(alpha = 0.12f) else Color(0xFF2E7D32).copy(alpha = 0.08f)

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .navigationBarsPadding()
            .padding(start = 24.dp, end = 24.dp, bottom = 24.dp), // Fixed padding syntax
        contentAlignment = Alignment.BottomCenter
    ) {
        Surface(
            modifier = Modifier
                .wrapContentWidth()
                .height(80.dp)
                .border(
                    width = 1.dp,
                    brush = Brush.verticalGradient(
                        listOf(borderColor, Color.Transparent)
                    ),
                    shape = RoundedCornerShape(40.dp)
                ),
            color = glassColor,
            shape = RoundedCornerShape(40.dp),
            shadowElevation = if (isDark) 0.dp else 10.dp
        ) {
            Row(
                modifier = Modifier
                    .padding(horizontal = 16.dp)
                    .wrapContentWidth()
                    .fillMaxHeight(),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                items.forEach { item ->
                    val isSelected = currentRoute == item.route

                    // Faster animations: 150ms duration
                    val bgColor by animateColorAsState(
                        targetValue = if (isSelected) itemActiveColor else Color.Transparent,
                        animationSpec = tween(durationMillis = 150),
                        label = "bg_anim"
                    )
                    val iconSize by animateDpAsState(
                        targetValue = if (isSelected) 30.dp else 26.dp,
                        animationSpec = tween(durationMillis = 150),
                        label = "size_anim"
                    )

                    Box(
                        modifier = Modifier
                            .size(64.dp)
                            .clip(RoundedCornerShape(32.dp))
                            .background(bgColor)
                            .clickable(
                                interactionSource = remember { MutableInteractionSource() },
                                indication = null
                            ) {
                                if (currentRoute != item.route || item.route == "home") {
                                    onNavigate(item.route)
                                }
                            },
                        contentAlignment = Alignment.Center
                    ) {
                        NavigationIcon(
                            item = item,
                            isSelected = isSelected,
                            cartCount = cartCount,
                            iconSize = iconSize,
                            isDark = isDark
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun NavigationIcon(
    item: BottomNavItem,
    isSelected: Boolean,
    cartCount: Int,
    iconSize: androidx.compose.ui.unit.Dp,
    isDark: Boolean
) {
    val activeTint = if (isDark) Color(0xFF81C784) else Color(0xFF2E7D32)
    val inactiveTint = if (isDark) Color.White.copy(alpha = 0.5f) else Color.Black.copy(alpha = 0.4f)
    val tint = if (isSelected) activeTint else inactiveTint

    BadgedBox(
        badge = {
            if (item is BottomNavItem.Cart && cartCount > 0) {
                Badge(
                    containerColor = Color(0xFFE53935),
                    contentColor = Color.White,
                    modifier = Modifier.offset(x = (-4).dp, y = 4.dp)
                ) {
                    Text(cartCount.toString(), fontSize = 10.sp, fontWeight = androidx.compose.ui.text.font.FontWeight.Bold)
                }
            }
        }
    ) {
        Icon(
            painter = painterResource(item.iconRes),
            contentDescription = item.label,
            modifier = Modifier.size(iconSize),
            tint = tint
        )
    }
}
