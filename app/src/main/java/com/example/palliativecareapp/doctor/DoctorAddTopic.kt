package com.example.palliativecareapp.doctor

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.provider.MediaStore
import android.webkit.MimeTypeMap
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.storage.FirebaseStorage
import java.io.IOException
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.example.palliativecareapp.R
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import kotlinx.android.synthetic.main.doctor_add_topic.*
import kotlinx.android.synthetic.main.doctor_add_topic.imageView
import kotlinx.android.synthetic.main.doctor_add_topic.topic_description
import kotlinx.android.synthetic.main.doctor_add_topic.topic_info
import kotlinx.android.synthetic.main.doctor_add_topic.topic_title
import kotlinx.android.synthetic.main.doctor_edit_topic.*
import java.util.*

class DoctorAddTopic : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth
    lateinit var db : FirebaseFirestore
    lateinit var userId : String
    lateinit var topicId : String
    var img:Uri? = null
    lateinit var uri : Uri
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.doctor_add_topic)

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
        topic_video.setOnClickListener {
            val intent = Intent(
                Intent.ACTION_PICK,
                MediaStore.Video.Media.EXTERNAL_CONTENT_URI)
            intent.setType("video/*")
            startActivityForResult(Intent.createChooser(intent, "Select VIDEO"), 100)
        }
        topic_chart.setOnClickListener {
            val intent = Intent (this,Chart::class.java)
            startActivity(intent)
        }
        topic_pdf.setOnClickListener {
            val intent = Intent()
            intent.setType("pdf/*")
            Intent.ACTION_GET_CONTENT
            MediaStore.Files.FileColumns.MEDIA_TYPE_DOCUMENT
            startActivityForResult(Intent.createChooser(intent, "Select PDF"), 100)

        }
        topic_image.setOnClickListener {
            val intent = Intent(
                Intent.ACTION_PICK,
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            intent.setType("image/*")
            startActivityForResult(Intent.createChooser(intent, "Select IMAGE"), 100)
        }
    }

    private fun createTopic(title: String, description: String, information: String, userId: String) {
        val topic = hashMapOf(
            "name" to title,
            "autherId" to userId,
            "description" to description,
            "information" to information,
            "hidden" to false,
            "subscription" to false,
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

    // Function to handle the result of image selection
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == RESULT_OK && requestCode == 100){
            img = data!!.data!!
            imageView.setImageURI(img)
            uri = data!!.data!!
//            uriTxt.setText(uri.toString())
            upload()
        }
    }
    private fun upload() {
        var mStorage = Firebase.storage.getReference("Uploads")
        val pdfref = mStorage.child(uri.lastPathSegment.toString())
        pdfref.putFile(uri).addOnSuccessListener {
            Toast.makeText(this, "Upload", Toast.LENGTH_LONG)
        }.addOnFailureListener {
            Toast.makeText(this,"Failed", Toast.LENGTH_LONG)
        }
    }
    // Function to upload the selected image to Firebase Storage
    private fun uploadImageToFirebase() {
        val randomNumber = UUID.randomUUID().toString()
        val extension = getFileExtension(img!!)
        val storageRef = FirebaseStorage.getInstance().getReference("topic_images")
        val imgRef = storageRef.child(randomNumber + "." + extension)
        imgRef.putFile(img!!)
            .addOnSuccessListener { taskSnapshot ->
                saveImageInFirestore(taskSnapshot.metadata?.name!!)
                Log.d("TAG", "SUCCESS : image uploaded")
            }
            .addOnFailureListener {
                Log.d("TAG", "FAILED : failed uploading image")
            }
    }
    // Function to get the file extension of the selected image
    private fun getFileExtension(uri: Uri): String? {
        val contentResolver = contentResolver
        val mimeTypeMap = MimeTypeMap.getSingleton()
        return mimeTypeMap.getExtensionFromMimeType(contentResolver.getType(uri))
    }

    // Function to save the image URL in Firebase Database (optional, based on your requirements)
    fun saveImageInFirestore(imageName:String) {
        var map = hashMapOf(
            "logo" to imageName,
        )
        db.collection("topics").document(topicId).update(map as Map<String, Any>)
            .addOnSuccessListener {
                Log.e("save image ", "image saved successfully")
//                    Toast.makeText(this, "Edited Successfully", Toast.LENGTH_SHORT).show()
                val intent = Intent(this, DoctorHome::class.java)
                startActivity(intent)
            }
            .addOnFailureListener { e ->
                Log.e("save image", "failed save image")
                // Handle the error and display an error message if necessary
            }
    }
}

