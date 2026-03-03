package com.example.bd_farmers.ui.auth

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.*
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay

private val ForestDeep   = Color(0xFF0D2B1A)
private val ForestMid    = Color(0xFF1B5E38)
private val VibrantGreen = Color(0xFF2ECC71)
private val SageLight    = Color(0xFF8FB99A)
private val GoldAccent   = Color(0xFFD4A853)

@Composable
fun SplashScreen(onAnimationFinished: () -> Unit) {
    val infiniteTransition = rememberInfiniteTransition(label = "splash")
    
    val orbScale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1.3f,
        animationSpec = infiniteRepeatable(
            tween(2500, easing = EaseInOutSine),
            RepeatMode.Reverse
        ),
        label = "orb"
    )

    val logoScale = remember { Animatable(0f) }
    
    LaunchedEffect(Unit) {
        logoScale.animateTo(
            targetValue = 1f,
            animationSpec = spring(
                dampingRatio = Spring.DampingRatioMediumBouncy,
                stiffness = Spring.StiffnessLow
            )
        )
        delay(2000)
        onAnimationFinished()
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .drawBehind {
                drawRect(
                    brush = Brush.verticalGradient(
                        listOf(ForestDeep, Color(0xFF142E1E), Color(0xFF0A1F12))
                    )
                )
                // Immersive glow effects matching Auth ambiance
                drawCircle(
                    brush = Brush.radialGradient(
                        colors = listOf(VibrantGreen.copy(alpha = 0.15f), Color.Transparent),
                        radius = size.width * orbScale,
                        center = Offset(size.width * 0.2f, size.height * 0.2f)
                    ),
                    radius = size.width * orbScale
                )
                drawCircle(
                    brush = Brush.radialGradient(
                        colors = listOf(GoldAccent.copy(alpha = 0.1f), Color.Transparent),
                        radius = size.width * 0.8f,
                        center = Offset(size.width * 0.8f, size.height * 0.8f)
                    ),
                    radius = size.width * 0.8f
                )
            },
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.scale(logoScale.value)
        ) {
            Box(
                modifier = Modifier
                    .size(120.dp)
                    .border(2.dp, Brush.linearGradient(listOf(VibrantGreen, GoldAccent)), CircleShape)
                    .clip(CircleShape)
                    .background(Color.White.copy(alpha = 0.05f)),
                contentAlignment = Alignment.Center
            ) {
                Text("🌾", fontSize = 56.sp)
            }

            Spacer(Modifier.height(24.dp))

            Text(
                "BD Farmers",
                fontSize = 32.sp,
                fontWeight = FontWeight.ExtraBold,
                color = Color.White,
                letterSpacing = 1.sp
            )
            Text(
                "Pure · Fresh · Direct",
                fontSize = 14.sp,
                color = SageLight.copy(alpha = 0.7f),
                fontStyle = FontStyle.Italic,
                modifier = Modifier.padding(top = 4.dp)
            )
            
            Spacer(Modifier.height(48.dp))
            
            CircularProgressIndicator(
                modifier = Modifier.size(24.dp),
                color = VibrantGreen.copy(alpha = 0.5f),
                strokeWidth = 2.dp
            )
        }
    }
}
