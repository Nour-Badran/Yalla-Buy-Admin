package com.example.yallabuyadmin.coupons.view

import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.example.yallabuyadmin.coupons.viewmodel.CouponsViewModel

import androidx.compose.material.icons.filled.Add
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.window.Dialog
import com.example.yallabuyadmin.coupons.model.DiscountCode
import com.example.yallabuyadmin.coupons.model.DiscountCodeRequest

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DiscountCodesScreen(
    priceRuleId: Long,
    onBack: () -> Unit,
    viewModel: CouponsViewModel
) {
    val discountCodes by viewModel.discountCodes.collectAsState()
    val showCreateDialog = remember { mutableStateOf(false) }
    val showEditDialog = remember { mutableStateOf(false) }
    var selectedDiscountCode by remember { mutableStateOf<DiscountCode?>(null) }

    // Fetch discount codes for the selected price rule
    LaunchedEffect(priceRuleId) {
        viewModel.fetchDiscountCodes(priceRuleId)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Discount Codes", color = Color.White) },
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
        floatingActionButton = {
            FloatingActionButton(onClick = { showCreateDialog.value = true },contentColor = Color.White, containerColor = Color.Black) {
                Icon(Icons.Default.Add, contentDescription = "Create Discount Code")
            }
        },
        content = { padding ->
            Box(modifier = Modifier.fillMaxSize()) {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(padding)
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(discountCodes) { discountCode ->
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(8.dp),
                            elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
                            shape = RoundedCornerShape(12.dp),
                            colors = CardDefaults.cardColors(containerColor = Color.White),
                            border = BorderStroke(1.dp,Color.Black)
                        ) {
                            Column(
                                modifier = Modifier
                                    .padding(16.dp)
                            ) {
                                Text(
                                    text = "Code: ${discountCode.code}",
                                    style = MaterialTheme.typography.titleMedium
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = "Usage Count: ${discountCode.usage_count}",
                                    style = MaterialTheme.typography.bodyMedium
                                )
                                // Add Edit and Delete buttons
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.End
                                ) {
                                    IconButton(onClick = {
                                        selectedDiscountCode = discountCode
                                        showEditDialog.value = true
                                    }) {
                                        Icon(Icons.Default.Edit, contentDescription = "Edit")
                                    }
                                    IconButton(onClick = {
                                        discountCode.id?.let { viewModel.deleteDiscountCode(priceRuleId,it) }
                                    }) {
                                        Icon(Icons.Default.Delete, contentDescription = "Delete")
                                    }
                                }
                            }
                        }
                    }
                }

                // Create Discount Code Dialog
                if (showCreateDialog.value) {
                    DiscountCodeDialog(
                        onDismiss = { showCreateDialog.value = false },
                        onConfirm = { newCode ->
                            viewModel.createDiscountCode(priceRuleId, newCode)
                            showCreateDialog.value = false
                        }
                    )
                }

                // Edit Discount Code Dialog
                if (showEditDialog.value && selectedDiscountCode != null) {
                    DiscountCodeDialog(
                        discountCode = selectedDiscountCode,
                        onDismiss = { showEditDialog.value = false },
                        onConfirm = { updatedCode ->
                            updatedCode.discount_code.id?.let { viewModel.updateDiscountCode(priceRuleId,it, updatedCode) }
                            showEditDialog.value = false
                        }
                    )
                }
            }
        }
    )
}

@Composable
fun DiscountCodeDialog(
    discountCode: DiscountCode? = null,
    onDismiss: () -> Unit,
    onConfirm: (DiscountCodeRequest) -> Unit
) {
    var code by remember { mutableStateOf(discountCode?.code ?: "") }

    Dialog(onDismissRequest = { onDismiss() }) {
        Surface(
            shape = MaterialTheme.shapes.medium,
            tonalElevation = 4.dp
        ) {
            Column(
                modifier = Modifier
                    .padding(16.dp)
                    .fillMaxWidth()
            ) {
                Text(
                    text = if (discountCode == null) "Create Discount Code" else "Edit Discount Code",
                    style = MaterialTheme.typography.titleLarge
                )
                Spacer(modifier = Modifier.height(8.dp))
                TextField(
                    value = code,
                    onValueChange = { code = it },
                    label = { Text("Code") }
                )
                Spacer(modifier = Modifier.height(16.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    TextButton(onClick = { onDismiss() }) {
                        Text("Cancel")
                    }
                    TextButton(onClick = {
                        val updatedCode = discountCode?.copy(
                            code = code
                        ) ?: DiscountCode(
                            code = code,
                            usage_count = 0,
                            created_at = ""
                        )
                        onConfirm(DiscountCodeRequest(updatedCode))
                    }) {
                        Text("Confirm")
                    }
                }
            }
        }
    }
}
