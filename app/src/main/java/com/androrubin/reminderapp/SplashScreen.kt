package com.androrubin.reminderapp

import android.app.PendingIntent.getActivity
import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class SplashScreen : AppCompatActivity() {

    private lateinit var db: FirebaseFirestore
    private lateinit var mAuth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash_screen)

        val hide = supportActionBar?.hide()
        mAuth = FirebaseAuth.getInstance()
        val user = mAuth.currentUser

        if (user != null) {

            Handler().postDelayed({

                val user = mAuth.currentUser
                val name = user?.uid

                db = FirebaseFirestore.getInstance()
                db.collection("Users").document("$name")
                    .get()
                    .addOnSuccessListener {

                        //Returns value of corresponding field
                        val b = it["ProfileCreated"].toString()


                        if (b=="1") {


                            val dashboardIntent = Intent(this, MainActivity::class.java)
                            startActivity(dashboardIntent)
                            finish()

                        }
                        else {

                            val dashboardIntent = Intent(this,CreateProfile::class.java)
                            startActivity(dashboardIntent)
                            finish()
                        }
                    }
            }, 1500)
        }else {

            Handler().postDelayed({
                if(onBoardingFinished()){
                    val loginInintent = Intent(this, LoginActivity::class.java)
                    startActivity(loginInintent)
                    finish()
                }
                else {
                    val signInintent = Intent(this, AboutApp::class.java)
                    startActivity(signInintent)
                    finish()
                }
            }, 3500)

        }
    }

    private fun onBoardingFinished(): Boolean {
        val sharedPref = applicationContext.getSharedPreferences("onBoarding", Context.MODE_PRIVATE)
        return sharedPref.getBoolean("Finished",false)

    }
}