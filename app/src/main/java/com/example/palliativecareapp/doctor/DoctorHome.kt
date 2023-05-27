package com.example.palliativecareapp.doctor

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.palliativecareapp.Analytics
import com.example.palliativecareapp.R
import com.example.palliativecareapp.adapters.DoctorTopicAdapter
import com.example.palliativecareapp.adapters.PatientTopicAdapter
import com.example.palliativecareapp.models.Topic
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import kotlinx.android.synthetic.main.doctor_home.*
import kotlinx.android.synthetic.main.doctor_home.chat
import kotlinx.android.synthetic.main.doctor_home.notifications
import kotlinx.android.synthetic.main.doctor_home.profile
import kotlinx.android.synthetic.main.doctor_home.progressBar
import kotlinx.android.synthetic.main.doctor_home.topics

class DoctorHome : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth
    lateinit var sharedPreferences : SharedPreferences
    var name = ""
    lateinit var db : FirebaseFirestore
    val myTopics = ArrayList<Topic>()
    var myAdapter = DoctorTopicAdapter(myTopics, this)
    var analytics = Analytics()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.doctor_home)

        chat.setOnClickListener {
            val intent = Intent(this@DoctorHome, DoctorChat::class.java)
            startActivity(intent)
        }
        notifications.setOnClickListener {
            val intent = Intent(this@DoctorHome, DoctorNotifications::class.java)
            startActivity(intent)
        }
        topics.setOnClickListener {
            val intent = Intent(this@DoctorHome, DoctorTopics::class.java)
            startActivity(intent)
        }
        profile.setOnClickListener {
            val intent = Intent(this@DoctorHome, DoctorProfile::class.java)
            startActivity(intent)
        }
        comment_analytics.setOnClickListener {

        }
        topics_analytics.setOnClickListener {

        }
        subscription_analytics.setOnClickListener {

        }
        attachment_analytics.setOnClickListener {

        }
        sharedPreferences = this.getSharedPreferences("my_preferences", Context.MODE_PRIVATE)
        auth = Firebase.auth
        var user = auth.currentUser
        val id = user!!.uid

        db = Firebase.firestore
        db.collection("doctors").document(id!!).get().addOnSuccessListener { result ->
            if (result != null ) {
                var data = result.data!!
                name = data.get("name") as String
                doctor_name.text = "د. $name"
            }
        }.addOnFailureListener {
            Log.e("user info", "Fail")
        }

        val editor = sharedPreferences.edit()
        editor.putString("name", name)
        editor.apply()


        myAdapter = DoctorTopicAdapter(myTopics, this)
        RV_topics.layoutManager = LinearLayoutManager(this)
        RV_topics.adapter = myAdapter

        var doctorId = "b7iSVSfKFCQlSdT1hpGyf5G3oIm2"
        val db = FirebaseFirestore.getInstance()
        val query = db.collection("topics")
            .whereEqualTo("autherId", id)
        query.get()
            .addOnSuccessListener { querySnapshot ->
                for (document in querySnapshot.documents) {
                    myTopics.add(
                        Topic(
                            document.id,
                            document.getString("logo").toString(),
                            document.getString("name").toString(),
                            document.getString("description").toString(),
                        )
                    )
                    Log.e("success", "${document.id} => ${document.data}")
                }
                myAdapter.notifyDataSetChanged()
                if (myTopics.isEmpty()) {
                    progressBar.isIndeterminate = true
                    progressBar.visibility = View.VISIBLE
                } else {
                    progressBar.isIndeterminate = false
                    progressBar.visibility = View.GONE
                }
            }
            .addOnFailureListener { exception ->
                Log.e("error", "Error getting topics", exception)
                Toast.makeText(this, "There is an error getting topics", Toast.LENGTH_SHORT)
            }
        if (myTopics.isEmpty()) {
            progressBar.isIndeterminate = true
            progressBar.visibility = View.VISIBLE
        } else {
            progressBar.isIndeterminate = false
            progressBar.visibility = View.GONE
        }

        myAdapter.onItemClickListener(object : DoctorTopicAdapter.OnItemClickListener {
            override fun onItemClick(position: Int) {
                val intent = Intent(this@DoctorHome, DoctorTopic::class.java)
                intent.putExtra("topicId", myTopics[position].id)
                print(myTopics[position].id)
                startActivity(intent)
            }
        })
        myAdapter.onItemLongClickListener(object : DoctorTopicAdapter.OnItemLongClickListener {
            override fun onItemLongClick(position: Int) {
                val topic = myTopics[position]
                val dialogBuilder = AlertDialog.Builder(this@DoctorHome)
                    .setTitle("Options")

                if (topic.hidden) {
                    dialogBuilder.setItems(arrayOf("Edit", "Delete", "Unhide")) { _, which ->
                        when (which) {
                            0 -> editTopic(topic)
                            1 -> deleteTopic(topic)
                            3 -> unhideTopic(topic)
                        }
                    }
                } else {
                    dialogBuilder.setItems(arrayOf("Edit", "Delete", "Hide")) { _, which ->
                        when (which) {
                            0 -> editTopic(topic)
                            1 -> deleteTopic(topic)
                            2 -> hideTopic(topic)
                        }
                    }
                }

                val dialog = dialogBuilder.create()
                dialog.show()
            }
            })
        analytics.screenTrack("DoctorHome","DoctorHome")
    }
    fun deleteTopicImage(logo: String){
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
    private fun deleteTopic(topic: Topic) {
        deleteTopicImage(topic.logo)
        val topicId = topic.id
        db.collection("topics").document(topicId).delete()
            .addOnSuccessListener {
                Log.e("delete topic", "deleted successfully")
                for (i in myTopics) {
                    if (i.id == topicId) {
                        myTopics.remove(i)
                        break
                    }
                }
                myAdapter.notifyDataSetChanged()
            }
            .addOnFailureListener { e ->
                Log.e("delete topic", "delete failed")
            }
    }

    private fun editTopic(topic: Topic){
       val intent = Intent(this@DoctorHome, DoctorEditTopic::class.java)
        intent.putExtra("topicId", topic.id)
        startActivity(intent)
    }
    private fun hideTopic(topic: Topic) {
        val topicsCollection = db.collection("topics")
        val topicId = topic.id
        val updateData = hashMapOf(
            "hidden" to true
        )
        topicsCollection.document(topicId)
            .update(updateData as Map<String, Any>)
            .addOnSuccessListener {
                Log.e("hide topic", "hided successfully")
                for (i in myTopics) {
                    if (i.id == topicId) {
                        i.hidden = true
                        break
                    }
                }
                myAdapter.notifyDataSetChanged()
            }
            .addOnFailureListener { e ->
                Log.e("hide topic", "hide failed")
                // Handle the error and display an error message if necessary
            }
    }
    private fun unhideTopic(topic: Topic) {
        val topicsCollection = db.collection("topics")
        val topicId = topic.id
        val updateData = hashMapOf(
            "hidden" to false
        )
        topicsCollection.document(topicId)
            .update(updateData as Map<String, Any>)
            .addOnSuccessListener {
                Log.e("unhide topic", "unhided successfully")
                for (i in myTopics) {
                    if (i.id == topicId) {
                        i.hidden = false
                        break
                    }
                }
                myAdapter.notifyDataSetChanged()
            }
            .addOnFailureListener { e ->
                Log.e("unhide topic", "unhide failed")
                // Handle the error and display an error message if necessary
            }
    }

}