package com.example.palliativecareapp

import android.os.Bundle
import android.util.Log
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.analytics.ktx.analytics
import com.google.firebase.analytics.ktx.logEvent
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase

class Analytics {
    private var analytics : FirebaseAnalytics

    init {
        analytics = Firebase.analytics
    }

    fun selectContent(contentId: String, contentName: String, contentType: String) {
        analytics.logEvent(FirebaseAnalytics.Event.SELECT_CONTENT) {
            param(FirebaseAnalytics.Param.ITEM_ID, contentId);
            param(FirebaseAnalytics.Param.ITEM_NAME, contentName);
            param(FirebaseAnalytics.Param.CONTENT_TYPE, contentType);
            Log.e("MyTag", "You are in select content function")
        }
    }

    fun screenTrack(screenClass: String, screenName: String) {
        analytics.logEvent(FirebaseAnalytics.Event.SCREEN_VIEW) {
            param(FirebaseAnalytics.Param.SCREEN_CLASS, screenClass);
            param(FirebaseAnalytics.Param.SCREEN_NAME, screenName);
            Log.e("MyTag", "You are in screen track function")
        }
    }

    private var currentScreenName: String? = null
    private var currentScreenStartTime: Long? = null

    fun trackScreenView(screenName: String) {
        Log.e("tag", "you are in track screen view")
        currentScreenName = screenName
        currentScreenStartTime = System.currentTimeMillis()
        val params = Bundle().apply {
            putString(FirebaseAnalytics.Param.SCREEN_NAME, screenName)
            putString(FirebaseAnalytics.Param.SCREEN_CLASS, screenName)
        }
        analytics.logEvent(FirebaseAnalytics.Event.SCREEN_VIEW, params)
    }

    fun trackScreenDuration() {
        Log.e("tag", "you are in track screen duration")
        if (currentScreenName != null && currentScreenStartTime != null) {
            val duration = System.currentTimeMillis() - currentScreenStartTime!!
            saveScreenDurationToFirestore(currentScreenName!!, duration)
            currentScreenName = null
            currentScreenStartTime = null
        }
    }

    private fun saveScreenDurationToFirestore(screenName: String, duration: Long) {
        val durationInSeconds = duration / 1000
        val firestore = FirebaseFirestore.getInstance()
        val docRef = firestore.collection("time_spent").document(screenName)
        docRef.get().addOnSuccessListener { documentSnapshot ->
            if (documentSnapshot.exists()) {
                val existingDuration = documentSnapshot.getLong("duration") ?: 0
                docRef.update("duration", existingDuration + durationInSeconds)
            } else {
                docRef.set(mapOf("screenName" to screenName, "duration" to durationInSeconds))
            }
        }
    }
}