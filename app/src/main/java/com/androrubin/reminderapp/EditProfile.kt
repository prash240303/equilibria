package com.androrubin.reminderapp

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.View
import android.widget.*
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class EditProfile : AppCompatActivity() {
    private lateinit var db: FirebaseFirestore
    private lateinit var mAuth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_profile)

        db= FirebaseFirestore.getInstance()
        mAuth = FirebaseAuth.getInstance()
        val currentUser = mAuth.currentUser
        val name = currentUser?.displayName
        val uid = currentUser?.uid

        val edtName = findViewById<EditText>(R.id.edtName)
        val edtEmail = findViewById<EditText>(R.id.edtEmail)
        val edtPhone = findViewById<EditText>(R.id.edtPhone)
        val edtBatchYear = findViewById<EditText>(R.id.edtBatchYear)
        val btnSave = findViewById<Button>(R.id.btnSave)
        val mySpinner = findViewById<Spinner>(R.id.spBranches)
        var spin_result:String?=null


        db = FirebaseFirestore.getInstance()
        db.collection("Users").document("$uid")
            .get()
            .addOnSuccessListener {

                //Returns value of corresponding field
                val l = it["Name"].toString()
                val m = it["Email Id"].toString()
                val n = it["Phone Number"].toString()
                val p= it["Batch Year"].toString()

                if(l != "" && m != "" && n != "" && p != ""){
                    edtName.setText(l)
                    edtEmail.setText(m)
                    edtPhone.setText(n)
                    edtBatchYear.setText(p)
                }
            }



        mySpinner.onItemSelectedListener = object :

            AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long)
            {
                spin_result = parent?.getItemAtPosition(position).toString()
            }
            override fun onNothingSelected(parent: AdapterView<*>?)
            {

            }

        }

        btnSave.setOnClickListener {

            if (TextUtils.isEmpty(edtName.getText()?.trim().toString())) {
                edtName.setError("Field cannot be empty")
                edtName.requestFocus()
            }else  if (TextUtils.isEmpty(edtPhone.getText()?.trim().toString())) {
                edtPhone.setError("Field cannot be empty")
                edtPhone.requestFocus()
            }
            else if (TextUtils.isEmpty(edtEmail.getText()?.trim().toString())) {
                edtEmail.setError("Field cannot be empty")
                edtEmail.requestFocus()
            }
            else  if (TextUtils.isEmpty(edtBatchYear.getText()?.trim().toString())) {
                edtBatchYear.setError("Field cannot be empty")
                edtBatchYear.requestFocus()
            }else{

                db.collection("Users").document("$uid")
                    .update(
                        mapOf(
                            "Name" to edtName.text.trim().toString(),
                            "Phone Number" to edtPhone.text.trim().toString(),
                            "Email Id" to edtEmail.text.trim().toString(),
                            "Batch Year" to edtBatchYear.text.trim().toString(),
                            "Branch" to "$spin_result",
                        )
                    ) .addOnSuccessListener {docRef ->
                        Log.d("Data Addition", "DocumentSnapshot written with ID: ${docRef}.id")
                        Toast.makeText(this,"Saved", Toast.LENGTH_SHORT).show()
                    }
                    .addOnFailureListener { e ->
                        Log.w("Data Addition", "Error adding document", e)
                    }

                startActivity(Intent(this,ProfileActivity::class.java))
                finish()
            }
        }
    }

    override fun onBackPressed() {
        super.onBackPressed()
        startActivity(Intent(this,ProfileActivity::class.java))
        finish()
    }
}