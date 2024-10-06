package com.example.yallabuyadmin.products.view

import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
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
import com.example.yallabuyadmin.products.model.Variant
import com.example.yallabuyadmin.products.viewmodel.ProductViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import android.net.Uri
import android.widget.Toast

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
    var variants by remember { mutableStateOf(product.variants) }
    var imageUrl by remember { mutableStateOf(product.images.firstOrNull()?.src ?: "") }
    var selectedImageUri by remember { mutableStateOf<Uri?>(null) }
    val isLoading by viewModel.isUpdating
    val errorMessage by viewModel.errorMessage.collectAsState()
    val successMessage by viewModel.successMessage.collectAsState()
    val coroutineScope = rememberCoroutineScope()

    // Launcher for selecting an image from the gallery
    val imagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        selectedImageUri = uri
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Update Product", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = Color.Cyan) },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Black),
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
                        .verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(200.dp)
                            .clip(RoundedCornerShape(16.dp))
                            .background(Color.LightGray)
                            .padding(8.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        if (selectedImageUri != null) {
                            Image(
                                painter = rememberAsyncImagePainter(selectedImageUri),
                                contentDescription = "Product Image",
                                modifier = Modifier.fillMaxSize(),
                                contentScale = ContentScale.FillBounds
                            )
                        } else if (imageUrl.isNotBlank()) {
                            Image(
                                painter = rememberAsyncImagePainter(imageUrl),
                                contentDescription = "Product Image",
                                modifier = Modifier.fillMaxSize(),
                                contentScale = ContentScale.FillBounds
                            )
                        } else {
                            Text("No Image Available", fontSize = 18.sp, color = Color.Gray)
                        }
                    }

                    Button(onClick = { imagePickerLauncher.launch("image/*") }) {
                        Text("Select Image")
                    }

                    // Rest of the UI fields
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
                            variants.forEachIndexed { index, variant ->
                                Column(modifier = Modifier.fillMaxWidth()) {
                                    OutlinedTextField(
                                        value = variant.title,
                                        onValueChange = { updatedTitle ->
                                            variants = variants.toMutableList().apply {
                                                this[index] = this[index].copy(title = updatedTitle)
                                            }
                                        },
                                        label = { Text("Variant ${index + 1} Title", fontWeight = FontWeight.Bold) },
                                        modifier = Modifier.fillMaxWidth(),
                                        singleLine = true
                                    )
                                    OutlinedTextField(
                                        value = variant.price,
                                        onValueChange = { updatedPrice ->
                                            variants = variants.toMutableList().apply {
                                                this[index] = this[index].copy(price = updatedPrice)
                                            }
                                        },
                                        label = { Text("Variant ${index + 1} Price", fontWeight = FontWeight.Bold) },
                                        modifier = Modifier.fillMaxWidth(),
                                        singleLine = true
                                    )
                                }
                            }
                            // Update Product Button
                            Button(
                                onClick = {
                                    val updatedProduct = product.copy(
                                        title = productName,
                                        vendor = vendor,
                                        product_type = productType,
                                        images = listOf(
                                            com.example.yallabuyadmin.products.model.Image(
                                                src = selectedImageUri?.toString() ?: imageUrl
                                            )
                                        ),
                                        variants = variants
                                    )
                                    viewModel.updateProduct(updatedProduct)
                                },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(50.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = Color.Black),
                                enabled = !isLoading
                            ) {
                                if (isLoading) {
                                    CircularProgressIndicator(color = Color.White, modifier = Modifier.size(24.dp))
                                } else {
                                    Text("Update Product", fontSize = 18.sp, color = Color.Cyan)
                                }
                            }
                        }
                    }
                }

                // Display error or success message
                errorMessage?.let { message ->
                    Snackbar(
                        modifier = Modifier
                            .align(Alignment.BottomCenter)
                            .padding(16.dp),
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
                        delay(2000)
                        viewModel.clearSuccess()
                        onBack()
                    }
                    Snackbar(
                        modifier = Modifier
                            .align(Alignment.BottomCenter)
                            .padding(16.dp),
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
