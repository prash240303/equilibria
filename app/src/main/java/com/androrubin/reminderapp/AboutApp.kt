package com.androrubin.reminderapp

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_about_app.*
import java.lang.Boolean


class AboutApp : AppCompatActivity() {
    var prevStarted = "yes"
    override fun onResume() {
        super.onResume()
        val sharedpreferences =
            getSharedPreferences("Equilibria", Context.MODE_PRIVATE)
        if (!sharedpreferences.getBoolean(prevStarted, false)) {
            val editor = sharedpreferences.edit()
            editor.putBoolean(prevStarted, Boolean.TRUE)
            editor.apply()

            buttonSkip.setOnClickListener {
                moveToSecondary()
            }


        } else {
            moveToSecondary()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_about_app)
    }

    fun moveToSecondary() {
        // use an intent to travel from one activity to another.
        val intent = Intent(this, LoginActivity::class.java)
        startActivity(intent)
    }
}