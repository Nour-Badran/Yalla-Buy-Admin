package com.example.yallabuyadmin.inventory

import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Snackbar
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.rememberAsyncImagePainter
import com.example.yallabuyadmin.products.view.uploadImage
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.yallabuyadmin.products.model.Product
import com.example.yallabuyadmin.products.model.Variant
import com.example.yallabuyadmin.products.viewmodel.ProductViewModel
import androidx.compose.ui.text.input.KeyboardType
import com.example.yallabuyadmin.network.ApiService


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UpdateInventoryScreen(
    product: Product,
    viewModel: ProductViewModel = viewModel(),
    onUpdateProduct: () -> Unit,
    onBack: () -> Unit
) {
    // States for product properties
    var productName by remember { mutableStateOf(product.title) }
    var vendor by remember { mutableStateOf(product.vendor) }
    var productType by remember { mutableStateOf(product.product_type) }
    var variants by remember { mutableStateOf(product.variants) }

    // Collect loading, error, and success states
    val isLoading by viewModel.isUpdating
    val errorMessage by viewModel.errorMessage.collectAsState()
    val successMessage by viewModel.successMessage.collectAsState()
    val coroutineScope = rememberCoroutineScope()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(productName, fontSize = 20.sp, color = Color.White) },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Black),
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = Color.White)
                    }
                }
            )
        },
        floatingActionButton = {
            // Floating Action Button for adding new variant
            FloatingActionButton(
                onClick = {
                    // Call the ViewModel to add the new variant
                    viewModel.createVariant(
                        productId = product.id!!,
                        variant = Variant(
                            option1 = "size",
                            price = "0",
                            inventory_quantity = 0L,
                            sku = "",
                            title = ""
                        )
                    )
                },
                containerColor = Color.Black,
                contentColor = Color.White
            ) {
                Icon(Icons.Default.Add, contentDescription = "Add Variant")
            }
        },
        content = { padding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                LazyVerticalGrid(
                    columns = GridCells.Fixed(2),
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    items(variants.size) { index ->
                        val variant = variants[index]
                        VariantCard(
                            variant = variant,
                            onPriceChange = { updatedPrice ->
                                variants = variants.toMutableList().apply {
                                    this[index] = this[index].copy(price = updatedPrice)
                                }
                            },
                            onQuantityChange = { updatedQuantity ->
                                variants = variants.toMutableList().apply {
                                    if (updatedQuantity.isNotEmpty()) {
                                        this[index] = this[index].copy(inventory_quantity = updatedQuantity.toLong())
                                    }
                                }
                            },
                            onOption1Change = { option1 ->
                                variants = variants.toMutableList().apply {
                                    this[index] = this[index].copy(option1 = option1)
                                }
                            },
                            onOption2Change = { option2 ->
                                variants = variants.toMutableList().apply {
                                    this[index] = this[index].copy(option2 = option2)
                                }
                            },
                            onDelete = {
                                viewModel.deleteVariant(product.id!!, variant.id!!)
                            }
                        )
                    }
                }

//                Button(
//                    onClick = {
//                        // Call the ViewModel to add the new variant
//                        viewModel.createVariant(
//                            productId = product.id!!,
//                                    Variant(
//                                option1 = "size",
//                                price = "0",
//                                inventory_quantity = 0L,
//                                        sku = "",
//                                        title = ""
//                            )
//                        )
//                    },
//                    modifier = Modifier.fillMaxWidth()
//                ) {
//                    Text("Add Variant")
//                }

                Button(
                    onClick = {
                        for(variant in variants)
                        {
                            viewModel.updateVariant(Variant(option2 = variant.option2, option1 = variant.option1, id = variant.id , price = variant.price, title = "Size", sku = "", inventory_quantity = variant.inventory_quantity))
                        }
                        //viewModel.updateProduct(updatedProduct)
                    },
                    modifier = Modifier
                        .padding(top = 16.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color.Black),
                    enabled = !isLoading
                ) {
                    if (isLoading) {
                        CircularProgressIndicator(color = Color.White, modifier = Modifier.size(24.dp))
                    } else {
                        Text("Update Product")
                    }
                }

                // Display error or success message
                errorMessage?.let {
                    coroutineScope.launch {
                        delay(3000)
                        viewModel.clearError()
                    }
                    Text("Error: $it", color = Color.Red)
                }

                successMessage?.let {
                    coroutineScope.launch {
                        delay(3000)
                        viewModel.clearSuccess()
                        onBack()
                    }
                    Text("Success: $it", color = Color.Green)
                }
            }
        }
    )
}
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VariantCard(
    variant: Variant,
    onPriceChange: (String) -> Unit,
    onQuantityChange: (String) -> Unit,
    onOption1Change: (String) -> Unit,
    onOption2Change: (String) -> Unit,
    onDelete: () -> Unit // Pass delete function
) {

    Card(
        modifier = Modifier
            .fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        shape = RoundedCornerShape(8.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // Size input field
            OutlinedTextField(
                value = variant.option1,
                onValueChange = onOption1Change,
                label = { Text("Size") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                colors = TextFieldDefaults.outlinedTextFieldColors(
                    focusedBorderColor = Color.Black,
                    unfocusedBorderColor = Color.Black
                )
            )

            // Color input field
            OutlinedTextField(
                value = variant.option2 ?: "",  // Use empty string if option2 is null
                onValueChange = onOption2Change,
                label = { Text("Color") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                colors = TextFieldDefaults.outlinedTextFieldColors(
                    focusedBorderColor = Color.Black,
                    unfocusedBorderColor = Color.Black
                )
            )


            // Price input field (allowing only numbers)
            OutlinedTextField(
                value = variant.price,
                onValueChange = onPriceChange,
                label = { Text("Price") },
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number),
                singleLine = true,
                colors = TextFieldDefaults.outlinedTextFieldColors(
                    focusedBorderColor = Color.Black,
                    unfocusedBorderColor = Color.Black
                )
            )

            // Quantity input field (allowing only numbers)
            OutlinedTextField(
                value = variant.inventory_quantity.toString(),
                onValueChange = onQuantityChange,
                label = { Text("Quantity") },
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number),
                singleLine = true,
                colors = TextFieldDefaults.outlinedTextFieldColors(
                    focusedBorderColor = Color.Black,
                    unfocusedBorderColor = Color.Black
                )
            )
            TextButton(
                onClick = onDelete,
                modifier = Modifier
                    .padding(vertical = 8.dp) // Add padding for spacing
                    .fillMaxWidth(), // Make it fill the width for better placement
                colors = ButtonDefaults.textButtonColors(contentColor = Color.Red),
                shape = RoundedCornerShape(12.dp), // Make the button rounded
            ) {
                Icon(Icons.Default.Delete, contentDescription = "Delete", tint = Color.Red)
                Text("Delete Variant", fontWeight = FontWeight.Bold, modifier = Modifier.padding(start = 4.dp)) // Spacing between icon and text
            }
        }
    }
}
