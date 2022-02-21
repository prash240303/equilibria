package com.androrubin.reminderapp.adapters

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.androrubin.reminderapp.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class ReminderAdapter (private val reminderList: ArrayList<RemindersDC>):RecyclerView.Adapter<ReminderAdapter.ViewHolder>(){

    private lateinit var db: FirebaseFirestore
    var mAuth = FirebaseAuth.getInstance()
    val user = mAuth.currentUser
    val name = user?.displayName
    val uid = user?.uid



    fun deleteitem(pos : Int, tasks: RemindersDC){

        reminderList.removeAt(pos)
        db = FirebaseFirestore.getInstance()
        db.collection("Tasks").document("$uid").collection("Reminders").document("${tasks.reminder}")
            .delete()
            .addOnSuccessListener { Log.d("Deleting Tasks", "DocumentSnapshot successfully deleted!") }
            .addOnFailureListener { e -> Log.w("Deleting Account", "Error deleting document", e) }
        notifyDataSetChanged()

    }

    fun addItem(i : Int, tasks : RemindersDC ){

        reminderList.add(i,tasks)
        notifyDataSetChanged()
    }



    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ReminderAdapter.ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.reminder_list_item, parent,false)
        return ViewHolder(view)
    }
//private val todoArrayList: ArrayList<RemindersDC>
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val currentitem = reminderList[position]
//        holder.task.text = "Help"
//        holder.date.text = "1"
//        holder.time.text = "2"
        holder.task.text = currentitem.reminder
        holder.date.text = currentitem.date
        holder.time.text = currentitem.time
    }

    override fun getItemCount(): Int {
//        return 10
        return reminderList.size
    }
    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){

        val task = itemView.findViewById<TextView>(R.id.task)
        val date = itemView.findViewById<TextView>(R.id.alarmDate)
        val time = itemView.findViewById<TextView>(R.id.alarmTime)

    }
}