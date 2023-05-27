package com.example.palliativecareapp.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.palliativecareapp.Analytics
import com.example.palliativecareapp.R
import com.example.palliativecareapp.models.Analytic

class AnalyticsAdapter(
    private val context: Context,
    private val analytics: ArrayList<Analytic>,
) : RecyclerView.Adapter<AnalyticsAdapter.ViewHolder>() {
    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.card_analytics, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val analytic = analytics[position]
    }

    override fun getItemCount(): Int {
        return analytics.size
    }

}