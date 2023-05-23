package com.example.palliativecareapp.doctor

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.palliativecareapp.R
import com.example.palliativecareapp.adapters.CommentAdapter
import com.example.palliativecareapp.models.Comment
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.doctor_topic.*

class DoctorTopic : AppCompatActivity() {
    // ToDo: المرفقات
    private lateinit var topicId: String
    lateinit var name : String
    lateinit var description : String
    lateinit var information : String
    lateinit var logo : String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.doctor_topic)

         topicId = intent.getStringExtra("topicId")!!

        var db = FirebaseFirestore.getInstance()
        db.collection("topics").document(topicId!!).get()
            .addOnSuccessListener { result->
                if (result != null ) {
                     name = result.getString("name").toString()
                     description = result.getString("description").toString()
                     information = result.getString("information").toString()
                     logo = result.getString("logo").toString()
                    topic_name.text = name
                    topic_information.text = information
                    topic_description.text = description
                    val storageRef = FirebaseStorage.getInstance().getReference()
                        .child("topic_images")
                        .child(logo)
                    storageRef.downloadUrl.addOnSuccessListener { uri ->
                        val imageUrl = uri.toString()
                        Picasso.with(this).load(imageUrl).into(topic_logo)
                    }
                }
                Log.e("success", "${result.id} => ${result.data}")
            }
            .addOnFailureListener { exception ->
                Log.e("error", "Error getting topic", exception)
                Toast.makeText(this, "There is an error getting topic", Toast.LENGTH_SHORT)
            }

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
                    if(comments.isNotEmpty()){
                        comments_title.setVisibility(View.VISIBLE)
                        myAdapter = CommentAdapter(this, comments)
                        lvComments.layoutManager = LinearLayoutManager(this)
                        lvComments.adapter = myAdapter
                        myAdapter.notifyDataSetChanged()
                    }else{
                        comments_title.setVisibility(View.INVISIBLE)
                    }
                }
            }
            .addOnFailureListener { exception ->
                Log.e("error", "Error getting comments", exception)
                Toast.makeText(this, "There is an error getting comments", Toast.LENGTH_SHORT)
            }

    }
}