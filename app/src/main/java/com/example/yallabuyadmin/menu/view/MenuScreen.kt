package com.example.yallabuyadmin.menu.view

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.yallabuyadmin.R

@Composable
fun MenuScreen(
    onNavigateToProducts: () -> Unit,
    onNavigateToInventory: () -> Unit,
    onNavigateToCoupons: () -> Unit,
    onLogout: () -> Unit,
    inventoryCount: String,
    productsCount: String,
    couponsCount: String
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(16.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            CircularImageWithText()

            Spacer(modifier = Modifier.height(16.dp))

            IconButton(onClick = onLogout) {
                Icon(
                    painter = painterResource(id = R.drawable.img),
                    contentDescription = "Logout",
                    tint = MaterialTheme.colorScheme.primary
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = "Start managing your business",
                style = MaterialTheme.typography.bodyLarge,
                modifier = Modifier.padding(bottom = 24.dp)
            )

            EnhancedButton(
                onClick = onNavigateToProducts,
                title = "Products",
                count = productsCount,
                description = "Manage and view your products"
            )

            Spacer(modifier = Modifier.height(16.dp))

            EnhancedButton(
                onClick = onNavigateToInventory,
                title = "Inventory",
                count = inventoryCount,
                description = "Check your stock and inventory levels"
            )

            Spacer(modifier = Modifier.height(16.dp))

            EnhancedButton(
                onClick = onNavigateToCoupons,
                title = "Coupons",
                count = couponsCount,
                description = "Manage and create coupons for customers"
            )
        }
    }
}


@Composable
fun CircularImageWithText() {
    // Adjust the size as needed
    val imageSize = 80.dp
    Box(
        modifier = Modifier
            .size(imageSize)
            .background(MaterialTheme.colorScheme.primary, shape = androidx.compose.foundation.shape.CircleShape)
    ) {
        Image(
            painter = painterResource(id = R.drawable.img_1),
            contentDescription = "Profile Image",
            modifier = Modifier.fillMaxSize()
        )
    }
}

@Composable
fun EnhancedButton(
    onClick: () -> Unit,
    title: String,
    count: String,
    description: String
) {
    Button(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.medium,
        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
    ) {
        Row(
            modifier = Modifier.padding(8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween // Space items apart
        ) {
            // Column to hold title, count, and description
            Column(
                modifier = Modifier.weight(1f) // Allow text to take available space
            ) {
                Text(text = title, fontSize = 18.sp, color = Color.White) // Title text
                Text(text = description, fontSize = 12.sp, color = Color.LightGray) // Description text
            }

            // Displaying count on the right side
            Text(text = count, fontSize = 20.sp, color = Color.White) // Larger count text
        }
    }
}
