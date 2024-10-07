package com.example.yallabuyadmin.products.view

import android.annotation.SuppressLint
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import android.content.Context
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import com.example.yallabuyadmin.R
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import kotlinx.coroutines.tasks.await
import java.util.UUID
import com.example.yallabuyadmin.products.model.Image as ProductImage

@SuppressLint("CoroutineCreationDuringComposition")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateProductScreen(
    viewModel: ProductViewModel = viewModel(),
    onCreateProduct: () -> Unit,
    onBack: () -> Unit
) {
    // State for product details
    var productName by remember { mutableStateOf("") }
    var vendor by remember { mutableStateOf("") }
    var productType by remember { mutableStateOf("") }
    var variants by remember { mutableStateOf("") }
    var imageUrl by remember { mutableStateOf("") }
    var price by remember { mutableStateOf("") }

    // State for loading and messages
    val isLoading by viewModel.isLoading.collectAsState()
    val errorMessage by viewModel.errorMessage.collectAsState()
    val successMessage by viewModel.successMessage.collectAsState()

    // State for image uploading
    var isUploadingImage by remember { mutableStateOf(false) }

    // Coroutine scope for handling async tasks
    val coroutineScope = rememberCoroutineScope()

    // Image picker
    val context = LocalContext.current
    val imagePickerLauncher = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri ->
        uri?.let {
            isUploadingImage = true // Set uploading state to true
            coroutineScope.launch {
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
                title = { Text("Create Product", fontSize = 20.sp, color = Color.White) },
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
                        .background(Color.White)
                        .verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // Image section with picker button
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

                    // Other input fields
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

//                    OutlinedTextField(
//                        value = variants,
//                        onValueChange = { variants = it },
//                        label = { Text("Variants (Comma separated)", fontWeight = FontWeight.Bold) },
//                        modifier = Modifier.fillMaxWidth(),
//                        singleLine = true
//                    )
                            OutlinedTextField(
                                value = price,
                                onValueChange = { price = it },
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
                            val variantList = variants.split(",").map { title ->
                                Variant(title = title.trim(), price = price, sku = "")
                            }
                            val newProduct = Product(
                                title = productName,
                                body_html = "Sample Description",
                                vendor = vendor,
                                product_type = productType,
                                tags = "Sample Tag",
                                images = listOf(ProductImage(src = imageUrl)),
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
                        Text("Error: $errorMessage", color = Color.Red)
                    }
                }
            }
        }
    )
}


//upload an image and get the URL
suspend fun uploadImage(context: Context, uri: Uri): String? {
    return try {
        val storageRef: StorageReference = FirebaseStorage.getInstance().reference
        val imageRef: StorageReference = storageRef.child("images/${UUID.randomUUID()}.jpg") // Create a unique filename

        // Upload the image
        imageRef.putFile(uri).await() // Await for the upload to complete

        // Get the URL of the uploaded image
        imageRef.downloadUrl.await().toString()
    } catch (e: Exception) {
        e.printStackTrace()
        null // Return null if there is an error
    }
}

@Preview(showBackground = true)
@Composable
fun CreateProductScreenPreview() {
    CreateProductScreen(
        onCreateProduct = {},
        onBack = {}
    )
}

