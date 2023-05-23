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
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.android.synthetic.main.patient_home.*

class PatientHome : AppCompatActivity() {
    val db = FirebaseFirestore.getInstance()
    lateinit var myAdapter: TopicAdapter
    var topics = ArrayList<Topic>()
    lateinit var bottomNav : BottomNavigationView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.patient_home)

        myAdapter = TopicAdapter(topics, this)
        recycler.layoutManager = LinearLayoutManager(this)
        recycler.adapter = myAdapter

        close_search.setOnClickListener {
            search_text.text = null
            clearSearch()
        }

        search_btn.setOnClickListener {
            if (search_text.text != null) {
                searchFirestore(search_text.text.toString())
            }
        }

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

    private fun searchFirestore(query: String) {
        val startValue = query
        val endValue = query + "\uf8ff"
        val collectionRef = db.collection("topics")
        val queryRef: Query = collectionRef.orderBy("name")
            .whereGreaterThanOrEqualTo("name", startValue)
            .whereLessThanOrEqualTo("name", endValue)
        queryRef.get().addOnSuccessListener { querySnapshot ->
            querySnapshot.documents.map { document ->
                document.toObject(Topic::class.java)
            }
            myAdapter.filter(query)
        }
    }
    private fun clearSearch() {
        if (myAdapter.isFiltering()) {
            myAdapter.filter("") // Pass an empty query to clear the filter
        }
    }
}