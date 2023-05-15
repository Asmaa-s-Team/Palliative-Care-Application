package com.example.palliativecareapp.mutualScreens.adapters

import android.content.Context
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.palliativecareapp.R
import com.example.palliativecareapp.mutualScreens.models.Message
import java.text.DateFormat
import java.util.*

class MessageAdapter(
    private val context: Context,
    private val messages: List<Message>,
    private val currentUserUid: String
) : RecyclerView.Adapter<MessageAdapter.MessageViewHolder>() {

    class MessageViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val messageText: TextView = itemView.findViewById(R.id.message_text)
        val messageTime: TextView = itemView.findViewById(R.id.message_time)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MessageViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.message_item, parent, false)
        return MessageViewHolder(view)
    }

    override fun onBindViewHolder(holder: MessageViewHolder, position: Int) {
        val message = messages[position]
        holder.messageText.text = message.text
        holder.messageTime.text = message.timestamp.dateToString()

        val layoutParams = holder.messageText.layoutParams as
                LinearLayout.LayoutParams
        layoutParams.gravity = if (message.senderId == currentUserUid)
            Gravity.END else Gravity.START
        holder.messageText.layoutParams = layoutParams
        holder.messageTime.layoutParams = layoutParams

    }

    override fun getItemCount(): Int {
        return messages.size
    }

    fun Long.dateToString(): String {
        val dateFormat = DateFormat.getDateTimeInstance()
        return dateFormat.format(Date(this))
    }
}