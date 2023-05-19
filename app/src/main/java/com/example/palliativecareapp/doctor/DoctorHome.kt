package com.example.palliativecareapp.doctor

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.example.palliativecareapp.R
import com.example.palliativecareapp.mutualScreens.DoctorChat
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.activity_doctor_home.*

class DoctorHome : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth
    lateinit var sharedPreferences : SharedPreferences
    var first = ""
    var middle = ""
    var last =""

    lateinit var db : FirebaseFirestore
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_doctor_home)
        sharedPreferences = this.getSharedPreferences("my_preferences", Context.MODE_PRIVATE)
        auth = Firebase.auth
        var user = auth.currentUser
        val id = user!!.uid

        db = Firebase.firestore
        db.collection("doctors").document(id!!).get().addOnSuccessListener { result ->
            if (result != null ) {
                var data = result.data!!
                var hash = data.get("name") as? HashMap<String, Any>
                first = hash?.get("first").toString()
                middle = hash?.get("middle").toString()
                last = hash?.get("last").toString()
                Log.e("first ", first)
                Log.e("last ", last)
                Log.e("middle ", middle)
                doctor_name.text = "د. $first $middle $last"
            }
        }.addOnFailureListener {
            Log.e("user info", "Fail")
        }

        val editor = sharedPreferences.edit()
        editor.putString("first", first)
        editor.putString("middle", middle)
        editor.putString("last", last)
        editor.apply()

        add_topic.setOnClickListener {
            val i = Intent(this, DoctorChat::class.java)
            startActivity(i)
        }
    }
}