package com.example.yallabuyadmin.inventory

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.rememberAsyncImagePainter
import com.example.yallabuyadmin.network.ApiState
import com.example.yallabuyadmin.products.model.Product
import com.example.yallabuyadmin.products.view.ProductCard
import com.example.yallabuyadmin.products.viewmodel.ProductViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InventoryScreen(
    onBack: () -> Unit,
    viewModel: ProductViewModel,
    onNavigateToUpdate: (Product) -> Unit
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
                title = { Text("Inventory", color = Color.White) },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Black),
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = Color.White)
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
        }
        },
        content = { padding ->
            Column(modifier = Modifier.padding(padding)) {
                // Search Bar
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    label = { Text("Search") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    colors = TextFieldDefaults.outlinedTextFieldColors(
                        focusedBorderColor = Color.Black,
                        unfocusedBorderColor = Color.Black,
                        cursorColor = Color.Black,
                        focusedLabelColor = Color.Black
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
                            columns = GridCells.Adaptive(minSize = 128.dp),
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(16.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            items(filteredProducts) { product ->
                                InventoryCard(
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
fun InventoryCard(product: Product, onSelect: () -> Unit, onDelete: () -> Unit, isDeleting: Boolean) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
            .clickable { onSelect() },
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(8.dp),
        border = BorderStroke(1.dp,Color.DarkGray)
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
            } ?: run {
                // Placeholder content if there is no image
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(120.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(Color.LightGray),
                    contentAlignment = Alignment.Center // Center content in the box
                ) {
                    Text("No Image Available", color = Color.DarkGray,textAlign = TextAlign.Center)
                }
            }

            Spacer(modifier = Modifier.height(8.dp))


            // Product Title
            Text(
                text = product.title,
                style = MaterialTheme.typography.titleMedium.copy(fontSize = 20.sp, fontWeight = FontWeight.Bold),
                maxLines = 2,
                modifier = Modifier.padding(horizontal = 8.dp)
            )

            Spacer(modifier = Modifier.height(4.dp))

            // Vendor Name
            Text(
                text = "by ${product.vendor}",
                style = MaterialTheme.typography.bodyLarge.copy(fontSize = 16.sp, color = Color.Gray),
                modifier = Modifier.padding(horizontal = 8.dp)
            )
//
//            Spacer(modifier = Modifier.height(4.dp))
//
//            // Product Price
//            Row(modifier = Modifier.fillMaxSize())
//            {
//                if (product.variants.isNotEmpty()) {
//                    val price = product.variants.first().price
//                    val annotatedString = buildAnnotatedString {
//                        withStyle(style = SpanStyle(color = Color.Black)) {
//                            append("Price: \$")
//                        }
//                        withStyle(style = SpanStyle(color = Color.Green)) {
//                            append("${price}")
//                        }
//                    }
//                    Text(
//                        text = annotatedString,
//                        style = MaterialTheme.typography.bodyLarge.copy(fontSize = 16.sp),
//                        modifier = Modifier.padding(horizontal = 8.dp)
//                    )
//
//                    Icon(Icons.Default.Delete, contentDescription = "Delete", tint = Color.Red,
//                        modifier = Modifier
//                            .wrapContentHeight()
//                            .clickable { onDelete() })
//                }
//            }
        }
    }
}