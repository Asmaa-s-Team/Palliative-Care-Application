package com.example.palliativecareapp.patient

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.palliativecareapp.R
import com.example.palliativecareapp.adapters.TopicAdapter
import com.example.palliativecareapp.models.Topic
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.activity_patient_home.*

class PatientHome : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_patient_home)

        val topics = ArrayList<Topic>()
        val myAdapter = TopicAdapter(topics, this)
        recycler.layoutManager = LinearLayoutManager(this)
        recycler.adapter = myAdapter

        val db = FirebaseFirestore.getInstance()
        db.collection("topics").get()
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
                val intent = Intent(this@PatientHome, PatientTopic::class.java)
                intent.putExtra("topicId", topics[position].id)
                print(topics[position].id)
                startActivity(intent)
            }
        })
    }
}