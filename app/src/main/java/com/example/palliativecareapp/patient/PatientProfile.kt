package com.example.palliativecareapp.patient

import android.content.Context
import android.content.Intent
import android.net.InetAddresses
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import com.example.palliativecareapp.Analytics
import com.example.palliativecareapp.R
import com.example.palliativecareapp.authentication.MainActivity
import com.example.palliativecareapp.doctor.DoctorHome
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.doctor_edit_topic.*
import kotlinx.android.synthetic.main.doctor_home.*
import kotlinx.android.synthetic.main.patient_profile.*
import kotlinx.android.synthetic.main.patient_profile.chat
import kotlinx.android.synthetic.main.patient_profile.home
import kotlinx.android.synthetic.main.patient_profile.imageView
import kotlinx.android.synthetic.main.patient_profile.notifications
import kotlinx.android.synthetic.main.patient_profile.topics
import java.util.*

class PatientProfile : AppCompatActivity() {
    private var auth: FirebaseAuth = Firebase.auth
    lateinit var db : FirebaseFirestore
    var name = ""
    var email = ""
    var phone = ""
    var image = ""
    var img : Uri? = null
    var user = auth.currentUser
    var userId = user!!.uid
    var analytics = Analytics()
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

        db = Firebase.firestore
        db.collection("patients").document(userId!!).get().addOnSuccessListener { result ->
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
        imageView.setOnClickListener {
            val i = Intent(Intent.ACTION_PICK,MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            startActivityForResult(i,100)
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
        analytics.screenTrack("PatientProfile","PatientProfile")
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == RESULT_OK && requestCode == 100){
            img = data!!.data!!
            imageView.setImageURI(img)
            uploadImageToFirebase()
        }
    }
    fun deleteOldImage(){
        FirebaseStorage.getInstance().getReference()
            .child("patients")
            .child(image)
            .delete()
            .addOnSuccessListener {
                Log.d("deleteOldImage", "Image deleted successfully.")
            }
            .addOnFailureListener { exception ->
                Log.d("deleteOldImage", "Failed to delete image: ${exception.message}")
            }
    }
    fun uploadImageToFirebase(){
        if(image != "person.png"){
            deleteOldImage()
        }
        val randomNumber = UUID.randomUUID().toString()
        val storageRef = FirebaseStorage.getInstance().getReference("patients")
        val imgRef = storageRef.child("patient" + randomNumber)
        imgRef.putFile(img!!)
            .addOnSuccessListener { taskSnapshot ->
                val imageName = taskSnapshot.metadata?.name
                saveImageInFirestore(imageName!!)
//                Toast.makeText(this, "$imageName", Toast.LENGTH_LONG).show()
                Log.d("TAG", "SUCCESS : image uploaded")
            }
            .addOnFailureListener {
                Log.d("TAG", "FAILED : failed uploading image")
            }
    }
    fun saveImageInFirestore(imageName:String) {
        var map = hashMapOf(
            "image" to imageName,
        )
        db.collection("patients").document(userId).update(map as Map<String, Any>)
            .addOnSuccessListener {
                Log.e("save image ", "image saved successfully")
//                    Toast.makeText(this, "Edited Successfully", Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener { e ->
                Log.e("save image", "failed save image")
                // Handle the error and display an error message if necessary
            }
    }

    override fun onResume() {
        super.onResume()
        //start
        analytics.trackScreenView("PatientProfile")
    }

    override fun onPause() {
        super.onPause()
        //end
        analytics.trackScreenDuration()
    }
}