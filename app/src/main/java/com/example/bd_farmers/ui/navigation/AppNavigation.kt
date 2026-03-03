package com.example.bd_farmers.ui.navigation

import android.app.Activity
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.compose.*
import com.example.bd_farmers.ui.auth.AuthScreen
import com.example.bd_farmers.ui.auth.SplashScreen
import com.example.bd_farmers.ui.cart.CartScreen
import com.example.bd_farmers.ui.checkout.CheckoutScreen
import com.example.bd_farmers.ui.components.BottomNavBar
import com.example.bd_farmers.ui.explore.ExploreScreen
import com.example.bd_farmers.ui.home.HomeScreen
import com.example.bd_farmers.ui.profile.ProfileScreen
import com.example.bd_farmers.viewmodel.AuthViewModel
import com.example.bd_farmers.viewmodel.ProductViewModel
import kotlinx.coroutines.launch

@Composable
fun AppNavigation(
    authViewModel: AuthViewModel,
    productViewModel: ProductViewModel
) {
    val navController = rememberNavController()
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()
    val cartItems by productViewModel.cartItems.collectAsState()

    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route ?: "splash"

    val showBottomNav = currentRoute in listOf("home", "explore", "cart", "profile")

    // Observe login state
    val user by authViewModel.userState.collectAsState()
    
    val context = LocalContext.current
    val activity = context as Activity

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        snackbarHost = { SnackbarHost(snackbarHostState) },
        containerColor = MaterialTheme.colorScheme.background
    ) { innerPadding ->
        Box(modifier = Modifier.fillMaxSize()) {
            NavHost(
                navController = navController,
                startDestination = "splash",
                modifier = Modifier.padding(top = innerPadding.calculateTopPadding())
            ) {
                composable("splash") {
                    SplashScreen(onAnimationFinished = {
                        val nextDest = if (user == null) "auth" else "home"
                        navController.navigate(nextDest) {
                            popUpTo("splash") { inclusive = true }
                        }
                    })
                }

                composable("auth") {
                    AuthScreen(
                        authViewModel = authViewModel,
                        activity = activity,
                        onAuthSuccess = {
                            scope.launch { snackbarHostState.showSnackbar("Login successful") }
                            navController.navigate("home") {
                                popUpTo("auth") { inclusive = true }
                            }
                        }
                    )
                }

                composable("home") {
                    HomeScreen(
                        viewModel = productViewModel,
                        onNavigateToExplore = { navController.navigate("explore") }
                    )
                }

                composable("explore") { ExploreScreen(productViewModel) }

                composable("cart") {
                    CartScreen(
                        viewModel = productViewModel,
                        onProceedToCheckout = { navController.navigate("checkout") },
                        onNavigateHome = { navController.navigate("home") }
                    )
                }

                composable("checkout") {
                    CheckoutScreen(
                        viewModel = productViewModel,
                        onBack = { navController.popBackStack() },
                        onOrderPlaced = {
                            scope.launch {
                                snackbarHostState.showSnackbar("🎉 Order placed successfully!")
                            }
                            navController.navigate("home") {
                                popUpTo("home") { inclusive = true }
                            }
                        }
                    )
                }

                composable("profile") {
                    ProfileScreen(
                        authViewModel = authViewModel,
                        onLogout = {
                            authViewModel.logout()
                            navController.navigate("auth") {
                                popUpTo(0) { inclusive = true }
                            }
                        }
                    )
                }
            }

            if (showBottomNav) {
                Box(modifier = Modifier.align(Alignment.BottomCenter)) {
                    BottomNavBar(
                        currentRoute = currentRoute,
                        onNavigate = { route ->
                            navController.navigate(route) {
                                popUpTo("home") { saveState = true }
                                launchSingleTop = true
                                restoreState = true
                            }
                        },
                        cartCount = cartItems.sumOf { it.quantity }
                    )
                }
            }
        }
    }
}