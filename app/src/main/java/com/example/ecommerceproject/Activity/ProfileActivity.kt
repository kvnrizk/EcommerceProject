package com.example.ecommerceproject.Activity

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.widget.Toast
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ecommerceproject.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class ProfileActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ProfileScreen()
        }
    }
}

// Function to Get User Data from SharedPreferences
fun getUserFromLocalStorage(context: Context): HashMap<String, String> {
    val sharedPreferences: SharedPreferences = context.getSharedPreferences("UserPrefs", Context.MODE_PRIVATE)

    return hashMapOf(
        "uid" to (sharedPreferences.getString("uid", "") ?: ""),
        "first_name" to (sharedPreferences.getString("first_name", "") ?: ""),
        "last_name" to (sharedPreferences.getString("last_name", "") ?: ""),
        "email" to (sharedPreferences.getString("email", "") ?: "")
    )
}

// Function to Save User Data to SharedPreferences
fun saveUserToLocal(context: Context, userData: HashMap<String, String>) {
    val sharedPreferences: SharedPreferences = context.getSharedPreferences("UserPrefs", Context.MODE_PRIVATE)
    val editor = sharedPreferences.edit()

    editor.putString("first_name", userData["first_name"])
    editor.putString("last_name", userData["last_name"])
    editor.putString("email", userData["email"])
    editor.apply()
}

// Function to Clear User Data from SharedPreferences
fun clearUserData(context: Context) {
    val sharedPreferences: SharedPreferences = context.getSharedPreferences("UserPrefs", Context.MODE_PRIVATE)
    sharedPreferences.edit().clear().apply()
}

@Composable
fun ProfileScreen() {
    val context = LocalContext.current
    val userData = getUserFromLocalStorage(context)

    val firestore = FirebaseFirestore.getInstance()
    val auth = FirebaseAuth.getInstance()
    val userId = auth.currentUser?.uid ?: ""

    // Mutable States for Editable TextFields
    var firstName by remember { mutableStateOf(userData["first_name"] ?: "") }
    var lastName by remember { mutableStateOf(userData["last_name"] ?: "") }
    var email by remember { mutableStateOf(userData["email"] ?: "") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = "Profile", fontSize = 24.sp)

        Spacer(modifier = Modifier.height(16.dp))

        // Editable TextFields
        OutlinedTextField(
            value = firstName,
            onValueChange = { firstName = it },
            label = { Text("First Name") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(10.dp))

        OutlinedTextField(
            value = lastName,
            onValueChange = { lastName = it },
            label = { Text("Last Name") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(10.dp))

        OutlinedTextField(
            value = email,
            onValueChange = { email = it },
            label = { Text("Email") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(20.dp))

        // Save Button
        Button(
            onClick = {
                if (userId.isNotEmpty()) {
                    val updatedData = hashMapOf(
                        "first_name" to firstName,
                        "last_name" to lastName,
                        "email" to email
                    )

                    // Save Data to Firestore
                    firestore.collection("users").document(userId)
                        .update(updatedData as Map<String, Any>)
                        .addOnSuccessListener {
                            Toast.makeText(context, "Profile Updated!", Toast.LENGTH_SHORT).show()
                            // Save Data to Local Storage
                            saveUserToLocal(context, updatedData)
                        }
                        .addOnFailureListener {
                            Toast.makeText(context, "Failed to update profile!", Toast.LENGTH_SHORT).show()
                        }
                } else {
                    Toast.makeText(context, "User not found!", Toast.LENGTH_SHORT).show()
                }
            },
            shape = RoundedCornerShape(10.dp),
            colors = ButtonDefaults.buttonColors(containerColor = colorResource(R.color.darkGrey)),
            modifier = Modifier.fillMaxWidth()
                .padding(top = 16.dp)
                .fillMaxWidth()
                .height(50.dp)
        ) {
            Text(text = "Save Changes")
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Logout Button
        Button(
            onClick = {
                auth.signOut()  // Sign out from Firebase
                clearUserData(context)  // Clear user data from local storage
                Toast.makeText(context, "Logged out successfully", Toast.LENGTH_SHORT).show()

                // Redirect to Sign-In Page
                val intent = Intent(context, SignInActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK // Clear activity stack
                context.startActivity(intent)
            },
            shape = RoundedCornerShape(10.dp),
            colors = ButtonDefaults.buttonColors(containerColor = colorResource(R.color.darkGrey)),
            modifier = Modifier.fillMaxWidth()
                .padding(top = 16.dp)
                .fillMaxWidth()
                .height(50.dp)
        ) {
            Text(text = "Logout")
        }
    }
}
