package com.example.palliativecareapp.patient

import android.annotation.SuppressLint
import android.content.Context
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.palliativecareapp.Analytics
import com.example.palliativecareapp.R
import com.example.palliativecareapp.adapters.GroupMessageAdapter
import com.example.palliativecareapp.models.GroupMessage
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.*
import com.google.firebase.ktx.Firebase
import java.text.SimpleDateFormat
import java.util.*

class GroupChattingScreen : AppCompatActivity() {
    private lateinit var messagesRecyclerView: RecyclerView
    private lateinit var messageEditText: EditText
    private lateinit var sendButton: Button
    private lateinit var senderUid: String
    private lateinit var senderName: String
    private var messagesRef: DatabaseReference = FirebaseDatabase.getInstance().getReference("chat/allPatients")
    private lateinit var messagesAdapter: GroupMessageAdapter
    private val messagesList = mutableListOf<GroupMessage>()
    private lateinit var auth: FirebaseAuth
    lateinit var sharedPreferences : SharedPreferences
    var analytics = Analytics()
    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
            super.onCreate(savedInstanceState)
            setContentView(R.layout.patient_group_chatting)
            sharedPreferences = this.getSharedPreferences("my_preferences", Context.MODE_PRIVATE)
            auth = Firebase.auth
            var user = auth.currentUser
            messagesRecyclerView = findViewById(R.id.RVallMessages)
            messageEditText = findViewById(R.id.messageInput2)
            sendButton = findViewById(R.id.send_btn2)

        val first = sharedPreferences.getString("first", "")
        val middle = sharedPreferences.getString("middle", "")
        val last = sharedPreferences.getString("last", "")
            senderName = "$first $middle $last"
//            senderName = " علي محمد أحمد"
            senderUid = user!!.uid

            messagesAdapter = GroupMessageAdapter(this, messagesList, senderUid)
            messagesRecyclerView.layoutManager = LinearLayoutManager(this)
            messagesRecyclerView.adapter = messagesAdapter

            sendButton.setOnClickListener {
                val messageText = messageEditText.text.toString().trim()
                Log.e("message ",messageText)
                if (messageText.isNotEmpty()) {
                    sendMessage(messageText)
                    messageEditText.setText("")
                }
            }

            messagesRef.addChildEventListener(object : ChildEventListener {
                override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
                    val message = snapshot.getValue(GroupMessage::class.java)
                    if (message != null) {
                        messagesList.add(message)
                        messagesAdapter.notifyItemInserted(messagesList.size - 1)
                        messagesRecyclerView.scrollToPosition(messagesList.size - 1)
                    }
                }
                override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {}
                override fun onChildRemoved(snapshot: DataSnapshot) {}
                override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {}
                override fun onCancelled(error: DatabaseError) {}
            })
        analytics.screenTrack("GroupChattingScreen","GroupChattingScreen")
        }

    override fun onResume() {
        super.onResume()
        //start
        analytics.trackScreenView("GroupChattingScreen")
    }

    override fun onPause() {
        super.onPause()
        //end
        analytics.trackScreenDuration()
    }

        private fun sendMessage(messageText: String) {
            val timestamp = System.currentTimeMillis()
            val sdf = SimpleDateFormat("YYYY-MM-dd HH:mm", Locale.getDefault())
            val formattedDate = sdf.format(Date(timestamp))
            val message = GroupMessage(messageText, senderUid, senderName, formattedDate)
            Log.e("message ","1")
            Log.e("message ",senderName)
            messagesRef.push().setValue(message)
            Log.e("message ","2")
        }
    }
