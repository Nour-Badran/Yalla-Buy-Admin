package com.example.yallabuyadmin.ui.theme.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun InventoryScreen(onBack: () -> Unit) {
    // Replace this with your actual inventory management UI
    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Text("Inventory Management", style = MaterialTheme.typography.titleLarge)
        // Add your inventory related UI here
        Button(onClick = onBack) {
            Text("Back to Menu")
        }
    }
}