package com.example.palliativecareapp.doctor

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
import kotlinx.android.synthetic.main.doctor_chat.*

class DoctorChat : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.doctor_chat)

        val doctors = ArrayList<Doctor>()
        val myAdapter = DoctorAdapter(doctors, this)
        RV_patients.layoutManager = LinearLayoutManager(this)
        RV_patients.adapter = myAdapter

        val db = FirebaseFirestore.getInstance()
        db.collection("patients")
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
                val intent = Intent(this@DoctorChat, ChattingScreen::class.java)
                intent.putExtra("userId", doctors[position].id)
                print(doctors[position].id)
                startActivity(intent)
            }
        })

    }
}