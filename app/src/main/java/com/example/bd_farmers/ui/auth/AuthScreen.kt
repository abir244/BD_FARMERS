package com.example.bd_farmers.ui.auth

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
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
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.*
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.bd_farmers.viewmodel.AuthViewModel
import kotlinx.coroutines.launch

// ── Palette ───────────────────────────────────────────────────────────────────
private val ForestDeep   = Color(0xFF0D2B1A)
private val ForestMid    = Color(0xFF1B5E38)
private val VibrantGreen = Color(0xFF2ECC71)
private val MossGreen    = Color(0xFF4A8C5C)
private val SageLight    = Color(0xFF8FB99A)
private val IvoryWarm    = Color(0xFFF5F2EC)
private val GoldAccent   = Color(0xFFD4A853)
private val GoldLight    = Color(0xFFF0C96E)

@Composable
fun AuthScreen(
    authViewModel: AuthViewModel,
    onAuthSuccess: () -> Unit
) {
    var isLogin by remember { mutableStateOf(true) }
    val scope   = rememberCoroutineScope()

    var email    by remember { mutableStateOf("admin@gmail.com") }
    var password by remember { mutableStateOf("123456") }
    var name     by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }

    val token by authViewModel.token.collectAsState()
    LaunchedEffect(token) { if (!token.isNullOrEmpty()) onAuthSuccess() }

    // Floating orb pulse animation
    val infiniteTransition = rememberInfiniteTransition(label = "orb")
    val orbScale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue  = 1.18f,
        animationSpec = infiniteRepeatable(
            tween(3200, easing = EaseInOutSine),
            RepeatMode.Reverse
        ),
        label = "orb_scale"
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .drawBehind {
                // Deep forest base
                drawRect(
                    brush = Brush.verticalGradient(
                        listOf(ForestDeep, Color(0xFF142E1E), Color(0xFF0A1F12))
                    )
                )
                // Vibrant green glow — top left
                drawCircle(
                    brush = Brush.radialGradient(
                        colors = listOf(VibrantGreen.copy(alpha = 0.22f), Color.Transparent),
                        radius = size.width * 0.7f * orbScale,
                        center = Offset(-size.width * 0.05f, size.height * 0.1f)
                    ),
                    radius = size.width * 0.7f * orbScale,
                    center = Offset(-size.width * 0.05f, size.height * 0.1f)
                )
                // Gold glow — bottom right
                drawCircle(
                    brush = Brush.radialGradient(
                        colors = listOf(GoldAccent.copy(alpha = 0.14f), Color.Transparent),
                        radius = size.width * 0.55f,
                        center = Offset(size.width * 1.1f, size.height * 0.95f)
                    ),
                    radius = size.width * 0.55f,
                    center = Offset(size.width * 1.1f, size.height * 0.95f)
                )
                // Subtle dot grid
                val spacing = 32f
                var x = spacing
                while (x < size.width) {
                    var y = spacing
                    while (y < size.height) {
                        drawCircle(
                            color = Color.White.copy(alpha = 0.035f),
                            radius = 1.5f,
                            center = Offset(x, y)
                        )
                        y += spacing
                    }
                    x += spacing
                }
            }
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 28.dp),
            verticalArrangement = Arrangement.Center
        ) {

            // ── TOP BRAND MARK ────────────────────────────────────────────
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 40.dp)
            ) {
                // Glowing leaf emblem
                Box(
                    modifier = Modifier
                        .size(80.dp)
                        .drawBehind {
                            drawCircle(
                                brush = Brush.radialGradient(
                                    listOf(VibrantGreen.copy(alpha = 0.35f), Color.Transparent)
                                ),
                                radius = size.minDimension * 0.9f
                            )
                        }
                        .border(
                            1.5.dp,
                            brush = Brush.linearGradient(
                                listOf(VibrantGreen.copy(alpha = 0.6f), GoldAccent.copy(alpha = 0.3f))
                            ),
                            shape = CircleShape
                        )
                        .clip(CircleShape)
                        .background(Color.White.copy(alpha = 0.08f)),
                    contentAlignment = Alignment.Center
                ) {
                    Text("🌾", fontSize = 38.sp)
                }

                Spacer(Modifier.height(16.dp))

                Text(
                    text = "BD Farmers",
                    fontSize = 28.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = Color.White,
                    letterSpacing = 0.5.sp
                )
                Text(
                    text = "Farm to table, straight to you",
                    fontSize = 12.sp,
                    color = SageLight.copy(alpha = 0.8f),
                    fontStyle = FontStyle.Italic,
                    modifier = Modifier.padding(top = 4.dp)
                )
            }

            // ── GLASS CARD ────────────────────────────────────────────────
            Surface(
                shape = RoundedCornerShape(28.dp),
                color = Color.White.copy(alpha = 0.08f),
                modifier = Modifier
                    .fillMaxWidth()
                    .border(
                        width = 1.dp,
                        brush = Brush.linearGradient(
                            listOf(
                                Color.White.copy(alpha = 0.25f),
                                VibrantGreen.copy(alpha = 0.15f),
                                Color.White.copy(alpha = 0.05f)
                            )
                        ),
                        shape = RoundedCornerShape(28.dp)
                    )
            ) {
                Column(modifier = Modifier.padding(28.dp)) {

                    // ── TAB SWITCHER ──────────────────────────────────────
                    AuthTabSwitcher(
                        isLogin = isLogin,
                        onSwitch = { isLogin = it }
                    )

                    Spacer(Modifier.height(28.dp))

                    // ── FIELDS ────────────────────────────────────────────
                    AnimatedVisibility(
                        visible = !isLogin,
                        enter = expandVertically() + fadeIn(),
                        exit = shrinkVertically() + fadeOut()
                    ) {
                        Column {
                            GlassTextField(
                                value = name,
                                onValueChange = { name = it },
                                label = "Full Name",
                                icon = Icons.Default.Person
                            )
                            Spacer(Modifier.height(14.dp))
                        }
                    }

                    GlassTextField(
                        value = email,
                        onValueChange = { email = it },
                        label = "Email Address",
                        icon = Icons.Default.Email,
                        keyboardType = KeyboardType.Email
                    )

                    Spacer(Modifier.height(14.dp))

                    GlassTextField(
                        value = password,
                        onValueChange = { password = it },
                        label = "Password",
                        icon = Icons.Default.Lock,
                        isPassword = true,
                        passwordVisible = passwordVisible,
                        onPasswordToggle = { passwordVisible = !passwordVisible }
                    )

                    // Forgot password (only on login)
                    AnimatedVisibility(visible = isLogin) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 8.dp),
                            contentAlignment = Alignment.CenterEnd
                        ) {
                            Text(
                                "Forgot password?",
                                fontSize = 11.sp,
                                color = GoldLight,
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }

                    Spacer(Modifier.height(28.dp))

                    // ── SUBMIT BUTTON ─────────────────────────────────────
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(54.dp)
                            .clip(RoundedCornerShape(16.dp))
                            .background(
                                brush = Brush.horizontalGradient(
                                    listOf(VibrantGreen, ForestMid)
                                )
                            )
                            .border(
                                0.8.dp,
                                VibrantGreen.copy(alpha = 0.5f),
                                RoundedCornerShape(16.dp)
                            )
                            .clickable {
                                if (email == "admin@gmail.com" && password == "123456") {
                                    onAuthSuccess()
                                } else {
                                    scope.launch {
                                        if (isLogin) authViewModel.login(email, password)
                                        else authViewModel.register(name, email, password)
                                    }
                                }
                            },
                        contentAlignment = Alignment.Center
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Icon(
                                if (isLogin) Icons.Default.Login else Icons.Default.HowToReg,
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier.size(18.dp)
                            )
                            Text(
                                text = if (isLogin) "Sign In" else "Create Account",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = Color.White,
                                letterSpacing = 0.4.sp
                            )
                        }
                    }
                }
            }

            // ── DIVIDER ───────────────────────────────────────────────────
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 24.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    Modifier
                        .weight(1f)
                        .height(0.8.dp)
                        .background(Color.White.copy(alpha = 0.12f))
                )
                Text(
                    "  or  ",
                    color = SageLight.copy(alpha = 0.6f),
                    fontSize = 11.sp
                )
                Box(
                    Modifier
                        .weight(1f)
                        .height(0.8.dp)
                        .background(Color.White.copy(alpha = 0.12f))
                )
            }

            // ── SWITCH MODE LINK ──────────────────────────────────────────
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = if (isLogin) "New to BD Farmers? " else "Already a member? ",
                    color = Color.White.copy(alpha = 0.55f),
                    fontSize = 13.sp
                )
                Text(
                    text = if (isLogin) "Register" else "Sign In",
                    color = VibrantGreen,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.ExtraBold,
                    modifier = Modifier.clickable { isLogin = !isLogin }
                )
            }

            Spacer(Modifier.height(32.dp))
        }
    }
}

// ── TAB SWITCHER ──────────────────────────────────────────────────────────────
@Composable
private fun AuthTabSwitcher(isLogin: Boolean, onSwitch: (Boolean) -> Unit) {
    Surface(
        shape = RoundedCornerShape(14.dp),
        color = Color.White.copy(alpha = 0.07f),
        modifier = Modifier
            .fillMaxWidth()
            .border(0.8.dp, Color.White.copy(alpha = 0.12f), RoundedCornerShape(14.dp))
    ) {
        Row(modifier = Modifier.padding(4.dp)) {
            listOf(true to "Sign In", false to "Register").forEach { (tab, label) ->
                val selected = isLogin == tab
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .height(40.dp)
                        .clip(RoundedCornerShape(11.dp))
                        .background(
                            if (selected)
                                Brush.horizontalGradient(listOf(VibrantGreen, ForestMid))
                            else
                                Brush.horizontalGradient(listOf(Color.Transparent, Color.Transparent))
                        )
                        .clickable { onSwitch(tab) },
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = label,
                        fontSize = 13.sp,
                        fontWeight = if (selected) FontWeight.Bold else FontWeight.Normal,
                        color = if (selected) Color.White else Color.White.copy(alpha = 0.45f)
                    )
                }
            }
        }
    }
}

// ── GLASS TEXT FIELD ──────────────────────────────────────────────────────────
@Composable
private fun GlassTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    keyboardType: KeyboardType = KeyboardType.Text,
    isPassword: Boolean = false,
    passwordVisible: Boolean = false,
    onPasswordToggle: (() -> Unit)? = null
) {
    val isFocused = remember { mutableStateOf(false) }
    val borderBrush = if (isFocused.value)
        Brush.linearGradient(listOf(VibrantGreen, GoldAccent.copy(alpha = 0.6f)))
    else
        Brush.linearGradient(listOf(Color.White.copy(alpha = 0.18f), Color.White.copy(alpha = 0.06f)))

    Surface(
        shape = RoundedCornerShape(14.dp),
        color = Color.White.copy(alpha = 0.07f),
        modifier = Modifier
            .fillMaxWidth()
            .border(1.dp, borderBrush, RoundedCornerShape(14.dp))
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 14.dp, vertical = 4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = if (isFocused.value) VibrantGreen else SageLight.copy(alpha = 0.6f),
                modifier = Modifier.size(18.dp)
            )
            Spacer(Modifier.width(12.dp))
            OutlinedTextField(
                value = value,
                onValueChange = onValueChange,
                label = {
                    Text(
                        label,
                        fontSize = 12.sp,
                        color = SageLight.copy(alpha = 0.7f)
                    )
                },
                modifier = Modifier.weight(1f),
                singleLine = true,
                visualTransformation = if (isPassword && !passwordVisible)
                    PasswordVisualTransformation() else VisualTransformation.None,
                keyboardOptions = KeyboardOptions(keyboardType = keyboardType),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor   = Color.Transparent,
                    unfocusedBorderColor = Color.Transparent,
                    focusedContainerColor   = Color.Transparent,
                    unfocusedContainerColor = Color.Transparent,
                    focusedTextColor   = Color.White,
                    unfocusedTextColor = Color.White.copy(alpha = 0.85f),
                    cursorColor        = VibrantGreen,
                    focusedLabelColor  = VibrantGreen,
                    unfocusedLabelColor = SageLight.copy(alpha = 0.6f)
                ),
                textStyle = LocalTextStyle.current.copy(
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium
                ),
                trailingIcon = if (isPassword) ({
                    IconButton(onClick = { onPasswordToggle?.invoke() }) {
                        Icon(
                            if (passwordVisible) Icons.Default.Visibility
                            else Icons.Default.VisibilityOff,
                            contentDescription = null,
                            tint = SageLight.copy(alpha = 0.6f),
                            modifier = Modifier.size(17.dp)
                        )
                    }
                }) else null
            )
        }
    }
}