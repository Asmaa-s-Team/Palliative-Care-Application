package com.example.palliativecareapp.patient

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.palliativecareapp.R
import android.widget.SearchView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.palliativecareapp.adapters.TopicAdapter
import com.example.palliativecareapp.models.Topic
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query

class Search : AppCompatActivity() {

    private lateinit var searchView: SearchView
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: TopicAdapter
    private lateinit var firestore: FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)

//        val topics = ArrayList<Topic>()
        val adapter = TopicAdapter(ArrayList(), this)
        searchView = findViewById(R.id.searchView)
        recyclerView = findViewById(R.id.recyclerView)

        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = adapter

        firestore = FirebaseFirestore.getInstance()

        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                return false
            }
            override fun onQueryTextChange(newText: String?): Boolean {
                if (newText != null) {
                    searchFirestore(newText)
                }
                return true
            }
        })
    }

    private fun searchFirestore(query: String) {
        val collectionRef = firestore.collection("topics")
        val queryRef: Query = collectionRef.whereEqualTo("name", query)
        queryRef.get().addOnSuccessListener { querySnapshot ->
            val results = querySnapshot.documents.map { document ->
                document.toObject(Topic::class.java)
            }
            adapter = TopicAdapter(results as ArrayList<Topic>, this)
            adapter.notifyDataSetChanged()
        }
    }
}
