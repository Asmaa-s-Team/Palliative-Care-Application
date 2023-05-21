package com.example.palliativecareapp.patient

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.palliativecareapp.R
import com.example.palliativecareapp.adapters.CommentAdapter
import com.example.palliativecareapp.models.Comment
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_patient_topic.*
import java.text.SimpleDateFormat
import java.util.*

class PatientTopic : AppCompatActivity() {
    // ToDo: المرفقات
    private lateinit var auth: FirebaseAuth
    lateinit var db : FirebaseFirestore
    lateinit var topicId: String
    lateinit var name : String
    lateinit var description : String
    lateinit var information : String
    lateinit var logo : String
    lateinit var userId : String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_patient_topic)

        auth = Firebase.auth
        var user = auth.currentUser
        userId = user!!.uid
        topicId = intent.getStringExtra("topicId")!!

        db = FirebaseFirestore.getInstance()
        db.collection("topics").document(topicId!!).get()
            .addOnSuccessListener { result->
                if (result != null ) {
                    name = result.getString("name").toString()
                    description = result.getString("description").toString()
                    information = result.getString("information").toString()
                    logo = result.getString("logo").toString()
                    topic_name.text = name
                    topic_info.text = information
                    val storageImage = FirebaseStorage.getInstance().reference
                    val storageRef = storageImage.child(logo!!)
                    storageRef.downloadUrl.addOnSuccessListener { uri ->
                        val imageUrl = uri.toString()
                        Picasso.with(this).load(imageUrl).into(topic_image)
                    }
                    getComments()
                }
                Log.e("success", "${result.id} => ${result.data}")
            }
            .addOnFailureListener { exception ->
                Log.e("error", "Error getting topic", exception)
                Toast.makeText(this, "There is an error getting topic", Toast.LENGTH_SHORT)
            }


        btn_comment.setOnClickListener {
            var commentText : String = commentEditText.text.toString()
            if(commentText.isNotEmpty()){
                createComment(commentText)
                commentEditText.text = null
                getComments()
            }
        }

    }

    fun getComments() {
        var myAdapter : CommentAdapter
        var comments = ArrayList<Comment>()
        val query = db.collection("comments")
            .whereEqualTo("topicId", topicId)
        query.get()
            .addOnSuccessListener { querySnapshot ->
                if (!querySnapshot.isEmpty()) {
                    for (document in querySnapshot.documents) {
                        comments.add(
                            Comment(
                                document.getString("text").toString(),
                                document.getString("senderId").toString(),
                                document.getString("timestamp").toString(),
                                document.getString("topicId").toString(),
                            )
                        )
                        Log.e("success", "${document.id} => ${document.data}")
                    }
                    if (comments.isNotEmpty()) {
                        lv_comments.setVisibility(View.VISIBLE)
                        myAdapter = CommentAdapter(this, comments)
                        lv_comments.layoutManager = LinearLayoutManager(this)
                        lv_comments.adapter = myAdapter
                        myAdapter.notifyDataSetChanged()
                    } else {
                        lv_comments.setVisibility(View.INVISIBLE)
                    }
                }
            }
            .addOnFailureListener { exception ->
                Log.e("error", "Error getting comments", exception)
                Toast.makeText(this, "There is an error getting comments", Toast.LENGTH_SHORT)
            }
    }
    private fun createComment(comment: String) {
        val timestamp = System.currentTimeMillis()
        val sdf = SimpleDateFormat("YYYY-MM-dd HH:mm", Locale.getDefault())
        val formattedDate = sdf.format(Date(timestamp))
        val user = hashMapOf(
            "text" to comment,
            "senderId" to userId,
            "timestamp" to formattedDate,
            "topicId" to topicId
        )
        db.collection("comments")
            .add(user)
            .addOnSuccessListener { documentReference ->
                Log.d("add success", "DocumentSnapshot added with ID: ${documentReference.id}")
            }
            .addOnFailureListener { e ->
                Log.w("add success", "Error adding document", e)
            }
    }
}