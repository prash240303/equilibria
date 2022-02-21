package com.androrubin.reminderapp.adapters

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.androrubin.reminderapp.ChatAdapter
import com.androrubin.reminderapp.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class TodoAdapter (private val todoArrayList: ArrayList<TasksTodo>):RecyclerView.Adapter<RecyclerView.ViewHolder>(){

    private lateinit var db: FirebaseFirestore
    var mAuth = FirebaseAuth.getInstance()
    val user = mAuth.currentUser
    val name = user?.displayName
    val uid = user?.uid


    val COMPLETED = 1
    val NOTCOMPLETE = 0

    fun deleteitem(pos : Int, tasks: TasksTodo){

        todoArrayList.removeAt(pos)
        db = FirebaseFirestore.getInstance()
        db.collection("Tasks").document("$uid").collection("Todo List").document("${tasks.TodoTask}")
            .delete()
            .addOnSuccessListener { Log.d("Deleting Tasks", "DocumentSnapshot successfully deleted!") }
            .addOnFailureListener { e -> Log.w("Deleting Account", "Error deleting document", e) }
        notifyDataSetChanged()

    }

    fun addItem(i : Int, tasks : TasksTodo ){
        db = FirebaseFirestore.getInstance()
        db.collection("Tasks").document("$uid").collection("Todo List").document("${tasks.TodoTask}")
            .update(
                mapOf(
                   "status" to "1"
                )
            ) .addOnSuccessListener {docRef ->
                Log.d("Data Addition", "DocumentSnapshot written with ID: ${docRef}.id")
            }
            .addOnFailureListener { e ->
                Log.w("Data Addition", "Error adding document", e)
            }
        todoArrayList.add(i,tasks)
        notifyDataSetChanged()
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        if (viewType == 1){

            val inflater = LayoutInflater.from(parent.context)
            val view: View = inflater.inflate(R.layout.todo_list_completed_item, parent ,false)
            return CompletedViewHolder(view)

        }
        else{

            val inflater = LayoutInflater.from(parent.context)
            val view: View = inflater.inflate(R.layout.todo_list_item, parent ,false)
            return NotCompleteViewHolder(view)

        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val currentitem = todoArrayList[position]
        if (holder.javaClass == CompletedViewHolder::class.java){
            val viewholder = holder as CompletedViewHolder
            viewholder.task.text = currentitem.TodoTask
        }
        else{
            val viewholder = holder as NotCompleteViewHolder
            viewholder.task.text = currentitem.TodoTask
        }
    }

    override fun getItemCount(): Int {
        return todoArrayList.size
    }
    class CompletedViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){

        val task= itemView.findViewById<TextView>(R.id.task2)

        }
    class NotCompleteViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){

        val task = itemView.findViewById<TextView>(R.id.task)

    }

    override fun getItemViewType(position: Int): Int {

        val currentMessage = todoArrayList[position]

        if ("1".equals(currentMessage.status)){
            return COMPLETED
        }else{
            return NOTCOMPLETE
        }
    }

    }