package com.example.yallabuyadmin.ui.theme.screens

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.*
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.yallabuyadmin.coupons.model.CouponsRemoteDataSource
import com.example.yallabuyadmin.coupons.model.CouponsRepository
import com.example.yallabuyadmin.coupons.view.CouponsScreen
import com.example.yallabuyadmin.coupons.view.DiscountCodesScreen
import com.example.yallabuyadmin.coupons.viewmodel.CouponsViewModel
import com.example.yallabuyadmin.coupons.viewmodel.CouponsViewModelFactory
import com.example.yallabuyadmin.menu.model.MenuRemoteDataSource
import com.example.yallabuyadmin.menu.model.MenuRepository
import com.example.yallabuyadmin.menu.view.MenuScreen
import com.example.yallabuyadmin.menu.viewmodel.MenuViewModel
import com.example.yallabuyadmin.menu.viewmodel.MenuViewModelFactory
import com.example.yallabuyadmin.products.model.Product
import com.example.yallabuyadmin.products.viewmodel.ProductViewModel
import com.example.yallabuyadmin.products.viewmodel.ProductViewModelFactory
import com.example.yallabuyadmin.network.RetrofitInstance
import com.example.yallabuyadmin.products.model.ProductRepository
import com.example.yallabuyadmin.products.view.CreateProductScreen
import com.example.yallabuyadmin.products.view.ProductManagementScreen
import com.example.yallabuyadmin.products.view.UpdateProductScreen
import kotlinx.coroutines.delay

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            var showSplashScreen by remember { mutableStateOf(true) }

            // Launch a coroutine to hide the splash screen after a delay
            LaunchedEffect(Unit) {
                delay(5000) // 5-second delay for splash screen
                showSplashScreen = false
            }

            MaterialTheme {
                Surface {
                    if (showSplashScreen) {
                        SplashScreen(onTimeout = { showSplashScreen = false })
                    } else {
                        MainContent()
                    }
                }
            }
        }
    }
}

@Composable
fun MainContent() {
    var selectedProduct by remember { mutableStateOf<Product?>(null) }
    var createNewProduct by remember { mutableStateOf(false) }
    var navigateToMenu by remember { mutableStateOf(true) }
    var navigateToInventory by remember { mutableStateOf(false) }
    var navigateToCoupons by remember { mutableStateOf(false) }
    var navigateToDiscountScreen by remember { mutableStateOf(false) } // New state for discount screen
    var selectedDiscountId by remember { mutableStateOf<Long?>(null) } // To hold selected discount ID

    val viewModel: ProductViewModel = viewModel(factory = ProductViewModelFactory(
        ProductRepository(
            RetrofitInstance.api)
    ))

    val menuRepository = MenuRepository(MenuRemoteDataSource(RetrofitInstance.api))
    val menuViewModel: MenuViewModel = viewModel(factory = MenuViewModelFactory(menuRepository))

    val couponsRepository = CouponsRepository(CouponsRemoteDataSource(RetrofitInstance.api))
    val couponsViewModel: CouponsViewModel = viewModel(factory = CouponsViewModelFactory(couponsRepository))

    val inventoryCount = menuViewModel.inventoryCount.collectAsState().value
    val productsCount = menuViewModel.productsCount.collectAsState().value
    val couponsCount = menuViewModel.couponsCount.collectAsState().value

    // Logout function
    fun logout() {
        // Handle logout logic here
        // For example, clear user session, navigate to login screen, etc.
        navigateToMenu = true // or set up your navigation logic
    }

    when {
        navigateToMenu -> {
            menuViewModel.loadCounts()
            MenuScreen(
                onNavigateToProducts = {
                    navigateToMenu = false
                },
                onNavigateToInventory = {
                    navigateToMenu = false
                    navigateToInventory = true
                },
                onNavigateToCoupons = {
                    navigateToMenu = false
                    navigateToCoupons = true
                },
                onLogout = { logout() },
                inventoryCount = inventoryCount.toString(),
                productsCount = productsCount.toString(),
                couponsCount = couponsCount.toString()
            )
        }
        navigateToInventory -> {
            InventoryScreen(onBack = {
                navigateToInventory = false
                navigateToMenu = true
            })
        }
        navigateToCoupons -> {
            CouponsScreen(
                onBack = {
                    navigateToCoupons = false
                    navigateToMenu = true
                },
                onNavigateToDiscount = { discountId ->
                    selectedDiscountId = discountId // Set the selected discount ID
                    navigateToCoupons = false // Close coupons screen
                    navigateToDiscountScreen = true // Navigate to discount screen
                },
                viewModel = couponsViewModel
            )
        }
        navigateToDiscountScreen -> {
            DiscountCodesScreen(
                priceRuleId = selectedDiscountId ?: 0, // Pass the priceRuleId
                onBack = {
                    navigateToDiscountScreen = false
                    navigateToCoupons = true // Return to coupons screen
                },
                viewModel = couponsViewModel // Pass the ViewModel
            )
        }
        createNewProduct -> {
            CreateProductScreen(
                onCreateProduct = {
                    createNewProduct = false
                },
                onBack = { createNewProduct = false }
            )
        }
        selectedProduct != null -> {
            UpdateProductScreen(
                product = selectedProduct!!,
                viewModel = viewModel,
                onBack = { selectedProduct = null },
                onUpdateProduct = {
                    selectedProduct = null // Reset selected product
                }
            )
        }
        else -> {
            ProductManagementScreen(
                viewModel = viewModel,
                onNavigateToUpdate = { product ->
                    selectedProduct = product
                },
                onNavigateToCreate = {
                    createNewProduct = true
                },
                onBack = {
                    navigateToMenu = true
                }
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun MenuScreenPreview() {
    val dummyInventoryCount = 5
    val dummyProductsCount = 10
    val dummyCouponsCount = 2

    MenuScreen(
        onNavigateToProducts = {},
        onNavigateToInventory = {},
        onNavigateToCoupons = {},
        onLogout = {},
        inventoryCount = dummyInventoryCount.toString(),
        productsCount = dummyProductsCount.toString(),
        couponsCount = dummyCouponsCount.toString()
    )
}
