package com.example.yallabuyadmin.products.view

import android.annotation.SuppressLint
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.rememberAsyncImagePainter
import com.example.yallabuyadmin.products.model.Product
import com.example.yallabuyadmin.products.viewmodel.ProductViewModel
import com.example.yallabuyadmin.products.model.Variant
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@SuppressLint("CoroutineCreationDuringComposition")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateProductScreen(
    viewModel: ProductViewModel = viewModel(),
    onCreateProduct: () -> Unit,
    onBack: () -> Unit
) {
    var productName by remember { mutableStateOf("") }
    var vendor by remember { mutableStateOf("") }
    var productType by remember { mutableStateOf("") }
    var variants by remember { mutableStateOf("") }
    var imageUrl by remember { mutableStateOf("") }
    var price by remember { mutableStateOf("") }
    val isLoading by viewModel.isLoading.collectAsState()
    val errorMessage by viewModel.errorMessage.collectAsState()
    val successMessage by viewModel.successMessage.collectAsState()
    val coroutineScope = rememberCoroutineScope()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Create Product", fontSize = 20.sp, color = Color.White) },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.Black
                ),
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = Color.White)
                    }
                }
            )
        },
        content = { padding ->
            Box(modifier = Modifier.fillMaxSize()) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(padding)
                        .padding(16.dp)
                        .background(Color.White)
                        .verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    if (imageUrl.isNotBlank()) {
                        Image(
                            painter = rememberAsyncImagePainter(imageUrl),
                            contentDescription = "Product Image",
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(200.dp)
                                .clip(RoundedCornerShape(16.dp))
                                .padding(8.dp),
                            contentScale = ContentScale.FillBounds
                        )
                    } else {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(200.dp)
                                .clip(RoundedCornerShape(16.dp))
                                .padding(8.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text("No Image Available", fontSize = 18.sp, color = Color.Gray)
                        }
                    }

                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White)
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

                            OutlinedTextField(
                                value = price,
                                onValueChange = { price = it },
                                label = { Text("Price", fontWeight = FontWeight.Bold) },
                                modifier = Modifier.fillMaxWidth(),
                                singleLine = true
                            )
                        }
                    }

                    Button(
                        onClick = {
                            val variantList = variants.split(",").map { title ->
                                Variant(title = title.trim(), price = price, sku = "")
                            }
                            val newProduct = Product(
                                title = productName,
                                body_html = "Sample Description",
                                vendor = vendor,
                                product_type = productType,
                                tags = "Sample Tag",
                                images = listOf(com.example.yallabuyadmin.products.model.Image(src = imageUrl)),
                                variants = variantList
                            )
                            viewModel.createProduct(newProduct)
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color.Black),
                        enabled = !isLoading // Disable the button while loading
                    ) {
                        if (isLoading) {
                            CircularProgressIndicator(color = Color.White, modifier = Modifier.size(24.dp))
                        } else {
                            Text("Create Product", fontSize = 18.sp)
                        }
                    }
                }

                // Display success Snackbar at the bottom
                if (successMessage != null) {
                    coroutineScope.launch {
                        delay(2000)
                        viewModel.clearSuccess()
                        onBack()
                    }
                    Snackbar(
                        modifier = Modifier
                            .align(Alignment.BottomCenter)
                            .padding(8.dp),
                        action = {
                            TextButton(onClick = { viewModel.clearSuccess() }) {
                                Text("Dismiss", color = Color.White)
                            }
                        }
                    ) {
                        Text("Success: $successMessage", color = Color.White)
                    }
                }

                // Display error Snackbar at the bottom
                if (errorMessage != null) {
                    Snackbar(
                        modifier = Modifier
                            .align(Alignment.BottomCenter)
                            .padding(8.dp),
                        action = {
                            TextButton(onClick = { viewModel.clearError() }) {
                                Text("Dismiss", color = Color.White)
                            }
                        }
                    ) {
                        Text("Error: $errorMessage", color = Color.White)
                    }
                }
            }
        }
    )
}
@Preview(showBackground = true)
@Composable
fun CreateProductScreenPreview() {
    CreateProductScreen(
        onCreateProduct = {},
        onBack = {}
    )
}

