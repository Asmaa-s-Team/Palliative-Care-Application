package com.example.palliativecareapp.doctor

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.example.palliativecareapp.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_doctor_topic.*

class DoctorTopic : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth
    private lateinit var topicId: String
    lateinit var name : String
    lateinit var description : String
    lateinit var information : String
    lateinit var logo : String
    lateinit var images : ArrayList<HashMap<String, String>>
    lateinit var video : ArrayList<HashMap<String, String>>
    lateinit var pdf : ArrayList<HashMap<String, String>>
    lateinit var infographic : ArrayList<HashMap<String, String>>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_doctor_topic)

//        auth = Firebase.auth
//        var user = auth.currentUser
         topicId = intent.getStringExtra("userId")!!

        val db = FirebaseFirestore.getInstance()
        db.collection("topics").document(topicId!!).get()
            .addOnSuccessListener { result->
                if (result != null ) {
                     name = result.getString("name").toString()
                     description = result.getString("description").toString()
                     information = result.getString("information").toString()
                     images = result.get("images") as ArrayList<HashMap<String, String>>
                     video = result.get("video") as ArrayList<HashMap<String, String>>
                     pdf = result.get("pdf") as ArrayList<HashMap<String, String>>
                     infographic = result.get("infographic") as ArrayList<HashMap<String, String>>
                     logo = result.getString("logo").toString()
                    topic_name.text = name
                    topic_description.text = description
                    val storageImage = FirebaseStorage.getInstance().reference
                    val storageRef = storageImage.child(logo!!)
                    storageRef.downloadUrl.addOnSuccessListener { uri ->
                        val imageUrl = uri.toString()
                        Picasso.with(this).load(imageUrl).into(topic_logo)
                    }
                }
            }
            .addOnFailureListener { exception ->
                Log.e("error", "Error getting topic", exception)
                Toast.makeText(this, "There is an error getting topic", Toast.LENGTH_SHORT)
            }
    }
}