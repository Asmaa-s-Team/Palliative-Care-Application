package com.example.palliativecareapp.patient

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.palliativecareapp.R
import com.example.palliativecareapp.adapters.DoctorAdapter
import com.example.palliativecareapp.models.Doctor
import com.example.palliativecareapp.mutualScreens.ChattingScreen
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.patient_chat.*

class PatientChat : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.patient_chat)

        all_patients.setOnClickListener {
            val intent = Intent(this@PatientChat, GroupChattingScreen::class.java)
            startActivity(intent)
        }

        val doctors = ArrayList<Doctor>()
        val myAdapter = DoctorAdapter(doctors, this)
        RV_doctors.layoutManager = LinearLayoutManager(this)
        RV_doctors.adapter = myAdapter

        val db = FirebaseFirestore.getInstance()
        db.collection("doctors")
            .get()
            .addOnSuccessListener { result ->
                for (document in result) {
                    doctors.add(
                        Doctor(
                            document.id,
                            document.get("name") as HashMap<Any, Any>,
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

    }
}