package com.example.bd_farmers.viewmodel

import android.app.Activity
import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.bd_farmers.data.repository.AuthRepository
import com.google.firebase.auth.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class AuthViewModel(private val repo: AuthRepository) : ViewModel() {

    val userState: StateFlow<FirebaseUser?> = repo.tokenFlow
        .let { flow ->
            val state = MutableStateFlow<FirebaseUser?>(FirebaseAuth.getInstance().currentUser)
            viewModelScope.launch {
                flow.collect {
                    state.value = FirebaseAuth.getInstance().currentUser
                }
            }
            state
        }

    private var _verificationId: String? = null

    fun loginWithEmail(email: String, password: String) {
        viewModelScope.launch {
            repo.login(email, password)
        }
    }

    fun register(name: String, email: String, password: String) {
        viewModelScope.launch {
            repo.register(name, email, password)
        }
    }

    fun sendOtp(
        phone: String,
        activity: Activity,
        callbacks: PhoneAuthProvider.OnVerificationStateChangedCallbacks
    ) {
        val wrappedCallbacks = object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
            override fun onVerificationCompleted(credential: PhoneAuthCredential) {
                callbacks.onVerificationCompleted(credential)
            }

            override fun onVerificationFailed(e: com.google.firebase.FirebaseException) {
                callbacks.onVerificationFailed(e)
            }

            override fun onCodeSent(verificationId: String, token: PhoneAuthProvider.ForceResendingToken) {
                _verificationId = verificationId
                callbacks.onCodeSent(verificationId, token)
            }
        }
        repo.sendOtp(phone, activity, wrappedCallbacks)
    }

    fun verifyOtp(otp: String) {
        val verId = _verificationId ?: return
        viewModelScope.launch {
            repo.verifyOtp(verId, otp)
        }
    }

    fun signInWithCredential(credential: AuthCredential) {
        viewModelScope.launch {
            repo.signInWithCredential(credential)
        }
    }

    fun loginWithGoogle(idToken: String) {
        val credential = GoogleAuthProvider.getCredential(idToken, null)
        signInWithCredential(credential)
    }

    fun updateProfilePicture(uri: Uri) {
        val user = FirebaseAuth.getInstance().currentUser
        val profileUpdates = userProfileChangeRequest {
            photoUri = uri
        }

        viewModelScope.launch {
            try {
                user?.updateProfile(profileUpdates)?.await()
                // The AuthStateListener in Repository will trigger userState update
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun logout() {
        viewModelScope.launch {
            repo.logout()
        }
    }
}
