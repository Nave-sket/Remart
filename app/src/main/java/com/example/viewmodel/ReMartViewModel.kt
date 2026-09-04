package com.example.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.ReMartRepository
import com.example.model.CartItem
import com.example.model.Order
import com.example.model.Product
import com.example.model.ReMartConstants
import com.example.model.ResellerProfile
import com.example.model.WalletRequest
import com.example.model.WithdrawRequest
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class ReMartViewModel(application: Application) : AndroidViewModel(application) {

    val repository = ReMartRepository(application)

    // Products & Filter
    val allProducts: StateFlow<List<Product>> = repository.products
    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _selectedCategory = MutableStateFlow("all")
    val selectedCategory: StateFlow<String> = _selectedCategory.asStateFlow()

    val filteredProducts: StateFlow<List<Product>> = combine(
        allProducts,
        searchQuery,
        selectedCategory
    ) { products, query, cat ->
        products.filter { p ->
            val matchesCategory = when (cat) {
                "all" -> true
                "reseller" -> p.isResellerProduct
                else -> p.category.equals(cat, ignoreCase = true) || p.name.contains(cat, ignoreCase = true)
            }
            val matchesQuery = if (query.isBlank()) true else {
                p.name.contains(query, ignoreCase = true) ||
                        p.category.contains(query, ignoreCase = true)
            }
            matchesCategory && matchesQuery
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Cart & Checkout
    val cart: StateFlow<List<CartItem>> = repository.cart
    val cartCount: StateFlow<Int> = cart.map { items ->
        items.sumOf { it.quantity }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

    val cartTotal: StateFlow<Int> = cart.map { items ->
        items.sumOf { it.subtotal }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

    private val _isCartOpen = MutableStateFlow(false)
    val isCartOpen: StateFlow<Boolean> = _isCartOpen.asStateFlow()

    private val _lastPlacedOrder = MutableStateFlow<Order?>(null)
    val lastPlacedOrder: StateFlow<Order?> = _lastPlacedOrder.asStateFlow()

    // Reseller State
    val currentReseller: StateFlow<ResellerProfile?> = repository.currentReseller

    // Admin State
    val orders: StateFlow<List<Order>> = repository.orders
    val walletRequests: StateFlow<List<WalletRequest>> = repository.walletRequests
    val withdrawRequests: StateFlow<List<WithdrawRequest>> = repository.withdrawRequests

    private val _isAdminAuthenticated = MutableStateFlow(false)
    val isAdminAuthenticated: StateFlow<Boolean> = _isAdminAuthenticated.asStateFlow()

    private val _snackbarMessage = MutableStateFlow<String?>(null)
    val snackbarMessage: StateFlow<String?> = _snackbarMessage.asStateFlow()

    fun setSnackbarMessage(msg: String?) {
        _snackbarMessage.value = msg
    }

    fun setSearchQuery(query: String) {
        _searchQuery.value = query
    }

    fun setCategory(category: String) {
        _selectedCategory.value = category
    }

    fun openCart() {
        _isCartOpen.value = true
    }

    fun closeCart() {
        _isCartOpen.value = false
    }

    fun addToCart(product: Product) {
        repository.addToCart(product)
        _snackbarMessage.value = "Added '${product.name}' to Cart"
    }

    fun updateCartQuantity(productId: String, delta: Int) {
        repository.updateQuantity(productId, delta)
    }

    fun removeFromCart(productId: String) {
        repository.removeFromCart(productId)
    }

    fun placeOrder(name: String, phone: String, address: String, isCOD: Boolean) {
        if (name.isBlank() || phone.isBlank() || address.isBlank()) {
            _snackbarMessage.value = "Please fill in Naam, Phone, and Full Address"
            return
        }
        viewModelScope.launch {
            try {
                val order = repository.placeOrder(name, phone, address, isCOD)
                _lastPlacedOrder.value = order
                _isCartOpen.value = false
                _snackbarMessage.value = "Order placed successfully! ID: ${order.id}"
            } catch (e: Exception) {
                _snackbarMessage.value = e.message ?: "Failed to place order"
            }
        }
    }

    fun dismissOrderSuccess() {
        _lastPlacedOrder.value = null
    }

    // Reseller actions
    fun loginReseller(phone: String) {
        if (phone.length < 10) {
            _snackbarMessage.value = "Enter valid 10-digit phone number"
            return
        }
        repository.loginReseller(phone)
        _snackbarMessage.value = "Logged in as Reseller $phone"
    }

    fun logoutReseller() {
        repository.logoutReseller()
        _snackbarMessage.value = "Reseller logged out"
    }

    fun addMoneyToWallet(amount: Int, utr: String) {
        if (amount <= 0) {
            _snackbarMessage.value = "Enter a valid amount"
            return
        }
        if (utr.trim().length < 6) {
            _snackbarMessage.value = "Enter valid 12-digit UTR number"
            return
        }
        viewModelScope.launch {
            val success = repository.addMoneyToWallet(amount, utr.trim())
            if (success) {
                _snackbarMessage.value = "₹$amount added to wallet successfully!"
            }
        }
    }

    fun requestWithdraw(amount: Int) {
        viewModelScope.launch {
            val (success, msg) = repository.requestWithdraw(amount)
            _snackbarMessage.value = msg
        }
    }

    fun addProductByReseller(
        name: String,
        category: String,
        cost: Int,
        sell: Int,
        imageUrl: String
    ) {
        if (name.isBlank()) {
            _snackbarMessage.value = "Please enter product name"
            return
        }
        if (cost <= 0 || sell <= 0) {
            _snackbarMessage.value = "Please enter valid cost and selling price"
            return
        }
        if (sell <= cost) {
            _snackbarMessage.value = "Selling price must be higher than original cost"
            return
        }
        viewModelScope.launch {
            val success = repository.addProductByReseller(name, category, cost, sell, imageUrl)
            if (success) {
                _snackbarMessage.value = "Product '$name' listed live on ReMart!"
            }
        }
    }

    // Admin actions
    fun loginAdmin(pin: String): Boolean {
        if (pin == ReMartConstants.ADMIN_PIN) {
            _isAdminAuthenticated.value = true
            _snackbarMessage.value = "Welcome Admin Owner!"
            return true
        } else {
            _snackbarMessage.value = "Incorrect PIN! Access denied."
            return false
        }
    }

    fun logoutAdmin() {
        _isAdminAuthenticated.value = false
    }

    fun markWithdrawPaid(id: String) {
        viewModelScope.launch {
            repository.markWithdrawPaid(id)
            _snackbarMessage.value = "Marked as PAID! UPI sent from ${ReMartConstants.ADMIN_UPI_ID}"
        }
    }

    fun updateOrderStatus(orderId: String, status: String) {
        viewModelScope.launch {
            repository.updateOrderStatus(orderId, status)
            _snackbarMessage.value = "Order $orderId status updated to $status"
        }
    }
}
