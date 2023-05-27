package com.example.palliativecareapp.patient

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.palliativecareapp.Analytics
import com.example.palliativecareapp.R
import kotlinx.android.synthetic.main.patient_notifications.*

class PatientNotifications : AppCompatActivity() {
    var analytics = Analytics()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.patient_notifications)
        home.setOnClickListener {
            val intent = Intent(this@PatientNotifications, PatientHome::class.java)
            startActivity(intent)
        }
        chat.setOnClickListener {
            val intent = Intent(this@PatientNotifications, PatientChat::class.java)
            startActivity(intent)
        }
        topics.setOnClickListener {
            val intent = Intent(this@PatientNotifications, PatientTopics::class.java)
            startActivity(intent)
        }
        profile.setOnClickListener {
            val intent = Intent(this@PatientNotifications, PatientProfile::class.java)
            startActivity(intent)
        }
        analytics.screenTrack("PatientNotifications","PatientNotifications")
    }

    override fun onResume() {
        super.onResume()
        //start
        analytics.trackScreenView("PatientNotifications")
    }

    override fun onPause() {
        super.onPause()
        //end
        analytics.trackScreenDuration()
    }
}