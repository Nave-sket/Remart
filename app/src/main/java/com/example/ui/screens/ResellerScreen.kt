package com.example.ui.screens

import android.content.Intent
import android.net.Uri
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBalanceWallet
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowDownward
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Storefront
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.model.ReMartConstants
import com.example.ui.theme.CharcoalDark
import com.example.ui.theme.GrayBorder
import com.example.ui.theme.GrayText
import com.example.ui.theme.GreenBg
import com.example.ui.theme.GreenSuccess
import com.example.ui.theme.PinkDark
import com.example.ui.theme.PinkLight
import com.example.ui.theme.PinkPrimary
import com.example.viewmodel.ReMartViewModel

@Composable
fun ResellerScreen(
    viewModel: ReMartViewModel,
    modifier: Modifier = Modifier
) {
    val currentReseller by viewModel.currentReseller.collectAsState()
    val allProducts by viewModel.allProducts.collectAsState()
    val orders by viewModel.orders.collectAsState()
    val context = LocalContext.current

    var loginPhoneInput by remember { mutableStateOf("") }
    var showAddMoney by remember { mutableStateOf(false) }
    var showWithdraw by remember { mutableStateOf(false) }

    // Add money inputs
    var addAmountInput by remember { mutableStateOf("") }
    var utrInput by remember { mutableStateOf("") }

    // Withdraw inputs
    var withdrawAmountInput by remember { mutableStateOf("") }

    // Add Product inputs
    var productName by remember { mutableStateOf("") }
    var productCategory by remember { mutableStateOf("saree") }
    var originalCostInput by remember { mutableStateOf("") }
    var sellingPriceInput by remember { mutableStateOf("") }
    var imageUrlInput by remember { mutableStateOf("") }

    val cost = originalCostInput.toIntOrNull() ?: 0
    val sell = sellingPriceInput.toIntOrNull() ?: 0
    val profit = (sell - cost).coerceAtLeast(0)
    val reseller70 = (profit * ReMartConstants.RESELLER_PROFIT_SHARE).toInt()
    val admin30 = (profit * ReMartConstants.ADMIN_PROFIT_SHARE).toInt()

    val sampleImages = listOf(
        "https://images.unsplash.com/photo-1610030469983-98e550d6193c?w=600" to "Saree",
        "https://images.unsplash.com/photo-1596755094514-f87e34085b2c?w=600" to "Kurta",
        "https://images.unsplash.com/photo-1542291026-7eec264c27ff?w=600" to "Shoes",
        "https://images.unsplash.com/photo-1523275335684-37898b6baf30?w=600" to "Watch"
    )

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(14.dp)
            .testTag("reseller_screen")
    ) {
        if (currentReseller == null) {
            // Login Card
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 20.dp),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(2.dp)
            ) {
                Column(
                    modifier = Modifier.padding(20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Box(
                        modifier = Modifier
                            .size(56.dp)
                            .background(PinkLight, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Storefront,
                            contentDescription = null,
                            tint = PinkPrimary,
                            modifier = Modifier.size(30.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Text(
                        text = "ReMart Reseller Portal",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Black,
                        color = CharcoalDark
                    )

                    Spacer(modifier = Modifier.height(6.dp))

                    Text(
                        text = "Apna phone number daalein — yehi aapka Reseller ID banega aur wallet connect hoga.",
                        fontSize = 12.sp,
                        color = GrayText,
                        textAlign = androidx.compose.ui.text.style.TextAlign.Center
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    OutlinedTextField(
                        value = loginPhoneInput,
                        onValueChange = { loginPhoneInput = it },
                        placeholder = { Text("Phone Number (e.g. 9876543210)") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                        leadingIcon = {
                            Icon(Icons.Default.Phone, contentDescription = null, tint = PinkPrimary)
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("reseller_login_phone_input"),
                        shape = RoundedCornerShape(10.dp),
                        singleLine = true
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Button(
                        onClick = { viewModel.loginReseller(loginPhoneInput) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(44.dp)
                            .testTag("reseller_login_btn"),
                        colors = ButtonDefaults.buttonColors(containerColor = PinkPrimary),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Text("Reseller Login Karein", fontWeight = FontWeight.Bold)
                    }
                }
            }
        } else {
            val profile = currentReseller!!

            // Reseller Wallet Header Card
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("reseller_wallet_card"),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = CharcoalDark)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = "Wallet Balance",
                                fontSize = 11.sp,
                                color = Color.LightGray
                            )
                            Text(
                                text = "₹${profile.walletBalance}",
                                fontSize = 28.sp,
                                fontWeight = FontWeight.Black,
                                color = Color.White
                            )
                            Text(
                                text = "Reseller ID: ${profile.phone}",
                                fontSize = 11.sp,
                                color = PinkPrimary,
                                fontWeight = FontWeight.SemiBold
                            )
                        }

                        Row {
                            Button(
                                onClick = { showAddMoney = !showAddMoney; showWithdraw = false },
                                colors = ButtonDefaults.buttonColors(containerColor = Color.White),
                                shape = RoundedCornerShape(20.dp),
                                contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 12.dp, vertical = 6.dp),
                                modifier = Modifier.testTag("add_money_toggle_btn")
                            ) {
                                Text(
                                    text = "+ Add Money",
                                    color = CharcoalDark,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }

                            Spacer(modifier = Modifier.width(6.dp))

                            IconButton(
                                onClick = { viewModel.logoutReseller() },
                                modifier = Modifier.size(36.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.ExitToApp,
                                    contentDescription = "Logout",
                                    tint = Color.White
                                )
                            }
                        }
                    }

                    // Withdraw trigger button
                    Spacer(modifier = Modifier.height(10.dp))
                    OutlinedButton(
                        onClick = { showWithdraw = !showWithdraw; showAddMoney = false },
                        shape = RoundedCornerShape(8.dp),
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.White),
                        border = androidx.compose.foundation.BorderStroke(1.dp, Color.Gray),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(34.dp)
                            .testTag("withdraw_toggle_btn")
                    ) {
                        Text("Withdraw Funds to Bank / UPI (Min ₹100)", fontSize = 12.sp)
                    }
                }
            }

            // Expandable Add Money Section
            AnimatedVisibility(visible = showAddMoney) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 10.dp)
                        .testTag("add_money_card"),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    border = androidx.compose.foundation.BorderStroke(2.dp, PinkPrimary)
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Text(
                            text = "Add Money - UPI: ${ReMartConstants.ADMIN_UPI_ID}",
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp,
                            color = CharcoalDark
                        )
                        Spacer(modifier = Modifier.height(8.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            OutlinedTextField(
                                value = addAmountInput,
                                onValueChange = { addAmountInput = it },
                                placeholder = { Text("Amount (₹)") },
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                modifier = Modifier
                                    .weight(1f)
                                    .testTag("add_money_amount_input"),
                                shape = RoundedCornerShape(8.dp),
                                singleLine = true
                            )
                            Button(
                                onClick = {
                                    val amt = addAmountInput.toIntOrNull() ?: 0
                                    val uri = Uri.parse("upi://pay?pa=${ReMartConstants.ADMIN_UPI_ID}&pn=ReMart&am=$amt&cu=INR&tn=Wallet ${profile.phone}")
                                    val upiIntent = Intent(Intent.ACTION_VIEW, uri)
                                    try {
                                        context.startActivity(upiIntent)
                                    } catch (_: Exception) {}
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = PinkPrimary),
                                shape = RoundedCornerShape(8.dp),
                                modifier = Modifier.height(52.dp)
                            ) {
                                Text("Pay UPI", fontWeight = FontWeight.Bold)
                            }
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            OutlinedTextField(
                                value = utrInput,
                                onValueChange = { utrInput = it },
                                placeholder = { Text("UTR 12-digit Number") },
                                modifier = Modifier
                                    .weight(1f)
                                    .testTag("utr_input"),
                                shape = RoundedCornerShape(8.dp),
                                singleLine = true
                            )
                            Button(
                                onClick = {
                                    val amt = addAmountInput.toIntOrNull() ?: 0
                                    viewModel.addMoneyToWallet(amt, utrInput)
                                    addAmountInput = ""
                                    utrInput = ""
                                    showAddMoney = false
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = CharcoalDark),
                                shape = RoundedCornerShape(8.dp),
                                modifier = Modifier
                                    .height(52.dp)
                                    .testTag("verify_payment_btn")
                            ) {
                                Text("Verify")
                            }
                        }
                    }
                }
            }

            // Expandable Withdraw Section
            AnimatedVisibility(visible = showWithdraw) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 10.dp)
                        .testTag("withdraw_card"),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    border = androidx.compose.foundation.BorderStroke(1.dp, GrayBorder)
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Text(
                            text = "Withdraw to UPI (Min ₹100)",
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp,
                            color = CharcoalDark
                        )
                        Spacer(modifier = Modifier.height(8.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            OutlinedTextField(
                                value = withdrawAmountInput,
                                onValueChange = { withdrawAmountInput = it },
                                placeholder = { Text("Amount (Min ₹100)") },
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                modifier = Modifier
                                    .weight(1f)
                                    .testTag("withdraw_amount_input"),
                                shape = RoundedCornerShape(8.dp),
                                singleLine = true
                            )
                            Button(
                                onClick = {
                                    val amt = withdrawAmountInput.toIntOrNull() ?: 0
                                    viewModel.requestWithdraw(amt)
                                    withdrawAmountInput = ""
                                    showWithdraw = false
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = CharcoalDark),
                                shape = RoundedCornerShape(8.dp),
                                modifier = Modifier
                                    .height(52.dp)
                                    .testTag("submit_withdraw_btn")
                            ) {
                                Text("Submit", fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Add New Product Form ("Bina Link Ke List Hoga")
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("add_product_card"),
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(1.dp)
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Text(
                        text = "Add New Product",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Black,
                        color = CharcoalDark
                    )
                    Text(
                        text = "Bina Link Ke List Hoga — Main Store Pe Dikhega",
                        fontSize = 11.sp,
                        color = GrayText
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    OutlinedTextField(
                        value = productName,
                        onValueChange = { productName = it },
                        placeholder = { Text("Product Ka Naam (e.g. Cotton Printed Kurti)") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("reseller_product_name_input"),
                        shape = RoundedCornerShape(8.dp),
                        singleLine = true
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    // Category chips
                    Text("Category chunein:", fontSize = 11.sp, color = GrayText)
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .horizontalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        listOf("saree", "kurta", "shoes", "watch", "earbuds").forEach { cat ->
                            Surface(
                                shape = RoundedCornerShape(14.dp),
                                color = if (productCategory == cat) PinkPrimary else Color(0xFFF3F4F6),
                                modifier = Modifier
                                    .clickable { productCategory = cat }
                                    .padding(vertical = 4.dp)
                            ) {
                                Text(
                                    text = cat.replaceFirstChar { it.uppercase() },
                                    fontSize = 11.sp,
                                    color = if (productCategory == cat) Color.White else CharcoalDark,
                                    fontWeight = FontWeight.SemiBold,
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp)
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        OutlinedTextField(
                            value = originalCostInput,
                            onValueChange = { originalCostInput = it },
                            placeholder = { Text("Cost Price (₹)") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            modifier = Modifier
                                .weight(1f)
                                .testTag("reseller_product_cost_input"),
                            shape = RoundedCornerShape(8.dp),
                            singleLine = true
                        )

                        OutlinedTextField(
                            value = sellingPriceInput,
                            onValueChange = { sellingPriceInput = it },
                            placeholder = { Text("Selling Price (₹)") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            modifier = Modifier
                                .weight(1f)
                                .testTag("reseller_product_sell_input"),
                            shape = RoundedCornerShape(8.dp),
                            singleLine = true
                        )
                    }

                    // Live Profit Share Calculation
                    Spacer(modifier = Modifier.height(8.dp))
                    Surface(
                        color = PinkLight,
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(10.dp)) {
                            Text(
                                text = "Margin: ₹$profit | Tera 70%: ₹$reseller70 | Admin 30%: ₹$admin30",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = PinkDark
                            )
                            Text(
                                text = "Or Commission: ₹${ReMartConstants.COMMISSION_FLAT} + ${ReMartConstants.COMMISSION_PERCENT}% locked",
                                fontSize = 10.sp,
                                color = GrayText
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    OutlinedTextField(
                        value = imageUrlInput,
                        onValueChange = { imageUrlInput = it },
                        placeholder = { Text("Image URL (https://...) ya neeche se chunein") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("reseller_product_img_input"),
                        shape = RoundedCornerShape(8.dp),
                        singleLine = true
                    )

                    Spacer(modifier = Modifier.height(6.dp))

                    // Preset quick image picker
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        sampleImages.forEach { (url, label) ->
                            Surface(
                                shape = RoundedCornerShape(6.dp),
                                color = Color(0xFFF3F4F6),
                                modifier = Modifier
                                    .clickable { imageUrlInput = url }
                                    .border(
                                        1.dp,
                                        if (imageUrlInput == url) PinkPrimary else Color.Transparent,
                                        RoundedCornerShape(6.dp)
                                    )
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 4.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    AsyncImage(
                                        model = url,
                                        contentDescription = label,
                                        modifier = Modifier
                                            .size(18.dp)
                                            .clip(RoundedCornerShape(3.dp)),
                                        contentScale = ContentScale.Crop
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(label, fontSize = 10.sp)
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Button(
                        onClick = {
                            viewModel.addProductByReseller(
                                name = productName,
                                category = productCategory,
                                cost = cost,
                                sell = sell,
                                imageUrl = imageUrlInput
                            )
                            productName = ""
                            originalCostInput = ""
                            sellingPriceInput = ""
                            imageUrlInput = ""
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(44.dp)
                            .testTag("list_product_btn"),
                        colors = ButtonDefaults.buttonColors(containerColor = CharcoalDark),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Text("List Product - Main Site Pe Dikhega", fontWeight = FontWeight.Bold)
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // My Listed Products
            val myListed = allProducts.filter { it.resellerPhone == profile.phone }
            Text(
                text = "My Listed Products (${myListed.size})",
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = CharcoalDark
            )
            Spacer(modifier = Modifier.height(6.dp))

            if (myListed.isEmpty()) {
                Text(
                    text = "Aapne abhi tak koi product list nahi kiya.",
                    fontSize = 12.sp,
                    color = GrayText
                )
            } else {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    myListed.forEach { p ->
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(10.dp),
                            colors = CardDefaults.cardColors(containerColor = Color.White),
                            border = androidx.compose.foundation.BorderStroke(1.dp, GrayBorder)
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(10.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                AsyncImage(
                                    model = p.imageUrl,
                                    contentDescription = p.name,
                                    modifier = Modifier
                                        .size(48.dp)
                                        .clip(RoundedCornerShape(6.dp)),
                                    contentScale = ContentScale.Crop
                                )
                                Spacer(modifier = Modifier.width(10.dp))
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(p.name, fontWeight = FontWeight.SemiBold, fontSize = 13.sp)
                                    Text(
                                        "Selling: ₹${p.sellingPrice} | Cost: ₹${p.originalCost} | Profit 70%: ₹${((p.sellingPrice - p.originalCost) * 0.7).toInt()}",
                                        fontSize = 11.sp,
                                        color = PinkDark
                                    )
                                }
                                Surface(
                                    color = GreenBg,
                                    shape = RoundedCornerShape(4.dp)
                                ) {
                                    Text(
                                        "Live",
                                        color = GreenSuccess,
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold,
                                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
