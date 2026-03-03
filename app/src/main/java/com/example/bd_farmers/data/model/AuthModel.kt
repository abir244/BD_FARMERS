package com.example.bd_farmers.data.model

data class LoginRequest(
    val email: String,
    val password: String
)

data class RegisterRequest(
    val name: String,
    val email: String,
    val password: String
)

data class AuthResponse(
    val token: String,
    val user: User? = null
)

data class User(
    val id: String,
    val name: String,
    val email: String
)
