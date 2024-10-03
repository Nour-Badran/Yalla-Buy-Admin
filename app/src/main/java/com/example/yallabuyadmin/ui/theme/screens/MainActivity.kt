package com.example.yallabuyadmin.ui.theme.screens

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.*
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.yallabuyadmin.products.model.Product
import com.example.yallabuyadmin.products.viewmodel.ProductViewModel
import com.example.yallabuyadmin.products.viewmodel.ProductViewModelFactory
import com.example.yallabuyadmin.network.RetrofitInstance
import com.example.yallabuyadmin.products.model.ProductRepository
import com.example.yallabuyadmin.products.view.CreateProductScreen
import com.example.yallabuyadmin.products.view.ProductManagementScreen
import com.example.yallabuyadmin.products.view.UpdateProductScreen

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            var selectedProduct by remember { mutableStateOf<Product?>(null) }
            var createNewProduct by remember { mutableStateOf(false) }
            var navigateToMenu by remember { mutableStateOf(true) }
            var navigateToInventory by remember { mutableStateOf(false) }
            var navigateToCoupons by remember { mutableStateOf(false) }

            val viewModel: ProductViewModel = viewModel(factory = ProductViewModelFactory(
                ProductRepository(
                    RetrofitInstance.api)
            ))

            MaterialTheme {
                Surface {
                    when {
                        navigateToMenu -> {
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
                                }
                            )
                        }
                        navigateToInventory -> {
                            InventoryScreen(onBack = {
                                navigateToInventory = false
                                navigateToMenu = true
                            })
                        }
                        navigateToCoupons -> {
                            CouponsScreen(onBack = {
                                navigateToCoupons = false
                                navigateToMenu = true
                            })
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
            }
        }
    }
}
