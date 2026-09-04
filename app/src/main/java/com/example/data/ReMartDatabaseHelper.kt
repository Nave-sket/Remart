package com.example.data

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import com.example.model.Order
import com.example.model.Product
import com.example.model.WalletRequest
import com.example.model.WithdrawRequest

class ReMartDatabaseHelper(context: Context) :
    SQLiteOpenHelper(context, "remart.db", null, 1) {

    override fun onCreate(db: SQLiteDatabase) {
        // Products table
        db.execSQL(
            """
            CREATE TABLE products (
                id TEXT PRIMARY KEY,
                name TEXT NOT NULL,
                category TEXT NOT NULL,
                selling_price INTEGER NOT NULL,
                original_cost INTEGER NOT NULL,
                image_url TEXT NOT NULL,
                is_reseller INTEGER NOT NULL,
                reseller_phone TEXT NOT NULL,
                rating REAL NOT NULL,
                rating_count INTEGER NOT NULL
            )
            """.trimIndent()
        )

        // Orders table
        db.execSQL(
            """
            CREATE TABLE orders (
                id TEXT PRIMARY KEY,
                customer_name TEXT NOT NULL,
                phone TEXT NOT NULL,
                address TEXT NOT NULL,
                items_summary TEXT NOT NULL,
                total INTEGER NOT NULL,
                platform_fee INTEGER NOT NULL,
                payment_mode TEXT NOT NULL,
                status TEXT NOT NULL,
                created_at INTEGER NOT NULL
            )
            """.trimIndent()
        )

        // Wallet Requests table
        db.execSQL(
            """
            CREATE TABLE wallet_requests (
                id TEXT PRIMARY KEY,
                phone TEXT NOT NULL,
                amount INTEGER NOT NULL,
                utr TEXT NOT NULL,
                status TEXT NOT NULL,
                created_at INTEGER NOT NULL
            )
            """.trimIndent()
        )

        // Withdraw Requests table
        db.execSQL(
            """
            CREATE TABLE withdraw_requests (
                id TEXT PRIMARY KEY,
                phone TEXT NOT NULL,
                amount INTEGER NOT NULL,
                status TEXT NOT NULL,
                created_at INTEGER NOT NULL
            )
            """.trimIndent()
        )

        // Resellers table
        db.execSQL(
            """
            CREATE TABLE resellers (
                phone TEXT PRIMARY KEY,
                wallet_balance INTEGER NOT NULL
            )
            """.trimIndent()
        )

        // Seed initial products from original repo
        seedInitialProducts(db)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS products")
        db.execSQL("DROP TABLE IF EXISTS orders")
        db.execSQL("DROP TABLE IF EXISTS wallet_requests")
        db.execSQL("DROP TABLE IF EXISTS withdraw_requests")
        db.execSQL("DROP TABLE IF EXISTS resellers")
        onCreate(db)
    }

    private fun seedInitialProducts(db: SQLiteDatabase) {
        val initialList = listOf(
            Product(
                id = "1",
                name = "Georgette Embroidered Saree",
                category = "saree",
                sellingPrice = 499,
                originalCost = 280,
                imageUrl = "https://images.unsplash.com/photo-1610030469983-98e550d6193c?w=600",
                isResellerProduct = false,
                rating = 4.4f,
                ratingCount = 340
            ),
            Product(
                id = "2",
                name = "Men Stylish Festive Kurta",
                category = "kurta",
                sellingPrice = 399,
                originalCost = 220,
                imageUrl = "https://images.unsplash.com/photo-1596755094514-f87e34085b2c?w=600",
                isResellerProduct = false,
                rating = 4.3f,
                ratingCount = 210
            ),
            Product(
                id = "3",
                name = "Wireless Earbuds Pro TWS",
                category = "earbuds",
                sellingPrice = 799,
                originalCost = 450,
                imageUrl = "https://images.unsplash.com/photo-1590658268037-6bf12165a8df?w=600",
                isResellerProduct = false,
                rating = 4.5f,
                ratingCount = 512
            ),
            Product(
                id = "4",
                name = "Sports Running Shoes",
                category = "shoes",
                sellingPrice = 899,
                originalCost = 500,
                imageUrl = "https://images.unsplash.com/photo-1542291026-7eec264c27ff?w=600",
                isResellerProduct = false,
                rating = 4.6f,
                ratingCount = 188
            ),
            Product(
                id = "5",
                name = "Smart Watch Series 8 AMOLED",
                category = "watch",
                sellingPrice = 999,
                originalCost = 600,
                imageUrl = "https://images.unsplash.com/photo-1523275335684-37898b6baf30?w=600",
                isResellerProduct = false,
                rating = 4.2f,
                ratingCount = 420
            ),
            Product(
                id = "6",
                name = "Designer Cotton Anarkali Kurti",
                category = "kurta",
                sellingPrice = 549,
                originalCost = 310,
                imageUrl = "https://images.unsplash.com/photo-1583391733956-3750e0ff4e8b?w=600",
                isResellerProduct = false,
                rating = 4.7f,
                ratingCount = 195
            ),
            Product(
                id = "7",
                name = "Classic Leather Analog Watch",
                category = "watch",
                sellingPrice = 449,
                originalCost = 250,
                imageUrl = "https://images.unsplash.com/photo-1524805444758-089113d48a6d?w=600",
                isResellerProduct = true,
                resellerPhone = "9876543210",
                rating = 4.1f,
                ratingCount = 89
            ),
            Product(
                id = "8",
                name = "Breathable Mesh Walking Shoes",
                category = "shoes",
                sellingPrice = 699,
                originalCost = 390,
                imageUrl = "https://images.unsplash.com/photo-1606107557195-0e29a4b5b4aa?w=600",
                isResellerProduct = true,
                resellerPhone = "9876543210",
                rating = 4.3f,
                ratingCount = 142
            )
        )

        for (p in initialList) {
            val cv = ContentValues().apply {
                put("id", p.id)
                put("name", p.name)
                put("category", p.category)
                put("selling_price", p.sellingPrice)
                put("original_cost", p.originalCost)
                put("image_url", p.imageUrl)
                put("is_reseller", if (p.isResellerProduct) 1 else 0)
                put("reseller_phone", p.resellerPhone)
                put("rating", p.rating)
                put("rating_count", p.ratingCount)
            }
            db.insert("products", null, cv)
        }

        // Seed an initial demo order so Admin dashboard immediately shows live metrics
        val initialOrder = Order(
            id = "ORD-1001",
            customerName = "Rahul Sharma",
            phone = "9876543210",
            address = "House 14, Gandhi Nagar, New Delhi 110031",
            itemsSummary = "Georgette Embroidered Saree (x1)",
            total = 499,
            platformFee = 150,
            paymentMode = "COD",
            status = "CONFIRMED",
            createdAt = System.currentTimeMillis() - 3600000
        )
        val cvOrder = ContentValues().apply {
            put("id", initialOrder.id)
            put("customer_name", initialOrder.customerName)
            put("phone", initialOrder.phone)
            put("address", initialOrder.address)
            put("items_summary", initialOrder.itemsSummary)
            put("total", initialOrder.total)
            put("platform_fee", initialOrder.platformFee)
            put("payment_mode", initialOrder.paymentMode)
            put("status", initialOrder.status)
            put("created_at", initialOrder.createdAt)
        }
        db.insert("orders", null, cvOrder)
    }
}
