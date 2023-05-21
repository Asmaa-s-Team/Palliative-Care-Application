package com.example.palliativecareapp.adapters

import android.content.Context
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.palliativecareapp.R
import com.example.palliativecareapp.models.GroupMessage
import java.text.DateFormat
import java.util.*

class GroupMessageAdapter(
    private val context: Context,
    private val messages: List<GroupMessage>,
    private val currentUserUid: String
) : RecyclerView.Adapter<GroupMessageAdapter.ViewHolder>() {

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val messageText: TextView = itemView.findViewById(R.id.message_text)
        val messageSender: TextView = itemView.findViewById(R.id.message_sender)
        val messageTime: TextView = itemView.findViewById(R.id.message_time)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.group_message_item, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val message = messages[position]
        holder.messageText.text = message.text
        holder.messageSender.text = message.senderName
        holder.messageTime.text = message.timestamp

        val layoutParams = holder.messageText.layoutParams as
                LinearLayout.LayoutParams
        layoutParams.gravity = if (message.senderId == currentUserUid)
            Gravity.END else Gravity.START
        holder.messageText.layoutParams = layoutParams
        holder.messageSender.layoutParams = layoutParams
        holder.messageTime.layoutParams = layoutParams

    }

    override fun getItemCount(): Int {
        return messages.size
    }
}