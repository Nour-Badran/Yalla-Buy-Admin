package com.example.yallabuyadmin.auth

import android.util.Log
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.DialogProperties
import com.example.yallabuyadmin.R
import com.example.yallabuyadmin.ui.theme.AppColors
import com.google.android.gms.auth.api.signin.GoogleSignIn

@Composable
fun CreateAccountScreen() {
    var userName by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var pass by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Create Account",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "Start learning with create your account!",
            style = MaterialTheme.typography.bodyMedium,
            color = Color.Gray
        )
        Spacer(modifier = Modifier.height(24.dp))

        OutlinedTextField(
            value = userName,
            onValueChange = {input -> userName = input},
            label = { Text("Create your username") },
            leadingIcon = { Icon(Icons.Default.Person, contentDescription = null) },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = email,
            onValueChange = {input -> email = input},
            label = { Text("Enter your email or phone number") },
            leadingIcon = { Icon(Icons.Default.Email, contentDescription = null) },
            modifier = Modifier.fillMaxWidth()

        )
        Spacer(modifier = Modifier.height(16.dp))

        // Password TextField
        OutlinedTextField(
            value = pass,
            onValueChange = {input -> pass = input},
            label = { Text("Create your password") },
            leadingIcon = { Icon(Icons.Default.Lock, contentDescription = null) },
           // trailingIcon = { Icon(Icons.Default.Lock, contentDescription = null) },
            modifier = Modifier.fillMaxWidth(),
            visualTransformation = PasswordVisualTransformation()
        )
        Spacer(modifier = Modifier.height(24.dp))

        Button(
            onClick = {
                isLoading = true

            },
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp),
            shape = RoundedCornerShape(8.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF5C4CE3))
        ) {
            if (isLoading) {
                CircularProgressIndicator(color = Color.White, modifier = Modifier.size(24.dp))
            } else {
                Text(text = "Create Account", color = Color.White)
            }
        }
        errorMessage?.let {
            Spacer(modifier = Modifier.height(16.dp))
            Text(text = it, color = Color.Red)
        }
        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "Or using other method",
            color = Color.Gray,
            style = MaterialTheme.typography.bodyMedium
        )
        Spacer(modifier = Modifier.height(16.dp))

        OutlinedButton(
            onClick = { /* Handle Google Sign Up */ },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(8.dp)
        ) {
            Icon(
                painter = painterResource(id = R.drawable.google), // replace with your Google icon resource
                contentDescription = null,
                tint = Color.Unspecified,
                modifier = Modifier.size(25.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(text = "Sign Up with Google")
        }

        Spacer(modifier = Modifier.height(8.dp))

        OutlinedButton(
            onClick = { /* Handle Facebook Sign Up */ },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(8.dp)
        ) {
            Icon(
                painter = painterResource(id = R.drawable.facebook), // replace with your Facebook icon resource
                contentDescription = null,
                tint = Color.Unspecified,
                modifier = Modifier.size(25.dp)

            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(text = "Sign Up with Facebook")
        }
    }
}


@Composable
fun SignupScreen(onSignupSuccess: () -> Unit,onLogin: () -> Unit) {
    var fullName by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var passwordVisibility by remember { mutableStateOf(false) }
    var isLoading by remember { mutableStateOf(false) }
    var isSuccess by remember { mutableStateOf(false) }
    var auth = FirebaseAuthun()
    val context = LocalContext.current
    var showDialog by remember { mutableStateOf(false) }
    val googleSignInClient = GoogleSignIn.getClient(context, auth.getGoogleSignInOptions(context))
    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { result ->
        val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
        auth.handleSignInResult(task, context, onSignupSuccess)
    }
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .padding(16.dp),
        verticalArrangement = Arrangement.Center
    ) {
        Column( horizontalAlignment = Alignment.Start,

            ) {
            Text(text = "Create an account",
                fontSize = 35.sp,
                color = Color.Black,
                modifier = Modifier.padding(8.dp)
            )

//            Spacer(modifier = Modifier.height(8.dp))
        }


        Spacer(modifier = Modifier.height(16.dp))
        Text(text = "Full Name", fontSize = 16.sp, modifier = Modifier.padding(start = 8.dp))
        OutlinedTextField(
            value = fullName,
            onValueChange = { fullName = it },
            shape = RoundedCornerShape(30.dp),
            modifier = Modifier
                .fillMaxWidth()
                .height(75.dp)
                .padding(10.dp),
            colors = OutlinedTextFieldDefaults.colors(unfocusedBorderColor = AppColors.MintGreen)
        )

        Spacer(modifier = Modifier.height(8.dp))

        // Email Input
        Text(text = "Email", fontSize = 16.sp, modifier = Modifier.padding(start = 8.dp))
        OutlinedTextField(
            value = email,
            onValueChange = { email = it },
            shape = RoundedCornerShape(30.dp),
            modifier = Modifier
                .fillMaxWidth()
                .height(75.dp)
                .padding(10.dp),
            keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Email),
            colors = OutlinedTextFieldDefaults.colors(unfocusedBorderColor = AppColors.MintGreen)
        )

        Spacer(modifier = Modifier.height(8.dp))

        // Password Input
        Text(text = "Password", fontSize = 16.sp, modifier = Modifier.padding(start = 8.dp))
        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            shape = RoundedCornerShape(30.dp),
            modifier = Modifier
                .fillMaxWidth()
                .height(75.dp)
                .padding(10.dp),
            visualTransformation = if (passwordVisibility) VisualTransformation.None else PasswordVisualTransformation(),
            trailingIcon = {
                IconButton(onClick = { passwordVisibility = !passwordVisibility }) {
                    // Add visibility icons here
                }
            },
            keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Password),
            colors = OutlinedTextFieldDefaults.colors(unfocusedBorderColor = AppColors.MintGreen)
        )

        Spacer(modifier = Modifier.height(8.dp))

        // Confirm Password Input
        Text(text = "Confirm Password", fontSize = 16.sp, modifier = Modifier.padding(start = 8.dp))
        OutlinedTextField(
            value = confirmPassword,
            onValueChange = { confirmPassword = it },
            shape = RoundedCornerShape(30.dp),
            modifier = Modifier
                .fillMaxWidth()
                .height(75.dp)
                .padding(10.dp),
            visualTransformation = if (passwordVisibility) VisualTransformation.None else PasswordVisualTransformation(),
            trailingIcon = {
                IconButton(onClick = { passwordVisibility = !passwordVisibility }) {
                    // Add visibility icons here
                }
            },
            keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Password),
            colors = OutlinedTextFieldDefaults.colors(unfocusedBorderColor = AppColors.MintGreen)
        )

        Spacer(modifier = Modifier.height(14.dp))

        // Create Account Button
        Button(
            onClick = {
              //  viewModel.signInWithEmailAndPassword(email,password,fullName)
                isLoading=true
                if(email.isEmpty() || password.isEmpty() || confirmPassword.isEmpty() || email.isEmpty()){
                    Toast.makeText(context,"complete empty fields please",Toast.LENGTH_LONG).show()
                    isLoading=false
                    //showDialog=true
                }else{
                    if(password == confirmPassword){
                         isSuccess = auth.signInWithEmailAndPassword(email,password,fullName)
                      if(!isSuccess){
                        showDialog = true
                      }
                    }else{
                        isLoading=false
                        Toast.makeText(context,"password and confirm password are not the same",Toast.LENGTH_LONG).show()
                        Log.i("TAG", "SignupScreen: password and confirm password are not the same")
                    }
                }

            },
            shape = RoundedCornerShape(30.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color.Black,        // Default background color
                contentColor = Color.White,         // Text color
                disabledContainerColor = Color.Gray // Background color when disabled
            ),
            modifier = Modifier
                .fillMaxWidth()
                .padding(10.dp)
                .height(60.dp)

        ) {
            if (isLoading) {
                CircularProgressIndicator(color = Color.White, modifier = Modifier.size(24.dp))
            } else {
                Text(text = "Create Account",fontSize = 20.sp)
            }

        }

        Spacer(modifier = Modifier.height(8.dp))

        // Signup with Google
        Text(
            text = "-OR Signup with Google-",
            fontSize = 16.sp,
            modifier = Modifier.align(Alignment.CenterHorizontally)
        )

        Spacer(modifier = Modifier.height(10.dp))

        // Google Icon
        TextButton(
            onClick = {
                val signInIntent = googleSignInClient.signInIntent
                launcher.launch(signInIntent)
            },
            modifier = Modifier.fillMaxWidth()
                .size(45.dp)
                .align(Alignment.CenterHorizontally)

        ) {
            Icon(
                painter = painterResource(id = R.drawable.google),
                contentDescription = null,
                tint = Color.Unspecified,
                //modifier = Modifier.size(35.dp).align(Alignment.CenterHorizontally)
            )}

        Spacer(modifier = Modifier.height(8.dp))

        Row(verticalAlignment = Alignment.CenterVertically) {
            Spacer(modifier = Modifier.width(10.dp))
            Text(text = "Already have an account?")
            TextButton(onClick = onLogin) {
                Text(text = " Login", color = AppColors.MintGreen)
            }
        }

        if (showDialog) {
            isLoading=false
            AlertDialog(
                onDismissRequest = { showDialog = false },
                confirmButton = {
                    TextButton(onClick = {
                        showDialog = false
                        onLogin()
                        //navController.navigate(Screen.LogInScreen.route)
                    }) {
                        Text("OK", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = Color.Cyan)
                    }
                },
                title = {
                    Text(text = "Success", fontSize = 24.sp, fontWeight = FontWeight.Bold)
                },
                text = {
                    Text("A verification email has been sent to your email. \nplease verify your email and login.", fontSize = 15.sp)
                },
                icon = {
                    Icon(
                        imageVector = Icons.Filled.CheckCircle,
                        contentDescription = "Success Icon",
                        tint = Color.Cyan,
                        modifier = Modifier.size(40.dp) // Adjust icon size
                    )
                },

                properties = DialogProperties(dismissOnBackPress = true) , shape = RectangleShape, containerColor = Color.White
            )
        }

    }
}
@Composable
fun customAlert(){
        var showDialog by remember { mutableStateOf(false) }


        // Dialog implementation
        if (showDialog) {
            AlertDialog(
                onDismissRequest = { showDialog = false }, // Action on dismiss
                confirmButton = {
                    TextButton(onClick = { showDialog = false }) {
                        Text("OK")
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showDialog = false }) {
                        Text("Cancel")
                    }
                },
                title = {
                    Text(text = "Dialog Title")
                },
                text = {
                    Text("This is a simple dialog in Jetpack Compose.")
                }
            )
        }
}
