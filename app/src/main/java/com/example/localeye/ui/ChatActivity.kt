package com.example.localeye.ui

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.TextView
import android.widget.Toast
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.example.localeye.data.Message
import com.example.localeye.R
import com.example.localeye.adapter.MessageAdatper
import com.example.localeye.services.PushNotificationHelper
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import de.hdodenhof.circleimageview.CircleImageView
import java.util.*

class ChatActivity : AppCompatActivity() {
    companion object{
        lateinit var senderImg:String
        lateinit var receiverImg:String
    }
    lateinit var messageAdatper: MessageAdatper
    private lateinit var receiverName:String
    lateinit var receiverUid:String
    lateinit var senderUid:String
    private lateinit var receiverProfile: CircleImageView
    lateinit var receiverNameTV: TextView
    lateinit var auth: FirebaseAuth
    lateinit var database: FirebaseDatabase

    lateinit var sendBtn: CardView
    lateinit var messageEt: TextInputEditText
    lateinit var senderRoom:String
    lateinit var receiverRoom:String
    lateinit var messageRV:RecyclerView
    var messageList=ArrayList<Message>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat)
        auth=FirebaseAuth.getInstance()
        receiverUid= intent.getStringExtra("uid")!!
        receiverImg = intent.getStringExtra("image")!!
        receiverName= intent.getStringExtra("name")!!

        senderUid=auth.uid!!
        senderRoom=senderUid+receiverUid
        receiverRoom=receiverUid+senderUid
        database=FirebaseDatabase.getInstance()



        receiverProfile=findViewById(R.id.profile_image)
        receiverNameTV=findViewById(R.id.recName)
        sendBtn=findViewById(R.id.sendBtn)
        messageEt=findViewById(R.id.message)
        messageRV=findViewById(R.id.messageRV)

        messageAdatper= MessageAdatper(this,messageList);
        val layout=LinearLayoutManager(this)
        layout.stackFromEnd=true
        messageRV.layoutManager= layout
        messageRV.adapter=messageAdatper


        receiverProfile.load(receiverImg)
        receiverNameTV.text=receiverName


        val reference=database.reference.child("user").child(auth.uid!!)
        val chatReference=database.reference.child("chats").child(senderRoom).child("messages")

        chatReference.addValueEventListener(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                messageList.clear()
                for(ds in snapshot.children){
                    val messages=ds.getValue(Message::class.java)
                    messageList.add(messages!!)
                }
                messageAdatper.notifyDataSetChanged()
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

        })

        reference.addValueEventListener(object: ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                senderImg =snapshot.child("imageUri").value.toString()

            }

            override fun onCancelled(error: DatabaseError) {
            }
        })

        sendBtn.setOnClickListener{
            val message=messageEt.text.toString()
            if(message.isEmpty()){
                Toast.makeText(this,"Please enter message...",Toast.LENGTH_SHORT).show()
                return@setOnClickListener;
            }
            messageEt.setText("")
            val date= Date()
            Log.d("SenderID",senderRoom)
            Log.d("ReceiverID",receiverRoom)
            val messages= Message(message,senderUid,date.time.toString())
            database.reference.child("chats").child(senderRoom).child("messages")

                .push().setValue(messages).addOnCompleteListener {
                    database.reference.child("chats").child(receiverRoom).child("messages")
                        .push().setValue(messages).addOnCompleteListener {

                        }
                }

            PushNotificationHelper.sendPushNotification(receiverUid, senderUid, "User", message)
        }

    }
}