package com.example.bd_farmers.ui.auth

import android.app.Activity
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.*
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Login
import androidx.compose.material.icons.automirrored.filled.Message
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.*
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.bd_farmers.viewmodel.AuthViewModel
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthProvider
import kotlinx.coroutines.launch

// ── Colors ───────────────────────────────────────────────
private val ForestDeep   = Color(0xFF0D2B1A)
private val ForestMid    = Color(0xFF1B5E38)
private val VibrantGreen = Color(0xFF2ECC71)
private val SageLight    = Color(0xFF8FB99A)
private val GoldLight    = Color(0xFFF0C96E)

@Composable
fun AuthScreen(
    authViewModel: AuthViewModel,
    activity: Activity,
    onAuthSuccess: () -> Unit
) {
    var isLogin by remember { mutableStateOf(true) }
    var isOtpStage by remember { mutableStateOf(false) }
    val context = LocalContext.current

    val scope = rememberCoroutineScope()
    var identifier by remember { mutableStateOf("") } 
    var password by remember { mutableStateOf("") }
    var name by remember { mutableStateOf("") }
    var otp by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }

    val user by authViewModel.userState.collectAsState()
    LaunchedEffect(user) { if (user != null) onAuthSuccess() }

    val infiniteTransition = rememberInfiniteTransition(label = "orb_anim")
    val orbScale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1.18f,
        animationSpec = infiniteRepeatable(tween(3200, easing = EaseInOutSine), RepeatMode.Reverse),
        label = "orb_scale"
    )

    val gso = remember {
        GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken("190881125906-aik20n706ov58n8aftt889ruifjldgii.apps.googleusercontent.com")
            .requestEmail()
            .build()
    }
    val googleSignInClient = remember { GoogleSignIn.getClient(activity, gso) }
    val launcher = rememberLauncherForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
            try {
                val account = task.getResult(ApiException::class.java)!!
                authViewModel.loginWithGoogle(account.idToken!!)
            } catch (e: ApiException) {
                Toast.makeText(context, "Google Error: ${e.statusCode}", Toast.LENGTH_SHORT).show()
            }
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .drawBehind {
                drawRect(brush = Brush.verticalGradient(listOf(ForestDeep, Color(0xFF142E1E), Color(0xFF0A1F12))))
                drawCircle(brush = Brush.radialGradient(listOf(VibrantGreen.copy(alpha = 0.22f), Color.Transparent)),
                    radius = size.width * 0.7f * orbScale,
                    center = Offset(-size.width * 0.05f, size.height * 0.1f))
                drawCircle(brush = Brush.radialGradient(listOf(GoldLight.copy(alpha = 0.14f), Color.Transparent)),
                    radius = size.width * 0.55f,
                    center = Offset(size.width * 1.1f, size.height * 0.95f))
            }
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 28.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier.size(80.dp).clip(CircleShape).background(Color.White.copy(alpha = 0.08f)),
                contentAlignment = Alignment.Center
            ) { Text("🌾", fontSize = 38.sp) }
            Spacer(Modifier.height(16.dp))
            Text("BD Farmers", fontSize = 28.sp, fontWeight = FontWeight.ExtraBold, color = Color.White)
            Text("Farm to table, straight to you", fontSize = 12.sp, color = SageLight)
            Spacer(Modifier.height(32.dp))

            Surface(
                shape = RoundedCornerShape(28.dp),
                color = Color.White.copy(alpha = 0.08f),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(28.dp)) {
                    AuthTabSwitcher(isLogin, onSwitch = { 
                        isLogin = it 
                        isOtpStage = false 
                    })
                    Spacer(Modifier.height(28.dp))

                    AnimatedVisibility(visible = !isLogin) {
                        Column {
                            GlassTextField(value = name, onValueChange = { name = it }, label = "Full Name", icon = Icons.Default.Person)
                            Spacer(Modifier.height(14.dp))
                        }
                    }

                    GlassTextField(
                        value = identifier, 
                        onValueChange = { identifier = it }, 
                        label = "Email or Phone Number", 
                        icon = if (identifier.all { it.isDigit() || it == '+' }) Icons.Default.Phone else Icons.Default.Email,
                        keyboardType = if (identifier.all { it.isDigit() || it == '+' }) KeyboardType.Phone else KeyboardType.Email
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
                    
                    AnimatedVisibility(
                        visible = isOtpStage && !isLogin,
                        enter = expandVertically() + fadeIn(),
                        exit = shrinkVertically() + fadeOut()
                    ) {
                        Column {
                            Spacer(Modifier.height(14.dp))
                            Text("Verify Identity", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = Color.White)
                            Text("Enter fixed code 123456 for test numbers", fontSize = 11.sp, color = SageLight)
                            Spacer(Modifier.height(8.dp))
                            GlassTextField(value = otp, onValueChange = { if (it.length <= 6) otp = it }, label = "6-digit OTP", icon = Icons.Default.Key, keyboardType = KeyboardType.Number)
                        }
                    }
                    
                    Spacer(Modifier.height(28.dp))

                    MainAuthButton(
                        label = if (isLogin) "Sign In" else if (isOtpStage) "Verify & Register" else "Get OTP",
                        icon = if (isLogin) Icons.AutoMirrored.Filled.Login else if (isOtpStage) Icons.Default.CheckCircle else Icons.AutoMirrored.Filled.Message,
                        onClick = {
                            if (isLogin) {
                                // Simple bypass for specific testing
                                if ((identifier == "admin@gmail.com" || identifier == "017") && password == "123456") {
                                    onAuthSuccess()
                                } else if (identifier.contains("@")) {
                                    scope.launch { authViewModel.loginWithEmail(identifier, password) }
                                } else {
                                    Toast.makeText(context, "Use Email/Pass or Google", Toast.LENGTH_SHORT).show()
                                }
                            } else {
                                if (!isOtpStage) {
                                    if (identifier.isEmpty()) {
                                        Toast.makeText(context, "Enter phone for OTP", Toast.LENGTH_SHORT).show()
                                        return@MainAuthButton
                                    }
                                    val formattedPhone = if (identifier.startsWith("+")) identifier else "+88$identifier"
                                    authViewModel.sendOtp(formattedPhone, activity, object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
                                        override fun onVerificationCompleted(p0: PhoneAuthCredential) {
                                            authViewModel.signInWithCredential(p0)
                                        }
                                        override fun onVerificationFailed(e: com.google.firebase.FirebaseException) {
                                            Log.e("AuthScreen", "Error: ${e.message}")
                                            Toast.makeText(context, "Firebase Error: Check Console Billing/Test Numbers", Toast.LENGTH_LONG).show()
                                        }
                                        override fun onCodeSent(id: String, token: PhoneAuthProvider.ForceResendingToken) {
                                            isOtpStage = true
                                            Toast.makeText(context, "Check fixed test code", Toast.LENGTH_SHORT).show()
                                        }
                                    })
                                } else {
                                    if (otp.length == 6) authViewModel.verifyOtp(otp) 
                                    else Toast.makeText(context, "Enter 6 digits", Toast.LENGTH_SHORT).show()
                                }
                            }
                        }
                    )

                    Spacer(Modifier.height(16.dp))
                    Button(onClick = { launcher.launch(googleSignInClient.signInIntent) }, modifier = Modifier.fillMaxWidth(), colors = ButtonDefaults.buttonColors(containerColor = Color.White.copy(alpha = 0.1f)), shape = RoundedCornerShape(16.dp)) {
                        Icon(imageVector = Icons.Default.AccountCircle, contentDescription = null, tint = Color.White)
                        Spacer(Modifier.width(8.dp))
                        Text("Sign in with Google", color = Color.White)
                    }
                }
            }

            Spacer(Modifier.height(24.dp))
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center, verticalAlignment = Alignment.CenterVertically) {
                Text(text = if (isLogin) "New member? " else "Already a member? ", color = Color.White.copy(alpha = 0.55f), fontSize = 13.sp)
                Text(text = if (isLogin) "Register" else "Sign In", color = VibrantGreen, fontSize = 13.sp, fontWeight = FontWeight.ExtraBold, modifier = Modifier.clickable { isLogin = !isLogin })
            }
            Spacer(Modifier.height(32.dp))
        }
    }
}

@Composable
fun GlassTextField(value: String, onValueChange: (String) -> Unit, label: String, icon: ImageVector, keyboardType: KeyboardType = KeyboardType.Text, isPassword: Boolean = false, passwordVisible: Boolean = false, onPasswordToggle: (() -> Unit)? = null) {
    Surface(shape = RoundedCornerShape(14.dp), color = Color.White.copy(alpha = 0.07f), modifier = Modifier.fillMaxWidth().border(1.dp, Color.White.copy(alpha = 0.12f), RoundedCornerShape(14.dp))) {
        Row(modifier = Modifier.padding(horizontal = 14.dp, vertical = 4.dp), verticalAlignment = Alignment.CenterVertically) {
            Icon(imageVector = icon, contentDescription = null, tint = SageLight.copy(alpha = 0.6f), modifier = Modifier.size(18.dp))
            Spacer(Modifier.width(12.dp))
            OutlinedTextField(
                value = value, onValueChange = onValueChange, label = { Text(label, fontSize = 12.sp, color = SageLight.copy(alpha = 0.7f)) }, modifier = Modifier.weight(1f), singleLine = true,
                visualTransformation = if (isPassword && !passwordVisible) PasswordVisualTransformation() else VisualTransformation.None,
                keyboardOptions = KeyboardOptions(keyboardType = keyboardType),
                colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = Color.Transparent, unfocusedBorderColor = Color.Transparent, focusedContainerColor = Color.Transparent, unfocusedContainerColor = Color.Transparent, focusedTextColor = Color.White, unfocusedTextColor = Color.White.copy(alpha = 0.85f), cursorColor = VibrantGreen, focusedLabelColor = VibrantGreen, unfocusedLabelColor = SageLight.copy(alpha = 0.6f)),
                trailingIcon = if (isPassword && onPasswordToggle != null) { { IconButton(onClick = onPasswordToggle) { Icon(imageVector = if (passwordVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff, contentDescription = null, tint = SageLight.copy(alpha = 0.6f), modifier = Modifier.size(17.dp)) } } } else null
            )
        }
    }
}

@Composable
private fun AuthTabSwitcher(isLogin: Boolean, onSwitch: (Boolean) -> Unit) {
    Surface(shape = RoundedCornerShape(14.dp), color = Color.White.copy(alpha = 0.07f), modifier = Modifier.fillMaxWidth().border(0.8.dp, Color.White.copy(alpha = 0.12f), RoundedCornerShape(14.dp))) {
        Row(modifier = Modifier.padding(4.dp)) {
            listOf(true to "Sign In", false to "Register").forEach { (tab, label) ->
                val selected = isLogin == tab
                Box(modifier = Modifier.weight(1f).height(40.dp).clip(RoundedCornerShape(11.dp)).background(if (selected) Brush.horizontalGradient(listOf(VibrantGreen, ForestMid)) else Brush.horizontalGradient(listOf(Color.Transparent, Color.Transparent))).clickable { onSwitch(tab) }, contentAlignment = Alignment.Center) {
                    Text(text = label, fontSize = 13.sp, fontWeight = if (selected) FontWeight.Bold else FontWeight.Normal, color = if (selected) Color.White else Color.White.copy(alpha = 0.45f))
                }
            }
        }
    }
}

@Composable
private fun MainAuthButton(label: String, icon: ImageVector, onClick: () -> Unit) {
    Box(modifier = Modifier.fillMaxWidth().height(54.dp).clip(RoundedCornerShape(16.dp)).background(brush = Brush.horizontalGradient(listOf(VibrantGreen, ForestMid))).border(0.8.dp, VibrantGreen.copy(alpha = 0.5f), RoundedCornerShape(16.dp)).clickable { onClick() }, contentAlignment = Alignment.Center) {
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            Icon(imageVector = icon, contentDescription = null, tint = Color.White, modifier = Modifier.size(18.dp))
            Text(text = label, fontSize = 16.sp, fontWeight = FontWeight.ExtraBold, color = Color.White, letterSpacing = 0.4.sp)
        }
    }
}
