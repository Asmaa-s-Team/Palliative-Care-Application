package com.example.palliativecareapp.patient

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.palliativecareapp.Analytics
import com.example.palliativecareapp.R
import com.example.palliativecareapp.adapters.PatientTopicAdapter
import com.example.palliativecareapp.models.Topic
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.Query
import kotlinx.android.synthetic.main.patient_home.*
import kotlin.math.log

class PatientHome : AppCompatActivity() {
    val db = FirebaseFirestore.getInstance()
    lateinit var myAdapter: PatientTopicAdapter
    var myTopics = ArrayList<Topic>()
    var analytics = Analytics()
    var listenerRegistration: ListenerRegistration? = null
    override fun onStart() {
        super.onStart()
        val query = db.collection("topics").whereEqualTo("hidden",false)
        listenerRegistration = query.addSnapshotListener{
            value,error ->
            if (error != null){
                Log.e("Listener","error")
                return@addSnapshotListener
            }
            myTopics.clear()
            if (value != null && !value.isEmpty){
                for (document in value!!.documents){
                    myTopics.add(
                        Topic(document.id,document.getString("logo").toString(),
                            document.getString("name").toString(),
                            document.getString("description").toString()),)
                    Log.e("Listener","success")
                }
            }
            else{
                Log.e("Listener","There is NO DATA ...")
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
    }

    override fun onStop() {
        super.onStop()
        listenerRegistration?.remove()
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.patient_home)

        chat.setOnClickListener {
            val intent = Intent(this@PatientHome, PatientChat::class.java)
            startActivity(intent)
        }
        notifications.setOnClickListener {
            val intent = Intent(this@PatientHome, PatientNotifications::class.java)
            startActivity(intent)
        }
        topics.setOnClickListener {
            val intent = Intent(this@PatientHome, PatientTopics::class.java)
            startActivity(intent)
        }
        profile.setOnClickListener {
            val intent = Intent(this@PatientHome, PatientProfile::class.java)
            startActivity(intent)
        }

        myAdapter = PatientTopicAdapter(myTopics, this)
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

        myAdapter.onItemClickListener(object : PatientTopicAdapter.OnItemClickListener {
            override fun onItemClick(position: Int) {
                val intent = Intent(this@PatientHome, PatientTopic::class.java)
                intent.putExtra("topicId", myTopics[position].id)
                analytics.selectContent("${myTopics[position].id}","${myTopics[position].name}","TopicCard")
                print(myTopics[position].id)
                startActivity(intent)
            }
        })
        analytics.screenTrack("PatientHome","PatientHome")
    }

    override fun onResume() {
        super.onResume()
        //start
        analytics.trackScreenView("PatientHome")
    }

    override fun onPause() {
        super.onPause()
        //end
        analytics.trackScreenDuration()
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