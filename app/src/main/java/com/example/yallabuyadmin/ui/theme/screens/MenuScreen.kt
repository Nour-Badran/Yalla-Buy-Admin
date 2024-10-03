package com.example.yallabuyadmin.ui.theme.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun MenuScreen(
    onNavigateToProducts: () -> Unit,
    onNavigateToInventory: () -> Unit,
    onNavigateToCoupons: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = "Choose an option", style = MaterialTheme.typography.titleLarge)

        Spacer(modifier = Modifier.height(24.dp))

        Button(
            onClick = onNavigateToProducts,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(text = "Products")
        }

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = onNavigateToInventory,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(text = "Inventory")
        }

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = onNavigateToCoupons,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(text = "Coupons")
        }
    }
}
