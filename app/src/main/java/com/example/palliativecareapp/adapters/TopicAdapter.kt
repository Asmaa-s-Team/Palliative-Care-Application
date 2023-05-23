package com.example.palliativecareapp.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.palliativecareapp.models.Topic
import com.google.firebase.storage.FirebaseStorage
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.item_topic.view.*


class TopicAdapter (private val list: List<Topic>, var context: Context) :
    RecyclerView.Adapter<TopicAdapter.ViewHolder>() {

    private var filteredItems: List<Topic> = list
    private lateinit var mlistener: OnItemClickListener
    private var isFiltering: Boolean = false
    private lateinit var itemLongClickListener: OnItemLongClickListener

    fun onItemLongClickListener(listener: OnItemLongClickListener) {
        itemLongClickListener = listener
    }

    fun onItemClickListener(listener: OnItemClickListener) {
        mlistener = listener
    }

    class ViewHolder(itemView: View, listener: OnItemClickListener, longListener: OnItemLongClickListener) :
        RecyclerView.ViewHolder(itemView) {
        init {
            itemView.setOnClickListener {
                listener.onItemClick(adapterPosition)
            }
            itemView.setOnLongClickListener {
                longListener.onItemLongClick(adapterPosition)
                true
            }
        }
        val name = itemView.topic_name
        val description = itemView.topic_description
        val logo = itemView.topic_logo
        val cardView = itemView.cardView
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(com.example.palliativecareapp.R.layout.item_topic, parent, false)
        return ViewHolder(itemView, mlistener, itemLongClickListener)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val current = filteredItems[position]
        var name = current.name
        var description = current.description
        holder.name.text = name
        holder.description.text = description

//        Glide.with(context).load(current.image).into(holder.image)
//        Picasso.with(context).load(current.image).into(holder.image)

        val storageRef = FirebaseStorage.getInstance().getReference()
            .child("topic_images")
            .child(current.logo)

        storageRef.downloadUrl.addOnSuccessListener { uri ->
            val imageUrl = uri.toString()
            Picasso.with(context).load(imageUrl).into(holder.logo)
        }
        val item: Topic = list[position]

        if (item.hidden) {
            holder.cardView.setCardBackgroundColor(
                ContextCompat.getColor(
                    holder.itemView.context,
                    com.example.palliativecareapp.R.color.hidden_card_color
                )
            )
        } else {
            holder.cardView.setCardBackgroundColor(
                ContextCompat.getColor(
                    holder.itemView.context,
                    com.example.palliativecareapp.R.color.white
                )
            )
        }
    }

    override fun getItemCount(): Int {
        return filteredItems.size
    }

    fun filter(query: String) {
        filteredItems = if (query.isEmpty()) {
            isFiltering = false
            list
        } else {
            isFiltering = true
            list.filter { item ->
                item.name.contains(query, ignoreCase = true)
            }
        }
        notifyDataSetChanged()
    }

    fun isFiltering(): Boolean {
        return isFiltering
    }

    interface OnItemClickListener {
        fun onItemClick(position: Int)
    }

    interface OnItemLongClickListener {
        fun onItemLongClick(position: Int)
    }
}
