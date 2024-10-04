package com.example.yallabuyadmin.products.view

import androidx.compose.foundation.Image
import androidx.compose.runtime.*
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Star
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.rememberAsyncImagePainter
import com.example.yallabuyadmin.network.ApiState
import com.example.yallabuyadmin.products.model.Product
import com.example.yallabuyadmin.products.viewmodel.ProductViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProductManagementScreen(
    viewModel: ProductViewModel,
    onNavigateToUpdate: (Product) -> Unit,
    onNavigateToCreate: () -> Unit,
    onBack: () -> Unit
) {
    val productsState by viewModel.apiState.collectAsState()
    val successMessage by viewModel.successMessage.collectAsState()
    var productToDelete by remember { mutableStateOf<Product?>(null) }
    var snackbarVisible by remember { mutableStateOf(false) }
    var snackbarMessage by remember { mutableStateOf("") }
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(Unit) {
        viewModel.getAllProducts()
    }

    if (snackbarVisible) {
        LaunchedEffect(snackbarMessage) {
            snackbarHostState.showSnackbar(snackbarMessage)
            snackbarVisible = false
        }
    }

    LaunchedEffect(successMessage) {
        successMessage?.let {
            snackbarMessage = it
            snackbarVisible = true
            viewModel.clearSuccess()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Product Management") },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary
                ),
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                },
                actions = {
                    IconButton(onClick = onNavigateToCreate) {
                        Icon(Icons.Default.Add, contentDescription = "Create Product")
                    }
                }
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) {
            Snackbar(
                modifier = Modifier.padding(8.dp),
                containerColor = Color.Red,
                contentColor = Color.White,
                actionColor = Color.White,
                snackbarData = it
            )
        }},
        content = { padding ->
            when (productsState) {
                is ApiState.Loading -> {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator()
                    }
                }
                is ApiState.Error -> {
                    val errorMessage = (productsState as ApiState.Error).message
                    Text(
                        text = errorMessage,
                        color = Color.Red,
                        modifier = Modifier.padding(16.dp)
                    )
                }
                is ApiState.Success -> {
                    val productList = (productsState as ApiState.Success<List<Product>>).data
                    LazyVerticalGrid(
                        columns = GridCells.Fixed(2), // Specify 2 columns
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(padding)
                            .padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp) // Adjust space between columns
                    ) {
                        items(productList) { product ->
                            ProductCard(
                                product = product,
                                onSelect = {
                                    onNavigateToUpdate(product)
                                },
                                onDelete = {
                                    productToDelete = product
                                },
                                isDeleting = viewModel.deletingProducts[product.id] ?: false
                            )
                        }
                    }
                }
            }
        }
    )

    // Confirmation dialog for deletion
    productToDelete?.let { product ->
        AlertDialog(
            onDismissRequest = { productToDelete = null },
            title = { Text("Confirm Delete") },
            text = { Text("Are you sure you want to delete '${product.title}'? This action cannot be undone.") },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.deleteProduct(product.id!!)
                        productToDelete = null
                    }
                ) {
                    Text("Delete")
                }
            },
            dismissButton = {
                TextButton(onClick = { productToDelete = null }) {
                    Text("Cancel")
                }
            }
        )
    }
}


@Composable
fun ProductCard(product: Product, onSelect: () -> Unit, onDelete: () -> Unit, isDeleting: Boolean) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
            .clickable { onSelect() },
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(8.dp)
    ) {
        Column(
            modifier = Modifier
                .background(MaterialTheme.colorScheme.surface)
                .padding(16.dp)
                .height(300.dp) // Set a fixed height for uniformity
        ) {
            // Load and display the first image of the product, if available
            product.images.firstOrNull()?.let { image ->
                Image(
                    painter = rememberAsyncImagePainter(image.src),
                    contentDescription = "Product Image",
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(100.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(Color.LightGray),
                    contentScale = ContentScale.Crop
                )
                Spacer(modifier = Modifier.height(8.dp))
            }

            // Product Title
            Text(
                text = product.title,
                style = MaterialTheme.typography.titleMedium.copy(fontSize = 18.sp),
                maxLines = 2,
                modifier = Modifier.padding(horizontal = 8.dp)
            )

            Spacer(modifier = Modifier.height(4.dp))

            // Vendor Name
            Text(
                text = "by ${product.vendor}",
                style = MaterialTheme.typography.bodySmall.copy(color = Color.Gray),
                modifier = Modifier.padding(horizontal = 8.dp)
            )

            Spacer(modifier = Modifier.height(4.dp))

            // Product Price
            if (product.variants.isNotEmpty()) {
                val price = product.variants.first().price
                Text(
                    text = "Price: $${price}",
                    style = MaterialTheme.typography.bodyLarge.copy(fontSize = 16.sp, color = MaterialTheme.colorScheme.primary),
                    modifier = Modifier.padding(horizontal = 8.dp)
                )
            }

            Spacer(modifier = Modifier.height(4.dp))

            // Ratings and Reviews (mocked for UI demonstration)
            Row(
                modifier = Modifier.padding(horizontal = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                repeat(5) {
                    Icon(
                        imageVector = Icons.Default.Add, // Replace this with a star icon in a real scenario
                        contentDescription = null,
                        tint = Color(0xFFFFD700),
                        modifier = Modifier.size(16.dp)
                    )
                }
                Spacer(modifier = Modifier.width(8.dp))
                Text(text = "(120 reviews)", style = MaterialTheme.typography.bodySmall.copy(color = Color.Gray))
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Action Button: Delete with FAB
            Box(
                contentAlignment = Alignment.TopEnd,
                modifier = Modifier.fillMaxWidth()
            ) {
                FloatingActionButton(
                    onClick = onDelete,
                    containerColor = MaterialTheme.colorScheme.error, // Use containerColor instead of backgroundColor
                    modifier = Modifier
                        .padding(8.dp)
                        .wrapContentHeight()
                ) {
                    if (isDeleting) {
                        CircularProgressIndicator(color = Color.White, modifier = Modifier.size(24.dp))
                    } else {
                        Icon(Icons.Default.Delete, contentDescription = "Delete Product")
                    }
                }
            }
        }
    }
}
@Composable
fun text(){
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
            .clickable { },
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(8.dp)
    ){
        Text(
            text = "hello",
            style = MaterialTheme.typography.titleMedium.copy(fontSize = 18.sp),
            maxLines = 2,
            modifier = Modifier.padding(horizontal = 8.dp)
        )
    }
}
@Preview(showBackground = true, showSystemUi = true)
@Composable
fun ProductCardPreview() {
    val product = Product(body_html = "test", title = "Nour", product_type = "Nour", tags = "Nour", vendor = "Nour")
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
            .clickable { },
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(8.dp)
    ) {
        Column(
            modifier = Modifier
                .background(MaterialTheme.colorScheme.surface)
                .padding(16.dp)
                .height(1.dp) // Set a fixed height for uniformity
        ) {
            // Load and display the first image of the product, if available
            product.images.firstOrNull()?.let { image ->
                Image(
                    painter = rememberAsyncImagePainter(image.src),
                    contentDescription = "Product Image",
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(100.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(Color.LightGray),
                    contentScale = ContentScale.Crop
                )
                Spacer(modifier = Modifier.height(8.dp))
            }

            // Product Title
            Text(
                text = product.title,
                style = MaterialTheme.typography.titleMedium.copy(fontSize = 18.sp),
                maxLines = 2,
                modifier = Modifier.padding(horizontal = 8.dp)
            )

            Spacer(modifier = Modifier.height(4.dp))

            // Vendor Name
            Text(
                text = "by ${product.vendor}",
                style = MaterialTheme.typography.bodySmall.copy(color = Color.Gray),
                modifier = Modifier.padding(horizontal = 8.dp)
            )

            Spacer(modifier = Modifier.height(4.dp))

            // Product Price
            if (product.variants.isNotEmpty()) {
                val price = product.variants.first().price
                Text(
                    text = "Price: $${price}",
                    style = MaterialTheme.typography.bodyLarge.copy(fontSize = 16.sp, color = MaterialTheme.colorScheme.primary),
                    modifier = Modifier.padding(horizontal = 8.dp)
                )
            }

            Spacer(modifier = Modifier.height(4.dp))

            // Ratings and Reviews (mocked for UI demonstration)
            Row(
                modifier = Modifier.padding(horizontal = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                repeat(5) {
                    Icon(
                        imageVector = Icons.Default.Add, // Replace this with a star icon in a real scenario
                        contentDescription = null,
                        tint = Color(0xFFFFD700),
                        modifier = Modifier.size(16.dp)
                    )
                }
                Spacer(modifier = Modifier.width(8.dp))
                Text(text = "(120 reviews)", style = MaterialTheme.typography.bodySmall.copy(color = Color.Gray))
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Action Button: Delete with FAB
            Box(
                contentAlignment = Alignment.TopEnd,
                modifier = Modifier.fillMaxWidth()
            ) {
                FloatingActionButton(
                    onClick = {},
                    containerColor = MaterialTheme.colorScheme.error, // Use containerColor instead of backgroundColor
                    modifier = Modifier.padding(8.dp)
                ) {

                }
            }
        }
    }
}