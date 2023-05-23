package com.example.palliativecareapp.doctor

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
import kotlinx.android.synthetic.main.doctor_profile.*

class DoctorProfile : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth
    lateinit var db : FirebaseFirestore
    var name = ""
    var email = ""
    var phone = ""
    var image = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.doctor_profile)

        home.setOnClickListener {
            val intent = Intent(this@DoctorProfile, DoctorHome::class.java)
            startActivity(intent)
        }
        chat.setOnClickListener {
            val intent = Intent(this@DoctorProfile, DoctorChat::class.java)
            startActivity(intent)
        }
        notifications.setOnClickListener {
            val intent = Intent(this@DoctorProfile, DoctorNotifications::class.java)
            startActivity(intent)
        }
        topics.setOnClickListener {
            val intent = Intent(this@DoctorProfile, DoctorTopics::class.java)
            startActivity(intent)
        }

        auth = Firebase.auth
        var user = auth.currentUser
        val id = user!!.uid

        db = Firebase.firestore
        db.collection("doctors").document(id!!).get().addOnSuccessListener { result ->
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
                    .child("doctors")
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
            editor.putString("doctor", "")
            editor.putString("patient", "")
            editor.apply()
            FirebaseAuth.getInstance().signOut()
            val i = Intent(this, MainActivity::class.java)
            startActivity(i)
        }
    }
}