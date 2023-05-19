package com.example.palliativecareapp.adapters

import android.content.Context
import android.graphics.BitmapFactory
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.module.AppGlideModule
import com.example.palliativecareapp.R
import com.example.palliativecareapp.models.Doctor
import com.example.palliativecareapp.models.Topic
import com.google.firebase.storage.FirebaseStorage
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.chat_search_item.view.*
import kotlinx.android.synthetic.main.topic_item.view.*

class TopicAdapter (private val list: ArrayList<Topic>, var context: Context) :
    RecyclerView.Adapter<TopicAdapter.ViewHolder>() {

    private lateinit var mlistener: OnItemClickListener

    fun onItemClickListener(listener: OnItemClickListener) {
        mlistener = listener
    }

    class ViewHolder(itemView: View, listener: OnItemClickListener) :
        RecyclerView.ViewHolder(itemView) {
        init {
            itemView.setOnClickListener {
                listener.onItemClick(adapterPosition)
            }
        }
        val name = itemView.topic_name
        val description = itemView.topic_description
        val logo = itemView.topic_logo
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.topic_item, parent, false)
        return ViewHolder(itemView, mlistener)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val current = list[position]
        var name = current.name
        var description = current.description
        holder.name.text = name
        holder.description.text = description

//        Glide.with(context).load(current.image).into(holder.image)
//        Picasso.with(context).load(current.image).into(holder.image)

        val storageImage = FirebaseStorage.getInstance().reference
        val storageRef = storageImage.child(current.logo.toString())
        storageRef.downloadUrl.addOnSuccessListener { uri ->
            val imageUrl = uri.toString()
            Picasso.with(context).load(imageUrl).into(holder.logo)
        }
    }

    override fun getItemCount(): Int {
        return list.size
    }

    interface OnItemClickListener {
        fun onItemClick(position: Int)
    }
}
