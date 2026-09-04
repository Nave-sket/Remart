package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.material.icons.filled.AdminPanelSettings
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Security
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
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
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun AdminScreen(
    viewModel: ReMartViewModel,
    modifier: Modifier = Modifier
) {
    val isAuthenticated by viewModel.isAdminAuthenticated.collectAsState()
    val orders by viewModel.orders.collectAsState()
    val walletRequests by viewModel.walletRequests.collectAsState()
    val withdrawRequests by viewModel.withdrawRequests.collectAsState()

    var pinInput by remember { mutableStateOf("") }

    val totalOrders = orders.size
    val totalSale = orders.sumOf { it.total }
    val totalProfit = orders.sumOf { it.platformFee }

    val dateFormat = remember { SimpleDateFormat("dd MMM, hh:mm a", Locale.getDefault()) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(14.dp)
            .testTag("admin_screen")
    ) {
        if (!isAuthenticated) {
            // PIN Login Screen
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 40.dp),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(2.dp)
            ) {
                Column(
                    modifier = Modifier.padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Box(
                        modifier = Modifier
                            .size(60.dp)
                            .background(CharcoalDark, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Security,
                            contentDescription = null,
                            tint = PinkPrimary,
                            modifier = Modifier.size(32.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    Text(
                        text = "ReMart Admin PRO",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Black,
                        color = CharcoalDark
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    Text(
                        text = "Sirf Owner ka Access (Secret PIN Enter Karein)",
                        fontSize = 12.sp,
                        color = GrayText
                    )

                    Spacer(modifier = Modifier.height(20.dp))

                    OutlinedTextField(
                        value = pinInput,
                        onValueChange = { pinInput = it },
                        placeholder = { Text("Enter Secret PIN") },
                        visualTransformation = PasswordVisualTransformation(),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.NumberPassword),
                        leadingIcon = {
                            Icon(Icons.Default.Lock, contentDescription = null, tint = CharcoalDark)
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("admin_pin_input"),
                        shape = RoundedCornerShape(10.dp),
                        singleLine = true
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Button(
                        onClick = {
                            val ok = viewModel.loginAdmin(pinInput)
                            if (ok) pinInput = ""
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(46.dp)
                            .testTag("admin_login_btn"),
                        colors = ButtonDefaults.buttonColors(containerColor = CharcoalDark),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Text("Admin Dashboard Open Karein", fontWeight = FontWeight.Bold)
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Text(
                        text = "Default owner PIN: 1234",
                        fontSize = 11.sp,
                        color = GrayText
                    )
                }
            }
        } else {
            // Admin Dashboard
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "ReMart Admin PRO",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Black,
                        color = CharcoalDark
                    )
                    Text(
                        text = "Owner Control Center",
                        fontSize = 11.sp,
                        color = PinkPrimary,
                        fontWeight = FontWeight.Bold
                    )
                }

                Button(
                    onClick = { viewModel.logoutAdmin() },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFF3F4F6)),
                    shape = RoundedCornerShape(8.dp),
                    contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                    modifier = Modifier.testTag("admin_logout_btn")
                ) {
                    Icon(
                        imageVector = Icons.Default.ExitToApp,
                        contentDescription = "Logout",
                        tint = CharcoalDark,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Lock", color = CharcoalDark, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // 3 KPI Metric Cards
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("admin_kpis"),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // Orders
                Card(
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    border = androidx.compose.foundation.BorderStroke(1.dp, GrayBorder)
                ) {
                    Column(modifier = Modifier.padding(10.dp)) {
                        Text("Orders", fontSize = 10.sp, color = GrayText, fontWeight = FontWeight.SemiBold)
                        Spacer(modifier = Modifier.height(2.dp))
                        Text("$totalOrders", fontSize = 18.sp, fontWeight = FontWeight.Black, color = CharcoalDark)
                    }
                }

                // Total Sale
                Card(
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    border = androidx.compose.foundation.BorderStroke(1.dp, GrayBorder)
                ) {
                    Column(modifier = Modifier.padding(10.dp)) {
                        Text("Total Sale", fontSize = 10.sp, color = GrayText, fontWeight = FontWeight.SemiBold)
                        Spacer(modifier = Modifier.height(2.dp))
                        Text("₹$totalSale", fontSize = 18.sp, fontWeight = FontWeight.Black, color = CharcoalDark)
                    }
                }

                // Profit 30%
                Card(
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = PinkPrimary)
                ) {
                    Column(modifier = Modifier.padding(10.dp)) {
                        Text("Profit 30%", fontSize = 10.sp, color = Color.White.copy(alpha = 0.8f), fontWeight = FontWeight.SemiBold)
                        Spacer(modifier = Modifier.height(2.dp))
                        Text("₹$totalProfit", fontSize = 18.sp, fontWeight = FontWeight.Black, color = Color.White)
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Commission Lock Notice Card
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(10.dp),
                colors = CardDefaults.cardColors(containerColor = PinkLight)
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Text(
                        text = "🔒 Commission Locked: ₹${ReMartConstants.COMMISSION_FLAT} + ${ReMartConstants.COMMISSION_PERCENT}%",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = PinkDark
                    )
                    Text(
                        text = "UPI: ${ReMartConstants.ADMIN_UPI_ID} • Payout sirf owner approval ke baad hota hai.",
                        fontSize = 11.sp,
                        color = GrayText
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Wallet Requests Section
            Text(
                text = "Wallet Requests (${walletRequests.size})",
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold,
                color = CharcoalDark
            )
            Spacer(modifier = Modifier.height(6.dp))

            if (walletRequests.isEmpty()) {
                Text("Koi wallet request nahi hai.", fontSize = 12.sp, color = GrayText)
            } else {
                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    walletRequests.forEach { r ->
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(8.dp),
                            colors = CardDefaults.cardColors(containerColor = Color.White),
                            border = androidx.compose.foundation.BorderStroke(1.dp, GrayBorder)
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(10.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column {
                                    Text("${r.phone} • ₹${r.amount}", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                                    Text("UTR: ${r.utr} • ${dateFormat.format(Date(r.createdAt))}", fontSize = 10.sp, color = GrayText)
                                }
                                Surface(
                                    color = GreenBg,
                                    shape = RoundedCornerShape(4.dp)
                                ) {
                                    Text(
                                        r.status,
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = GreenSuccess,
                                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                    )
                                }
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Withdraw Requests Section
            Text(
                text = "Withdraw Requests (${withdrawRequests.size})",
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold,
                color = CharcoalDark
            )
            Spacer(modifier = Modifier.height(6.dp))

            if (withdrawRequests.isEmpty()) {
                Text("Koi pending withdraw request nahi hai.", fontSize = 12.sp, color = GrayText)
            } else {
                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    withdrawRequests.forEach { wd ->
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(8.dp),
                            colors = CardDefaults.cardColors(containerColor = Color.White),
                            border = androidx.compose.foundation.BorderStroke(1.dp, GrayBorder)
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(10.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text("${wd.phone} — ₹${wd.amount}", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                                    Text("Status: ${wd.status}", fontSize = 11.sp, color = if (wd.status == "PAID") GreenSuccess else PinkDark)
                                }
                                if (wd.status != "PAID") {
                                    Button(
                                        onClick = { viewModel.markWithdrawPaid(wd.id) },
                                        colors = ButtonDefaults.buttonColors(containerColor = CharcoalDark),
                                        shape = RoundedCornerShape(6.dp),
                                        contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 12.dp, vertical = 4.dp),
                                        modifier = Modifier.testTag("approve_wd_btn_${wd.id}")
                                    ) {
                                        Text("Mark Paid", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                    }
                                } else {
                                    Surface(
                                        color = GreenBg,
                                        shape = RoundedCornerShape(4.dp)
                                    ) {
                                        Text(
                                            "PAID",
                                            fontSize = 10.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = GreenSuccess,
                                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Live Orders Section
            Text(
                text = "Live Orders (${orders.size})",
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold,
                color = CharcoalDark
            )
            Spacer(modifier = Modifier.height(6.dp))

            if (orders.isEmpty()) {
                Text("Koi live order nahi hai.", fontSize = 12.sp, color = GrayText)
            } else {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    orders.forEach { o ->
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("admin_order_card_${o.id}"),
                            shape = RoundedCornerShape(10.dp),
                            colors = CardDefaults.cardColors(containerColor = Color.White),
                            border = androidx.compose.foundation.BorderStroke(1.dp, GrayBorder)
                        ) {
                            Column(modifier = Modifier.padding(12.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = "${o.customerName} • ${o.phone}",
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 13.sp,
                                        color = CharcoalDark
                                    )
                                    Surface(
                                        color = if (o.status == "DELIVERED") GreenBg else PinkLight,
                                        shape = RoundedCornerShape(4.dp)
                                    ) {
                                        Text(
                                            text = o.status,
                                            fontSize = 10.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = if (o.status == "DELIVERED") GreenSuccess else PinkDark,
                                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                        )
                                    }
                                }

                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = o.address,
                                    fontSize = 11.sp,
                                    color = GrayText
                                )

                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = "Items: ${o.itemsSummary}",
                                    fontSize = 11.sp,
                                    color = CharcoalDark
                                )

                                HorizontalDivider(modifier = Modifier.padding(vertical = 6.dp), color = GrayBorder)

                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = "₹${o.total} | Profit ₹${o.platformFee} | ${o.paymentMode}",
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.SemiBold,
                                        color = CharcoalDark
                                    )

                                    // Quick status transition buttons
                                    Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                                        if (o.status != "SHIPPED" && o.status != "DELIVERED") {
                                            Button(
                                                onClick = { viewModel.updateOrderStatus(o.id, "SHIPPED") },
                                                colors = ButtonDefaults.buttonColors(containerColor = PinkPrimary),
                                                shape = RoundedCornerShape(4.dp),
                                                contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 8.dp, vertical = 2.dp)
                                            ) {
                                                Text("Ship", fontSize = 10.sp)
                                            }
                                        }
                                        if (o.status != "DELIVERED") {
                                            Button(
                                                onClick = { viewModel.updateOrderStatus(o.id, "DELIVERED") },
                                                colors = ButtonDefaults.buttonColors(containerColor = GreenSuccess),
                                                shape = RoundedCornerShape(4.dp),
                                                contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 8.dp, vertical = 2.dp)
                                            ) {
                                                Text("Deliver", fontSize = 10.sp)
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
