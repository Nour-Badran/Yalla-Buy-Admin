package com.example.yallabuyadmin

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            // Your app theme
            MaterialTheme {
                Surface {
                    // Call the ProductManagementScreen here
                    ProductManagementScreen()
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun ProductManagementScreenPreview() {
    MaterialTheme {
        Surface {
            ProductManagementScreen()
        }
    }
}
