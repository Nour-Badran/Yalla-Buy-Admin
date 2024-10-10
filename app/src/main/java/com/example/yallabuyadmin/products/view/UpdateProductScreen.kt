package com.example.yallabuyadmin.products.view

import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.Crossfade
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.rememberAsyncImagePainter
import com.example.yallabuyadmin.products.model.Product
import com.example.yallabuyadmin.products.model.Variant
import com.example.yallabuyadmin.products.viewmodel.ProductViewModel
import com.example.yallabuyadmin.ui.theme.AppColors
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UpdateProductScreen(
    product: Product,
    viewModel: ProductViewModel = viewModel(),
    onBack: () -> Unit
) {
    Log.d("Id",product.id.toString())
    var productName by remember { mutableStateOf(product.title) }
    var vendor by remember { mutableStateOf(product.vendor) }
    var productType by remember { mutableStateOf(product.product_type) }
    var variants by remember { mutableStateOf(product.variants) } // Maintain a list of variants
    var imageUrl by remember { mutableStateOf(product.images.firstOrNull()?.src ?: "") }
    val isLoading by viewModel.isUpdating // Observe loading state from the ViewModel
    val errorMessage by viewModel.errorMessage.collectAsState()
    val successMessage by viewModel.successMessage.collectAsState() // Collect success message
    val coroutineScope = rememberCoroutineScope() // Coroutine scope for launching coroutines
    // State for image uploading
    var isUploadingImage by remember { mutableStateOf(false) }

    // Coroutine scope for handling async tasks
    val coroutineScope2 = rememberCoroutineScope()

    // Image picker
    val context = LocalContext.current
    val imagePickerLauncher = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri ->
        uri?.let {
            isUploadingImage = true // Set uploading state to true
            coroutineScope2.launch {
                val uploadedImageUrl = uploadImage(context, it)
                isUploadingImage = false // Set uploading state to false
                if (uploadedImageUrl != null) {
                    imageUrl = uploadedImageUrl // Update image URL with the uploaded URL
                } else {
                    // Handle upload failure (e.g., show a Snackbar)
                }
            }
        }
    }
    Scaffold(
        containerColor = Color.White,
        topBar = {
            TopAppBar(
                title = { Text("Update Product", fontSize = 20.sp, color = AppColors.Teal) },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.White
                ),
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = AppColors.Teal)
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
                    if (isUploadingImage) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(200.dp)
                                .clip(RoundedCornerShape(16.dp)),
                            contentAlignment = Alignment.Center
                        ) {
                            CircularProgressIndicator(color = AppColors.Teal, modifier = Modifier.size(24.dp))
                        }
                    } else if (imageUrl.isNotBlank()) {
                        Image(
                            painter = rememberAsyncImagePainter(imageUrl),
                            contentDescription = "Product Image",
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(300 .dp)
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

                    // Button to select image
                    Button(
                        onClick = {
                            imagePickerLauncher.launch("image/*")
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = AppColors.Teal),
                        enabled = !isUploadingImage
                    ) {
                        if (isUploadingImage) {
                            CircularProgressIndicator(color = AppColors.Teal, modifier = Modifier.size(24.dp))
                        } else {
                            Text("Select Image", fontSize = 18.sp)
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
                                singleLine = true,
                                colors = TextFieldDefaults.outlinedTextFieldColors(
                                    focusedBorderColor = AppColors.Teal,
                                    unfocusedBorderColor = Color.Black,
                                    cursorColor = AppColors.Teal,
                                    focusedLabelColor = AppColors.Teal,
                                    focusedTextColor = AppColors.Teal
                                )
                            )

                            OutlinedTextField(
                                value = vendor,
                                onValueChange = { vendor = it },
                                label = { Text("Vendor", fontWeight = FontWeight.Bold) },
                                modifier = Modifier.fillMaxWidth(),
                                singleLine = true,
                                colors = TextFieldDefaults.outlinedTextFieldColors(
                                    focusedBorderColor = AppColors.Teal,
                                    unfocusedBorderColor = Color.Black,
                                    cursorColor = AppColors.Teal,
                                    focusedLabelColor = AppColors.Teal,
                                    focusedTextColor = AppColors.Teal
                                )
                            )

                            OutlinedTextField(
                                value = productType,
                                onValueChange = { productType = it },
                                label = { Text("Product Type", fontWeight = FontWeight.Bold) },
                                modifier = Modifier.fillMaxWidth(),
                                singleLine = true,
                                colors = TextFieldDefaults.outlinedTextFieldColors(
                                    focusedBorderColor = AppColors.Teal,
                                    unfocusedBorderColor = Color.Black,
                                    cursorColor = AppColors.Teal,
                                    focusedLabelColor = AppColors.Teal,
                                    focusedTextColor = AppColors.Teal
                                )
                            )
                            if (variants.isNotEmpty()) {
                                val firstVariant = variants.first()
                                OutlinedTextField(
                                    value = firstVariant.price,
                                    onValueChange = { updatedPrice ->
                                        variants = variants.toMutableList().apply {
                                            this[0] = this[0].copy(price = updatedPrice) // Update only the first variant
                                        }
                                    },
                                    label = { Text("Price", fontWeight = FontWeight.Bold) },
                                    modifier = Modifier.fillMaxWidth(),
                                    singleLine = true,
                                    colors = TextFieldDefaults.outlinedTextFieldColors(
                                        focusedBorderColor = AppColors.Teal,
                                        unfocusedBorderColor = Color.Black,
                                        cursorColor = AppColors.Teal,
                                        focusedLabelColor = AppColors.Teal,
                                        focusedTextColor = AppColors.Teal
                                    )
                                )
                            }

                            OutlinedTextField(
                                value = imageUrl,
                                onValueChange = { imageUrl = it },
                                label = { Text("Image URL", fontWeight = FontWeight.Bold) },
                                modifier = Modifier.fillMaxWidth(),
                                singleLine = true,
                                colors = TextFieldDefaults.outlinedTextFieldColors(
                                    focusedBorderColor = AppColors.Teal,
                                    unfocusedBorderColor = Color.Black,
                                    cursorColor = AppColors.Teal,
                                    focusedLabelColor = AppColors.Teal,
                                    focusedTextColor = AppColors.Teal
                                )
                            )
                        }
                    }

                    Button(
                        onClick = {
                            // Update the product with new details
                            val updatedProduct = product.copy(
                                title = productName,
                                vendor = vendor,
                                product_type = productType,
                                images = listOf(com.example.yallabuyadmin.products.model.Image(src = imageUrl)),
                                variants = variants
                            )
                            viewModel.updateProduct(updatedProduct) // Ensure the API call updates the errorMessage state in ViewModel
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = AppColors.Teal),
                        enabled = !isLoading // Disable the button while loading
                    ) {
                        if (isLoading) {
                            CircularProgressIndicator(color = AppColors.Teal, modifier = Modifier.size(24.dp))
                        } else {
                            Text("Update Product", fontSize = 18.sp, color = Color.White)
                        }
                    }
                }

                // Display error or success message at the bottom of the screen
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
                        delay(2000) // Show the message for 3 seconds
                        viewModel.clearSuccess()
                        onBack() // Navigate back after delay
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

