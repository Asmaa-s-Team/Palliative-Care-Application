package com.example.palliativecareapp.doctor

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import com.example.palliativecareapp.Analytics
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
import kotlinx.android.synthetic.main.doctor_profile.chat
import kotlinx.android.synthetic.main.doctor_profile.emailText
import kotlinx.android.synthetic.main.doctor_profile.home
import kotlinx.android.synthetic.main.doctor_profile.imageView
import kotlinx.android.synthetic.main.doctor_profile.logout
import kotlinx.android.synthetic.main.doctor_profile.nameText
import kotlinx.android.synthetic.main.doctor_profile.notifications
import kotlinx.android.synthetic.main.doctor_profile.phoneText
import kotlinx.android.synthetic.main.doctor_profile.topics
import kotlinx.android.synthetic.main.patient_profile.*
import java.util.*

class DoctorProfile : AppCompatActivity() {
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
        imageView.setOnClickListener {
            val i = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            startActivityForResult(i,100)
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
        analytics.screenTrack("DoctorProfile","DoctorProfile")
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
            .child("doctors")
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
        val storageRef = FirebaseStorage.getInstance().getReference("doctors")
        val imgRef = storageRef.child("doctor" + randomNumber)
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
        db.collection("doctors").document(userId).update(map as Map<String, Any>)
            .addOnSuccessListener {
                Log.e("save image ", "image saved successfully")
//                    Toast.makeText(this, "Edited Successfully", Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener { e ->
                Log.e("save image", "failed save image")
                // Handle the error and display an error message if necessary
            }
    }
}