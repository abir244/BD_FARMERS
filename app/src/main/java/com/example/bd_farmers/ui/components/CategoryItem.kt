package com.example.bd_farmers.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.*
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.bd_farmers.data.model.Category

// ── Palette ───────────────────────────────────────────────────────────────────
private val ForestDeep   = Color(0xFF0D2B1A)
private val ForestMid    = Color(0xFF1B5E38)
private val VibrantGreen = Color(0xFF2ECC71)
private val MossGreen    = Color(0xFF4A8C5C)
private val SageLight    = Color(0xFF8FB99A)
private val IvoryWarm    = Color(0xFFF6F3EE)
private val GoldAccent   = Color(0xFFD4A853)

// ── Icon + accent colour per category name ────────────────────────────────────
private data class CategoryStyle(val emoji: String, val accent: Color)

private fun categoryStyle(name: String): CategoryStyle = when {
    name.contains("vegetable", true) || name.contains("veggie", true)
        -> CategoryStyle("🥦", Color(0xFF4CAF50))
    name.contains("fruit",  true)
        -> CategoryStyle("🍎", Color(0xFFEF5350))
    name.contains("dairy",  true) || name.contains("milk", true)
        -> CategoryStyle("🥛", Color(0xFF64B5F6))
    name.contains("grain",  true) || name.contains("rice",  true) || name.contains("wheat", true)
        -> CategoryStyle("🌾", Color(0xFFD4A853))
    name.contains("meat",   true) || name.contains("fish",  true) || name.contains("poultry", true)
        -> CategoryStyle("🍖", Color(0xFFFF7043))
    name.contains("spice",  true) || name.contains("herb",  true)
        -> CategoryStyle("🌶️", Color(0xFFFF5722))
    name.contains("organic",true)
        -> CategoryStyle("🌿", Color(0xFF66BB6A))
    name.contains("flower", true)
        -> CategoryStyle("🌸", Color(0xFFF48FB1))
    name.contains("honey",  true)
        -> CategoryStyle("🍯", Color(0xFFFFB300))
    name.contains("egg",    true)
        -> CategoryStyle("🥚", Color(0xFFFFEE58))
    name.contains("oil",    true)
        -> CategoryStyle("🫙", Color(0xFFFFB74D))
    name.contains("nut",    true) || name.contains("seed", true)
        -> CategoryStyle("🥜", Color(0xFFA1887F))
    name.contains("all",    true) || name.contains("popular", true)
        -> CategoryStyle("⭐", Color(0xFFFFD54F))
    name.contains("new",    true) || name.contains("fresh", true)
        -> CategoryStyle("✨", VibrantGreen)
    name.contains("sale",   true) || name.contains("offer", true)
        -> CategoryStyle("🏷️", Color(0xFFFF5252))
    else -> CategoryStyle("🌱", MossGreen)
}

// ── COMPONENT ─────────────────────────────────────────────────────────────────
@Composable
fun CategoryItem(
    category: Category,
    isSelected: Boolean = false,
    onClick: () -> Unit
) {
    val style = remember(category.name) { categoryStyle(category.name) }

    val elevation by animateDpAsState(
        targetValue = if (isSelected) 14.dp else 3.dp,
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy),
        label = "elev"
    )
    val labelColor by animateColorAsState(
        targetValue = if (isSelected) ForestMid else SageLight,
        animationSpec = tween(220),
        label = "label"
    )
    val tileScale by animateFloatAsState(
        targetValue = if (isSelected) 1.10f else 1f,
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy),
        label = "scale"
    )

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .padding(end = 14.dp)
            .clickable(interactionSource = null, indication = null) { onClick() }
    ) {

        // ── TILE ──────────────────────────────────────────────────────────────
        Box(
            Modifier
                .size(70.dp)
                .drawBehind {
                    if (isSelected) {
                        // Soft coloured halo matching the category accent
                        drawCircle(
                            Brush.radialGradient(
                                listOf(style.accent.copy(0.30f), Color.Transparent),
                                center = Offset(size.width / 2, size.height / 2),
                                radius = size.minDimension * 0.82f
                            ),
                            radius = size.minDimension * 0.82f,
                            center = Offset(size.width / 2, size.height / 2)
                        )
                    }
                },
            contentAlignment = Alignment.Center
        ) {
            Surface(
                modifier = Modifier.size(62.dp).scale(tileScale),
                shape = RoundedCornerShape(20.dp),
                color = Color.Transparent,
                shadowElevation = elevation
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            if (isSelected)
                            // Gradient from a lightened accent to the category colour
                                Brush.linearGradient(
                                    listOf(
                                        style.accent.copy(alpha = 0.85f),
                                        style.accent
                                    )
                                )
                            else
                                Brush.linearGradient(listOf(Color.White, IvoryWarm))
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = style.emoji,
                        fontSize = 26.sp
                    )
                }
            }
        }

        Spacer(Modifier.height(8.dp))

        // ── LABEL ─────────────────────────────────────────────────────────────
        Text(
            text = category.name,
            fontSize = 11.sp,
            fontWeight = if (isSelected) FontWeight.ExtraBold else FontWeight.Medium,
            color = labelColor,
            maxLines = 1
        )

        Spacer(Modifier.height(5.dp))

        // ── SELECTION PILL — uses category accent colour ───────────────────
        val pillWidth by animateDpAsState(
            targetValue = if (isSelected) 24.dp else 0.dp,
            animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy),
            label = "pill"
        )
        Box(
            Modifier.height(3.dp).width(pillWidth)
                .clip(CircleShape)
                .background(
                    Brush.horizontalGradient(
                        listOf(style.accent, GoldAccent)
                    )
                )
        )
    }
}