package com.example.palliativecareapp.doctor

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.provider.MediaStore
import android.webkit.MimeTypeMap
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import java.io.IOException

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.example.palliativecareapp.R
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.activity_doctor_add_topic.*
import java.util.*

class DoctorAddTopic : AppCompatActivity() {
    private val PICK_IMAGE_REQUEST = 123
    private var filePath: Uri? = null
    private lateinit var auth: FirebaseAuth
    lateinit var db : FirebaseFirestore
    lateinit var userId : String
    lateinit var topicId : String
    var img:Uri? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_doctor_add_topic)

        imageView.setOnClickListener{
            val i = Intent(Intent.ACTION_PICK,MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            startActivityForResult(i,100)
        }

        db = FirebaseFirestore.getInstance()
        auth = Firebase.auth
        var user = auth.currentUser
        userId = user!!.uid

        add_topic.setOnClickListener {
            var title : String = topic_title.text.toString()
            var description : String = topic_description.text.toString()
            var information : String = topic_info.text.toString()
            if(title.isNotEmpty() && description.isNotEmpty() && information.isNotEmpty()){
                createTopic(title, description, information,userId)
                topic_title.text = null
                topic_description.text = null
                topic_info.text = null
            }
        }
    }

    private fun createTopic(title: String, description: String, information: String, userId: String) {
        val topic = hashMapOf(
            "name" to title,
            "autherId" to userId,
            "description" to description,
            "information" to information
        )
        db.collection("topics")
            .add(topic)
            .addOnSuccessListener { documentReference ->
                topicId = documentReference.id
                uploadImageToFirebase()
                Log.d("add success", "DocumentSnapshot added with ID: ${documentReference.id}")
            }
            .addOnFailureListener { e ->
                Log.w("add success", "Error adding document", e)
            }
    }

    // Function to open the image picker
    private fun openImageChooser() {
        val intent = Intent()
        intent.type = "image/*"
        intent.action = Intent.ACTION_GET_CONTENT
        startActivityForResult(Intent.createChooser(intent, "Select Image"), PICK_IMAGE_REQUEST)
    }

    // Function to handle the result of image selection
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == Activity.RESULT_OK && data != null && data.data != null) {
            filePath = data.data
            try {
                val bitmap = MediaStore.Images.Media.getBitmap(contentResolver, filePath)
                // Display the selected image if needed
                imageView.setImageBitmap(bitmap)
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
    }

    // Function to upload the selected image to Firebase Storage
    private fun uploadImageToFirebase() {

        val randomNumber = UUID.randomUUID().toString()
        val storageRef = FirebaseStorage.getInstance().getReference("images")
        val imgRef = storageRef.child("image " + randomNumber)
        imgRef.putFile(img!!)
            .addOnSuccessListener {
                Log.d("TAG", "SUCCESS : ")
            }
            .addOnFailureListener {
                Log.d("TAG", "FAILED : ")
            }
    }
    // Function to get the file extension of the selected image
    private fun getFileExtension(uri: Uri): String? {
        val contentResolver = contentResolver
        val mimeTypeMap = MimeTypeMap.getSingleton()
        return mimeTypeMap.getExtensionFromMimeType(contentResolver.getType(uri))
    }

    // Function to save the image URL in Firebase Database (optional, based on your requirements)
    private fun saveImageUrlToDatabase(imageUrl: String, topicId:String) {
        var documentReference = db.collection("topics").document(topicId!!)
        val data = hashMapOf(
            "logo" to imageUrl,
        )
        documentReference.update(data as Map<String, Any>)
            .addOnSuccessListener {
                // Field added successfully
            }
            .addOnFailureListener { e ->
                // Handle any errors
            }
    }
}

