package com.androrubin.reminderapp

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import com.google.firebase.auth.FirebaseAuth

class SplashScreen : AppCompatActivity() {
    private lateinit var mAuth: FirebaseAuth
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash_screen)

        val hide = supportActionBar?.hide()
        mAuth = FirebaseAuth.getInstance()
        val user = mAuth.currentUser

        if (user != null) {

            Handler().postDelayed({
                val intent = Intent(this, MainActivity::class.java)
                startActivity(intent)
                finish()
            }, 2500)
        } else {

            Handler().postDelayed({
                val signInintent = Intent(this, LoginActivity::class.java)
                startActivity(signInintent)
                finish()
            }, 3500)
        }
    }
}