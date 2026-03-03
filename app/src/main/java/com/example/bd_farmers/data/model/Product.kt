package com.example.bd_farmers.data.model

data class Product(
    val id: Int,
    val name: String,
    val price: Int,
    val rating: Float,
    val reviewCount: Int,
    val category: String,
    val imageUrl: String,
    val isFavorite: Boolean = false
)