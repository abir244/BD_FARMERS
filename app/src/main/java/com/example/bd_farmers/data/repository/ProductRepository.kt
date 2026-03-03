package com.example.bd_farmers.data.repository

import com.example.bd_farmers.R
import com.example.bd_farmers.data.model.Category
import com.example.bd_farmers.data.model.Product

object ProductRepository {

    fun getCategories(): List<Category> = listOf(
        Category(1, "Fruits", R.drawable.ic_fruits),
        Category(2, "Grains", R.drawable.ic_grains),
        Category(3, "Herbs", R.drawable.ic_herbs),
        Category(4, "Vegetables", R.drawable.ic_vegetables)
    )

    fun getProducts(): List<Product> = listOf(
        Product(1, "Berries",    500, 4.5f, 672,  "Fruits",     "https://images.unsplash.com/photo-1464960350423-9f85a505b822?q=80&w=2070&auto=format&fit=crop"),
        Product(2, "Tulsi",      100, 4.9f, 324,  "Herbs",      "https://images.unsplash.com/photo-1615485290382-441e4d0c9cb5?q=80&w=2070&auto=format&fit=crop"),
        Product(3, "Milk",        70, 4.9f, 560,  "Dairy",      "https://images.unsplash.com/photo-1563636619-e9107da5a76a?q=80&w=1964&auto=format&fit=crop"),
        Product(4, "Tomatos",     50, 4.7f, 874,  "Vegetables", "https://images.unsplash.com/photo-1518977676601-b53f02bad6d5?q=80&w=2070&auto=format&fit=crop"),
        Product(5, "Wheat",      800, 4.9f, 526,  "Grains",     "https://images.unsplash.com/photo-1574323347407-f5e1ad6d020b?q=80&w=1974&auto=format&fit=crop"),
        Product(6, "Apples",     120, 4.2f, 458,  "Fruits",     "https://images.unsplash.com/photo-1560806887-1e4cd0b6bcd6?q=80&w=1974&auto=format&fit=crop"),
        Product(7, "Pomegranate",100, 4.8f, 310,  "Fruits",     "https://images.unsplash.com/photo-1541344999736-83eca872977a?q=80&w=2070&auto=format&fit=crop")
    )

    fun getProductsByCategory(category: String): List<Product> =
        getProducts().filter { it.category.equals(category, ignoreCase = true) }

    fun getFeaturedProducts(): List<Product> = getProducts().take(4)
}