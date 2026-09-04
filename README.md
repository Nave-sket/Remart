# ReMart - Lowest Price Online Shopping & Reseller App

ReMart is a modern Android social commerce and reseller platform built using Kotlin and Jetpack Compose with Material Design 3.

## Features

### 🛍️ Shopper Marketplace
- **Products For You**: Dynamic product catalog featuring sarees, kurtas, shoes, watches, earbuds, and community reseller listings.
- **Search & Filter**: Real-time multi-attribute search and category filters (All, Saree, Kurta, Shoes, Watch, Earbuds, Reseller).
- **Free Delivery & COD**: Free shipping badges, MRP discount strikethrough calculations, and star ratings.
- **Shopping Cart & Checkout**: Slide-out cart drawer with quantity adjustments, delivery address input, and dual checkout modes:
  - **Online Pay**: Direct UPI payment integration to Admin (`8368322869@ybl`).
  - **Cash on Delivery (COD)**: Instant order confirmation.

### 💼 Reseller Pro Portal
- **Reseller Identity**: Phone-number-based reseller profile and authentication.
- **Real-Time Wallet**: Live balance tracking with "+ Add Money" via UPI and 12-digit UTR verification.
- **Withdrawals**: Instant withdrawal requests with a minimum threshold of ₹100.
- **Product Listing**: List custom products directly to the main store without links.
- **Margin & Commission Engine**: Real-time margin calculator reflecting 70% reseller profit share and 30% admin share (or locked ₹49 + 5% commission).
- **My Listings**: Tracking for reseller-created products and orders.

### 👑 Admin PRO Dashboard
- **Owner Security**: PIN-protected dashboard (Default Owner PIN: `1234`).
- **Real-Time KPIs**: Track Total Orders, Total Sales (₹), and 30% Platform Profit (₹).
- **Wallet Requests**: Review and verify incoming wallet recharge requests with UTR.
- **Withdraw Requests**: Review pending payouts with one-tap "Mark Paid" confirmation.
- **Live Order Management**: Monitor customer details, addresses, order totals, and update statuses (Confirmed, Shipped, Delivered).

## Tech Stack & Architecture
- **Language**: Kotlin
- **UI Framework**: Jetpack Compose (Material 3)
- **Architecture**: MVVM with Kotlin Coroutines & StateFlow
- **Local Persistence**: Android SQLite Database (`remart.db`) with reactive updates
- **Image Loading**: Coil for Compose
- **Target SDK**: Android 36 (minSdk 26)
