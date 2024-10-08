package com.example.yallabuyadmin.inventory

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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.rememberAsyncImagePainter
import com.example.yallabuyadmin.products.model.Product
import com.example.yallabuyadmin.products.view.uploadImage
import com.example.yallabuyadmin.products.viewmodel.ProductViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UpdateInventoryScreen(
    product: Product,
    viewModel: ProductViewModel = viewModel(),
    onUpdateProduct: () -> Unit,
    onBack: () -> Unit
){
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
        topBar = {
            TopAppBar(
                title = { Text("Update Product", fontSize = 20.sp, color = Color.White) },
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
                    if (isUploadingImage) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(200.dp)
                                .clip(RoundedCornerShape(16.dp)),
                            contentAlignment = Alignment.Center
                        ) {
                            CircularProgressIndicator(color = Color.Black, modifier = Modifier.size(24.dp))
                        }
                    } else if (imageUrl.isNotBlank()) {
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

                    // Button to select image
                    Button(
                        onClick = {
                            imagePickerLauncher.launch("image/*")
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = Color.Black),
                        enabled = !isUploadingImage
                    ) {
                        if (isUploadingImage) {
                            CircularProgressIndicator(color = Color.Black, modifier = Modifier.size(24.dp))
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
                                    focusedBorderColor = Color.Black,
                                    unfocusedBorderColor = Color.Black,
                                    cursorColor = Color.Black,
                                    focusedLabelColor = Color.Black
                                )
                            )

                            OutlinedTextField(
                                value = vendor,
                                onValueChange = { vendor = it },
                                label = { Text("Vendor", fontWeight = FontWeight.Bold) },
                                modifier = Modifier.fillMaxWidth(),
                                singleLine = true,
                                colors = TextFieldDefaults.outlinedTextFieldColors(
                                    focusedBorderColor = Color.Black,
                                    unfocusedBorderColor = Color.Black,
                                    cursorColor = Color.Black,
                                    focusedLabelColor = Color.Black
                                )
                            )

                            OutlinedTextField(
                                value = productType,
                                onValueChange = { productType = it },
                                label = { Text("Product Type", fontWeight = FontWeight.Bold) },
                                modifier = Modifier.fillMaxWidth(),
                                singleLine = true,
                                colors = TextFieldDefaults.outlinedTextFieldColors(
                                    focusedBorderColor = Color.Black,
                                    unfocusedBorderColor = Color.Black,
                                    cursorColor = Color.Black,
                                    focusedLabelColor = Color.Black
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
                                        focusedBorderColor = Color.Black,
                                        unfocusedBorderColor = Color.Black,
                                        cursorColor = Color.Black,
                                        focusedLabelColor = Color.Black
                                    )
                                )
                            }

//                            variants.forEachIndexed { index, variant ->
//                                Column(modifier = Modifier.fillMaxWidth()) {
//                                    OutlinedTextField(
//                                        value = variant.title,
//                                        onValueChange = { updatedTitle ->
//                                            variants = variants.toMutableList().apply {
//                                                this[index] = this[index].copy(title = updatedTitle)
//                                            }
//                                        },
//                                        label = { Text("Variant ${index + 1} Title", fontWeight = FontWeight.Bold) },
//                                        modifier = Modifier.fillMaxWidth(),
//                                        singleLine = true
//                                    )
//                                    OutlinedTextField(
//                                        value = variant.price,
//                                        onValueChange = { updatedPrice ->
//                                            variants = variants.toMutableList().apply {
//                                                this[index] = this[index].copy(price = updatedPrice)
//                                            }
//                                        },
//                                        label = { Text("Variant ${index + 1} Price", fontWeight = FontWeight.Bold) },
//                                        modifier = Modifier.fillMaxWidth(),
//                                        singleLine = true
//                                    )
//                                }
//                            }

                            OutlinedTextField(
                                value = imageUrl,
                                onValueChange = { imageUrl = it },
                                label = { Text("Image URL", fontWeight = FontWeight.Bold) },
                                modifier = Modifier.fillMaxWidth(),
                                singleLine = true,
                                colors = TextFieldDefaults.outlinedTextFieldColors(
                                    focusedBorderColor = Color.Black,
                                    unfocusedBorderColor = Color.Black,
                                    cursorColor = Color.Black,
                                    focusedLabelColor = Color.Black
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
                        colors = ButtonDefaults.buttonColors(containerColor = Color.Black),
                        enabled = !isLoading // Disable the button while loading
                    ) {
                        if (isLoading) {
                            CircularProgressIndicator(color = Color.White, modifier = Modifier.size(24.dp))
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
