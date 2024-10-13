package com.example.yallabuyadmin.inventory

import android.util.Log
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import com.example.yallabuyadmin.products.model.Product
import com.example.yallabuyadmin.products.model.Variant
import com.example.yallabuyadmin.products.viewmodel.ProductViewModel
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextOverflow
import com.example.yallabuyadmin.R
import com.example.yallabuyadmin.network.ApiState
import com.example.yallabuyadmin.ui.theme.AppColors

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UpdateInventoryScreen(
    product: Product,
    viewModel: ProductViewModel = viewModel(),
    onBack: () -> Unit
) {
    // States for product properties
    var productName by remember { mutableStateOf(product.title) }
    var variants by remember { mutableStateOf(product.variants) }
    val context = LocalContext.current

    // Collect loading, error, and success states
    val isLoading by viewModel.isUpdating
    val errorMessage by viewModel.errorMessage.collectAsState()
    val successMessage by viewModel.successMessage.collectAsState()
    val coroutineScope = rememberCoroutineScope()

    // Fetch variants on screen entry
    LaunchedEffect(product.id) {
        viewModel.getVariants(product.id!!)
    }

    // Observe the variants state
    val variantsState by viewModel.variants.collectAsState()

    // Update the local variants state based on the viewModel's state
    when (variantsState) {
        is ApiState.Loading -> {
            // Handle loading state
        }

        is ApiState.Success -> {
            variants = (variantsState as ApiState.Success<List<Variant>>).data
        }

        is ApiState.Error -> {
            // Handle error state
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                modifier = Modifier.padding(top = 8.dp),
                title = {
                    Text(
                        productName,
                        fontSize = 22.sp,
                        color = AppColors.Teal,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.White),
                navigationIcon = {
                    IconButton(onClick = { onBack() }) {
                        Icon(
                            Icons.Default.ArrowBack,
                            contentDescription = "Back",
                            tint = AppColors.Teal
                        )
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                modifier = Modifier.padding(bottom = 60.dp),
                onClick = {
                    viewModel.createVariant(
                        productId = product.id!!,
                        variant = Variant(
                            option1 = "size",
                            price = "0",
                            inventory_quantity = 20L,
                            sku = "",
                            title = ""
                        )
                    )
                },
                containerColor = AppColors.Teal,
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
                    verticalArrangement = Arrangement.spacedBy(24.dp),
                    horizontalArrangement = Arrangement.spacedBy(24.dp)
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
                                        this[index] =
                                            this[index].copy(inventory_quantity = updatedQuantity.toLong())
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
                                if (variants.size == 1) {
                                    Toast.makeText(context, "You can't delete the only variant", Toast.LENGTH_SHORT).show()
                                } else {
                                    viewModel.deleteVariant(product.id!!, variant.id!!)
                                }
                            }
                        )
                    }
                }
                Button(
                    onClick = {
                        variants.forEach { variant ->
                            viewModel.updateVariant(variant.copy(title = "Size"))
                        }
                    },
                    modifier = Modifier
                        .padding(end = 8.dp, start = 8.dp)
                        .fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(containerColor = AppColors.Teal),
                    enabled = !isLoading
                ) {
                    if (isLoading) {
                        CircularProgressIndicator(
                            color = Color.White,
                            modifier = Modifier.size(24.dp)
                        )
                    } else {
                        Text("Update Variant", fontSize = 18.sp)
                    }
                }

                // Display error or success message
                errorMessage?.let {
                    coroutineScope.launch {
                        delay(1500)
                        viewModel.clearError()
                    }
                    Snackbar {
                        Text("Error: $it", color = Color.Red)
                    }
                }

                successMessage?.let {
                    coroutineScope.launch {
                        delay(500)
                        viewModel.getVariants(product.id!!)
                        delay(1500)
                        viewModel.clearSuccess()
                        //onBack()
                    }
                    Snackbar {
                        Text("Success: $it", color = Color.Green)
                    }
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
    var showDeleteDialog by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(12.dp),  // Added padding to create space around the card
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        shape = RoundedCornerShape(12.dp),  // Increased the corner radius
        border = BorderStroke(1.dp, AppColors.Teal)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Size input field
            OutlinedTextField(
                value = variant.option1,
                onValueChange = onOption1Change,
                label = { Text("Size") },
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

            // Color input field
            OutlinedTextField(
                value = variant.option2 ?: "",  // Use empty string if option2 is null
                onValueChange = onOption2Change,
                label = { Text("Color") },
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


            // Price input field (allowing only numbers)
            OutlinedTextField(
                value = variant.price,
                onValueChange = onPriceChange,
                label = { Text("Price") },
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number),
                singleLine = true,
                colors = TextFieldDefaults.outlinedTextFieldColors(
                    focusedBorderColor = AppColors.Teal,
                    unfocusedBorderColor = Color.Black,
                    cursorColor = AppColors.Teal,
                    focusedLabelColor = AppColors.Teal,
                    focusedTextColor = AppColors.Teal
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
                enabled = false,
                colors = TextFieldDefaults.outlinedTextFieldColors(
                    focusedBorderColor = AppColors.Teal,
                    unfocusedBorderColor = Color.Black,
                    cursorColor = AppColors.Teal,
                    focusedLabelColor = AppColors.Teal,
                    focusedTextColor = AppColors.Teal
                )
            )
            TextButton(
                onClick = {
                    showDeleteDialog = true
                }, // Show the dialog instead of deleting directly
                modifier = Modifier
                    .padding(vertical = 8.dp)
                    .fillMaxWidth(),
                colors = ButtonDefaults.textButtonColors(contentColor = Color.Red),
                shape = RoundedCornerShape(12.dp)
            ) {
                Image(
                    painter = painterResource(id = R.drawable.trash),
                    contentDescription = "Delete",
                    modifier = Modifier.size(24.dp)
                )
                Text(
                    "Delete Variant",
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(start = 4.dp)
                )
            }

            // Confirmation Dialog
            if (showDeleteDialog) {
                AlertDialog(
                    containerColor = Color.White,
                    onDismissRequest = { showDeleteDialog = false },
                    title = { Text("Delete Variant", color = AppColors.Teal) },
                    text = {
                        Text(
                            "Are you sure you want to delete this variant?",
                            color = Color.Black
                        )
                    },
                    confirmButton = {
                        TextButton(
                            onClick = {
                                onDelete() // Call the delete action
                                showDeleteDialog = false // Hide the dialog
                            }
                        ) {
                            Text("Yes", color = Color.Red)
                        }
                    },
                    dismissButton = {
                        TextButton(onClick = { showDeleteDialog = false }) {
                            Text("No", color = AppColors.Teal)
                        }
                    }
                )
            }
        }
    }
}
