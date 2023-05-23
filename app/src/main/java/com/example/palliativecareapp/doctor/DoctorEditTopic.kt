package com.example.palliativecareapp.doctor

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.palliativecareapp.R
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.doctor_add_topic.*
import kotlinx.android.synthetic.main.doctor_edit_topic.*
import kotlinx.android.synthetic.main.doctor_edit_topic.imageView
import kotlinx.android.synthetic.main.doctor_edit_topic.topic_description
import kotlinx.android.synthetic.main.doctor_edit_topic.topic_info
import kotlinx.android.synthetic.main.doctor_edit_topic.topic_title
import kotlinx.android.synthetic.main.doctor_topic.*
import kotlinx.android.synthetic.main.item_chat_search.*
import java.util.*


class DoctorEditTopic : AppCompatActivity() {
    lateinit var name : String
    lateinit var description : String
    lateinit var information : String
    lateinit var logo : String
    var img: Uri? = null
    var db = FirebaseFirestore.getInstance()
    lateinit var topicId : String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.doctor_edit_topic)

        topicId = intent.getStringExtra("topicId")!!

        imageView.setOnClickListener {
            val i = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            startActivityForResult(i, 100)
        }

        db.collection("topics").document(topicId!!).get()
            .addOnSuccessListener { result ->
                if (result != null) {
                    name = result.getString("name").toString()
                    description = result.getString("description").toString()
                    information = result.getString("information").toString()
                    logo = result.getString("logo").toString()
                    topic_title.setText(name)
                    topic_info.setText(information)
                    topic_description.setText(description)
                    val storageRef = FirebaseStorage.getInstance().getReference()
                        .child("topic_images")
                        .child(logo)
                    storageRef.downloadUrl.addOnSuccessListener { uri ->
                        val imageUrl = uri.toString()
                        Picasso.with(this).load(imageUrl).into(imageView)
                    }
                }
                Log.e("success", "${result.id} => ${result.data}")
            }
            .addOnFailureListener { exception ->
                Log.e("error", "Error getting topic", exception)
                Toast.makeText(this, "There is an error getting topic", Toast.LENGTH_SHORT)
            }


        edit_topic.setOnClickListener {
            var title: String = topic_title.text.toString()
            var description: String = topic_description.text.toString()
            var information: String = topic_info.text.toString()
            if (title.isNotEmpty() && description.isNotEmpty() && information.isNotEmpty()) {
                editTopic(title, description, information)
                topic_title.text = null
                topic_description.text = null
                topic_info.text = null
            }
        }
    }
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == RESULT_OK && requestCode == 100){
            img = data!!.data!!
            imageView.setImageURI(img)
        }
    }
    fun deleteOldImage(){
        FirebaseStorage.getInstance().getReference()
            .child("topic_images")
            .child(logo)
            .delete()
            .addOnSuccessListener {
                Log.d("deleteOldImage", "Image deleted successfully.")
            }
            .addOnFailureListener { exception ->
                Log.d("deleteOldImage", "Failed to delete image: ${exception.message}")
            }
    }

        fun editTopic(title: String, description: String, information: String){
            var map = hashMapOf(
                "name" to title,
                "description" to description,
                "information" to information,
            )
            db.collection("topics").document(topicId!!)
                .update(map as Map<String, Any>)
                .addOnSuccessListener {
                    uploadImageToFirebase()
                    Log.e("edit topic", "edited successfully")
//                    Toast.makeText(this, "Edited Successfully", Toast.LENGTH_SHORT).show()
//                    val intent = Intent(this, DoctorHome::class.java)
//                    startActivity(intent)
                }
                .addOnFailureListener { e ->
                    Log.e("edit topic", "edit failed")
                    // Handle the error and display an error message if necessary
                }
        }
    private fun uploadImageToFirebase() {
        deleteOldImage()
        val randomNumber = UUID.randomUUID().toString()
        val storageRef = FirebaseStorage.getInstance().getReference("topic_images")
        val imgRef = storageRef.child("image" + randomNumber)
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