package com.androrubin.reminderapp

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.EditText
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.view.ActionMode
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.*

class ChatActivity : AppCompatActivity() {

    private lateinit var chatRecyclerView: RecyclerView
    private lateinit var messageBox: EditText
    private lateinit var sendButton: ImageView
    private lateinit var db: FirebaseFirestore
    private lateinit var messageAdapter: ChatAdapter
    private lateinit var messageList: ArrayList<Message>
    private lateinit var mAuth: FirebaseAuth

    private lateinit var chat: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat)

        val bundle: Bundle? = intent.extras
        chat = bundle?.getString("chat").toString()

        val actionbar = supportActionBar
        //set back button
        actionbar?.setDisplayHomeAsUpEnabled(true)
        actionbar?.setDisplayHomeAsUpEnabled(true)


        supportActionBar?.title = "$chat"

        mAuth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()

        val currentUser = mAuth.currentUser
        var name = currentUser?.displayName



        chatRecyclerView = findViewById(R.id.chatRecyclerView)
        messageBox = findViewById(R.id.messageBox)
        sendButton = findViewById(R.id.sentButton)
        messageList = ArrayList()
        messageAdapter = ChatAdapter(messageList)

        chatRecyclerView.layoutManager = LinearLayoutManager(this)
        chatRecyclerView.adapter = messageAdapter
        EventChangeListener()


        sendButton.setOnClickListener {




            if (TextUtils.isEmpty(messageBox.getText().trim().toString())) {
                messageBox.setError("Description cannot be empty")
                messageBox.requestFocus()
            } else {
                val data = hashMapOf(
                    "sendername" to "$name",
                    "message" to messageBox.getText().toString(),
                    "time" to FieldValue.serverTimestamp()
                )

                db.collection("Communities").document("$chat").collection("chats")
                    .add(data)
                    .addOnSuccessListener { docref ->
                        Log.d("Chat Data Addition",
                            "DocumentSnapshot written with ID: ${docref}.id")

                    }
                    .addOnFailureListener { e ->
                        Toast.makeText(this,
                            "Chat Data Addition Error adding document",
                            Toast.LENGTH_SHORT).show()
                    }

                messageAdapter.notifyDataSetChanged()
                messageBox.text = null

//                val intent = Intent(this,ChatActivity::class.java)
//                startActivity(intent)
//                finish()
            }
        }
    }


        override fun onSupportNavigateUp(): Boolean {
            onBackPressed()
            return true
        }

        private fun EventChangeListener() {
            db = FirebaseFirestore.getInstance()
            db.collection("Communities").document("$chat").collection("chats")
                .orderBy("time", Query.Direction.ASCENDING)
                .addSnapshotListener(object : EventListener<QuerySnapshot> {
                    override fun onEvent(
                        value: QuerySnapshot?,
                        error: FirebaseFirestoreException?
                    ) {

                        if (error != null) {
                            Log.e("Firestore Error", error.message.toString())
                            return
                        }
                        for (dc: DocumentChange in value?.documentChanges!!) {
                            if (dc.type == DocumentChange.Type.ADDED) {

                                messageList.add(dc.document.toObject(Message::class.java))
                            }
                        }
                        messageAdapter.notifyDataSetChanged()
                    }
                })
        }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        val inflater=menuInflater
        inflater.inflate(R.menu.announcement_menu,menu)
        return true
    }
    override fun onOptionsItemSelected(item: MenuItem):Boolean {
        return when (item.itemId) {
            R.id.notifybtn -> {
                val intent = Intent(this, Announcements::class.java)
                intent.putExtra("chat", chat)
                startActivity(intent)

                true
            }
            else -> {
                super.onOptionsItemSelected(item)
            }
        }
    }
}
