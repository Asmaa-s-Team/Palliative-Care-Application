package com.example.palliativecareapp.authentication

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.example.palliativecareapp.R
import com.example.palliativecareapp.doctor.DoctorHome
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.doctor_login.*

class LoginDoctor : AppCompatActivity() {
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
        setContentView(R.layout.doctor_login)

        sharedPreferences = getSharedPreferences("myPrefs", Context.MODE_PRIVATE)
        auth = Firebase.auth
        val email = email.text
        val password = password.text
        buttonLogin.setOnClickListener {
            if(email.isNotEmpty() && password.isNotEmpty()){
                    signInWithEmailAndPassword(email.toString(),password.toString())
                    Toast.makeText(baseContext, "LogIn Success.",
                        Toast.LENGTH_SHORT).show()
                }
                else{
                    Toast.makeText(baseContext, "LogIn Failed. Please enter the EMPTY Fields .",
                        Toast.LENGTH_SHORT).show()
                }
        }
        signLogin.setOnClickListener {
            val i = Intent(this, RegisterDoctor::class.java)
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
                    editor.putString("doctor", "login")
                    editor.apply()
                    updateUI(user)
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
        val doctor = sharedPreferences.getString("doctor", "error")
        if(doctor!!.equals("login")){
            val editor = sharedPreferences.edit()
            editor.putString("userEmail", user!!.email)
            editor.putString("userId", user.uid)
            editor.apply()
            var i = Intent(this, DoctorHome::class.java)
            i.putExtra("email",user!!.email)
            i.putExtra("id",user.uid)
            startActivity(i)
        }
        if(doctor!!.equals("register")){
            var i = Intent(this, LoginDoctor::class.java)
            startActivity(i)
        }
    }
}