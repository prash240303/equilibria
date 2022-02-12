package com.androrubin.reminderapp

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.TextView
import android.widget.Toast
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.auth.FirebaseAuth

class Profile_Activity : AppCompatActivity() {

    private lateinit var mAuth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)

        supportActionBar?.title="Profile"

        val editProfileBtn = findViewById<TextView>(R.id.editProfile)
        val deleteAccountBtn = findViewById<TextView>(R.id.deleteAccount)
        val editProfilePicBtn = findViewById<FloatingActionButton>(R.id.editProfilePic)
        val editBannerPicBtn = findViewById<FloatingActionButton>(R.id.editBannerPic)
        val logoutBtn = findViewById<TextView>(R.id.logoutBtn)
        mAuth = FirebaseAuth.getInstance()

        logoutBtn.setOnClickListener {
            Toast.makeText(this,"User logged out ", Toast.LENGTH_SHORT).show()
            mAuth.signOut()
            val intent = Intent(this,LoginActivity::class.java)
            startActivity(intent)
            finishAffinity()
        }

        editProfileBtn.setOnClickListener {
            Toast.makeText(this,"Clicked Edit Profile",Toast.LENGTH_SHORT).show()
        }

        editProfilePicBtn.setOnClickListener {
            Toast.makeText(this,"Clicked Edit Profile Picture",Toast.LENGTH_SHORT).show()
        }

        editBannerPicBtn.setOnClickListener {
            Toast.makeText(this,"Clicked Edit Banner Picture",Toast.LENGTH_SHORT).show()
        }

        deleteAccountBtn.setOnClickListener {
            Toast.makeText(this,"Clicked Delete Account",Toast.LENGTH_SHORT).show()
        }
    }
}