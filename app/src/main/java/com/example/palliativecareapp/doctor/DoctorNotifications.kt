package com.example.palliativecareapp.doctor

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.palliativecareapp.Analytics
import com.example.palliativecareapp.R
import kotlinx.android.synthetic.main.doctor_chat.*

class DoctorNotifications : AppCompatActivity() {
    var analytics = Analytics()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.doctor_notifications)

        home.setOnClickListener {
            val intent = Intent(this@DoctorNotifications, DoctorHome::class.java)
            startActivity(intent)
        }
        chat.setOnClickListener {
            val intent = Intent(this@DoctorNotifications, DoctorChat::class.java)
            startActivity(intent)
        }
        topics.setOnClickListener {
            val intent = Intent(this@DoctorNotifications, DoctorTopics::class.java)
            startActivity(intent)
        }
        profile.setOnClickListener {
            val intent = Intent(this@DoctorNotifications, DoctorProfile::class.java)
            startActivity(intent)
        }
        analytics.screenTrack("DoctorNotifications","DoctorNotifications")
    }
}