package com.example.bd_farmers.ui.profile

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.automirrored.outlined.HelpOutline
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.bd_farmers.ui.theme.ThemeManager
import com.example.bd_farmers.viewmodel.AuthViewModel

@Composable
fun ProfileScreen(
    authViewModel: AuthViewModel,
    onLogout: () -> Unit
) {
    val scrollState = rememberScrollState()
    val isDark = ThemeManager.isDarkTheme
    val colors = MaterialTheme.colorScheme
    
    // Observe Firebase user
    val user by authViewModel.userState.collectAsState()

    // Image Picker Launcher
    val photoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let { authViewModel.updateProfilePicture(it) }
    }

    // Theme-adaptive colors
    val forestDeep = if (isDark) Color(0xFF00150A) else Color(0xFF0D2B1A)
    val forestMid = if (isDark) Color(0xFF1B5E38) else Color(0xFF1B5E38)
    val vibrantGreen = Color(0xFF2ECC71)
    val mossGreen = if (isDark) Color(0xFF6B8E7B) else Color(0xFF4A8C5C)
    val sageLight = if (isDark) Color(0xFF4A5D4F) else Color(0xFF8FB99A)
    val ivoryWarm = if (isDark) Color(0xFF121212) else Color(0xFFF0F7F1)
    val creamDeep = if (isDark) Color(0xFF1A1A1A) else Color(0xFFEDE8DF)
    val goldAccent = Color(0xFFD4A853)
    val goldLight = Color(0xFFF0C96E)
    val errorRed = Color(0xFFE53935)

    val inf = rememberInfiniteTransition(label = "bg")
    val pulse by inf.animateFloat(
        1f, 1.2f,
        infiniteRepeatable(tween(5000, easing = EaseInOutSine), RepeatMode.Reverse),
        "pulse"
    )

    Box(
        Modifier.fillMaxSize().background(colors.background).drawBehind {
            drawRect(Brush.verticalGradient(listOf(ivoryWarm, ivoryWarm, creamDeep)))
            drawCircle(
                Brush.radialGradient(
                    listOf(vibrantGreen.copy(0.11f * pulse), Color.Transparent),
                    center = Offset(size.width * 0.95f, 0f), radius = size.width * 0.75f
                ), radius = size.width * 0.75f, center = Offset(size.width * 0.95f, 0f)
            )
        }
    ) {
        Column(
            Modifier.fillMaxSize().verticalScroll(scrollState)
        ) {
            Row(
                Modifier.fillMaxWidth().padding(horizontal = 22.dp).padding(top = 52.dp, bottom = 20.dp),
                Arrangement.SpaceBetween, Alignment.CenterVertically
            ) {
                Column {
                    Text("My Account 👤", fontSize = 12.sp, color = mossGreen, fontStyle = FontStyle.Italic)
                    Text("Profile", fontSize = 30.sp, fontWeight = FontWeight.ExtraBold, color = colors.onBackground)
                }
                Box(Modifier.size(44.dp).clip(RoundedCornerShape(14.dp)).background(colors.surface).clickable { }, Alignment.Center) {
                    Icon(imageVector = Icons.Outlined.Settings, contentDescription = null, tint = colors.onSurface, modifier = Modifier.size(20.dp))
                }
            }

            // User Info Section
            ProfileHeroCard(
                userName = user?.displayName ?: "Guest User",
                userEmail = user?.email ?: user?.phoneNumber ?: "Not logged in",
                photoUrl = user?.photoUrl?.toString(),
                onPickImage = { photoPickerLauncher.launch("image/*") },
                forestDeep = forestDeep,
                forestMid = forestMid,
                mossGreen = mossGreen,
                vibrantGreen = vibrantGreen,
                goldAccent = goldAccent,
                goldLight = goldLight,
                sageLight = sageLight,
                colors = colors
            )

            Spacer(Modifier.height(24.dp))

            Surface(Modifier.fillMaxWidth().padding(horizontal = 22.dp), shape = RoundedCornerShape(20.dp), color = forestDeep, shadowElevation = 10.dp) {
                Row(Modifier.fillMaxWidth().padding(horizontal = 24.dp, vertical = 18.dp), Arrangement.SpaceBetween) {
                    StatItem("12", "Orders", goldLight, sageLight)
                    StatDivider()
                    StatItem("3", "Pending", goldLight, sageLight)
                    StatDivider()
                    StatItem("₹2.4k", "Spent", goldLight, sageLight)
                }
            }

            Spacer(Modifier.height(28.dp))

            SectionLabel("Account", vibrantGreen, goldAccent, colors.onBackground)
            Spacer(Modifier.height(10.dp))
            ProfileOptionItem(Icons.Outlined.ShoppingBag, "My Orders", "View history", goldAccent, sageLight, colors) { }
            ProfileOptionItem(Icons.Outlined.CreditCard, "Payment", "Cards & wallets", mossGreen, sageLight, colors) { }

            Spacer(Modifier.height(20.dp))

            SectionLabel("Preferences", vibrantGreen, goldAccent, colors.onBackground)
            Spacer(Modifier.height(10.dp))
            ProfileOptionItem(
                icon = if (isDark) Icons.Default.DarkMode else Icons.Default.LightMode,
                title = "Dark Mode",
                subtitle = if (isDark) "Enabled" else "Disabled",
                iconTint = goldAccent,
                labelColor = sageLight,
                colors = colors,
                trailing = {
                    Switch(
                        checked = isDark,
                        onCheckedChange = { ThemeManager.toggleTheme() },
                        colors = SwitchDefaults.colors(checkedThumbColor = goldAccent)
                    )
                }
            ) { ThemeManager.toggleTheme() }
            
            ProfileOptionItem(Icons.AutoMirrored.Outlined.HelpOutline, "Help", "FAQ & Support", vibrantGreen, sageLight, colors) { }

            Spacer(Modifier.height(20.dp))

            LogoutButton(onLogout, errorRed, colors)

            Spacer(Modifier.height(110.dp))
        }
    }
}

@Composable
private fun ProfileHeroCard(
    userName: String,
    userEmail: String,
    photoUrl: String?,
    onPickImage: () -> Unit,
    forestDeep: Color, forestMid: Color, mossGreen: Color, vibrantGreen: Color, goldAccent: Color, goldLight: Color, sageLight: Color, colors: ColorScheme
) {
    Surface(Modifier.fillMaxWidth().padding(horizontal = 22.dp), shape = RoundedCornerShape(28.dp), color = colors.surface, shadowElevation = 10.dp) {
        Box {
            Box(Modifier.fillMaxWidth().height(72.dp).clip(RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp)).background(Brush.horizontalGradient(listOf(forestDeep, forestMid, mossGreen))))
            Column(Modifier.fillMaxWidth().padding(bottom = 22.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                Spacer(Modifier.height(28.dp))
                Box(Modifier.size(90.dp), Alignment.Center) {
                    Box(Modifier.fillMaxSize().background(Brush.linearGradient(listOf(goldAccent, goldLight)), CircleShape))
                    
                    AsyncImage(
                        model = ImageRequest.Builder(LocalContext.current)
                            .data(photoUrl ?: "https://i.pravatar.cc/150?u=default")
                            .crossfade(true)
                            .build(),
                        contentDescription = "Avatar",
                        modifier = Modifier
                            .size(82.dp)
                            .clip(CircleShape)
                            .clickable { onPickImage() },
                        contentScale = ContentScale.Crop
                    )
                    
                    // Change Photo Button / Icon
                    Box(
                        Modifier
                            .size(28.dp)
                            .background(vibrantGreen, CircleShape)
                            .border(2.dp, Color.White, CircleShape)
                            .align(Alignment.BottomEnd)
                            .clickable { onPickImage() },
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.Default.CameraAlt, null, tint = Color.White, modifier = Modifier.size(14.dp))
                    }
                }

                Spacer(Modifier.height(12.dp))

                Text(userName, fontWeight = FontWeight.ExtraBold, fontSize = 20.sp, color = colors.onSurface)
                Text(userEmail, fontSize = 13.sp, color = sageLight)
            }
        }
    }
}

@Composable
private fun SectionLabel(title: String, vibrantGreen: Color, goldAccent: Color, onBackground: Color) {
    Row(Modifier.padding(horizontal = 22.dp), verticalAlignment = Alignment.CenterVertically) {
        Box(Modifier.width(4.dp).height(16.dp).background(Brush.verticalGradient(listOf(vibrantGreen, goldAccent)), RoundedCornerShape(2.dp)))
        Spacer(Modifier.width(8.dp))
        Text(title, fontWeight = FontWeight.ExtraBold, fontSize = 14.sp, color = onBackground)
    }
}

@Composable
private fun ProfileOptionItem(icon: ImageVector, title: String, subtitle: String, iconTint: Color, labelColor: Color, colors: ColorScheme, trailing: (@Composable () -> Unit)? = null, onClick: () -> Unit) {
    Surface(Modifier.fillMaxWidth().padding(horizontal = 22.dp, vertical = 5.dp).clickable { onClick() }, shape = RoundedCornerShape(18.dp), color = colors.surface, shadowElevation = 4.dp) {
        Row(Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 14.dp), verticalAlignment = Alignment.CenterVertically) {
            Box(Modifier.size(44.dp).clip(RoundedCornerShape(14.dp)).background(iconTint.copy(alpha = 0.1f)), Alignment.Center) {
                Icon(imageVector = icon, contentDescription = null, tint = iconTint, modifier = Modifier.size(20.dp))
            }
            Spacer(Modifier.width(14.dp))
            Column(Modifier.weight(1f)) {
                Text(title, fontWeight = FontWeight.ExtraBold, fontSize = 14.sp, color = colors.onSurface)
                Text(subtitle, fontSize = 11.sp, color = labelColor)
            }
            if (trailing != null) trailing() else Icon(imageVector = Icons.Default.ChevronRight, contentDescription = null, tint = labelColor.copy(0.6f), modifier = Modifier.size(18.dp))
        }
    }
}

@Composable
private fun LogoutButton(onClick: () -> Unit, errorRed: Color, colors: ColorScheme) {
    Surface(Modifier.fillMaxWidth().padding(horizontal = 22.dp).clickable { onClick() }, shape = RoundedCornerShape(18.dp), color = errorRed.copy(alpha = 0.06f)) {
        Row(Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 16.dp), verticalAlignment = Alignment.CenterVertically) {
            Box(Modifier.size(44.dp).clip(RoundedCornerShape(14.dp)).background(errorRed.copy(0.1f)), Alignment.Center) {
                Icon(imageVector = Icons.AutoMirrored.Filled.Logout, contentDescription = null, tint = errorRed, modifier = Modifier.size(20.dp))
            }
            Spacer(Modifier.width(14.dp))
            Text("Logout", fontWeight = FontWeight.ExtraBold, fontSize = 14.sp, color = errorRed, modifier = Modifier.weight(1f))
            Icon(imageVector = Icons.Default.ChevronRight, contentDescription = null, tint = errorRed.copy(0.4f), modifier = Modifier.size(18.dp))
        }
    }
}

@Composable
private fun StatItem(value: String, label: String, goldLight: Color, sageLight: Color) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(value, fontSize = 18.sp, fontWeight = FontWeight.ExtraBold, color = goldLight)
        Text(label, fontSize = 11.sp, color = sageLight)
    }
}

@Composable
private fun StatDivider() {
    Box(Modifier.width(1.dp).height(32.dp).background(Color.White.copy(0.12f)))
}
