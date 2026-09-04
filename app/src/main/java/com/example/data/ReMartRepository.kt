package com.example.data

import android.content.ContentValues
import android.content.Context
import com.example.model.CartItem
import com.example.model.Order
import com.example.model.Product
import com.example.model.ReMartConstants
import com.example.model.ResellerProfile
import com.example.model.WalletRequest
import com.example.model.WithdrawRequest
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.UUID

class ReMartRepository(context: Context) {

    private val dbHelper = ReMartDatabaseHelper(context)
    private val prefs = context.getSharedPreferences("remart_prefs", Context.MODE_PRIVATE)
    private val coroutineScope = CoroutineScope(Dispatchers.IO)

    private val _products = MutableStateFlow<List<Product>>(emptyList())
    val products: StateFlow<List<Product>> = _products.asStateFlow()

    private val _cart = MutableStateFlow<List<CartItem>>(emptyList())
    val cart: StateFlow<List<CartItem>> = _cart.asStateFlow()

    private val _orders = MutableStateFlow<List<Order>>(emptyList())
    val orders: StateFlow<List<Order>> = _orders.asStateFlow()

    private val _walletRequests = MutableStateFlow<List<WalletRequest>>(emptyList())
    val walletRequests: StateFlow<List<WalletRequest>> = _walletRequests.asStateFlow()

    private val _withdrawRequests = MutableStateFlow<List<WithdrawRequest>>(emptyList())
    val withdrawRequests: StateFlow<List<WithdrawRequest>> = _withdrawRequests.asStateFlow()

    private val _currentReseller = MutableStateFlow<ResellerProfile?>(null)
    val currentReseller: StateFlow<ResellerProfile?> = _currentReseller.asStateFlow()

    init {
        val savedPhone = prefs.getString("reseller_phone", null)
        if (!savedPhone.isNullOrBlank()) {
            _currentReseller.value = ResellerProfile(savedPhone, getResellerWallet(savedPhone))
        }
        refreshAll()
    }

    private fun refreshAll() {
        coroutineScope.launch {
            loadProducts()
            loadOrders()
            loadWalletRequests()
            loadWithdrawRequests()
            _currentReseller.value?.let { current ->
                val balance = getResellerWallet(current.phone)
                _currentReseller.value = current.copy(walletBalance = balance)
            }
        }
    }

    private suspend fun loadProducts() = withContext(Dispatchers.IO) {
        val list = mutableListOf<Product>()
        val db = dbHelper.readableDatabase
        val cursor = db.rawQuery("SELECT * FROM products ORDER BY is_reseller DESC, id DESC", null)
        cursor.use { c ->
            while (c.moveToNext()) {
                list.add(
                    Product(
                        id = c.getString(c.getColumnIndexOrThrow("id")),
                        name = c.getString(c.getColumnIndexOrThrow("name")),
                        category = c.getString(c.getColumnIndexOrThrow("category")),
                        sellingPrice = c.getInt(c.getColumnIndexOrThrow("selling_price")),
                        originalCost = c.getInt(c.getColumnIndexOrThrow("original_cost")),
                        imageUrl = c.getString(c.getColumnIndexOrThrow("image_url")),
                        isResellerProduct = c.getInt(c.getColumnIndexOrThrow("is_reseller")) == 1,
                        resellerPhone = c.getString(c.getColumnIndexOrThrow("reseller_phone")),
                        rating = c.getFloat(c.getColumnIndexOrThrow("rating")),
                        ratingCount = c.getInt(c.getColumnIndexOrThrow("rating_count"))
                    )
                )
            }
        }
        _products.value = list
    }

    private suspend fun loadOrders() = withContext(Dispatchers.IO) {
        val list = mutableListOf<Order>()
        val db = dbHelper.readableDatabase
        val cursor = db.rawQuery("SELECT * FROM orders ORDER BY created_at DESC", null)
        cursor.use { c ->
            while (c.moveToNext()) {
                list.add(
                    Order(
                        id = c.getString(c.getColumnIndexOrThrow("id")),
                        customerName = c.getString(c.getColumnIndexOrThrow("customer_name")),
                        phone = c.getString(c.getColumnIndexOrThrow("phone")),
                        address = c.getString(c.getColumnIndexOrThrow("address")),
                        itemsSummary = c.getString(c.getColumnIndexOrThrow("items_summary")),
                        total = c.getInt(c.getColumnIndexOrThrow("total")),
                        platformFee = c.getInt(c.getColumnIndexOrThrow("platform_fee")),
                        paymentMode = c.getString(c.getColumnIndexOrThrow("payment_mode")),
                        status = c.getString(c.getColumnIndexOrThrow("status")),
                        createdAt = c.getLong(c.getColumnIndexOrThrow("created_at"))
                    )
                )
            }
        }
        _orders.value = list
    }

    private suspend fun loadWalletRequests() = withContext(Dispatchers.IO) {
        val list = mutableListOf<WalletRequest>()
        val db = dbHelper.readableDatabase
        val cursor = db.rawQuery("SELECT * FROM wallet_requests ORDER BY created_at DESC", null)
        cursor.use { c ->
            while (c.moveToNext()) {
                list.add(
                    WalletRequest(
                        id = c.getString(c.getColumnIndexOrThrow("id")),
                        phone = c.getString(c.getColumnIndexOrThrow("phone")),
                        amount = c.getInt(c.getColumnIndexOrThrow("amount")),
                        utr = c.getString(c.getColumnIndexOrThrow("utr")),
                        status = c.getString(c.getColumnIndexOrThrow("status")),
                        createdAt = c.getLong(c.getColumnIndexOrThrow("created_at"))
                    )
                )
            }
        }
        _walletRequests.value = list
    }

    private suspend fun loadWithdrawRequests() = withContext(Dispatchers.IO) {
        val list = mutableListOf<WithdrawRequest>()
        val db = dbHelper.readableDatabase
        val cursor = db.rawQuery("SELECT * FROM withdraw_requests ORDER BY created_at DESC", null)
        cursor.use { c ->
            while (c.moveToNext()) {
                list.add(
                    WithdrawRequest(
                        id = c.getString(c.getColumnIndexOrThrow("id")),
                        phone = c.getString(c.getColumnIndexOrThrow("phone")),
                        amount = c.getInt(c.getColumnIndexOrThrow("amount")),
                        status = c.getString(c.getColumnIndexOrThrow("status")),
                        createdAt = c.getLong(c.getColumnIndexOrThrow("created_at"))
                    )
                )
            }
        }
        _withdrawRequests.value = list
    }

    private fun getResellerWallet(phone: String): Int {
        val db = dbHelper.readableDatabase
        val cursor = db.rawQuery("SELECT wallet_balance FROM resellers WHERE phone = ?", arrayOf(phone))
        cursor.use {
            if (it.moveToFirst()) {
                return it.getInt(0)
            }
        }
        return 0
    }

    // Cart Management
    fun addToCart(product: Product) {
        val current = _cart.value.toMutableList()
        val index = current.indexOfFirst { it.product.id == product.id }
        if (index != -1) {
            val existing = current[index]
            current[index] = existing.copy(quantity = existing.quantity + 1)
        } else {
            current.add(CartItem(product = product, quantity = 1))
        }
        _cart.value = current
    }

    fun updateQuantity(productId: String, delta: Int) {
        val current = _cart.value.toMutableList()
        val index = current.indexOfFirst { it.product.id == productId }
        if (index != -1) {
            val updatedQty = current[index].quantity + delta
            if (updatedQty <= 0) {
                current.removeAt(index)
            } else {
                current[index] = current[index].copy(quantity = updatedQty)
            }
            _cart.value = current
        }
    }

    fun removeFromCart(productId: String) {
        _cart.value = _cart.value.filter { it.product.id != productId }
    }

    fun clearCart() {
        _cart.value = emptyList()
    }

    // Order Placement
    suspend fun placeOrder(
        customerName: String,
        phone: String,
        address: String,
        isCOD: Boolean
    ): Order = withContext(Dispatchers.IO) {
        val cartList = _cart.value
        require(cartList.isNotEmpty()) { "Cart is empty" }

        val totalAmount = cartList.sumOf { it.subtotal }
        // 30% platform fee / admin profit or 49 flat + 5%
        val totalCost = cartList.sumOf { it.product.originalCost * it.quantity }
        val grossMargin = (totalAmount - totalCost).coerceAtLeast(0)
        val platformFee = if (grossMargin > 0) {
            (grossMargin * ReMartConstants.ADMIN_PROFIT_SHARE).toInt()
        } else {
            ReMartConstants.COMMISSION_FLAT + (totalAmount * ReMartConstants.COMMISSION_PERCENT / 100)
        }

        val summary = cartList.joinToString(", ") { "${it.product.name} (x${it.quantity})" }
        val order = Order(
            id = "ORD-" + (1000 + (Math.random() * 9000).toInt()),
            customerName = customerName.trim(),
            phone = phone.trim(),
            address = address.trim(),
            itemsSummary = summary,
            total = totalAmount,
            platformFee = platformFee,
            paymentMode = if (isCOD) "Cash on Delivery" else "UPI / Online Pay",
            status = "CONFIRMED",
            createdAt = System.currentTimeMillis()
        )

        val db = dbHelper.writableDatabase
        val cv = ContentValues().apply {
            put("id", order.id)
            put("customer_name", order.customerName)
            put("phone", order.phone)
            put("address", order.address)
            put("items_summary", order.itemsSummary)
            put("total", order.total)
            put("platform_fee", order.platformFee)
            put("payment_mode", order.paymentMode)
            put("status", order.status)
            put("created_at", order.createdAt)
        }
        db.insert("orders", null, cv)

        // If products belong to a reseller, credit their 70% share to their wallet
        for (item in cartList) {
            if (item.product.isResellerProduct && item.product.resellerPhone.isNotBlank()) {
                val itemMargin = ((item.product.sellingPrice - item.product.originalCost) * item.quantity).coerceAtLeast(0)
                val resellerShare = (itemMargin * ReMartConstants.RESELLER_PROFIT_SHARE).toInt()
                if (resellerShare > 0) {
                    creditResellerWallet(item.product.resellerPhone, resellerShare)
                }
            }
        }

        clearCart()
        loadOrders()
        order
    }

    private fun creditResellerWallet(phone: String, amount: Int) {
        val db = dbHelper.writableDatabase
        val currentBalance = getResellerWallet(phone)
        val newBalance = currentBalance + amount
        val cv = ContentValues().apply {
            put("phone", phone)
            put("wallet_balance", newBalance)
        }
        db.insertWithOnConflict("resellers", null, cv, android.database.sqlite.SQLiteDatabase.CONFLICT_REPLACE)
    }

    // Reseller Authentication & Operations
    fun loginReseller(phone: String) {
        prefs.edit().putString("reseller_phone", phone).apply()
        val balance = getResellerWallet(phone)
        _currentReseller.value = ResellerProfile(phone, balance)
    }

    fun logoutReseller() {
        prefs.edit().remove("reseller_phone").apply()
        _currentReseller.value = null
    }

    suspend fun addMoneyToWallet(amount: Int, utr: String): Boolean = withContext(Dispatchers.IO) {
        val current = _currentReseller.value ?: return@withContext false
        val db = dbHelper.writableDatabase

        // Record request
        val requestId = UUID.randomUUID().toString().take(8).uppercase()
        val cvReq = ContentValues().apply {
            put("id", requestId)
            put("phone", current.phone)
            put("amount", amount)
            put("utr", utr)
            put("status", "PAID")
            put("created_at", System.currentTimeMillis())
        }
        db.insert("wallet_requests", null, cvReq)

        // Update reseller wallet
        creditResellerWallet(current.phone, amount)

        val updatedBalance = getResellerWallet(current.phone)
        _currentReseller.value = current.copy(walletBalance = updatedBalance)
        loadWalletRequests()
        true
    }

    suspend fun requestWithdraw(amount: Int): Pair<Boolean, String> = withContext(Dispatchers.IO) {
        val current = _currentReseller.value ?: return@withContext Pair(false, "Please log in first")
        if (amount < 100) {
            return@withContext Pair(false, "Minimum withdrawal is ₹100")
        }
        if (current.walletBalance < amount) {
            return@withContext Pair(false, "Insufficient wallet balance (₹${current.walletBalance})")
        }

        val db = dbHelper.writableDatabase
        // Deduct from wallet
        val newBalance = current.walletBalance - amount
        val cvRes = ContentValues().apply {
            put("phone", current.phone)
            put("wallet_balance", newBalance)
        }
        db.update("resellers", cvRes, "phone = ?", arrayOf(current.phone))

        // Create withdraw request
        val reqId = "WD-" + (1000 + (Math.random() * 9000).toInt())
        val cvReq = ContentValues().apply {
            put("id", reqId)
            put("phone", current.phone)
            put("amount", amount)
            put("status", "PENDING")
            put("created_at", System.currentTimeMillis())
        }
        db.insert("withdraw_requests", null, cvReq)

        _currentReseller.value = current.copy(walletBalance = newBalance)
        loadWithdrawRequests()
        Pair(true, "Withdrawal request submitted for ₹$amount. Admin will transfer via UPI within 24h.")
    }

    suspend fun addProductByReseller(
        name: String,
        category: String,
        originalCost: Int,
        sellingPrice: Int,
        imageUrl: String
    ): Boolean = withContext(Dispatchers.IO) {
        val current = _currentReseller.value ?: return@withContext false
        val db = dbHelper.writableDatabase
        val id = (System.currentTimeMillis() % 1000000).toString()
        val effectiveImg = if (imageUrl.isNotBlank()) imageUrl else "https://images.unsplash.com/photo-1523275335684-37898b6baf30?w=600"
        val cv = ContentValues().apply {
            put("id", id)
            put("name", name.trim())
            put("category", category.trim().lowercase())
            put("selling_price", sellingPrice)
            put("original_cost", originalCost)
            put("image_url", effectiveImg)
            put("is_reseller", 1)
            put("reseller_phone", current.phone)
            put("rating", 4.3f)
            put("rating_count", 1)
        }
        db.insert("products", null, cv)
        loadProducts()
        true
    }

    // Admin Operations
    suspend fun markWithdrawPaid(requestId: String) = withContext(Dispatchers.IO) {
        val db = dbHelper.writableDatabase
        val cv = ContentValues().apply {
            put("status", "PAID")
        }
        db.update("withdraw_requests", cv, "id = ?", arrayOf(requestId))
        loadWithdrawRequests()
    }

    suspend fun updateOrderStatus(orderId: String, newStatus: String) = withContext(Dispatchers.IO) {
        val db = dbHelper.writableDatabase
        val cv = ContentValues().apply {
            put("status", newStatus)
        }
        db.update("orders", cv, "id = ?", arrayOf(orderId))
        loadOrders()
    }
}
