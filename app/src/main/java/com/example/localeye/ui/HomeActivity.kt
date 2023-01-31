package com.example.localeye.ui

import android.app.Dialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.localeye.R
import com.example.localeye.data.User
import com.example.localeye.adapter.UserAdapter
import com.example.localeye.services.FcmTokenHelper
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class HomeActivity : AppCompatActivity() {
    lateinit var auth:FirebaseAuth
    lateinit var database:FirebaseDatabase
    lateinit var userRV:RecyclerView
    lateinit var userAdapter: UserAdapter
    lateinit var logOut:ImageView
    lateinit var yesBtn:TextView
    lateinit var noBtn:TextView
    var userList=ArrayList<User>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)
        auth=FirebaseAuth.getInstance()
        database=FirebaseDatabase.getInstance()
        val databaseReference=database.reference.child("user")
        userRV=findViewById(R.id.userRV)
        logOut=findViewById(R.id.imgLogout)

        databaseReference.addValueEventListener(object:ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {

                for(ds in snapshot.children){
                    val user=ds.getValue(User::class.java)

                    userList.add(user!!)
                }
                userAdapter.notifyDataSetChanged()
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@HomeActivity,"Something went wrong",Toast.LENGTH_SHORT).show()
            }
        })

        userRV.layoutManager=LinearLayoutManager(this)
        userAdapter= UserAdapter(this@HomeActivity,userList)
        userRV.adapter=userAdapter

        logOut.setOnClickListener {
            val dialog=Dialog(this, R.style.Dialog)
            dialog.setContentView(R.layout.logout_layout)
            yesBtn=dialog.findViewById(R.id.btnYes)
            noBtn=dialog.findViewById(R.id.btnCancel)
            dialog.show()

            yesBtn.setOnClickListener{
                FirebaseAuth.getInstance().signOut()
                startActivity(Intent(this, LoginActivity::class.java))
            }
            noBtn.setOnClickListener{
                dialog.dismiss()
            }
        }

        if(auth.currentUser==null){
            val intent=Intent(this, RegisterActivity::class.java)
            startActivity(intent)
        }
    }

    override fun onStart() {
        super.onStart()
        FcmTokenHelper.subscribeToFcm(this)
    }
}