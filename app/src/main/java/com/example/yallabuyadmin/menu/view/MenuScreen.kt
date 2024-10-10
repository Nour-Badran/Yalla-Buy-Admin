package com.example.yallabuyadmin.menu.view

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.IconButton
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.yallabuyadmin.R
import com.example.yallabuyadmin.menu.viewmodel.MenuViewModel
import com.example.yallabuyadmin.ui.theme.AppColors
import com.google.firebase.auth.FirebaseAuth

@Composable
fun MenuScreen(
    menuViewModel: MenuViewModel,
    onNavigateToProducts: () -> Unit,
    onNavigateToInventory: () -> Unit,
    onNavigateToCoupons: () -> Unit,
    onLogout: () -> Unit
) {
    val mAuth = FirebaseAuth.getInstance()
    val currentUser = mAuth.currentUser
    var showGreeting by remember { mutableStateOf(true) }
    var showLogoutConfirmation by remember { mutableStateOf(false) }

    val greetingMessage = if (currentUser != null) {
        "Hello, ${currentUser.displayName ?: "User"}!"
    } else {
        "Hello!"
    }

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

            if (showGreeting) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 16.dp),
                    shape = RoundedCornerShape(12.dp),
                    elevation = CardDefaults.cardElevation(4.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFECEFF1))
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Icon(
                            painter = painterResource(id = R.drawable.img_3),
                            contentDescription = null,
                            modifier = Modifier.size(30.dp),
                            tint = AppColors.Teal
                        )

                        Text(
                            text = greetingMessage,
                            style = MaterialTheme.typography.headlineMedium,
                            color = Color.Black,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.weight(1f)
                        )

                        IconButton(onClick = { showLogoutConfirmation = true }) {
                            Icon(
                                painter = painterResource(id = R.drawable.img_2),
                                contentDescription = "Logout",
                                tint = AppColors.Teal,
                                modifier = Modifier.size(30.dp),
                            )
                        }
                    }
                }
            }

            if (showLogoutConfirmation) {
                AlertDialog(
                    containerColor = Color.White,
                    onDismissRequest = { showLogoutConfirmation = false },
                    title = { Text("Confirm Logout") },
                    text = { Text("Are you sure you want to log out?") },
                    confirmButton = {
                        TextButton(onClick = {
                            onLogout()
                            showLogoutConfirmation = false
                        }) {
                            Text("Logout", color = Color.Red)
                        }
                    },
                    dismissButton = {
                        TextButton(onClick = {
                            showLogoutConfirmation = false
                        }) {
                            Text("Cancel", color = Color.Black)
                        }
                    }
                )
            }


            Spacer(modifier = Modifier.height(16.dp))

            CircularImageWithText()

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = "Start managing your business",
                fontSize = 24.sp,
                color = Color.Black,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 24.dp)
            )

            if (isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier.size(48.dp),
                    color = AppColors.Teal
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
            .size(150.dp)
    ) {
        Image(
            painter = painterResource(id = R.drawable.manager1),
            contentDescription = "Profile Image",
            modifier = Modifier.fillMaxSize().clip(CircleShape)
        )
    }
}

@Composable
fun EnhancedButton(
    onClick: () -> Unit,
    title: String,
    count: String,
    description: String,
) {
    Button(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.large,
        colors = ButtonDefaults.buttonColors(containerColor = AppColors.Teal),
        elevation = ButtonDefaults.buttonElevation(defaultElevation = 10.dp)
    ) {
        Row(
            modifier = Modifier.padding(8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(text = title, fontSize = 18.sp, color = AppColors.White, fontWeight = FontWeight.Bold)
                Text(text = description, fontSize = 12.sp, color = AppColors.White)
            }
            Text(text = count, fontSize = 20.sp, color = AppColors.White)
        }
    }
}


