package com.example.palliativecareapp.mutualScreens

import android.content.Context
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.palliativecareapp.R
import com.example.palliativecareapp.mutualScreens.adapters.MessageAdapter
import com.example.palliativecareapp.mutualScreens.models.Message
import kotlinx.android.synthetic.main.activity_chatting_screen.*
import com.google.firebase.database.*
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.squareup.picasso.Picasso

class ChattingScreen : AppCompatActivity() {
    lateinit var sharedPreferences : SharedPreferences
    private lateinit var senderUid: String
    private lateinit var receiverUid: String
    private var messagesRef: DatabaseReference = FirebaseDatabase.getInstance().getReference("chat")
    private lateinit var messagesAdapter: MessageAdapter
    private val messagesList = mutableListOf<Message>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chatting_screen)

        val doctorId = intent.getStringExtra("doctorId")

        val db = FirebaseFirestore.getInstance()
        val documentRef = db.collection("doctors").document(doctorId!!)

        documentRef.get().addOnSuccessListener { result->

            if (result != null ) {
                val image = result.getString("image")
                val storageImage = FirebaseStorage.getInstance().reference
                val storageRef = storageImage.child(image!!)
                storageRef.downloadUrl.addOnSuccessListener { uri ->
                    val imageUrl = uri.toString()
                    Picasso.with(this).load(imageUrl).into(chatImageView)
                }
            }
        }

        sharedPreferences = getSharedPreferences("myPrefs", Context.MODE_PRIVATE)
        val userId = sharedPreferences.getString("userId", "error")
        val doctorName = intent.getStringExtra("doctorName")
        chatName.text = doctorName
        receiverUid = doctorId.toString()
        senderUid = userId.toString()

        messagesAdapter = MessageAdapter(this, messagesList, senderUid)
        RVMessages.layoutManager = LinearLayoutManager(this)
        RVMessages.adapter = messagesAdapter

        send_btn.setOnClickListener {
            val messageText = messageInput.text.toString().trim()
            Log.e("message ",messageText)
            if (messageText.isNotEmpty()) {
                sendMessage(messageText)
                messageInput.setText("")
            }
        }

        messagesRef.addChildEventListener(object : ChildEventListener {
            override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
                val message = snapshot.getValue(Message::class.java)
                if (message != null) {
                    messagesList.add(message)
                    messagesAdapter.notifyItemInserted(messagesList.size - 1)
                    RVMessages.scrollToPosition(messagesList.size - 1)
                }
            }
            override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {}
            override fun onChildRemoved(snapshot: DataSnapshot) {}
            override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {}
            override fun onCancelled(error: DatabaseError) {}
        })

    }

    private fun sendMessage(messageText: String) {
        val timestamp = System.currentTimeMillis()
        val message = Message(messageText, senderUid, receiverUid, timestamp)
        Log.e("message ","1")
        messagesRef.push().setValue(message)
        Log.e("message ","2")
    }
}
