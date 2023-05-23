package com.example.palliativecareapp.patient

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.example.palliativecareapp.R
import com.example.palliativecareapp.authentication.MainActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.doctor_home.*
import kotlinx.android.synthetic.main.patient_profile.*
import kotlinx.android.synthetic.main.patient_profile.chat
import kotlinx.android.synthetic.main.patient_profile.home
import kotlinx.android.synthetic.main.patient_profile.notifications
import kotlinx.android.synthetic.main.patient_profile.topics

class PatientProfile : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth
    lateinit var db : FirebaseFirestore
    var name = ""
    var email = ""
    var phone = ""
    var image = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.patient_profile)

        home.setOnClickListener {
            val intent = Intent(this@PatientProfile, PatientHome::class.java)
            startActivity(intent)
        }
        chat.setOnClickListener {
            val intent = Intent(this@PatientProfile, PatientChat::class.java)
            startActivity(intent)
        }
        notifications.setOnClickListener {
            val intent = Intent(this@PatientProfile, PatientNotifications::class.java)
            startActivity(intent)
        }
        topics.setOnClickListener {
            val intent = Intent(this@PatientProfile, PatientTopics::class.java)
            startActivity(intent)
        }

        auth = Firebase.auth
        var user = auth.currentUser
        val id = user!!.uid

        db = Firebase.firestore
        db.collection("patients").document(id!!).get().addOnSuccessListener { result ->
            if (result != null ) {
                var data = result.data!!
                name = data.get("name") as String
                email = data.get("email") as String
                phone = data.get("phone") as String
                image = data.get("image") as String
                nameText.text = "$name"
                emailText.text = "$email"
                phoneText.text = "$phone"
                val storageRef = FirebaseStorage.getInstance().getReference()
                    .child("patients")
                    .child(image)
                storageRef.downloadUrl.addOnSuccessListener { uri ->
                    val imageUrl = uri.toString()
                    Picasso.with(this).load(imageUrl).into(imageView)
                }
            }
        }.addOnFailureListener {
            Log.e("user info", "Fail")
        }

        logout.setOnClickListener {
            val sharedPreferences = getSharedPreferences("myPrefs", Context.MODE_PRIVATE)
            val editor = sharedPreferences.edit()
            editor.putString("patient", "")
            editor.putString("doctor", "")
            editor.apply()
            FirebaseAuth.getInstance().signOut()
            val i = Intent(this, MainActivity::class.java)
            startActivity(i)
        }
    }
}