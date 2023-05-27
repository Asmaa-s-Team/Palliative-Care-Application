package com.example.palliativecareapp.patient

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.palliativecareapp.Analytics
import com.example.palliativecareapp.R
import kotlinx.android.synthetic.main.patient_topics.*

class PatientTopics : AppCompatActivity() {
    var analytics = Analytics()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.patient_topics)

        home.setOnClickListener {
            val intent = Intent(this@PatientTopics, PatientHome::class.java)
            startActivity(intent)
        }
        chat.setOnClickListener {
            val intent = Intent(this@PatientTopics, PatientChat::class.java)
            startActivity(intent)
        }
        notifications.setOnClickListener {
            val intent = Intent(this@PatientTopics, PatientNotifications::class.java)
            startActivity(intent)
        }
        profile.setOnClickListener {
            val intent = Intent(this@PatientTopics, PatientProfile::class.java)
            startActivity(intent)
        }
        analytics.screenTrack("PatientTopics","PatientTopics")
    }
}