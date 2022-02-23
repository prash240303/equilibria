package com.androrubin.reminderapp

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth

class ChatAdapter(private val messageList:ArrayList<Message>) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    val ITEM_RECIEVE = 1
    val ITEM_SENT = 2

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        if (viewType == 1){

            val inflater = LayoutInflater.from(parent.context)
            val view: View = inflater.inflate(R.layout.recieve, parent ,false)
            return ReceiveViewHolder(view)

        }
        else{

            val inflater = LayoutInflater.from(parent.context)
            val view: View = inflater.inflate(R.layout.sent, parent ,false)
            return SentViewHolder(view)

        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {

        val currentMessage:Message = messageList[position]
        if (holder.javaClass == SentViewHolder::class.java){

            val viewHolder = holder as SentViewHolder
            viewHolder.sentMessage.text = currentMessage.message
            viewHolder.time.text = currentMessage.time?.toDate().toString()

        }else{

            val viewHolder = holder as ReceiveViewHolder
            viewHolder.recieveMessage.text = currentMessage.message
            viewHolder.sender2.text = currentMessage.sendername
            viewHolder.time2.text = currentMessage.time?.toDate().toString()
        }

    }

    override fun getItemCount(): Int {
        return messageList.size
    }

    class SentViewHolder(itemView: View): RecyclerView.ViewHolder(itemView){

        val sentMessage = itemView.findViewById<TextView>(R.id.message2)
        val time = itemView.findViewById<TextView>(R.id.time2)

    }

    class ReceiveViewHolder(itemView: View): RecyclerView.ViewHolder(itemView){

        val recieveMessage = itemView.findViewById<TextView>(R.id.message)
        val sender2 = itemView.findViewById<TextView>(R.id.sender_name)
        val time2 = itemView.findViewById<TextView>(R.id.time)

    }

    override fun getItemViewType(position: Int): Int {

        val currentMessage = messageList[position]

        if (FirebaseAuth.getInstance().currentUser?.displayName.equals(currentMessage.sendername)){
            return ITEM_SENT
        }else{
            return ITEM_RECIEVE
        }
    }
}