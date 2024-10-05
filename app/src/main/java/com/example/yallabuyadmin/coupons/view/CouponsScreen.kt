package com.example.yallabuyadmin.coupons.view

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.yallabuyadmin.coupons.viewmodel.CouponsViewModel
import com.example.yallabuyadmin.coupons.model.DiscountCode
import com.example.yallabuyadmin.coupons.model.PriceRule
import com.example.yallabuyadmin.coupons.model.priceRuleRequest

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CouponsScreen(onBack: () -> Unit, onNavigateToDiscount: (Long) -> Unit,viewModel: CouponsViewModel) {
    val discountCodes by viewModel.discountCodes.collectAsState()
    val priceRules by viewModel.priceRules.collectAsState()
    // State for dialogs
    var showDiscountCodeDialog by remember { mutableStateOf(false) }
    var showPriceRuleDialog by remember { mutableStateOf(false) }
    var currentDiscountCode by remember { mutableStateOf<DiscountCode?>(null) }
    var currentPriceRule by remember { mutableStateOf<PriceRule?>(null) } // New state for price rule

    // Fetch price rules when the screen is launched
    LaunchedEffect(Unit) {
        viewModel.fetchPriceRules()
    }

    // Dialogs for discount codes and price rules
//    if (showDiscountCodeDialog) {
//        DiscountCodeDialog(
//            onDismiss = { showDiscountCodeDialog = false },
//            onSubmit = { discountCode ->
//                currentDiscountCode?.let {
//                    viewModel.updateDiscountCode(it.id, discountCode)
//                } ?: run {
//                    viewModel.createDiscountCode(1L, discountCode) // Replace with actual priceRuleId
//                }
//                currentDiscountCode = null
//                showDiscountCodeDialog = false
//            },
//            discountCode = currentDiscountCode
//        )
//    }

    if (showPriceRuleDialog) {
        PriceRuleDialog(
            onDismiss = {
                showPriceRuleDialog = false
                currentPriceRule = null // Reset the current price rule
            },
            onSubmit = { priceRule ->
                currentPriceRule?.let {
                    viewModel.updatePriceRule(it.id!!, priceRule) // Update existing price rule
                } ?: run {
                    viewModel.createNewPriceRule(priceRule) // Create new price rule
                }
                showPriceRuleDialog = false
            },
            priceRule = currentPriceRule // Pass the current price rule for editing
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Coupons Management") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = {
                currentPriceRule = null // Reset for new price rule
                showPriceRuleDialog = true
            }) {
                Text("+")
            }
        },
        content = { padding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .padding(16.dp)
            ) {
                Text("Price Rules", style = MaterialTheme.typography.titleLarge)

                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(priceRules) { priceRule ->
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(8.dp),
                            elevation = CardDefaults.cardElevation(4.dp)
                        ) {
                            Column(
                                modifier = Modifier
                                    .padding(16.dp)
                            ) {
                                Text(
                                    text = "Title: ${priceRule.title}",
                                    style = MaterialTheme.typography.titleMedium
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = "Discount Percentage: ${priceRule.value}",
                                    style = MaterialTheme.typography.bodyMedium
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = "Starts At: ${priceRule.starts_at}",
                                    style = MaterialTheme.typography.bodyMedium
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = "Ends At: ${priceRule.ends_at}",
                                    style = MaterialTheme.typography.bodyMedium
                                )
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.End
                                ) {
                                    IconButton(onClick = {
                                        currentPriceRule = priceRule // Set the current price rule for editing
                                        showPriceRuleDialog = true // Show the dialog
                                    }) {
                                        Icon(Icons.Default.Edit, contentDescription = "Edit")
                                    }
                                    IconButton(onClick = {
                                        viewModel.deletePriceRule(priceRule.id!!)
                                    }) {
                                        Icon(Icons.Default.Delete, contentDescription = "Delete")
                                    }
                                    IconButton(onClick = {
                                        onNavigateToDiscount(priceRule.id!!)
                                    }) {
                                        Icon(Icons.Default.PlayArrow, contentDescription = "Delete")
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    )
}

@Composable
fun PriceRuleDialog(
    onDismiss: () -> Unit,
    onSubmit: (priceRuleRequest) -> Unit,
    priceRule: PriceRule? // Pass in an existing PriceRule for editing, or null for new
) {
    var ruleName by remember { mutableStateOf(priceRule?.title ?: "") }
    var discountPercentage by remember { mutableStateOf(priceRule?.value?.toString() ?: "") }
    var valueType by remember { mutableStateOf(priceRule?.value_type ?: "") }
    var startsAt by remember { mutableStateOf(priceRule?.starts_at ?: "") }
    var endsAt by remember { mutableStateOf(priceRule?.ends_at ?: "") }
    var targetSelection by remember { mutableStateOf(priceRule?.target_selection ?: "") }
    var allocationMethod by remember { mutableStateOf(priceRule?.allocation_method ?: "") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Add/Edit Price Rule") },
        text = {
            Column {
                TextField(
                    value = ruleName,
                    onValueChange = { ruleName = it },
                    label = { Text("Title") },
                    isError = ruleName.isEmpty()
                )
                TextField(
                    value = discountPercentage,
                    onValueChange = { discountPercentage = it },
                    label = { Text("Discount Percentage") },
                    isError = discountPercentage.toDoubleOrNull() == null || discountPercentage.toDouble() < 0
                )
                TextField(
                    value = valueType,
                    onValueChange = { valueType = it },
                    label = { Text("Value Type") },
                    isError = valueType.isEmpty()
                )
                TextField(
                    value = startsAt,
                    onValueChange = { startsAt = it },
                    label = { Text("Starts At") },
                    isError = startsAt.isEmpty()
                )
                TextField(
                    value = endsAt,
                    onValueChange = { endsAt = it },
                    label = { Text("Ends At") },
                    isError = endsAt.isEmpty()
                )
                TextField(
                    value = targetSelection,
                    onValueChange = { targetSelection = it },
                    label = { Text("Target Selection") },
                    isError = targetSelection.isEmpty()
                )
                TextField(
                    value = allocationMethod,
                    onValueChange = { allocationMethod = it },
                    label = { Text("Allocation Method") },
                    isError = allocationMethod.isEmpty()
                )
            }
        },
        confirmButton = {
            Button(onClick = {
                onSubmit(
                    priceRuleRequest(
                        PriceRule(
                            id = priceRule?.id, // Pass the existing id if editing
                            title = ruleName,
                            target_type = "line_item", // Adjust this as needed
                            value_type = valueType,
                            value = discountPercentage.toDoubleOrNull() ?: 0.0,
                            starts_at = startsAt,
                            ends_at = endsAt,
                            target_selection = targetSelection,
                            allocation_method = allocationMethod,
                            customer_selection = "all" // Adjust this as needed
                        )
                    )
                )
            }) {
                Text("Submit")
            }
        },
        dismissButton = {
            Button(onClick = onDismiss) {
                Text("Dismiss")
            }
        }
    )
}

//@Composable
//fun DiscountCodeDialog(
//    onDismiss: () -> Unit,
//    onSubmit: (DiscountCode) -> Unit,
//    discountCode: DiscountCode?
//) {
//    var code by remember { mutableStateOf(discountCode?.code ?: "") }
//    var usageCount by remember { mutableStateOf(discountCode?.usageCount?.toString() ?: "") }
//
//    AlertDialog(
//        onDismissRequest = onDismiss,
//        title = { Text("Add/Edit Discount Code") },
//        text = {
//            Column {
//                TextField(
//                    value = code,
//                    onValueChange = { code = it },
//                    label = { Text("Code") }
//                )
//                TextField(
//                    value = usageCount,
//                    onValueChange = { usageCount = it },
//                    label = { Text("Usage Count") }
//                )
//            }
//        },
//        confirmButton = {
//            Button(onClick = {
//                val usageCountInt = usageCount.toIntOrNull() ?: 0
//
//                if (discountCode != null) {
//                    // Update existing DiscountCode
//                    onSubmit(discountCode.copy(code = code, usageCount = usageCountInt))
//                } else {
//                    // Create a new DiscountCode
//                    onSubmit(
//                        DiscountCode(
//                            id = 0, // Placeholder, will be generated by the server
//                            code = code,
//                            usageCount = usageCountInt,
//                            createdAt = "" // Placeholder, adjust accordingly
//                        )
//                    )
//                }
//            }) {
//                Text("Submit")
//            }
//        },
//        dismissButton = {
//            Button(onClick = onDismiss) {
//                Text("Dismiss")
//            }
//        }
//    )
//}

