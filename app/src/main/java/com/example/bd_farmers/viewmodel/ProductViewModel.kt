package com.example.bd_farmers.viewmodel

import androidx.lifecycle.ViewModel
import com.example.bd_farmers.data.model.Category
import com.example.bd_farmers.data.model.Product
import com.example.bd_farmers.data.model.CartItem
import com.example.bd_farmers.data.repository.ProductRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class ProductViewModel : ViewModel() {
    private val repository = ProductRepository

    private val _products = MutableStateFlow<List<Product>>(emptyList())
    val products: StateFlow<List<Product>> = _products.asStateFlow()

    private val _categories = MutableStateFlow<List<Category>>(emptyList())
    val categories: StateFlow<List<Category>> = _categories.asStateFlow()

    private val _cartItems = MutableStateFlow<List<CartItem>>(emptyList())
    val cartItems: StateFlow<List<CartItem>> = _cartItems.asStateFlow()

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _selectedCategory = MutableStateFlow<Category?>(null)
    val selectedCategory: StateFlow<Category?> = _selectedCategory.asStateFlow()

    private val _favorites = MutableStateFlow<Set<Int>>(emptySet())
    val favorites: StateFlow<Set<Int>> = _favorites.asStateFlow()

    init {
        loadData()
    }

    private fun loadData() {
        _products.value = repository.getProducts()
        _categories.value = repository.getCategories()
    }

    fun onSearchQueryChange(query: String) {
        _searchQuery.value = query
    }

    fun onCategorySelected(category: Category?) {
        _selectedCategory.value = if (_selectedCategory.value == category) null else category
    }

    fun addToCart(product: Product) {
        val currentList = _cartItems.value.toMutableList()
        val existingItem = currentList.find { it.product.id == product.id }
        if (existingItem != null) {
            val index = currentList.indexOf(existingItem)
            currentList[index] = existingItem.copy(quantity = existingItem.quantity + 1)
        } else {
            currentList.add(CartItem(product, 1))
        }
        _cartItems.value = currentList
    }

    fun removeFromCart(productId: Int) {
        val currentList = _cartItems.value.toMutableList()
        val existingItem = currentList.find { it.product.id == productId }
        if (existingItem != null) {
            val index = currentList.indexOf(existingItem)
            if (existingItem.quantity > 1) {
                currentList[index] = existingItem.copy(quantity = existingItem.quantity - 1)
            } else {
                currentList.removeAt(index)
            }
        }
        _cartItems.value = currentList
    }

    fun deleteFromCart(productId: Int) {
        _cartItems.value = _cartItems.value.filter { it.product.id != productId }
    }

    fun toggleFavorite(productId: Int) {
        val currentFavorites = _favorites.value.toMutableSet()
        if (currentFavorites.contains(productId)) {
            currentFavorites.remove(productId)
        } else {
            currentFavorites.add(productId)
        }
        _favorites.value = currentFavorites
    }

    fun filteredProducts(): List<Product> {
        return _products.value.filter { product ->
            (selectedCategory.value == null || product.category == selectedCategory.value?.name) &&
            (searchQuery.value.isEmpty() || product.name.contains(searchQuery.value, ignoreCase = true))
        }
    }

    val deliveryFee = 40

    fun cartSubtotal(): Int {
        return _cartItems.value.sumOf { it.product.price * it.quantity }
    }

    fun cartGst(): Int {
        return (cartSubtotal() * 0.05).toInt()
    }

    fun cartTotal(): Int {
        return cartSubtotal() + deliveryFee + cartGst()
    }
}