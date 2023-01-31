package com.example.localeye.adapter

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.example.localeye.R
import com.example.localeye.ui.ChatActivity
import com.example.localeye.data.User
import com.google.firebase.auth.FirebaseAuth
import de.hdodenhof.circleimageview.CircleImageView

class UserAdapter(val context: Context, val userList:List<User>): RecyclerView.Adapter<UserAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_user_row, parent, false)

        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return userList.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val user=userList[position]
        if(FirebaseAuth.getInstance().currentUser?.uid == user.uid){
            holder.itemView.visibility=View.GONE
        }
        holder.userImage.load(user.imageUri)
        holder.userName.text=user.name
        holder.status.text=user.status
        holder.itemView.setOnClickListener{
            val intent=Intent(context, ChatActivity::class.java)
            intent.putExtra("name",user.name)
            intent.putExtra("image",user.imageUri)
            intent.putExtra("uid",user.uid)
            context.startActivity(intent)
        }
    }

    class ViewHolder(item: View): RecyclerView.ViewHolder(item) {
        val userImage:CircleImageView=item.findViewById(R.id.profile_image)
        val userName:TextView=item.findViewById(R.id.userName)
        val status:TextView=item.findViewById(R.id.userStatus)

    }

}