package com.example.yallabuyadmin.coupons

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CouponsScreen(onBack: () -> Unit, viewModel: CouponsViewModel) {
    val discountCodes by viewModel.discountCodes.collectAsState()
    val priceRules by viewModel.priceRules.collectAsState()
    // State for dialogs
    var showDiscountCodeDialog by remember { mutableStateOf(false) }
    var showPriceRuleDialog by remember { mutableStateOf(false) }
    var currentDiscountCode by remember { mutableStateOf<DiscountCode?>(null) }

    // Fetch price rules when the screen is launched
    LaunchedEffect(Unit) {
        viewModel.fetchPriceRules()
    }

    // Dialogs for discount codes and price rules
    if (showDiscountCodeDialog) {
        DiscountCodeDialog(
            onDismiss = { showDiscountCodeDialog = false },
            onSubmit = { discountCode ->
                currentDiscountCode?.let {
                    viewModel.updateDiscountCode(it.id, discountCode)
                } ?: run {
                    viewModel.createDiscountCode(1L, discountCode) // Replace with actual priceRuleId
                }
                currentDiscountCode = null
                showDiscountCodeDialog = false
            },
            discountCode = currentDiscountCode
        )
    }

    if (showPriceRuleDialog) {
        PriceRuleDialog(
            onDismiss = { showPriceRuleDialog = false },
            onSubmit = { priceRule ->
                viewModel.createNewPriceRule(priceRule)
                showPriceRuleDialog = false
            },
            priceRule = null
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
                                        // Handle editing the price rule (open dialog with existing data)
                                    }) {
                                        Icon(Icons.Default.Edit, contentDescription = "Edit")
                                    }
                                    IconButton(onClick = {
                                        viewModel.deletePriceRule(priceRule.id!!)
                                    }) {
                                        Icon(Icons.Default.Delete, contentDescription = "Delete")
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
    var discountPercentage by remember { mutableStateOf(priceRule?.value ?: "") }
    var valueType by remember { mutableStateOf(priceRule?.value_type ?: "") } // Updated field
    var startsAt by remember { mutableStateOf(priceRule?.starts_at ?: "") }
    var endsAt by remember { mutableStateOf(priceRule?.ends_at ?: "") } // New field for endsAt
    var targetSelection by remember { mutableStateOf(priceRule?.target_selection ?: "") } // Updated field
    var allocationMethod by remember { mutableStateOf(priceRule?.allocation_method ?: "") } // Updated field

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Add/Edit Price Rule") },
        text = {
            Column {
                TextField(
                    value = ruleName,
                    onValueChange = { ruleName = it },
                    label = { Text("Title") },
                    isError = ruleName.isEmpty() // Show error if the field is empty
                )
                var discountPercentage by remember { mutableStateOf(0.0) } // Initialize as Double

                TextField(
                    value = discountPercentage.toString(), // Convert Double to String for display
                    onValueChange = {
                        // Try to convert the input to a Double
                        discountPercentage = it.toDoubleOrNull() ?: 0.0 // Set to 0.0 if conversion fails
                    },
                    label = { Text("Discount Percentage") },
                    isError = discountPercentage < 0 // Show error if the value is negative
                )
                TextField(
                    value = valueType,
                    onValueChange = { valueType = it },
                    label = { Text("Value Type") },
                    isError = valueType.isEmpty() // Show error if the field is empty
                )
                TextField(
                    value = startsAt,
                    onValueChange = { startsAt = it },
                    label = { Text("Starts At") },
                    isError = startsAt.isEmpty() // Show error if the field is empty
                )
                TextField(
                    value = endsAt,
                    onValueChange = { endsAt = it },
                    label = { Text("Ends At") },
                    isError = endsAt.isEmpty() // Show error if the field is empty
                )
                TextField(
                    value = targetSelection,
                    onValueChange = { targetSelection = it },
                    label = { Text("Target Selection") },
                    isError = targetSelection.isEmpty() // Show error if the field is empty
                )
                TextField(
                    value = allocationMethod,
                    onValueChange = { allocationMethod = it },
                    label = { Text("Allocation Method") },
                    isError = allocationMethod.isEmpty() // Show error if the field is empty
                )
            }
        },
        confirmButton = {
            Button(onClick = {
                onSubmit(
                    priceRuleRequest(
                    PriceRule(
                        title = "Basic Discount",
                        target_type = "line_item", // Add target_type field
                        value_type = "percentage",
                        value = -10.0,
                        starts_at = "2024-10-06T00:00:00Z",
                        ends_at = "2024-10-16T00:00:00Z",
                        target_selection = "all",
                        allocation_method = "across",
                        customer_selection = "all"
                    ))
                )
                // Optionally log the submission
                //Log.d("PriceRuleDialog", "Submitted price rule: ${price_rule}")
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

@Composable
fun DiscountCodeDialog(
    onDismiss: () -> Unit,
    onSubmit: (DiscountCode) -> Unit,
    discountCode: DiscountCode?
) {
    var code by remember { mutableStateOf(discountCode?.code ?: "") }
    var usageCount by remember { mutableStateOf(discountCode?.usageCount?.toString() ?: "") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Add/Edit Discount Code") },
        text = {
            Column {
                TextField(
                    value = code,
                    onValueChange = { code = it },
                    label = { Text("Code") }
                )
                TextField(
                    value = usageCount,
                    onValueChange = { usageCount = it },
                    label = { Text("Usage Count") }
                )
            }
        },
        confirmButton = {
            Button(onClick = {
                val usageCountInt = usageCount.toIntOrNull() ?: 0

                if (discountCode != null) {
                    // Update existing DiscountCode
                    onSubmit(discountCode.copy(code = code, usageCount = usageCountInt))
                } else {
                    // Create a new DiscountCode
                    onSubmit(
                        DiscountCode(
                            id = 0, // Placeholder, will be generated by the server
                            code = code,
                            usageCount = usageCountInt,
                            createdAt = "" // Placeholder, adjust accordingly
                        )
                    )
                }
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

