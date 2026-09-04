package com.example.model

data class Product(
    val id: String,
    val name: String,
    val category: String,
    val sellingPrice: Int,
    val originalCost: Int,
    val imageUrl: String,
    val isResellerProduct: Boolean = false,
    val resellerPhone: String = "",
    val rating: Float = 4.2f,
    val ratingCount: Int = 128
) {
    val mrpPrice: Int
        get() = sellingPrice + 300

    val discountPercent: Int
        get() = if (mrpPrice > 0) ((mrpPrice - sellingPrice) * 100) / mrpPrice else 0
}

data class CartItem(
    val product: Product,
    val quantity: Int = 1
) {
    val subtotal: Int
        get() = product.sellingPrice * quantity
}

data class Order(
    val id: String,
    val customerName: String,
    val phone: String,
    val address: String,
    val itemsSummary: String,
    val total: Int,
    val platformFee: Int,
    val paymentMode: String, // "ONLINE" or "COD"
    val status: String = "CONFIRMED", // "CONFIRMED", "SHIPPED", "DELIVERED"
    val createdAt: Long = System.currentTimeMillis()
)

data class WalletRequest(
    val id: String,
    val phone: String,
    val amount: Int,
    val utr: String,
    val status: String = "VERIFIED",
    val createdAt: Long = System.currentTimeMillis()
)

data class WithdrawRequest(
    val id: String,
    val phone: String,
    val amount: Int,
    val status: String = "PENDING", // "PENDING", "PAID"
    val createdAt: Long = System.currentTimeMillis()
)

data class ResellerProfile(
    val phone: String,
    val walletBalance: Int = 0
)

object ReMartConstants {
    const val ADMIN_PIN = "1234"
    const val ADMIN_UPI_ID = "8368322869@ybl"
    const val RESELLER_PROFIT_SHARE = 0.70
    const val ADMIN_PROFIT_SHARE = 0.30
    const val COMMISSION_FLAT = 49
    const val COMMISSION_PERCENT = 5
}
