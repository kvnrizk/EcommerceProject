package com.example.ecommerceproject.Activity

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.widget.Toast
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.ButtonDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ecommerceproject.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.UserProfileChangeRequest
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.firestore.FirebaseFirestore

class SignUpActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            SignUpScreen()
        }
    }
}

fun saveUserToLocalStorage(context: Context, userData: HashMap<String, String>) {
    val sharedPreferences: SharedPreferences = context.getSharedPreferences("UserPrefs", Context.MODE_PRIVATE)
    val editor = sharedPreferences.edit()
    // Save user data
    editor.putString("uid", userData["uid"])
    editor.putString("first_name", userData["first_name"])
    editor.putString("last_name", userData["last_name"])
    editor.putString("email", userData["email"])
    // Apply changes
    editor.apply()
}

@Preview
@Composable
fun SignUpScreen() {
    val auth = FirebaseAuth.getInstance()
    val db = FirebaseFirestore.getInstance()

    var ctxt = LocalContext.current;

    val email = remember { mutableStateOf("") }
    val first_name = remember { mutableStateOf("") }
    val last_name = remember { mutableStateOf("") }
    val password = remember { mutableStateOf("") }
    val loading = remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(text = "E-Commerce App", fontSize = 24.sp, color = Color.Black)
        Spacer(modifier = Modifier.height(16.dp))
        Text(text = "Create a New Account", fontSize = 20.sp, color = Color.Gray)
        Spacer(modifier = Modifier.height(32.dp))
        TextField(value = first_name.value, onValueChange = { first_name.value = it }, label = { Text("First name") }, modifier = Modifier.fillMaxWidth())
        Spacer(modifier = Modifier.height(16.dp))
        TextField(value = last_name.value, onValueChange = { last_name.value = it }, label = { Text("Last name") }, modifier = Modifier.fillMaxWidth())
        Spacer(modifier = Modifier.height(16.dp))
        TextField(value = email.value, onValueChange = { email.value = it }, label = { Text("Email") }, modifier = Modifier.fillMaxWidth())
        Spacer(modifier = Modifier.height(16.dp))
        TextField(value = password.value, onValueChange = { password.value = it }, label = { Text("Password") }, visualTransformation = PasswordVisualTransformation(), modifier = Modifier.fillMaxWidth())
        Spacer(modifier = Modifier.height(16.dp))
        Button(onClick = {
            loading.value = true
            auth.createUserWithEmailAndPassword(email.value, password.value).addOnCompleteListener {  task ->
                loading.value = false
                if(task.isSuccessful){
                    val user = auth.currentUser
                    val fullName = "${first_name.value} ${last_name.value}"
                    val profileUpdates = UserProfileChangeRequest.Builder()
                        .setDisplayName(fullName)
                        .build()
                    user?.updateProfile(profileUpdates)?.addOnCompleteListener { profileTask ->
                        if (profileTask.isSuccessful) {
                            // **2️⃣ Save User Data in Firestore**
                            val userData = hashMapOf(
                                "uid" to user.uid,
                                "first_name" to first_name.value,
                                "last_name" to last_name.value,
                                "email" to email.value
                            )
                            db.collection("users").document(user.uid)
                                .set(userData)
                                .addOnSuccessListener {
                                    loading.value = false
                                    Toast.makeText(ctxt, "Account created successfully!", Toast.LENGTH_LONG).show()
                                    ctxt.startActivity(Intent(ctxt, MainActivity::class.java))
                                    saveUserToLocalStorage(ctxt, userData)
                                }
                            loading.value = false
                        }
                    }
                } else {
                    // moss
                    loading.value = false
                    Toast.makeText(ctxt, "Error: Unable to create user", Toast.LENGTH_SHORT).show()
                }
            }

        },
            shape = RoundedCornerShape(10.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = colorResource(R.color.darkGrey)
            ),
            modifier = Modifier
                .padding(top = 16.dp)
                .fillMaxWidth()
                .height(50.dp)) {
            Text(text = "Sign Up")
        }
        Spacer(modifier = Modifier.height(8.dp))
        Text(text = "Already have an account? Sign in here", color = Color.DarkGray, modifier = Modifier.clickable { ctxt.startActivity(Intent(ctxt, SignInActivity::class.java)) })
    }
}
