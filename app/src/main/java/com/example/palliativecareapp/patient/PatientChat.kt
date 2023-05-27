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
import com.example.palliativecareapp.adapters.DoctorAdapter
import com.example.palliativecareapp.models.Doctor
import com.example.palliativecareapp.mutualScreens.ChattingScreen
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.android.synthetic.main.patient_chat.*

class PatientChat : AppCompatActivity() {
    val db = FirebaseFirestore.getInstance()
    val doctors = ArrayList<Doctor>()
    var analytics = Analytics()
    val myAdapter = DoctorAdapter(doctors, this)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.patient_chat)

        home.setOnClickListener {
            val intent = Intent(this@PatientChat, PatientHome::class.java)
            startActivity(intent)
        }
        notifications.setOnClickListener {
            val intent = Intent(this@PatientChat, PatientNotifications::class.java)
            startActivity(intent)
        }
        topics.setOnClickListener {
            val intent = Intent(this@PatientChat, PatientTopics::class.java)
            startActivity(intent)
        }
        profile.setOnClickListener {
            val intent = Intent(this@PatientChat, PatientProfile::class.java)
            startActivity(intent)
        }

        all_patients.setOnClickListener {
            val intent = Intent(this@PatientChat, GroupChattingScreen::class.java)
            startActivity(intent)
        }

        close_search.setOnClickListener {
            search_text.text = null
            clearSearch()
        }

        search_btn.setOnClickListener {
            if (search_text.text != null) {
                searchFirestore(search_text.text.toString())
            }
        }

        RV_doctors.layoutManager = LinearLayoutManager(this)
        RV_doctors.adapter = myAdapter

        db.collection("doctors")
            .get()
            .addOnSuccessListener { result ->
                for (document in result) {
                    doctors.add(
                        Doctor(
                            document.id,
                            document.getString("name").toString(),
                            document.getString("image").toString(),
                        )
                    )
                    Log.e("success", "${document.id} => ${document.data}")
                }
                myAdapter.notifyDataSetChanged()
                if (doctors.isEmpty()) {
                    progressBar.isIndeterminate = true
                    progressBar.visibility = View.VISIBLE
                } else {
                    progressBar.isIndeterminate = false
                    progressBar.visibility = View.GONE
                }
            }
            .addOnFailureListener { exception ->
                Log.e("error", "Error getting documents.", exception)
                Toast.makeText(this, "There is an error getting the data", Toast.LENGTH_SHORT)
            }

//        val storageRef = FirebaseStorage.getInstance().getReference()

        if (doctors.isEmpty()) {
            progressBar.isIndeterminate = true
            progressBar.visibility = View.VISIBLE
        } else {
            progressBar.isIndeterminate = false
            progressBar.visibility = View.GONE
        }

        myAdapter.onItemClickListener(object : DoctorAdapter.OnItemClickListener {
            override fun onItemClick(position: Int) {
                val intent = Intent(this@PatientChat, ChattingScreen::class.java)
                intent.putExtra("doctorId", doctors[position].id)
                print(doctors[position].id)
                startActivity(intent)
            }
        })
        analytics.screenTrack("PatientChat","PatientChat")
    }

    override fun onResume() {
        super.onResume()
        //start
        analytics.trackScreenView("PatientChat")
    }

    override fun onPause() {
        super.onPause()
        //end
        analytics.trackScreenDuration()
    }
    private fun searchFirestore(query: String) {
        val startValue = query
        val endValue = query + "\uf8ff"
        val collectionRef = db.collection("doctors")
        val queryRef: Query = collectionRef.orderBy("name")
            .whereGreaterThanOrEqualTo("name", startValue)
            .whereLessThanOrEqualTo("name", endValue)
        queryRef.get().addOnSuccessListener { querySnapshot ->
            querySnapshot.documents.map { document ->
                document.toObject(Doctor::class.java)
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