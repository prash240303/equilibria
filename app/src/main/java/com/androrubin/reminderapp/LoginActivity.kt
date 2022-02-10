package com.androrubin.reminderapp

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.TextView
import android.widget.Toast
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.firestore.FirebaseFirestore

class LoginActivity : AppCompatActivity() {

    companion object{
        private const val RC_SIGN_IN = 120

    }
    private lateinit var mAuth: FirebaseAuth
    private lateinit var googleSignInClient: GoogleSignInClient
    private lateinit var db: FirebaseFirestore
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        supportActionBar?.hide()

        //Configure Google Sign In
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()
        googleSignInClient = GoogleSignIn.getClient(this,gso)
        mAuth = FirebaseAuth.getInstance()

        val sign_in_btn = findViewById<TextView>(R.id.sign_in_btn)
        sign_in_btn.setOnClickListener {
            signIn()
        }
    }
    private fun signIn() {
        val signInIntent = googleSignInClient.signInIntent
        googleSignInClient.signOut();
        startActivityForResult(signInIntent, RC_SIGN_IN)
    }
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            val exception =task.exception
            if(task.isSuccessful){

                try {
                    // Google Sign In was successful, authenticate with Firebase
                    val account = task.getResult(ApiException::class.java)!!
                    Log.d("MainActivity2", "firebaseAuthWithGoogle:" + account.id)
                    firebaseAuthWithGoogle(account.idToken!!)
                } catch (e: ApiException) {
                    // Google Sign In failed, update UI appropriately
                    Log.w("MainActivity2", "Google sign in failed", e)
                }
            }
            else{
                Log.w("MainActivity2",exception.toString())
            }

        }
    }
    private fun firebaseAuthWithGoogle(idToken: String) {
        val credential = GoogleAuthProvider.getCredential(idToken, null)
        mAuth.signInWithCredential(credential)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    Log.d("MainActivity2", "signInWithCredential:success")
                    Toast.makeText(this, "Login Successful", Toast.LENGTH_SHORT).show()

                    val dashboardIntent = Intent(this, MainActivity::class.java)
                    startActivity(dashboardIntent)
                    finish()
                }
                else {
                    // If sign in fails, display a message to the user
                    Log.w("MainActivity2", "signInWithCredential:failure", task.exception)
                }
            }
    }
}