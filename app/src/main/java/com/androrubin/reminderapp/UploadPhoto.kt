package com.androrubin.reminderapp

import android.app.ProgressDialog
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.ImageView
import android.widget.Toast
import androidx.core.net.toUri
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import java.text.SimpleDateFormat
import java.util.*

class UploadPhoto : AppCompatActivity() {

    private lateinit var db:FirebaseFirestore
    private lateinit var mAuth: FirebaseAuth
    private lateinit var preview : ImageView
    private var name :String ?= null
    private var uid :String ?= null
    lateinit var ImageUri:Uri
    var type:String?=null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_upload_photo)

        Toast.makeText(this,"Select an image to upload",Toast.LENGTH_LONG).show()

        mAuth = FirebaseAuth.getInstance()
        val user = mAuth.currentUser
        uid = user?.uid
        name = user?.displayName

        type = intent.getStringExtra("PictureType")
        ImageUri = "".toUri()


        val uploadBtn:Button = findViewById(R.id.uploadImageBtn)
        val selectBtn:Button = findViewById(R.id.selectImageBtn)
        preview = findViewById(R.id.preview)

        selectBtn.setOnClickListener {

            selectImage()

        }

        uploadBtn.setOnClickListener {

            uploadImage()
        }
    }

    private fun uploadImage() {
        val progressDialog = ProgressDialog(this)
        progressDialog.setMessage("Uploading image....")
        progressDialog.setCancelable(false)
        progressDialog.show()

        val formatter = SimpleDateFormat("yyyy_MM_dd_HH_mm_ss", Locale.getDefault())
        val now = Date()
        val fileName = formatter.format(now)


        val storageReference = FirebaseStorage.getInstance().getReference("Profile Images/$uid/$fileName")

        if(ImageUri!="".toUri()){


         storageReference.putFile(ImageUri)
             .addOnSuccessListener {
                it.metadata!!.reference!!.downloadUrl
                    .addOnSuccessListener {

                        db = FirebaseFirestore.getInstance()
                        if(type == "Profile"){
                            db.collection("Users").document("$uid")
                                .update(
                                    mapOf(
                                        "LinkProfilePic" to "$it"
                                    )
                                )
                        }else{
                            db.collection("Users").document("$uid")
                                .update(
                                    mapOf(
                                        "LinkBannerPic" to "$it"
                                    )
                                )
                        }
                        preview.setImageURI(null)
                        Toast.makeText(this,"Image Uploaded",Toast.LENGTH_SHORT).show()
                        if(progressDialog.isShowing)
                            progressDialog.dismiss()
                        val intent = Intent(this,ProfileActivity::class.java)
                        startActivity(intent)
                        finish()

                    }.addOnFailureListener {
                        Log.w("Data Addition", "Error adding document", it)
                    }


         }.addOnFailureListener {

                 if(progressDialog.isShowing)
                     progressDialog.dismiss()
                 Toast.makeText(this,"Unable To upload image "+it.message,Toast.LENGTH_SHORT).show()
             }
        }else{
            if(progressDialog.isShowing)
                progressDialog.dismiss()
            Toast.makeText(this,"Select an Image to Upload",Toast.LENGTH_SHORT).show()
        }

    }

    private fun selectImage() {
        val intent = Intent()
        intent.type = "image/*"
        intent.action = Intent.ACTION_GET_CONTENT

        startActivityForResult(intent , 100)
    }

    override fun onBackPressed() {
        super.onBackPressed()
        val intent = Intent(this,ProfileActivity::class.java)
        startActivity(intent)
        finish()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode==100 && resultCode == RESULT_OK){

            ImageUri= data?.data!!
            preview.setImageURI(ImageUri)
        }
    }
}