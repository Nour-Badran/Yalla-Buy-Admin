package com.example.yallabuyadmin.ui.theme.screens

import android.content.Context
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.yallabuyadmin.auth.FirebaseAuthun
import com.example.yallabuyadmin.auth.LogInScreen
import com.example.yallabuyadmin.auth.SignupScreen
import com.example.yallabuyadmin.coupons.model.CouponsRemoteDataSource
import com.example.yallabuyadmin.coupons.model.CouponsRepository
import com.example.yallabuyadmin.coupons.view.CouponsScreen
import com.example.yallabuyadmin.coupons.view.DiscountCodesScreen
import com.example.yallabuyadmin.coupons.viewmodel.CouponsViewModel
import com.example.yallabuyadmin.coupons.viewmodel.CouponsViewModelFactory
import com.example.yallabuyadmin.inventory.InventoryScreen
import com.example.yallabuyadmin.inventory.UpdateInventoryScreen
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
    val context = LocalContext.current
    val sharedPreferences = context.getSharedPreferences("user_prefs", Context.MODE_PRIVATE)
    val loggedIn = sharedPreferences.getBoolean("logged_in", false)

    var selectedProduct by remember { mutableStateOf<Product?>(null) }
    var selectedInventory by remember { mutableStateOf<Product?>(null) }
    var createNewProduct by remember { mutableStateOf(false) }
    var navigateToMenu by remember { mutableStateOf(loggedIn) }
    var navigateToInventory by remember { mutableStateOf(false) }
    var navigateToCoupons by remember { mutableStateOf(false) }
    var navigateToDiscountScreen by remember { mutableStateOf(false) } // New state for discount screen
    var selectedDiscountId by remember { mutableStateOf<Long?>(null) } // To hold selected discount ID
    var navigateToSignUp by remember { mutableStateOf(false) }
    var navigateToLogin by remember { mutableStateOf(!loggedIn) }

    val viewModel: ProductViewModel = viewModel(factory = ProductViewModelFactory(
        ProductRepository(
            RetrofitInstance.api)
    ))

    val menuRepository = MenuRepository(MenuRemoteDataSource(RetrofitInstance.api))
    val menuViewModel: MenuViewModel = viewModel(factory = MenuViewModelFactory(menuRepository))

    val couponsRepository = CouponsRepository(CouponsRemoteDataSource(RetrofitInstance.api))
    val couponsViewModel: CouponsViewModel = viewModel(factory = CouponsViewModelFactory(couponsRepository))

    // Logout function
    fun logout() {
        //FirebaseAuth.getInstance().signOut()

        FirebaseAuthun().logOut()

        with(sharedPreferences.edit()) {
            putBoolean("logged_in", false)
            apply()
        }

        navigateToMenu = false
        navigateToLogin = true // or set up your navigation logic
    }

    when {
        navigateToSignUp ->{
            SignupScreen(
                onLogin = {
                    navigateToSignUp = false
                    navigateToLogin = true
                }, onSignupSuccess = {

                    with(sharedPreferences.edit()) {
                        putBoolean("logged_in", true)
                        apply()
                    }

                    navigateToSignUp = false
                    navigateToMenu = true
                }
            )
        }
        navigateToLogin -> {
            LogInScreen(
                onSignup = {
                    navigateToLogin = false
                    navigateToSignUp = true
                },
                onLogIn = {

                    with(sharedPreferences.edit()) {
                        putBoolean("logged_in", true)
                        apply()
                    }

                    navigateToLogin = false
                    navigateToMenu = true
                }
            )
        }
        navigateToMenu -> {
            menuViewModel.loadCounts()
            MenuScreen(
                menuViewModel = menuViewModel, // Pass MenuViewModel
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
                onLogout = { logout() }
            )
        }
        navigateToInventory -> {
            InventoryScreen(onBack = {
                navigateToInventory = false
                navigateToMenu = true
            },viewModel,
                onNavigateToUpdate = { product ->
                    navigateToInventory = false
                    selectedInventory = product
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
        selectedInventory != null -> {
            UpdateInventoryScreen(
                product = selectedInventory!!,
                viewModel = viewModel,
                onBack = {
                    selectedInventory=null
                    navigateToInventory = true
                         },
                onUpdateProduct = {selectedInventory=null}
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


