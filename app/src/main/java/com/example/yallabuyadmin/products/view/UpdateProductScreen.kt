package com.example.yallabuyadmin.products.view

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.rememberAsyncImagePainter
import com.example.yallabuyadmin.products.model.Product
import com.example.yallabuyadmin.products.viewmodel.ProductViewModel
import com.example.yallabuyadmin.products.model.Variant
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UpdateProductScreen(
    product: Product,
    viewModel: ProductViewModel = viewModel(),
    onUpdateProduct: () -> Unit,
    onBack: () -> Unit
) {
    var productName by remember { mutableStateOf(product.title) }
    var vendor by remember { mutableStateOf(product.vendor) }
    var productType by remember { mutableStateOf(product.product_type) }
    var variants by remember { mutableStateOf(product.variants.joinToString(", ") { it.title }) }
    var imageUrl by remember { mutableStateOf(product.images.firstOrNull()?.src ?: "") }
    val isLoading by viewModel.isUpdating // Observe loading state from the ViewModel
    val errorMessage by viewModel.errorMessage.collectAsState()
    val successMessage by viewModel.successMessage.collectAsState() // Collect success message
    val coroutineScope = rememberCoroutineScope() // Coroutine scope for launching coroutines

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Update Product", fontSize = 20.sp, fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        },
        content = { padding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .padding(16.dp)
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text("Update Product Information", fontSize = 22.sp, fontWeight = FontWeight.Bold)

                Card(
                    modifier = Modifier.fillMaxWidth(),
                    elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        OutlinedTextField(
                            value = productName,
                            onValueChange = { productName = it },
                            label = { Text("Product Name", fontWeight = FontWeight.Bold) },
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true
                        )

                        OutlinedTextField(
                            value = vendor,
                            onValueChange = { vendor = it },
                            label = { Text("Vendor", fontWeight = FontWeight.Bold) },
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true
                        )

                        OutlinedTextField(
                            value = productType,
                            onValueChange = { productType = it },
                            label = { Text("Product Type", fontWeight = FontWeight.Bold) },
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true
                        )

                        OutlinedTextField(
                            value = variants,
                            onValueChange = { variants = it },
                            label = { Text("Variants (Comma separated)", fontWeight = FontWeight.Bold) },
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true
                        )

                        OutlinedTextField(
                            value = imageUrl,
                            onValueChange = { imageUrl = it },
                            label = { Text("Image URL", fontWeight = FontWeight.Bold) },
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true
                        )
                    }
                }

                if (imageUrl.isNotBlank()) {
                    Image(
                        painter = rememberAsyncImagePainter(imageUrl),
                        contentDescription = "Product Image",
                        modifier = Modifier
                            .size(150.dp)
                            .padding(8.dp),
                        contentScale = ContentScale.Crop
                    )
                }

                Button(
                    onClick = {
                        val variantList = variants.split(",").map { title ->
                            Variant(title = title.trim(), price = "0.0", sku = "")
                        }
                        // Update the product with new details
                        val updatedProduct = product.copy(
                            title = productName,
                            vendor = vendor,
                            product_type = productType,
                            images = listOf(com.example.yallabuyadmin.products.model.Image(src = imageUrl)),
                            variants = variantList
                        )
                        viewModel.updateProduct(updatedProduct) // Ensure the API call updates the errorMessage state in ViewModel
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                    enabled = !isLoading // Disable the button while loading
                ) {
                    if (isLoading) {
                        CircularProgressIndicator(color = Color.White, modifier = Modifier.size(24.dp))
                    } else {
                        Text("Update Product", fontSize = 18.sp)
                    }
                }

                // Display error message if any
                errorMessage?.let { message ->
                    Snackbar(
                        modifier = Modifier.padding(8.dp),
                        action = {
                            TextButton(onClick = { viewModel.clearError() }) {
                                Text("Dismiss", color = Color.White)
                            }
                        }
                    ) {
                        Text("Error: $message", color = Color.White)
                    }
                }

                successMessage?.let {
                    coroutineScope.launch {
                        delay(3000) // Show the message for 2 seconds
                        viewModel.clearSuccess()
                        onBack() // Navigate back after delay
                    }
                    Snackbar(
                        modifier = Modifier.padding(8.dp),
                        action = {
                            TextButton(onClick = { viewModel.clearSuccess() }) {
                                Text("Dismiss", color = Color.White)
                            }
                        }
                    ) {
                        Text("Success: $it", color = Color.White)
                    }
                }
            }
        }
    )
}
