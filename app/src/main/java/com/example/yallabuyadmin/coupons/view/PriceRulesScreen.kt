package com.example.yallabuyadmin.coupons.view

import android.annotation.SuppressLint
import android.app.TimePickerDialog
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.example.yallabuyadmin.R
import com.example.yallabuyadmin.coupons.viewmodel.CouponsViewModel
import com.example.yallabuyadmin.coupons.model.DiscountCode
import com.example.yallabuyadmin.coupons.model.PriceRule
import com.example.yallabuyadmin.coupons.model.priceRuleRequest
import com.example.yallabuyadmin.network.ApiState
import com.example.yallabuyadmin.ui.theme.AppColors
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import java.util.Calendar

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CouponsScreen(
    onBack: () -> Unit,
    onNavigateToDiscount: (Long) -> Unit,
    viewModel: CouponsViewModel
) {
    val discountCodes by viewModel.discountCodes.collectAsState()
    val priceRules by viewModel.priceRules.collectAsState()
    // State for dialogs
    var showDiscountCodeDialog by remember { mutableStateOf(false) }
    var showCreatePriceRuleDialog by remember { mutableStateOf(false) }
    var showEditPriceRuleDialog by remember { mutableStateOf(false) }

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
    if (showCreatePriceRuleDialog) {
        PriceRuleDialog(
            onDismiss = {
                showCreatePriceRuleDialog = false
                currentPriceRule = null // Reset the current price rule
            },
            onSubmit = { priceRule ->
                currentPriceRule?.let {
                    viewModel.updatePriceRule(it.id!!, priceRule) // Update existing price rule
                } ?: run {
                    viewModel.createNewPriceRule(priceRule) // Create new price rule
                }
                showCreatePriceRuleDialog = false
            }
        )
    }
    if (showEditPriceRuleDialog) {
        PriceRuleDialog(
            onDismiss = {
                showEditPriceRuleDialog = false
                currentPriceRule = null // Reset the current price rule
            },
            onSubmit = { priceRule ->
                currentPriceRule?.let {
                    viewModel.updatePriceRule(it.id!!, priceRule) // Update existing price rule
                } ?: run {
                    viewModel.createNewPriceRule(priceRule) // Create new price rule
                }
                showEditPriceRuleDialog = false
            },
            priceRule = currentPriceRule // Pass the current price rule for editing
        )
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
            )
        },

        floatingActionButton = {
            FloatingActionButton(onClick = {
                currentPriceRule = null // Reset for new price rule
                showCreatePriceRuleDialog = true
            },contentColor = Color.White, containerColor = AppColors.Teal) {
                Icon(Icons.Default.Add, contentDescription = "Create Discount Code")
            }
        },
        content = { padding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .padding(16.dp)
            ) {
                //Text("Price Rules", style = MaterialTheme.typography.titleLarge)

                when (priceRules) {
                    is ApiState.Loading -> {
                        // Show a loading indicator
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(48.dp),
                                color = AppColors.Teal
                            )
                        }
                    }
                    is ApiState.Success -> {
                        val priceRules = (priceRules as ApiState.Success<List<PriceRule>>).data
                        LazyColumn(
                            modifier = Modifier.fillMaxSize(),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            items(priceRules) { priceRule ->
                                Card(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(8.dp),
                                    elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
                                    shape = RoundedCornerShape(12.dp),
                                    colors = CardDefaults.cardColors(containerColor = Color.White),
                                    border = BorderStroke(.5.dp, AppColors.Teal)
                                ) {
                                    Column(
                                        modifier = Modifier
                                            .padding(16.dp),
                                        verticalArrangement = Arrangement.spacedBy(8.dp)
                                    ) {
                                        // Title
                                        Text(
                                            text = priceRule.title,
                                            style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                                            color = AppColors.Teal
                                        )
                                        // Discount Value
                                        Text(
                                            text = "Discount: ${priceRule.value}%",
                                            style = MaterialTheme.typography.bodyMedium,
                                            color = Color(0xFF616161)
                                        )
                                        // Starts At
                                        val startDateTime = ZonedDateTime.parse(priceRule.starts_at)
                                        val formattedStartDateTime = startDateTime.format(DateTimeFormatter.ofPattern("dd MMMM, yyyy, hh:mm a"))
                                        Text(
                                            text = "Starts At: $formattedStartDateTime",
                                            style = MaterialTheme.typography.bodySmall,
                                            color = Color(0xFF757575)
                                        )
                                        // Ends At
                                        val endDateTime = ZonedDateTime.parse(priceRule.ends_at)
                                        val formattedEndDateTime = endDateTime.format(DateTimeFormatter.ofPattern("dd MMMM, yyyy, hh:mm a"))
                                        Text(
                                            text = "Ends At: $formattedEndDateTime",
                                            style = MaterialTheme.typography.bodySmall,
                                            color = Color(0xFF757575)
                                        )
                                        // Action buttons
                                        Divider(
                                            modifier = Modifier.padding(vertical = 8.dp),
                                            color = Color(0xFFE0E0E0)
                                        )
                                        var showDeleteConfirmation by remember { mutableStateOf(false) }
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.SpaceBetween
                                        ) {
                                            TextButton(onClick = {
                                                currentPriceRule = priceRule // Set the current price rule for editing
                                                showEditPriceRuleDialog = true // Show the dialog
                                            }) {
                                                Icon(Icons.Default.Edit, contentDescription = "Edit", tint = Color.Black)
                                                Spacer(modifier = Modifier.width(4.dp))
                                                Text("Edit", color = Color.Black)
                                            }
                                            TextButton(onClick = {
                                                showDeleteConfirmation = true
                                            }) {
                                                Image(
                                                    painter = painterResource(id = R.drawable.trash),
                                                    contentDescription = "Delete",
                                                    modifier = Modifier.size(24.dp))
                                                Spacer(modifier = Modifier.width(4.dp))
                                                Text("Delete", color = Color.Red)
                                            }
                                            TextButton(onClick = {
                                                onNavigateToDiscount(priceRule.id!!)
                                            }) {
                                                Icon(Icons.Default.PlayArrow, contentDescription = "Apply", tint = Color.Black)
                                                Spacer(modifier = Modifier.width(4.dp))
                                                Text("Coupons", color = Color.Black)
                                            }
                                        }

                                        if (showDeleteConfirmation) {
                                            AlertDialog(
                                                containerColor = Color.White,
                                                onDismissRequest = { showDeleteConfirmation = false },
                                                title = { Text("Confirm Delete", color = AppColors.Teal) },
                                                text = { Text("Are you sure you want to delete this price rule?") },
                                                confirmButton = {
                                                    TextButton(onClick = {
                                                        viewModel.deletePriceRule(priceRule.id!!)
                                                        showDeleteConfirmation = false
                                                    }) {
                                                        Text("Delete", color = Color.Red)
                                                    }
                                                },
                                                dismissButton = {
                                                    TextButton(onClick = {
                                                        showDeleteConfirmation = false
                                                    }) {
                                                        Text("Cancel", color = Color.Black)
                                                    }
                                                }
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                    is ApiState.Error -> {
                        val errorMessage = (priceRules as ApiState.Error).message
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(text = "Error: $errorMessage", color = Color.Red)
                        }
                    }
                }
            }
        }
    )
}
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PriceRuleDialog(
    onDismiss: () -> Unit,
    onSubmit: (priceRuleRequest) -> Unit,
    priceRule: PriceRule? = null // Pass in an existing PriceRule for editing, or null for new
) {
    // State for the input fields
    var ruleName by remember { mutableStateOf(priceRule?.title ?: "Title") }
    var discountPercentage by remember { mutableStateOf(priceRule?.value?.toString() ?: "-0.00") }
    var valueType by remember { mutableStateOf(priceRule?.value_type ?: "percentage") }
    var startsAt by remember { mutableStateOf(priceRule?.starts_at ?: "2024-10-14T17:52:44-04:00") }
    var endsAt by remember { mutableStateOf(priceRule?.ends_at ?: "2025-10-14T17:52:44-04:00") }
    var targetSelection by remember { mutableStateOf(priceRule?.target_selection ?: "all") }
    var allocationMethod by remember { mutableStateOf(priceRule?.allocation_method ?: "across") }
    var targetType by remember { mutableStateOf(priceRule?.target_type ?: "line_item") }
    var customerSelection by remember { mutableStateOf(priceRule?.customer_selection ?: "all") }
    var usageLimit by remember { mutableStateOf(priceRule?.usage_limit?.toString() ?: "1") }

    val context = LocalContext.current

    @SuppressLint("DefaultLocale")
    fun showDateTimePicker(onDateTimeSelected: (String) -> Unit) {
        val calendar = Calendar.getInstance()
        android.app.DatePickerDialog(
            context,
            { _, year, month, dayOfMonth ->
                TimePickerDialog(
                    context,
                    { _, hour, minute ->
                        val selectedDateTime = String.format(
                            "%04d-%02d-%02dT%02d:%02d:00-04:00",
                            year, month + 1, dayOfMonth, hour, minute
                        )
                        onDateTimeSelected(selectedDateTime)
                    },
                    calendar.get(Calendar.HOUR_OF_DAY),
                    calendar.get(Calendar.MINUTE),
                    true
                ).show()
            },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        ).show()
    }

    AlertDialog(
        containerColor = Color.White,
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = if (priceRule == null) "Add Price Rule" else "Edit Price Rule",
                style = MaterialTheme.typography.titleLarge,
                color = AppColors.Teal
            )
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                TextField(
                    value = ruleName,
                    onValueChange = { ruleName = it },
                    label = { Text("Title") },
                    isError = ruleName.isEmpty(),
                    modifier = Modifier.fillMaxWidth(),
                    colors = TextFieldDefaults.textFieldColors(
                        containerColor = Color.White
                    )
                )
                TextField(
                    value = discountPercentage,
                    onValueChange = {discountPercentage = it },
                    label = { Text("Discount Percentage") },
                    keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number),
                    modifier = Modifier.fillMaxWidth(),
                    colors = TextFieldDefaults.textFieldColors(
                        containerColor = Color.White
                    )
                )

                TextField(
                    value = usageLimit,
                    onValueChange = {
                        if (it.toIntOrNull() != null && it.toInt() >= 0) {
                            usageLimit = it
                        }
                    },
                    label = { Text("Usage Limit") },
                    keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number),
                    modifier = Modifier.fillMaxWidth(),
                    colors = TextFieldDefaults.textFieldColors(
                        containerColor = Color.White
                    )
                )

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "Starts At: $startsAt",
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier.weight(1f)
                    )
                    IconButton(onClick = { showDateTimePicker { startsAt = it } }) {
                        Icon(
                            imageVector = Icons.Default.DateRange,
                            contentDescription = "Select Start Date & Time",
                            tint = AppColors.Teal
                        )
                    }
                }

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "Ends At: $endsAt",
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier.weight(1f)
                    )
                    IconButton(onClick = { showDateTimePicker { endsAt = it } }) {
                        Icon(
                            imageVector = Icons.Default.DateRange,
                            contentDescription = "Select End Date & Time",
                            tint = AppColors.Teal
                        )

                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    onSubmit(
                        priceRuleRequest(
                            PriceRule(
                                id = priceRule?.id,
                                title = ruleName,
                                value = discountPercentage.toDoubleOrNull()?.let { if (it > 0) -it else it } ?: 0.0,
                                value_type = valueType,
                                starts_at = startsAt,
                                ends_at = endsAt,
                                target_selection = targetSelection,
                                allocation_method = allocationMethod,
                                target_type = targetType,
                                customer_selection = customerSelection,
                                usage_limit = usageLimit.toLongOrNull() ?: 0
                            )
                        )
                    )
                },
                colors = ButtonDefaults.buttonColors(containerColor = AppColors.Teal),
            ) {
                Text("Submit")
            }
        },
        dismissButton = {
            Button(onClick = onDismiss,
                colors = ButtonDefaults.buttonColors(containerColor = AppColors.Teal),
            ) {
                Text("Dismiss")
            }
        },
        modifier = Modifier.padding(16.dp)
    )
}
