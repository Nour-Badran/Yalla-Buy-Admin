package com.example.yallabuyadmin.menu.view

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.yallabuyadmin.R
import com.example.yallabuyadmin.menu.viewmodel.MenuViewModel

@Composable
fun MenuScreen(
    menuViewModel: MenuViewModel,
    onNavigateToProducts: () -> Unit,
    onNavigateToInventory: () -> Unit,
    onNavigateToCoupons: () -> Unit,
    onLogout: () -> Unit
) {
    val inventoryCount = menuViewModel.inventoryCount.collectAsState().value
    val productsCount = menuViewModel.productsCount.collectAsState().value
    val couponsCount = menuViewModel.couponsCount.collectAsState().value
    val isLoading = menuViewModel.isLoading.collectAsState().value

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF3F4F6))
            .padding(16.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            IconButton(onClick = onLogout, modifier = Modifier.align(Alignment.End)) {
                Icon(
                    painter = painterResource(id = R.drawable.img_2),
                    contentDescription = "Logout",
                    tint = Color.Black
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            CircularImageWithText()

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = "Start managing your business",
                fontSize = 24.sp,
                color = Color(0xFF1E1E1E),
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 24.dp)
            )

            if (isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier.size(48.dp),
                    color = Color.Black
                )
                Spacer(modifier = Modifier.height(24.dp))
            } else {
                EnhancedButton(
                    onClick = onNavigateToProducts,
                    title = "Products",
                    count = productsCount.toString(),
                    description = "Manage and view your products"
                )

                Spacer(modifier = Modifier.height(16.dp))

                EnhancedButton(
                    onClick = onNavigateToInventory,
                    title = "Inventory",
                    count = inventoryCount.toString(),
                    description = "Check your stock and inventory levels"
                )

                Spacer(modifier = Modifier.height(16.dp))

                EnhancedButton(
                    onClick = onNavigateToCoupons,
                    title = "Coupons",
                    count = couponsCount.toString(),
                    description = "Manage and create coupons for customers"
                )
            }
        }
    }
}


@Composable
fun CircularImageWithText() {
    val imageSize = 80.dp
    Box(
        modifier = Modifier
            .padding(26.dp)
            .size(100.dp)
            .clip(CircleShape) // Optional: Makes the image circular
            .background(Color(0xFFBDBDBD)) // Gray background for the image
    ) {
        Image(
            painter = painterResource(id = R.drawable.m), // Update with your image
            contentDescription = "Profile Image",
            modifier = Modifier.fillMaxSize().clip(CircleShape) // Clip the image to a circular shape
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
        colors = ButtonDefaults.buttonColors(containerColor = Color.Black),
        elevation = ButtonDefaults.buttonElevation(defaultElevation = 4.dp)
    ) {
        Row(
            modifier = Modifier.padding(8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(text = title, fontSize = 18.sp, color = Color.White, fontWeight = FontWeight.Bold)
                Text(text = description, fontSize = 12.sp, color = Color(0xFFE0E0E0))
            }
            Text(text = count, fontSize = 20.sp, color = Color.White)
        }
    }
}


