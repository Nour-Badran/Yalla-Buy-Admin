package com.example.yallabuyadmin

import android.annotation.SuppressLint
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel

@OptIn(ExperimentalMaterial3Api::class)
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun ProductManagementScreen(viewModel: ProductViewModel = viewModel()) {
    var productName by remember { mutableStateOf("") }
    var productList by remember { mutableStateOf(listOf<Product>()) }
    var selectedProduct by remember { mutableStateOf<Product?>(null) }
    var errorMessage by remember { mutableStateOf("") }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Product Management") },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary
                )
            )
        },
        content = { padding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                OutlinedTextField(
                    value = productName,
                    onValueChange = { productName = it },
                    label = { Text("Product Name") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )
                Spacer(modifier = Modifier.height(16.dp))

                Button(
                    onClick = {
                        // Create a new product
                        val newProduct = Product(
                            title = "productName",
                            body_html = "Sample Description",
                            vendor = "Sample Vendor",
                            product_type = "Sample Type",
                            tags = "Sample Tag"
                        )
                        viewModel.createProduct(newProduct, {
                            productName = ""
                            errorMessage = ""
                            viewModel.getAllProducts({ products ->
                                productList = products
                            }, { error -> errorMessage = error })
                        }, { error -> errorMessage = error })
                    },
                    modifier = Modifier.fillMaxWidth().height(50.dp)
                ) {
                    Text("Create Product")
                }

                Spacer(modifier = Modifier.height(16.dp))

                Button(
                    onClick = {
                        // Fetch all products
                        viewModel.getAllProducts({ products ->
                            productList = products
                            errorMessage = ""
                        }, { error -> errorMessage = error })
                    },
                    modifier = Modifier.fillMaxWidth().height(50.dp)
                ) {
                    Text("Get All Products")
                }

                Spacer(modifier = Modifier.height(16.dp))

                if (errorMessage.isNotEmpty()) {
                    Text(
                        text = "Error: $errorMessage",
                        color = Color.Red,
                        fontSize = 14.sp
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f), // Take remaining space
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(productList) { product ->
                        ProductCard(product = product, onSelect = {
                            selectedProduct = product
                            productName = product.title // Set selected product details in the text field
                        })
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Update Button
                Button(
                    onClick = {
                        selectedProduct?.let {
                            val updatedProduct = it.copy(title = productName)
                            viewModel.updateProduct(updatedProduct, {
                                productName = ""
                                selectedProduct = null
                                errorMessage = ""
                                viewModel.getAllProducts({ products ->
                                    productList = products
                                }, { error -> errorMessage = error })
                            }, { error -> errorMessage = error })
                        }
                    },
                    modifier = Modifier.fillMaxWidth().height(50.dp)
                ) {
                    Text("Update Product")
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Delete Button
                Button(
                    onClick = {
                        selectedProduct?.let {
                            viewModel.deleteProduct(it.id ?: 0, {
                                productName = ""
                                selectedProduct = null
                                errorMessage = ""
                                viewModel.getAllProducts({ products ->
                                    productList = products
                                }, { error -> errorMessage = error })
                            }, { error -> errorMessage = error })
                        }
                    },
                    modifier = Modifier.fillMaxWidth().height(50.dp)
                ) {
                    Text("Delete Product")
                }
            }
        }
    )
}

@Composable
fun ProductCard(product: Product, onSelect: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.surfaceVariant)
            .padding(8.dp)
            .clickable { onSelect() }, // Handle click to select product
        shape = RoundedCornerShape(8.dp),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(text = "Title: ${product.title}", style = MaterialTheme.typography.titleMedium)
            Spacer(modifier = Modifier.height(4.dp))
            Text(text = "Vendor: ${product.vendor}", style = MaterialTheme.typography.bodySmall)
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "Product Type: ${product.product_type}",
                style = MaterialTheme.typography.bodySmall
            )
        }
    }
}
