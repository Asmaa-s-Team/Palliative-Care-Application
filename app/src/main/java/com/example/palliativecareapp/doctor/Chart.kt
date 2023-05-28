package com.example.palliativecareapp.doctor

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.palliativecareapp.R
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import kotlinx.android.synthetic.main.activity_chart.*
import java.util.ArrayList

class Chart : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chart)
        setBarChartValues()
    }

    private fun setBarChartValues(){
        val xvalue=ArrayList<String>()
        xvalue.add("السرطان")
        xvalue.add("السكري")
        xvalue.add("القلب والاوعية الدموية")
        xvalue.add("الجهاز التنفسي")
        // Create data entries
        val yaxis= arrayOf<Float>(13.0f,3f,30f,3f)
        val yaxis1= arrayOf<Float>(20f,39f,30f,4f)
//    val yaxis2= arrayOf<Float>(6.0f,4f,5.8f,1.4f)
        val entries = ArrayList<BarEntry>()
        val entries1 = ArrayList<BarEntry>()
//    val entries2 = ArrayList<BarEntry>()
        for (i in 0..yaxis.size-1) {
            entries.add(BarEntry(yaxis[i], i))
        }
        for (i in 0..yaxis1.size-1) {
            entries1.add(BarEntry(yaxis1[i], i))
        }
//    for (i in 0..yaxis2.size-1){
//        entries2.add(BarEntry(yaxis2[i],i))
//        }
        val barDataSet = BarDataSet(entries, "الامارات")
        val barDataSet1 = BarDataSet(entries1, "غزة")
//   val barDataSet2 = BarDataSet(entries2, "Third")
        barDataSet.color=resources.getColor(R.color.orange)
        barDataSet1.color=resources.getColor(R.color.green)
//    barDataSet2.color=resources.getColor(R.color.orange)
        val finalData=ArrayList<BarDataSet>()
        finalData.add(barDataSet)
        finalData.add(barDataSet1)
//    finalData.add(barDataSet2)
        val data = BarData(xvalue,finalData)
        chart.data = data
        chart.setBackgroundColor(resources.getColor(R.color.white))
        chart.animateXY(3000,3000)
    }}