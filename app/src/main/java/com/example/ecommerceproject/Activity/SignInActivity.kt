package com.example.ecommerceproject.Activity

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.widget.Toast
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ecommerceproject.R
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.firestore.FirebaseFirestore

class SignInActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var googleSignInClient: GoogleSignInClient

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        auth = FirebaseAuth.getInstance()
        configureGoogleSignIn() // Initialize Google Sign-In

        setContent {
            SignInScreen { signInWithGoogle() }
        }
    }

    private fun configureGoogleSignIn() {
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken("21568314397-p70eu0st9s05stcmn94ffeokgu0n26lc.apps.googleusercontent.com")
            .requestEmail()
            .build()
        googleSignInClient = GoogleSignIn.getClient(this, gso)
    }

    private fun signInWithGoogle() {
        val signInIntent = googleSignInClient.signInIntent
        startActivityForResult(signInIntent, RC_SIGN_IN)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == RC_SIGN_IN) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            try {
                val account = task.getResult(ApiException::class.java)
                firebaseAuthWithGoogle(account!!)
            } catch (e: ApiException) {
                Toast.makeText(this, "Google Sign-In failed!", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun firebaseAuthWithGoogle(account: GoogleSignInAccount) {
        val credential = GoogleAuthProvider.getCredential(account.idToken, null)
        auth.signInWithCredential(credential)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    val user = auth.currentUser
                    val userData = hashMapOf(
                        "uid" to (user?.uid ?: ""),
                        "first_name" to (user?.displayName ?: ""),
                        "email" to (user?.email ?: "")
                    )

                    FirebaseFirestore.getInstance().collection("users")
                        .document(user!!.uid)
                        .set(userData)
                        .addOnSuccessListener {
                            Toast.makeText(this, "Google Sign-In Successful!", Toast.LENGTH_SHORT).show()
                            startActivity(Intent(this, MainActivity::class.java))
                        }
                } else {
                    Toast.makeText(this, "Authentication Failed!", Toast.LENGTH_SHORT).show()
                }
            }
    }

    companion object {
        private const val RC_SIGN_IN = 9001
    }
}


@Composable
fun SignInScreen(onGoogleSignIn: () -> Unit) {
    val auth = FirebaseAuth.getInstance()
    val firestore = FirebaseFirestore.getInstance()
    val ctxt = LocalContext.current
    val email = remember { mutableStateOf("") }
    val password = remember { mutableStateOf("") }
    val loading = remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(text = "E-Commerce App", fontSize = 24.sp)
        Spacer(modifier = Modifier.height(16.dp))
        Text(text = "Welcome Back", fontSize = 20.sp)
        Spacer(modifier = Modifier.height(32.dp))

        TextField(value = email.value, onValueChange = { email.value = it }, label = { Text("Email") }, modifier = Modifier.fillMaxWidth())
        Spacer(modifier = Modifier.height(16.dp))
        TextField(value = password.value, onValueChange = { password.value = it }, label = { Text("Password") }, visualTransformation = PasswordVisualTransformation(), modifier = Modifier.fillMaxWidth())
        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = {
                loading.value = true
                auth.signInWithEmailAndPassword(email.value, password.value).addOnCompleteListener { task ->
                    loading.value = false
                    if (task.isSuccessful) {
                        Toast.makeText(ctxt, "Sign-in Successful!", Toast.LENGTH_SHORT).show()
                        ctxt.startActivity(Intent(ctxt, MainActivity::class.java))
                    } else {
                        Toast.makeText(ctxt, "Error: Incorrect credentials", Toast.LENGTH_SHORT).show()
                    }
                }
            },
            shape = RoundedCornerShape(10.dp),
            colors = ButtonDefaults.buttonColors(containerColor = colorResource(R.color.darkGrey)),
            modifier = Modifier
                .padding(top = 16.dp)
                .fillMaxWidth()
                .height(50.dp)
        ) {
            Text(text = "Sign In")
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Google Sign-In Button
        Button(
            onClick = onGoogleSignIn,
            shape = RoundedCornerShape(10.dp),
            colors = ButtonDefaults.buttonColors(containerColor = colorResource(R.color.darkGrey)),
            modifier = Modifier
                .padding(top = 8.dp)
                .fillMaxWidth()
                .height(50.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center
            ) {
                Text(text = "Sign In with Google", fontSize = 16.sp, color = Color.White)
                Spacer(modifier = Modifier.width(8.dp))
                Image(
                    painter = painterResource(id = R.drawable.google), // Add Google icon in drawable
                    contentDescription = "Google Logo",
                    modifier = Modifier.size(24.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = "Don't have an account? Create one here",
            modifier = Modifier.clickable {
                ctxt.startActivity(Intent(ctxt, SignUpActivity::class.java))
            }
        )
    }
}