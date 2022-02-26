package com.androrubin.reminderapp.fragments

import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.androrubin.reminderapp.R
import com.androrubin.reminderapp.adapters.SwipeGesture
import com.androrubin.reminderapp.adapters.TasksTodo
import com.androrubin.reminderapp.adapters.TodoAdapter
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.*

class ToDoFragment : Fragment() {

    private lateinit var newRecyclerView: RecyclerView
    private lateinit var mAuth: FirebaseAuth
    private lateinit var todoArrayList: ArrayList<TasksTodo>
    private lateinit var db: FirebaseFirestore
    private lateinit var todoAdapter: TodoAdapter
    private var uid: String? = null
    private lateinit var task : EditText
    private lateinit var addtaskDialog: AlertDialog


    override fun onResume() {
        super.onResume()
        setValue()
    }

    fun setValue() {
        todoArrayList = ArrayList()
        todoArrayList.clear()
        todoAdapter = TodoAdapter(todoArrayList)
        newRecyclerView.layoutManager = LinearLayoutManager(context)
        newRecyclerView.adapter = todoAdapter
        EventChangeListener()

    }
    fun addData()
    {
        val data = hashMapOf(

            "TodoTask" to task.text.toString(),
            "status" to "0"
        )
        db = FirebaseFirestore.getInstance()
        db.collection("Tasks").document("$uid").collection("Todo List")
            .document("${task.text}")
            .set(data)
            .addOnSuccessListener { docref ->

                Log.d(
                    "Chat Data Addition",
                    "DocumentSnapshot written with ID: ${docref}.id"
                )
            }
            .addOnFailureListener { e ->
                Toast.makeText(
                    context,
                    "Chat Data Addition Error adding document",
                    Toast.LENGTH_SHORT
                ).show()
            }
//        setValue()

    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val view = inflater.inflate(R.layout.fragment_to_do, container, false)

//        Toast.makeText(context, "Running on CreateView", Toast.LENGTH_SHORT).show()
        val addTaskBtn = view.findViewById<FloatingActionButton>(R.id.addTaskBtn)

        addTaskBtn.setOnClickListener {
            addtaskDialog.show()
        }
        newRecyclerView = view.findViewById(R.id.toDoRecycler)
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val swipeGesture = object : SwipeGesture(requireContext()) {
            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {

                when (direction) {

                    ItemTouchHelper.LEFT -> {
                        val archiveditem = todoArrayList[viewHolder.adapterPosition]
                        Toast.makeText(
                            context,
                            "Task deleted",
                            Toast.LENGTH_SHORT
                        ).show()
                        todoAdapter.deleteitem(viewHolder.adapterPosition, archiveditem)
                    }
                    ItemTouchHelper.RIGHT -> {
                        val archiveditem = todoArrayList[viewHolder.adapterPosition]
                        todoArrayList.removeAt(viewHolder.adapterPosition)
                        todoAdapter.addItem(todoArrayList.size, archiveditem)
                        Toast.makeText(
                            context,
                            "Task completed",
                            Toast.LENGTH_SHORT
                        ).show()
                        setValue()
                    }
                }
            }
        }
        val touchHelper = ItemTouchHelper(swipeGesture)
        touchHelper.attachToRecyclerView(newRecyclerView)
        val builder = AlertDialog.Builder(requireContext())
        val view2 = layoutInflater.inflate(R.layout.add_task_dialog, null)
        builder.setView(view2)
        addtaskDialog = builder.create()
        mAuth = FirebaseAuth.getInstance()
        val user = mAuth.currentUser
        uid = user?.uid

        val save = view2.findViewById<Button>(R.id.saveTaskBtn)
        task = view2.findViewById<EditText>(R.id.edtNewTask)
        save.setOnClickListener {

            if (TextUtils.isEmpty(task!!.getText()?.trim().toString())) {

                task.error = "This cannot be empty"
                task.requestFocus()
            } else {
                addtaskDialog.dismiss()
                addData()
                task.text = null
            }
        }
    }

    private fun EventChangeListener() {
        db = FirebaseFirestore.getInstance()
        db.collection("Tasks").document("$uid").collection("Todo List")
            .orderBy("status", Query.Direction.ASCENDING)
            .addSnapshotListener(object : EventListener<QuerySnapshot> {
                override fun onEvent(value: QuerySnapshot?, error: FirebaseFirestoreException?) {

                    if (error != null) {
                        Log.e("Firestore Error", error.message.toString())
                        return
                    }
                    for (dc: DocumentChange in value?.documentChanges!!) {
                        if (dc.type == DocumentChange.Type.ADDED) {

                            todoArrayList.add(dc.document.toObject(TasksTodo::class.java))
                        }
                    }
                    todoAdapter.notifyDataSetChanged()
                }
            })
    }
}