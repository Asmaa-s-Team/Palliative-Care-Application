package com.example.palliativecareapp.doctor

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
import com.example.palliativecareapp.adapters.PatientAdapter
import com.example.palliativecareapp.models.Doctor
import com.example.palliativecareapp.models.Patient
import com.example.palliativecareapp.models.Topic
import com.example.palliativecareapp.mutualScreens.ChattingScreen
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.android.synthetic.main.doctor_chat.*
import kotlinx.android.synthetic.main.doctor_chat.close_search
import kotlinx.android.synthetic.main.doctor_chat.home
import kotlinx.android.synthetic.main.doctor_chat.notifications
import kotlinx.android.synthetic.main.doctor_chat.profile
import kotlinx.android.synthetic.main.doctor_chat.progressBar
import kotlinx.android.synthetic.main.doctor_chat.search_btn
import kotlinx.android.synthetic.main.doctor_chat.search_text
import kotlinx.android.synthetic.main.doctor_chat.topics
import kotlinx.android.synthetic.main.patient_home.*

class DoctorChat : AppCompatActivity() {
    val db = FirebaseFirestore.getInstance()
    val patients = ArrayList<Patient>()
    val myAdapter = PatientAdapter(patients, this)
    var analytics = Analytics()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.doctor_chat)

        home.setOnClickListener {
            val intent = Intent(this@DoctorChat, DoctorHome::class.java)
            startActivity(intent)
        }
        notifications.setOnClickListener {
            val intent = Intent(this@DoctorChat, DoctorNotifications::class.java)
            startActivity(intent)
        }
        topics.setOnClickListener {
            val intent = Intent(this@DoctorChat, DoctorTopics::class.java)
            startActivity(intent)
        }
        profile.setOnClickListener {
            val intent = Intent(this@DoctorChat, DoctorProfile::class.java)
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

        RV_patients.layoutManager = LinearLayoutManager(this)
        RV_patients.adapter = myAdapter

        db.collection("patients")
            .get()
            .addOnSuccessListener { result ->
                for (document in result) {
                    patients.add(
                        Patient(
                            document.id,
                            document.getString("name").toString(),
                            document.getString("image").toString(),
                        )
                    )
                    Log.e("success", "${document.id} => ${document.data}")
                }
                myAdapter.notifyDataSetChanged()
                if (patients.isEmpty()) {
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

        if (patients.isEmpty()) {
            progressBar.isIndeterminate = true
            progressBar.visibility = View.VISIBLE
        } else {
            progressBar.isIndeterminate = false
            progressBar.visibility = View.GONE
        }

        myAdapter.onItemClickListener(object : PatientAdapter.OnItemClickListener {
            override fun onItemClick(position: Int) {
                val intent = Intent(this@DoctorChat, ChattingScreen::class.java)
                intent.putExtra("userId", patients[position].id)
                print(patients[position].id)
                startActivity(intent)
            }
        })
        analytics.screenTrack("DoctorChat","DoctorChat")
    }
    private fun searchFirestore(query: String) {
        val startValue = query
        val endValue = query + "\uf8ff"
        val collectionRef = db.collection("patients")
        val queryRef: Query = collectionRef.orderBy("name")
            .whereGreaterThanOrEqualTo("name", startValue)
            .whereLessThanOrEqualTo("name", endValue)
        queryRef.get().addOnSuccessListener { querySnapshot ->
            querySnapshot.documents.map { document ->
                document.toObject(Patient::class.java)
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