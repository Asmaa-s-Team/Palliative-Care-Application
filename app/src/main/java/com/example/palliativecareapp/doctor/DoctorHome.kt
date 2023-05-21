package com.example.palliativecareapp.doctor

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.palliativecareapp.R
import com.example.palliativecareapp.adapters.TopicAdapter
import com.example.palliativecareapp.models.Topic
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.activity_doctor_home.*

class DoctorHome : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth
    lateinit var sharedPreferences : SharedPreferences
    var first = ""
    var middle = ""
    var last =""

    lateinit var db : FirebaseFirestore
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_doctor_home)
        sharedPreferences = this.getSharedPreferences("my_preferences", Context.MODE_PRIVATE)
        auth = Firebase.auth
        var user = auth.currentUser
        val id = user!!.uid

        db = Firebase.firestore
        db.collection("doctors").document(id!!).get().addOnSuccessListener { result ->
            if (result != null ) {
                var data = result.data!!
                var hash = data.get("name") as? HashMap<String, Any>
                first = hash?.get("first").toString()
                middle = hash?.get("middle").toString()
                last = hash?.get("last").toString()
                Log.e("first ", first)
                Log.e("last ", last)
                Log.e("middle ", middle)
                doctor_name.text = "د. $first $middle $last"
            }
        }.addOnFailureListener {
            Log.e("user info", "Fail")
        }

        val editor = sharedPreferences.edit()
        editor.putString("first", first)
        editor.putString("middle", middle)
        editor.putString("last", last)
        editor.apply()

        add_topic.setOnClickListener {
            val i = Intent(this, DoctorAddTopic::class.java)
            startActivity(i)
        }

        val topics = ArrayList<Topic>()
        val myAdapter = TopicAdapter(topics, this)
        lvTopics.layoutManager = LinearLayoutManager(this)
        lvTopics.adapter = myAdapter

        var doctorId = "b7iSVSfKFCQlSdT1hpGyf5G3oIm2"
        val db = FirebaseFirestore.getInstance()
        val query = db.collection("topics")
            .whereEqualTo("autherId", doctorId)
        query.get()
            .addOnSuccessListener { querySnapshot ->
                for (document in querySnapshot.documents) {
                    topics.add(
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
                if (topics.isEmpty()) {
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
        if (topics.isEmpty()) {
            progressBar.isIndeterminate = true
            progressBar.visibility = View.VISIBLE
        } else {
            progressBar.isIndeterminate = false
            progressBar.visibility = View.GONE
        }

        myAdapter.onItemClickListener(object : TopicAdapter.OnItemClickListener {
            override fun onItemClick(position: Int) {
                val intent = Intent(this@DoctorHome, DoctorTopic::class.java)
                intent.putExtra("topicId", topics[position].id)
                print(topics[position].id)
                startActivity(intent)
            }
        })
    }
}