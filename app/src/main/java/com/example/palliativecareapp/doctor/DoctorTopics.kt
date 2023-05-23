package com.example.palliativecareapp.doctor

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.palliativecareapp.R
import kotlinx.android.synthetic.main.doctor_topics.*

class DoctorTopics : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.doctor_topics)

        add_topic_btn.setOnClickListener {
            val i = Intent(this, DoctorAddTopic::class.java)
            startActivity(i)
        }

        // on long Click Lis => delete
        //swipe => edit and hiding
    }
}