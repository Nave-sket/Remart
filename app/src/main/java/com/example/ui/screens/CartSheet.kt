package com.example.ui.screens

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberModalBottomSheetState
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
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.model.CartItem
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CartSheet(
    viewModel: ReMartViewModel,
    modifier: Modifier = Modifier
) {
    val isOpen by viewModel.isCartOpen.collectAsState()
    val cartItems by viewModel.cart.collectAsState()
    val totalAmount by viewModel.cartTotal.collectAsState()
    val lastPlacedOrder by viewModel.lastPlacedOrder.collectAsState()
    val context = LocalContext.current

    var customerName by remember { mutableStateOf("") }
    var customerPhone by remember { mutableStateOf("") }
    var customerAddress by remember { mutableStateOf("") }

    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    if (isOpen) {
        ModalBottomSheet(
            onDismissRequest = { viewModel.closeCart() },
            sheetState = sheetState,
            containerColor = Color.White,
            modifier = modifier.testTag("cart_bottom_sheet")
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .fillMaxHeight(0.9f)
                    .padding(horizontal = 16.dp)
            ) {
                // Header
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = "Shopping Cart",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Black,
                            color = CharcoalDark
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Surface(
                            color = PinkPrimary,
                            shape = CircleShape
                        ) {
                            Text(
                                text = "${cartItems.sumOf { it.quantity }}",
                                color = Color.White,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                            )
                        }
                    }

                    IconButton(
                        onClick = { viewModel.closeCart() },
                        modifier = Modifier.testTag("close_cart_btn")
                    ) {
                        Icon(Icons.Default.Close, contentDescription = "Close", tint = CharcoalDark)
                    }
                }

                HorizontalDivider(color = GrayBorder)

                if (cartItems.isEmpty()) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text("🛒", fontSize = 48.sp)
                            Spacer(modifier = Modifier.height(10.dp))
                            Text(
                                "Cart khali hai",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = CharcoalDark
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                "Products add karke shopping shuru karein!",
                                fontSize = 12.sp,
                                color = GrayText
                            )
                        }
                    }
                } else {
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f)
                            .padding(vertical = 8.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        items(cartItems, key = { it.product.id }) { item ->
                            CartItemRow(
                                item = item,
                                onAdd = { viewModel.updateCartQuantity(item.product.id, 1) },
                                onMinus = { viewModel.updateCartQuantity(item.product.id, -1) },
                                onDelete = { viewModel.removeFromCart(item.product.id) }
                            )
                        }

                        // Delivery details form
                        item {
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "Delivery Details",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = CharcoalDark
                            )

                            Spacer(modifier = Modifier.height(8.dp))

                            OutlinedTextField(
                                value = customerName,
                                onValueChange = { customerName = it },
                                placeholder = { Text("Naam (Full Name)", fontSize = 13.sp) },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .testTag("cust_name_input"),
                                shape = RoundedCornerShape(8.dp),
                                singleLine = true
                            )

                            Spacer(modifier = Modifier.height(8.dp))

                            OutlinedTextField(
                                value = customerPhone,
                                onValueChange = { customerPhone = it },
                                placeholder = { Text("Phone Number (10 digit)", fontSize = 13.sp) },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .testTag("cust_phone_input"),
                                shape = RoundedCornerShape(8.dp),
                                singleLine = true
                            )

                            Spacer(modifier = Modifier.height(8.dp))

                            OutlinedTextField(
                                value = customerAddress,
                                onValueChange = { customerAddress = it },
                                placeholder = { Text("Full Address (House, Street, City, PIN)", fontSize = 13.sp) },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .testTag("cust_address_input"),
                                shape = RoundedCornerShape(8.dp),
                                maxLines = 3
                            )
                        }

                        // Price Breakdown
                        item {
                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(10.dp),
                                colors = CardDefaults.cardColors(containerColor = Color(0xFFF9FAFB))
                            ) {
                                Column(modifier = Modifier.padding(12.dp)) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        Text("Subtotal", fontSize = 13.sp, color = GrayText)
                                        Text("₹$totalAmount", fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
                                    }
                                    Spacer(modifier = Modifier.height(6.dp))
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        Text("Delivery Charge", fontSize = 13.sp, color = GrayText)
                                        Text("FREE", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = GreenSuccess)
                                    }
                                    HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp), color = GrayBorder)
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        Text("Total Amount", fontSize = 15.sp, fontWeight = FontWeight.Black)
                                        Text("₹$totalAmount", fontSize = 16.sp, fontWeight = FontWeight.Black, color = PinkPrimary)
                                    }
                                }
                            }
                        }

                        // UPI Admin Banner
                        item {
                            Surface(
                                modifier = Modifier.fillMaxWidth(),
                                color = GreenBg,
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                Row(
                                    modifier = Modifier.padding(10.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text("💰", fontSize = 16.sp)
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        text = "Pay direct to Admin UPI: ${ReMartConstants.ADMIN_UPI_ID}",
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.SemiBold,
                                        color = Color(0xFF1B5E20)
                                    )
                                }
                            }
                        }
                    }

                    // Bottom Checkout Action Buttons
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 16.dp, top = 8.dp)
                    ) {
                        Button(
                            onClick = {
                                viewModel.placeOrder(customerName, customerPhone, customerAddress, isCOD = false)
                                // Launch UPI payment intent if available
                                val uri = Uri.parse("upi://pay?pa=${ReMartConstants.ADMIN_UPI_ID}&pn=ReMart&am=$totalAmount&cu=INR&tn=ReMart Order")
                                val upiIntent = Intent(Intent.ACTION_VIEW, uri)
                                try {
                                    context.startActivity(upiIntent)
                                } catch (_: Exception) {
                                    // Fallback if no UPI app installed on device/emulator
                                }
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(46.dp)
                                .testTag("checkout_online_btn"),
                            colors = ButtonDefaults.buttonColors(containerColor = PinkPrimary),
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Text(
                                text = "Online Pay - GPay / PhonePe (₹$totalAmount)",
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp
                            )
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        Button(
                            onClick = {
                                viewModel.placeOrder(customerName, customerPhone, customerAddress, isCOD = true)
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(46.dp)
                                .testTag("checkout_cod_btn"),
                            colors = ButtonDefaults.buttonColors(containerColor = CharcoalDark),
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Text(
                                text = "Cash on Delivery (COD)",
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp
                            )
                        }
                    }
                }
            }
        }
    }

    // Order Success Dialog
    lastPlacedOrder?.let { order ->
        AlertDialog(
            onDismissRequest = { viewModel.dismissOrderSuccess() },
            title = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.CheckCircle,
                        contentDescription = null,
                        tint = GreenSuccess,
                        modifier = Modifier.size(28.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Order Confirmed!", fontWeight = FontWeight.Black)
                }
            },
            text = {
                Column {
                    Text(
                        text = "Aapka order successfully place ho gaya hai.",
                        fontSize = 13.sp,
                        color = CharcoalDark
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = Color(0xFFF9FAFB))
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Text("Order ID: ${order.id}", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                            Text("Customer: ${order.customerName}", fontSize = 12.sp)
                            Text("Phone: ${order.phone}", fontSize = 12.sp)
                            Text("Amount: ₹${order.total}", fontWeight = FontWeight.Bold, fontSize = 13.sp, color = PinkPrimary)
                            Text("Payment: ${order.paymentMode}", fontSize = 12.sp, color = GreenSuccess, fontWeight = FontWeight.SemiBold)
                            Text("Status: ${order.status}", fontSize = 12.sp)
                        }
                    }
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "Deliver address: ${order.address}",
                        fontSize = 11.sp,
                        color = GrayText
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = { viewModel.dismissOrderSuccess() },
                    colors = ButtonDefaults.buttonColors(containerColor = PinkPrimary),
                    modifier = Modifier.testTag("order_success_ok_btn")
                ) {
                    Text("Theek Hai (Done)")
                }
            }
        )
    }
}

@Composable
fun CartItemRow(
    item: CartItem,
    onAdd: () -> Unit,
    onMinus: () -> Unit,
    onDelete: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(10.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFF9FAFB)),
        border = androidx.compose.foundation.BorderStroke(1.dp, GrayBorder)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            AsyncImage(
                model = item.product.imageUrl,
                contentDescription = item.product.name,
                modifier = Modifier
                    .size(56.dp)
                    .clip(RoundedCornerShape(8.dp)),
                contentScale = ContentScale.Crop
            )

            Spacer(modifier = Modifier.width(10.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = item.product.name,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.SemiBold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    color = CharcoalDark
                )
                Spacer(modifier = Modifier.height(2.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "₹${item.product.sellingPrice}",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = CharcoalDark
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "x ${item.quantity} = ₹${item.subtotal}",
                        fontSize = 11.sp,
                        color = GrayText
                    )
                }
            }

            // Quantity Controls
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .border(1.dp, GrayBorder, RoundedCornerShape(6.dp))
                    .padding(2.dp)
            ) {
                IconButton(
                    onClick = onMinus,
                    modifier = Modifier.size(24.dp)
                ) {
                    Icon(
                        imageVector = if (item.quantity == 1) Icons.Default.Delete else Icons.Default.Remove,
                        contentDescription = "Decrease",
                        modifier = Modifier.size(14.dp),
                        tint = CharcoalDark
                    )
                }

                Text(
                    text = "${item.quantity}",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(horizontal = 6.dp)
                )

                IconButton(
                    onClick = onAdd,
                    modifier = Modifier.size(24.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = "Increase",
                        modifier = Modifier.size(14.dp),
                        tint = CharcoalDark
                    )
                }
            }
        }
    }
}
