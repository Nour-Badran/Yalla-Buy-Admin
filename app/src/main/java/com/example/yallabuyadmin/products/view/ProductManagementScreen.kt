package com.example.yallabuyadmin.products.view

import androidx.compose.foundation.BorderStroke
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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.rememberAsyncImagePainter
import com.example.yallabuyadmin.R
import com.example.yallabuyadmin.network.ApiState
import com.example.yallabuyadmin.products.model.Product
import com.example.yallabuyadmin.products.viewmodel.ProductViewModel
import com.example.yallabuyadmin.ui.theme.AppColors

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
    var searchQuery by remember { mutableStateOf("") }

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
                title = { Text("Price Rules", color = AppColors.Teal) },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.White),
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Image(
                            painter = painterResource(id = R.drawable.back_arrow),
                            contentDescription = "back arrow",
                            modifier = Modifier.size(24.dp)
                        )
                    }
                },
                actions = {
                    IconButton(onClick = onNavigateToCreate) {
                        Icon(Icons.Default.Add, contentDescription = "Create Product", tint = AppColors.Teal)
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
            Column(modifier = Modifier.padding(padding)) {
                // Search Bar
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    label = { Text("Search Products") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    shape = RoundedCornerShape(20.dp),
                    colors =  OutlinedTextFieldDefaults.colors(unfocusedBorderColor = AppColors.Teal
                    , focusedBorderColor = AppColors.Teal,
                        focusedLabelColor = AppColors.Teal,
                        cursorColor = AppColors.Teal
                    )
                )
                when (productsState) {
                    is ApiState.Loading -> {
                        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(48.dp),
                                color = Color.Black
                            )
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
                        val filteredProducts = productList.filter {
                            it.title.contains(searchQuery, ignoreCase = true) ||
                                    it.vendor.contains(searchQuery, ignoreCase = true)
                        }
                        LazyVerticalGrid(
                            columns = GridCells.Fixed(2),
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(8.dp),
                            verticalArrangement = Arrangement.spacedBy(2.dp),
                            horizontalArrangement = Arrangement.spacedBy(2.dp)
                        ) {
                            items(filteredProducts) { product ->
                                ProductCard(
                                    product = product,
                                    onSelect = { onNavigateToUpdate(product) },
                                    onDelete = { productToDelete = product },
                                    isDeleting = viewModel.deletingProducts[product.id] ?: false
                                )
                            }
                        }
                    }
                }
            }
        }
    )
    // Confirmation dialog for deletion
    productToDelete?.let { product ->
        AlertDialog(
            containerColor = Color.White,
            onDismissRequest = { productToDelete = null },
            title = { Text("Confirm Delete") },
            text = { Text("Are you sure you want to delete '${product.title}'? This action cannot be undone.") },
            confirmButton = {
                TextButton(onClick = {
                    viewModel.deleteProduct(product.id!!)
                    productToDelete = null
                }) {
                    Text("Delete", color = Color.Red)
                }
            },
            dismissButton = {
                TextButton(onClick = { productToDelete = null }) {
                    Text("Cancel", color = Color.Black)
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
        elevation = CardDefaults.cardElevation(8.dp),
        border = BorderStroke(.5.dp, AppColors.Teal)
    ) {
        Column(
            modifier = Modifier
                .background(MaterialTheme.colorScheme.surface)
                .padding(16.dp)
                .wrapContentHeight()
        ) {
            product.images.firstOrNull()?.let { image ->
                Image(
                    painter = rememberAsyncImagePainter(image.src),
                    contentDescription = "Product Image",
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(120.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(Color.LightGray),
                    contentScale = ContentScale.Crop
                )
            } ?: Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(120.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color.LightGray),
                contentAlignment = Alignment.Center
            ) {
                Text("No Image Available", color = Color.DarkGray, textAlign = TextAlign.Center)
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = product.title,
                style = MaterialTheme.typography.titleMedium.copy(fontSize = 20.sp, fontWeight = FontWeight.Bold),
                maxLines = 2,
                modifier = Modifier.padding(horizontal = 8.dp)
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = "by ${product.vendor}",
                style = MaterialTheme.typography.bodyLarge.copy(fontSize = 16.sp, color = Color.Gray),
                modifier = Modifier.padding(horizontal = 8.dp)
            )

            Spacer(modifier = Modifier.height(4.dp))

            Row(modifier = Modifier.fillMaxSize(), verticalAlignment = Alignment.CenterVertically) {
                if (product.variants.isNotEmpty()) {
                    val price = product.variants.first().price
                    val annotatedString = buildAnnotatedString {
                        withStyle(style = SpanStyle(color = AppColors.Teal)) {
                            append("\$${price}")
                        }
                    }
                    Text(
                        text = annotatedString,
                        style = MaterialTheme.typography.bodyLarge.copy(fontSize = 16.sp),
                        modifier = Modifier.weight(1f).padding(horizontal = 8.dp)
                    )

                    Image(
                            painter = painterResource(id = R.drawable.trash),
                            contentDescription = "Delete",
                            modifier = Modifier.size(24.dp)
                            .clickable { onDelete() }

                        )
                }
            }
        }
    }

}

@Preview(showSystemUi = true, showBackground = true)
@Composable
fun ProductCardPre() {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
            .clickable { /*onSelect()*/ },
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(8.dp),
        border = BorderStroke(1.dp,Color.Black)
    ) {
        Column(
            modifier = Modifier
                .background(MaterialTheme.colorScheme.surface)
                .padding(16.dp)
                .height(300.dp) // Set a fixed height for uniformity
        ) {
            // Load and display the first image of the product, if available

                Image(
                    painter = rememberAsyncImagePainter(Icons.Default),
                    contentDescription = "Product Image",
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(100.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(Color.LightGray),
                    contentScale = ContentScale.Crop
                )
                Spacer(modifier = Modifier.height(8.dp))


            // Product Title
            Text(
                text = "product.title",
                style = MaterialTheme.typography.titleMedium.copy(fontSize = 18.sp),
                maxLines = 2,
                modifier = Modifier.padding(horizontal = 8.dp)
            )

            Spacer(modifier = Modifier.height(4.dp))

            // Vendor Name
            Text(
                text = "by product.vendor",
                style = MaterialTheme.typography.bodySmall.copy(color = Color.Gray),
                modifier = Modifier.padding(horizontal = 8.dp)
            )

            Spacer(modifier = Modifier.height(4.dp))

            // Product Price

                Text(
                    text = "Price:price",
                    style = MaterialTheme.typography.bodyLarge.copy(fontSize = 16.sp, color = Color.Cyan),
                    modifier = Modifier.padding(horizontal = 8.dp)
                )


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
                        tint = Color.Cyan,
                        modifier = Modifier.size(16.dp)
                    )
                }
                Spacer(modifier = Modifier.width(8.dp))
                Text(text = "(120 reviews)", style = MaterialTheme.typography.bodySmall.copy(color = Color.Gray))
            }

            Spacer(modifier = Modifier.height(12.dp))
            FloatingActionButton(
                onClick = { /*onDelete*/ },
                containerColor = Color.White,
                modifier = Modifier
                    .padding(8.dp)
                    .wrapContentHeight()
            ) {
                if (false) {
                    CircularProgressIndicator(color = Color.White, modifier = Modifier.size(24.dp))
                } else {
                    Icon(painter = painterResource(id = R.drawable.trash),
                        contentDescription = "Delete Product",tint = Color.Red)

                }
            }

            // Action Button: Delete with FAB
            Box(
                contentAlignment = Alignment.TopEnd,
                modifier = Modifier.fillMaxWidth()
            ) {

            }
        }
    }
}
