package com.androrubin.reminderapp

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.bumptech.glide.Glide
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase

class ProfileActivity : AppCompatActivity() {

    private lateinit var mAuth: FirebaseAuth
    private lateinit var db: FirebaseFirestore
    private var name :String ?= null
    private var uid :String ?= null
    private lateinit var deleteDialog :AlertDialog
    private lateinit var logoutDialog :AlertDialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)

        supportActionBar?.title="Profile"

        val actionbar = supportActionBar
        //set back button
        actionbar?.setDisplayHomeAsUpEnabled(true)
        actionbar?.setDisplayHomeAsUpEnabled(true)

        val editProfileBtn = findViewById<TextView>(R.id.editProfile)
        val deleteAccountBtn = findViewById<TextView>(R.id.deleteAccount)
        val editProfilePicBtn = findViewById<FloatingActionButton>(R.id.editProfilePic)
        val editBannerPicBtn = findViewById<FloatingActionButton>(R.id.editBannerPic)
        val logoutBtn = findViewById<TextView>(R.id.logoutBtn)
        val bannerPic = findViewById<ImageView>(R.id.bannerPic)
        val profilePic = findViewById<ImageView>(R.id.profilePic)
        mAuth = FirebaseAuth.getInstance()
        val user = mAuth.currentUser
        name = user?.displayName
        uid = user?.uid

        setData()

        db = FirebaseFirestore.getInstance()
        db.collection("Users").document("$uid")
            .get()
            .addOnSuccessListener {

                //Returns value of corresponding field
                val a = it["LinkBannerPic"].toString()
                val b = it["LinkProfilePic"].toString()
                val c = it["Role"].toString()
                if(a != "")
                {
                    Glide.with(this).load(a).into(bannerPic)
                }
                if(b!= "")
                {
                    Glide.with(this).load(b).into(profilePic)
                }
            }

        val builder = AlertDialog.Builder(this)

        val view = layoutInflater.inflate(R.layout.delete_account_dialog,null)
        val no = view.findViewById<TextView>(R.id.no)
        val yes = view.findViewById<TextView>(R.id.yes)
        yes.setOnClickListener {

            deleteDialog.dismiss()
            val user2 = Firebase.auth.currentUser!!

            user2.delete()
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        Log.d("Deleting user", "User account deleted.")
                    }
                    else{
                        Log.d("Deleting user", "User account not deleted.")
                    }
                }
            db.collection("Users").document("$uid")
                .delete()
                .addOnSuccessListener { Log.d("Deleting Account", "DocumentSnapshot successfully deleted!") }
                .addOnFailureListener { e -> Log.w("Deleting Account", "Error deleting document", e) }
            val dashboardIntent = Intent(this,LoginActivity::class.java)
            startActivity(dashboardIntent)
            finishAffinity()
        }
        no.setOnClickListener {
            deleteDialog.dismiss()
        }
        builder.setView(view)
        deleteDialog =  builder.create()


        val view2 = layoutInflater.inflate(R.layout.logout_account_dialog,null)
        val no2 = view2.findViewById<TextView>(R.id.no2)
        val yes2 = view2.findViewById<TextView>(R.id.yes2)
        yes2.setOnClickListener {

            logoutDialog.dismiss()

            Toast.makeText(this,"User logged out ", Toast.LENGTH_SHORT).show()
            mAuth.signOut()
            val intent = Intent(this,LoginActivity::class.java)
            startActivity(intent)
            finishAffinity()

        }
        no2.setOnClickListener {
            logoutDialog.dismiss()
        }

        builder.setView(view2)
        logoutDialog =  builder.create()

        logoutBtn.setOnClickListener {
            logoutDialog.show()
        }

        editProfileBtn.setOnClickListener {
            val intent = Intent(this,EditProfile::class.java)
            startActivity(intent)
            finish()
        }

        editProfilePicBtn.setOnClickListener {

            val intent = Intent(this,UploadPhoto::class.java)
            intent.putExtra("PictureType","Profile")
            startActivity(intent)
            finish()
        }

        editBannerPicBtn.setOnClickListener {
            val intent = Intent(this,UploadPhoto::class.java)
            intent.putExtra("PictureType","Banner")
            startActivity(intent)
            finish()
        }

        deleteAccountBtn.setOnClickListener {

            deleteDialog.show()

        }
    }

    private fun setData() {

        var txtName = findViewById<TextView>(R.id.textView4)
        var txtEmail = findViewById<TextView>(R.id.txtEmail)
        var txtPhone = findViewById<TextView>(R.id.txtPhone)
        var txtBranch = findViewById<TextView>(R.id.txtBranch)
        var txtBatchYear = findViewById<TextView>(R.id.txtBatchYear)

        db = FirebaseFirestore.getInstance()
        db.collection("Users").document("$uid")
            .get()
            .addOnSuccessListener {

                //Returns value of corresponding field
                val l = it["Name"].toString()
                val m = it["Email Id"].toString()
                val n = it["Phone Number"].toString()
                val o = it["Branch"].toString()
                val p= it["Batch Year"].toString()

                if(l != "" && m != "" && n != "" && p != ""){
                    txtName.text = l
                    txtEmail.text = m
                    txtPhone.text = n
                    txtBranch.text = o
                    txtBatchYear.text = p
                }
            }

    }
    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }
}