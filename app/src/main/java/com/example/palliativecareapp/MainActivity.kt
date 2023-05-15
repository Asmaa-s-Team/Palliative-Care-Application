package com.example.palliativecareapp

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.palliativecareapp.authentication.Register
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {
    lateinit var sharedPreferences : SharedPreferences
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        sharedPreferences = getSharedPreferences("myPrefs", Context.MODE_PRIVATE)
        patient_btn.setOnClickListener {
            val editor = sharedPreferences.edit()
            editor.putString("userType", "patient")
            editor.apply()
            var i = Intent(this, Register::class.java)
            startActivity(i)
        }

        doctor_btn.setOnClickListener {
            val sharedPreferences = getSharedPreferences("myPrefs", Context.MODE_PRIVATE)
            val editor = sharedPreferences.edit()
            editor.putString("userType", "doctor")
            editor.apply()
            var i = Intent(this, Register::class.java)
            startActivity(i)
        }
    }
}