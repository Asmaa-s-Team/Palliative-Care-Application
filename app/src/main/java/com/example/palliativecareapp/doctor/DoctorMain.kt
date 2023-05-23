package com.example.palliativecareapp.doctor

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.fragment.app.Fragment
import com.example.palliativecareapp.R
import com.example.palliativecareapp.patient.*
import com.google.android.material.bottomnavigation.BottomNavigationView

class DoctorMain : AppCompatActivity() {
    lateinit var bottomNav : BottomNavigationView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.patient_main)

        loadFragment(HomeFragment())
        bottomNav = findViewById(R.id.bottomView) as BottomNavigationView
        bottomNav.setOnItemSelectedListener {
            when (it.itemId) {
                R.id.home -> {
                    loadFragment(HomeFragment())
                    true
                }
                R.id.chat -> {
                    loadFragment(ChatFragment())
                    true
                }
                R.id.topics -> {
                    loadFragment(TopicsFragment())
                    true
                }
                R.id.notifications -> {
                    loadFragment(NotificationFragment())
                    true
                }
                R.id.profile -> {
                    loadFragment(ProfileFragment())
                    true
                }
            }
            false
        }
    }

    private fun loadFragment(fragment: Fragment){
        val transaction = supportFragmentManager.beginTransaction()
        transaction.replace(R.id.container,fragment)
        transaction.commit()
    }
}