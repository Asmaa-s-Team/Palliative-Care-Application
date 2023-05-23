package com.example.palliativecareapp.authentication

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.example.palliativecareapp.R
import com.example.palliativecareapp.patient.PatientHome
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.patient_login.*

class LoginPatient : AppCompatActivity() {
    lateinit var sharedPreferences : SharedPreferences

    private lateinit var auth: FirebaseAuth
    override fun onStart() {
        super.onStart()
        val currentUser = auth.currentUser
        if(currentUser != null){
            updateUI(currentUser)
        }
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.patient_login)

        sharedPreferences = getSharedPreferences("myPrefs", Context.MODE_PRIVATE)
        auth = Firebase.auth
        val email = email.text
        val password = password.text
        buttonLogin.setOnClickListener {
            if(email.isNotEmpty() && password.isNotEmpty()){
                    signInWithEmailAndPassword(email.toString(),password.toString())
                var i = Intent(this, PatientHome::class.java)
                startActivity(i)
                    Toast.makeText(baseContext, "LogIn Success.",
                        Toast.LENGTH_SHORT).show()
                }
                else{
                    Toast.makeText(baseContext, "LogIn Failed. Please enter the EMPTY Fields .",
                        Toast.LENGTH_SHORT).show()
                }
        }
        signLogin.setOnClickListener {
            val i = Intent(this, RegisterPatient::class.java)
            startActivity(i)
        }
        }
    private fun signInWithEmailAndPassword(email: String, password: String) {
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    Log.d("User:signInWithEmail", "signInWithEmail:success")
                    Toast.makeText(baseContext, "Authentication success.",
                        Toast.LENGTH_SHORT).show()
                    val user = auth.currentUser
                    val editor = sharedPreferences.edit()
                    editor.putString("patient", "login")
                    editor.apply()
                } else {
                    // If sign in fails, display a message to the user.
                    Log.w("User:signInWithEmail", "signInWithEmail:failure", task.exception)
                    Toast.makeText(baseContext, "Authentication failed.",
                        Toast.LENGTH_SHORT).show()
                    updateUI(null)
                }
            }
    }
    private fun updateUI(user: FirebaseUser?) {
        val patient = sharedPreferences.getString("patient", "error")
        if(patient!!.equals("login")){
            val editor = sharedPreferences.edit()
            editor.putString("userEmail", user!!.email)
            editor.putString("userId", user.uid)
            editor.apply()
            var i = Intent(this, PatientHome::class.java)
            i.putExtra("email",user!!.email)
            i.putExtra("id",user.uid)
            startActivity(i)
        }
        if(patient!!.equals("register")) {
            var i = Intent(this, LoginDoctor::class.java)
            startActivity(i)
        }
    }
}