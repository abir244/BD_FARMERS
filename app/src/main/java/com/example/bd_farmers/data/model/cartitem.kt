package com.example.bd_farmers.data.model

data class CartItem(
    val product: Product,
    val quantity: Int = 1
)