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
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import kotlinx.android.synthetic.main.activity_chatting_screen.*
import com.google.firebase.database.*
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_doctor_home.*
import kotlinx.coroutines.tasks.await
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.HashMap

class ChattingScreen : AppCompatActivity() {
    private lateinit var senderUid: String
    private lateinit var receiverUid: String
    private lateinit var messagesRef : DatabaseReference
    private lateinit var messagesAdapter: MessageAdapter
    private val messagesList = mutableListOf<Message>()
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chatting_screen)

        auth = Firebase.auth
        var user = auth.currentUser
        val userId = intent.getStringExtra("userId")


        val db = FirebaseFirestore.getInstance()
        val doctorDocumentRef = db.collection("doctors").document(userId!!)
        val patientDocumentRef = db.collection("patients").document(userId!!)
        doctorDocumentRef.get().addOnSuccessListener { result->
            if (result.exists() ) {
                val hash = result.data!!.get("name") as HashMap<Any, Any>
                var first = hash.get("first").toString()
                var middle = hash.get("middle").toString()
                var last = hash.get("last").toString()
                chatName.text = "د. $first $middle $last"
                val image = result.getString("image")
                val storageImage = FirebaseStorage.getInstance().reference
                val storageRef = storageImage.child(image!!)
                storageRef.downloadUrl.addOnSuccessListener { uri ->
                    val imageUrl = uri.toString()
                    Picasso.with(this).load(imageUrl).into(chatImageView)
                }
            }
        }
        patientDocumentRef.get().addOnSuccessListener { result ->
            if (result.exists() ) {
                val hash = result.data!!.get("name") as HashMap<Any, Any>
                var first = hash.get("first").toString()
                var middle = hash.get("middle").toString()
                var last = hash.get("last").toString()
                chatName.text = "$first $middle $last"
                val image = result.getString("image")
                val storageImage = FirebaseStorage.getInstance().reference
                val storageRef = storageImage.child(image!!)
                storageRef.downloadUrl.addOnSuccessListener { uri ->
                    val imageUrl = uri.toString()
                    Picasso.with(this).load(imageUrl).into(chatImageView)
                }
            }
        }

        receiverUid = userId.toString()
        senderUid = user!!.uid

        var chatId = generateChatId(senderUid, receiverUid)
        messagesRef = FirebaseDatabase.getInstance().getReference("chat/$chatId")

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

    fun generateChatId(userId1: String, userId2: String): String {
        val sortedIds = listOf(userId1, userId2).sorted()
        return sortedIds.joinToString("-")
    }

    private fun sendMessage(messageText: String) {
        val timestamp = System.currentTimeMillis()
        val sdf = SimpleDateFormat("YYYY-MM-dd HH:mm", Locale.getDefault())
        val formattedDate = sdf.format(Date(timestamp))
        val message = Message(messageText, senderUid, receiverUid, formattedDate)
        Log.e("message ","1")
        messagesRef.push().setValue(message)
        Log.e("message ","2")
    }

}
