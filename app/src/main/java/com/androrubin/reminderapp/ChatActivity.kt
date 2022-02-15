package com.androrubin.reminderapp

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.TextView
import org.w3c.dom.Text

class ChatActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat)

        val chatAcr : TextView = findViewById(R.id.tv_chat)
        val bundle : Bundle? = intent.extras

        val chat = bundle?.getString("chat")
        chatAcr.text = chat




    }
}