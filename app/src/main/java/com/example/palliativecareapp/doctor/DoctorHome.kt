package com.example.palliativecareapp.doctor

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.example.palliativecareapp.R
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.activity_doctor_home.*

class DoctorHome : AppCompatActivity() {
    lateinit var db : FirebaseFirestore
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_doctor_home)

        var first = ""
        var middle = ""
        var last =""
        var id = intent.getStringExtra("id")
        db = Firebase.firestore
        db.collection("doctors").document(id!!).get().addOnSuccessListener { result ->
            var data = result.data
            var hash = data?.get("name") as? HashMap<String, Any>
             first = hash?.get("first").toString()
             middle = hash?.get("middle").toString()
             last = hash?.get("last").toString()
            Log.e("first " , first)
            Log.e("last " , last)
            Log.e("middle " , middle)
            doctor_name.text = "د. $first $last"

        }.addOnFailureListener {
            Log.e("user info", "Fail")
        }
    }
}