package com.example.ecommerceproject.Activity

import android.content.Intent
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.ecommerceproject.R


class IntroActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            IntroScreen(
            onCLick = {
                startActivity(Intent(this, MainActivity::class.java))
            }
        )
        }
    }
}
@Composable
@Preview
fun IntroScreen(onCLick:()-> Unit ={}) {
    var ctxt = LocalContext.current;
    Image(
        painter = painterResource(id= R.drawable.backround1),
        contentDescription = null,
        modifier = Modifier
            .background(Color.White)
            .fillMaxSize(),
        contentScale = ContentScale.Crop
    )
    Column (
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ){
    Image(
        painter = painterResource(id= R.drawable.fashions1),
        contentDescription = null,
        modifier = Modifier
            .padding(top=8.dp)
            .fillMaxSize(),
        contentScale = ContentScale.Fit
    )
        Image(
            painter = painterResource(id= R.drawable.title),
            contentDescription = null,
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            contentScale = ContentScale.Fit
        )

        Spacer(modifier = Modifier.height(24.dp))
        Button(
            onClick = {
                ctxt.startActivity(Intent(ctxt, SignInActivity::class.java))
            },
            shape = RoundedCornerShape(10.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = colorResource(R.color.darkGrey)),
            modifier = Modifier
                .padding(top=16.dp)
                .fillMaxWidth()
                .height(50.dp)
        ) {
            Text(text = "Sign In", color = Color.White)
        }

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = {   ctxt.startActivity(Intent(ctxt, SignUpActivity::class.java)) },
            shape = RoundedCornerShape(10.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = colorResource(R.color.darkGrey)),
            modifier = Modifier
                .padding(top=16.dp)
                .fillMaxWidth()
                .height(50.dp)
        ) {
            Text(text = "Sign Up", color = Color.White)
        }
    }
}
