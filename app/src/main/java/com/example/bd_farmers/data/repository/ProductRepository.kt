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
        Product(1, "Berries",    500, 4.5f, 672,  "Fruits",     "https://images.unsplash.com/photo-1544052819-282c286dfad6?q=80&w=2070&auto=format&fit=crop"),
        Product(2, "Tulsi",      100, 4.9f, 324,  "Herbs",      "https://images.unsplash.com/photo-1615485290382-441e4d0c9cb5?q=80&w=2070&auto=format&fit=crop"),
        Product(3, "Milk",        70, 4.9f, 560,  "Dairy",      "https://images.unsplash.com/photo-1550583724-125581f77833?q=80&w=1974&auto=format&fit=crop"),
        Product(4, "Tomatos",     50, 4.7f, 874,  "Vegetables", "https://images.unsplash.com/photo-1592924357228-91a4daadcfea?q=80&w=1974&auto=format&fit=crop"),
        Product(5, "Wheat",      800, 4.9f, 526,  "Grains",     "https://images.unsplash.com/photo-1574323347407-f5e1ad6d020b?q=80&w=1974&auto=format&fit=crop"),
        Product(6, "Apples",     120, 4.2f, 458,  "Fruits",     "https://images.unsplash.com/photo-1567306226416-28f0efdc88ce?q=80&w=2070&auto=format&fit=crop"),
        Product(7, "Pomegranate",100, 4.8f, 310,  "Fruits",     "https://images.unsplash.com/photo-1618897996318-5a901fa6ca71?q=80&w=1964&auto=format&fit=crop")
    )

    fun getProductsByCategory(category: String): List<Product> =
        getProducts().filter { it.category.equals(category, ignoreCase = true) }

    fun getFeaturedProducts(): List<Product> = getProducts().take(4)
}