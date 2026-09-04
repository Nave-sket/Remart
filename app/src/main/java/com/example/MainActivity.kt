package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AdminPanelSettings
import androidx.compose.material.icons.filled.ShoppingBag
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material.icons.filled.Storefront
import androidx.compose.material.icons.outlined.AdminPanelSettings
import androidx.compose.material.icons.outlined.ShoppingBag
import androidx.compose.material.icons.outlined.Storefront
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.screens.AdminScreen
import com.example.ui.screens.CartSheet
import com.example.ui.screens.ResellerScreen
import com.example.ui.screens.ShopScreen
import com.example.ui.theme.CharcoalDark
import com.example.ui.theme.PinkLight
import com.example.ui.theme.PinkPrimary
import com.example.ui.theme.ReMartTheme
import com.example.viewmodel.ReMartViewModel

class MainActivity : ComponentActivity() {

    private val viewModel: ReMartViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            ReMartTheme {
                ReMartApp(viewModel = viewModel)
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReMartApp(viewModel: ReMartViewModel) {
    var selectedTab by remember { mutableIntStateOf(0) }
    val cartCount by viewModel.cartCount.collectAsState()
    val snackbarMsg by viewModel.snackbarMessage.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(snackbarMsg) {
        snackbarMsg?.let {
            snackbarHostState.showSnackbar(it)
            viewModel.setSnackbarMessage(null)
        }
    }

    Scaffold(
        contentWindowInsets = WindowInsets.safeDrawing,
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
        topBar = {
            TopAppBar(
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = "ReMart",
                            fontSize = 22.sp,
                            fontWeight = FontWeight.Black,
                            color = PinkPrimary
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Surface(
                            color = PinkLight,
                            shape = RoundedCornerShape(4.dp)
                        ) {
                            Text(
                                text = "Lowest Price",
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Bold,
                                color = PinkPrimary,
                                modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
                            )
                        }
                    }
                },
                actions = {
                    // Resell Karo Quick Button
                    Button(
                        onClick = { selectedTab = 1 },
                        colors = ButtonDefaults.buttonColors(containerColor = PinkPrimary),
                        shape = RoundedCornerShape(20.dp),
                        contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                        modifier = Modifier.testTag("top_resell_btn")
                    ) {
                        Text(
                            text = "Resell Karo",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }

                    Spacer(modifier = Modifier.width(6.dp))

                    // Cart Icon with Badge
                    IconButton(
                        onClick = { viewModel.openCart() },
                        modifier = Modifier.testTag("top_cart_btn")
                    ) {
                        BadgedBox(
                            badge = {
                                if (cartCount > 0) {
                                    Badge(
                                        containerColor = CharcoalDark,
                                        contentColor = Color.White
                                    ) {
                                        Text(
                                            text = "$cartCount",
                                            fontSize = 10.sp,
                                            fontWeight = FontWeight.Bold
                                        )
                                    }
                                }
                            }
                        ) {
                            Icon(
                                imageVector = Icons.Default.ShoppingCart,
                                contentDescription = "Cart",
                                tint = CharcoalDark,
                                modifier = Modifier.size(24.dp)
                            )
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.White
                )
            )
        },
        bottomBar = {
            NavigationBar(
                containerColor = Color.White,
                tonalElevation = 6.dp,
                modifier = Modifier.testTag("bottom_nav_bar")
            ) {
                NavigationBarItem(
                    selected = selectedTab == 0,
                    onClick = { selectedTab = 0 },
                    icon = {
                        Icon(
                            imageVector = if (selectedTab == 0) Icons.Default.ShoppingBag else Icons.Outlined.ShoppingBag,
                            contentDescription = "Shop"
                        )
                    },
                    label = { Text("Shop", fontSize = 11.sp, fontWeight = FontWeight.SemiBold) },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = PinkPrimary,
                        selectedTextColor = PinkPrimary,
                        indicatorColor = PinkLight,
                        unselectedIconColor = CharcoalDark.copy(alpha = 0.6f),
                        unselectedTextColor = CharcoalDark.copy(alpha = 0.6f)
                    ),
                    modifier = Modifier.testTag("nav_tab_shop")
                )

                NavigationBarItem(
                    selected = selectedTab == 1,
                    onClick = { selectedTab = 1 },
                    icon = {
                        Icon(
                            imageVector = if (selectedTab == 1) Icons.Default.Storefront else Icons.Outlined.Storefront,
                            contentDescription = "Reseller"
                        )
                    },
                    label = { Text("Reseller Pro", fontSize = 11.sp, fontWeight = FontWeight.SemiBold) },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = PinkPrimary,
                        selectedTextColor = PinkPrimary,
                        indicatorColor = PinkLight,
                        unselectedIconColor = CharcoalDark.copy(alpha = 0.6f),
                        unselectedTextColor = CharcoalDark.copy(alpha = 0.6f)
                    ),
                    modifier = Modifier.testTag("nav_tab_reseller")
                )

                NavigationBarItem(
                    selected = selectedTab == 2,
                    onClick = { selectedTab = 2 },
                    icon = {
                        Icon(
                            imageVector = if (selectedTab == 2) Icons.Default.AdminPanelSettings else Icons.Outlined.AdminPanelSettings,
                            contentDescription = "Admin"
                        )
                    },
                    label = { Text("Admin PRO", fontSize = 11.sp, fontWeight = FontWeight.SemiBold) },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = PinkPrimary,
                        selectedTextColor = PinkPrimary,
                        indicatorColor = PinkLight,
                        unselectedIconColor = CharcoalDark.copy(alpha = 0.6f),
                        unselectedTextColor = CharcoalDark.copy(alpha = 0.6f)
                    ),
                    modifier = Modifier.testTag("nav_tab_admin")
                )
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(Color(0xFFF8F8F8))
        ) {
            when (selectedTab) {
                0 -> ShopScreen(
                    viewModel = viewModel,
                    onOpenReseller = { selectedTab = 1 }
                )
                1 -> ResellerScreen(
                    viewModel = viewModel
                )
                2 -> AdminScreen(
                    viewModel = viewModel
                )
            }
        }

        // Cart Drawer Bottom Sheet
        CartSheet(viewModel = viewModel)
    }
}
