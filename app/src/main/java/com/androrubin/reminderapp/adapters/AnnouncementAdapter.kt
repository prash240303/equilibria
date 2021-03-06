package com.androrubin.reminderapp.adapters

import android.annotation.SuppressLint
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.androrubin.reminderapp.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import java.security.AccessController.getContext
import kotlin.system.measureNanoTime

class AnnouncementAdapter(private val AnnouncementList: ArrayList<AnnouncementDC>) :
    RecyclerView.Adapter<AnnouncementAdapter.ViewHolder>() {

    private lateinit var db: FirebaseFirestore
    var mAuth = FirebaseAuth.getInstance()
    val user = mAuth.currentUser
    val name = user?.displayName
    val uid = user?.uid

    fun addItem(i: Int, announcements: AnnouncementDC) {
        AnnouncementList.add(i, announcements)
        db = FirebaseFirestore.getInstance()
        val dat = hashMapOf(

            "reminder" to announcements.title,
            "date" to announcements.date,
            "time" to announcements.time

        )
        db.collection("Tasks").document("$uid").collection("Reminders")
            .document("${announcements.title}")
            .set(dat)
            .addOnSuccessListener { docref ->
                Log.d(
                    "Chat Data Addition",
                    "DocumentSnapshot written with ID: ${docref}.id"
                )
            }
            .addOnFailureListener { e ->
                Log.d(
                    "Chat Data Addition",
                    "Data cannot be added"
                )
            }
        notifyDataSetChanged()
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        val title = itemView.findViewById<TextView>(R.id.AnnouncementTitle)
        val date = itemView.findViewById<TextView>(R.id.announcementDate)
        val time = itemView.findViewById<TextView>(R.id.announcementTime)
        val desc = itemView.findViewById<TextView>(R.id.announcementDesc)

    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val currentitem = AnnouncementList[position]
        holder.title.text = currentitem.title
        holder.date.text = currentitem.date
        holder.time.text = currentitem.time
        holder.desc.text = currentitem.description
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): AnnouncementAdapter.ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.announcement_list_item, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return AnnouncementList.size
    }


}