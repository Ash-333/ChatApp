package com.example.localeye.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.localeye.data.Message;
import com.example.localeye.R;
import com.example.localeye.ui.ChatActivity;
import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class MessageAdatper extends RecyclerView.Adapter {
    Context context;
    ArrayList<Message> messages;

    public MessageAdatper(Context context, ArrayList<Message> messages) {
        this.context = context;
        this.messages = messages;
    }

    int ITEM_SEND=1;
    int ITEM_RECEIVE=2;

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if(viewType==ITEM_SEND){
            View view= LayoutInflater.from(context).inflate(R.layout.sender_layout_item,parent,false);
            return new SenderViewHolder(view);
        }
        else{
            View view= LayoutInflater.from(context).inflate(R.layout.receiver_item_layout,parent,false);
            return new ReceiverViewHolder(view);
        }

    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        String img= ChatActivity.receiverImg;
        String img2=ChatActivity.senderImg;
        Message message=messages.get(position);
        if(holder.getClass()==SenderViewHolder.class){
            SenderViewHolder viewHolder=(SenderViewHolder) holder;
            viewHolder.senderMessage.setText(message.getMessage());
            Glide.with(context).load(img2).into(viewHolder.senderImg);
        }else{
            ReceiverViewHolder viewHolder=(ReceiverViewHolder) holder;
            viewHolder.receiverMessage.setText(message.getMessage());
            Glide.with(context).load(img).into(viewHolder.receiverImg);
        }
    }

    @Override
    public int getItemCount() {
        return messages.size();
    }

    @Override
    public int getItemViewType(int position) {
        Message message=messages.get(position);
        if(FirebaseAuth.getInstance().getCurrentUser().getUid().equals(message.getSenderId())){
            return ITEM_SEND;
        }else{
            return ITEM_RECEIVE;
        }

    }
}

class ReceiverViewHolder extends RecyclerView.ViewHolder{

    CircleImageView receiverImg;
    TextView receiverMessage;

    public ReceiverViewHolder(@NonNull View itemView) {
        super(itemView);
        receiverImg=itemView.findViewById(R.id.receiver_image);
        receiverMessage=itemView.findViewById(R.id.receiver_message);

    }
}

class SenderViewHolder extends RecyclerView.ViewHolder{
    CircleImageView senderImg;
    TextView senderMessage;
    public SenderViewHolder(@NonNull View itemView) {
        super(itemView);
        senderImg=itemView.findViewById(R.id.sender_image);
        senderMessage=itemView.findViewById(R.id.sender_message);

    }
}
