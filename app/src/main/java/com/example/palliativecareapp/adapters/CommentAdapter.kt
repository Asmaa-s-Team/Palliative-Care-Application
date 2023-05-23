package com.example.palliativecareapp.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.palliativecareapp.R
import com.example.palliativecareapp.models.Comment
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.item_comment.view.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap

class CommentAdapter(
    private val context: Context,
    private val comments: ArrayList<Comment>,
) : RecyclerView.Adapter<CommentAdapter.ViewHolder>() {

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val commentText = itemView.comment_text
        val commentTime = itemView.comment_time
        val senderName = itemView.comment_name
        val senderImage = itemView.comment_image
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.item_comment, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val comment = comments[position]
        holder.commentText.text = comment.text
        holder.commentTime.text = comment.timestamp

        val storageImage = FirebaseStorage.getInstance().reference
        var db = FirebaseFirestore.getInstance()
        db.collection("patients").document(comment.senderId!!).get()
            .addOnSuccessListener { result ->
                if (result != null) {
                    var name = result.getString("name")
                    var senderImage = result.getString("image").toString()
                    holder.senderName.text = name
                    val storageRef = storageImage.child(senderImage.toString())
                    storageRef.downloadUrl.addOnSuccessListener { uri ->
                        val imageUrl = uri.toString()
                        Picasso.with(context).load(imageUrl).into(holder.senderImage)
                    }
                }
            }

    }

    override fun getItemCount(): Int {
        return comments.size
    }
}