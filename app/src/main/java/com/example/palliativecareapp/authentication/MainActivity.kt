package com.example.palliativecareapp.authentication

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.palliativecareapp.R
import com.example.palliativecareapp.doctor.DoctorHome
import com.example.palliativecareapp.patient.PatientHome
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {
    lateinit var sharedPreferences : SharedPreferences
    private lateinit var auth: FirebaseAuth

    override fun onStart() {
        super.onStart()
            updateUI()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        sharedPreferences = getSharedPreferences("myPrefs", Context.MODE_PRIVATE)

        patient_btn.setOnClickListener {
            val editor = sharedPreferences.edit()
            editor.putString("patient", "patient")
            editor.apply()
            var i = Intent(this, RegisterPatient::class.java)
            startActivity(i)
        }

        doctor_btn.setOnClickListener {
            val sharedPreferences = getSharedPreferences("myPrefs", Context.MODE_PRIVATE)
            val editor = sharedPreferences.edit()
            editor.putString("doctor", "doctor")
            editor.apply()
            var i = Intent(this, RegisterDoctor::class.java)
            startActivity(i)
        }
    }
    private fun updateUI() {
        val doctor = sharedPreferences.getString("doctor", "error")
        val patient = sharedPreferences.getString("patient", "error")
        if(doctor!!.equals("login")) {
            var i = Intent(this, DoctorHome::class.java)
            startActivity(i)
        }
        if(doctor!!.equals("register")) {
            var i = Intent(this, LoginDoctor::class.java)
            startActivity(i)
        }
        if(patient!!.equals("login")) {
            var i = Intent(this, PatientHome::class.java)
            startActivity(i)
        }
        if(patient!!.equals("register")) {
            var i = Intent(this, LoginPatient::class.java)
            startActivity(i)
        }
    }
}