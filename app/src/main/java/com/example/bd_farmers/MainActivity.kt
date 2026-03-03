package com.example.bd_farmers

import android.graphics.Color
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.bd_farmers.data.repository.AuthRepository
import com.example.bd_farmers.ui.navigation.AppNavigation
import com.example.bd_farmers.ui.theme.BdFarmersTheme
import com.example.bd_farmers.viewmodel.AuthViewModel
import com.example.bd_farmers.viewmodel.ProductViewModel
import com.google.firebase.ktx.initialize

class MainActivity : ComponentActivity() {

    private val productViewModel: ProductViewModel by viewModels()
    
    private val authViewModel: AuthViewModel by viewModels {
        object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                @Suppress("UNCHECKED_CAST")
                return AuthViewModel(AuthRepository(applicationContext)) as T
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Initialize Firebase
        com.google.firebase.ktx.Firebase.initialize(this)

        enableEdgeToEdge(
            statusBarStyle = SystemBarStyle.auto(Color.TRANSPARENT, Color.TRANSPARENT),
            navigationBarStyle = SystemBarStyle.auto(Color.TRANSPARENT, Color.TRANSPARENT)
        )

        setContent {
            BdFarmersTheme {
                Surface(
                    modifier = Modifier.fillMaxSize()
                ) {
                    AppNavigation(
                        authViewModel = authViewModel,
                        productViewModel = productViewModel
                    )
                }
            }
        }
    }
}