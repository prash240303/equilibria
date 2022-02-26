package com.androrubin.reminderapp.fragments

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.androrubin.reminderapp.R
import com.androrubin.reminderapp.adapters.ReminderAdapter
import com.androrubin.reminderapp.adapters.RemindersDC
import com.androrubin.reminderapp.adapters.SwipeGesture
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.*
import com.google.firebase.firestore.EventListener
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class ReminderFragment : Fragment() {

    private lateinit var newRecyclerView: RecyclerView
    private lateinit var mAuth: FirebaseAuth
    private lateinit var reminderList: ArrayList<RemindersDC>
    private lateinit var db: FirebaseFirestore
    private lateinit var reminderAdapter: ReminderAdapter
    private var uid: String? = null
    private lateinit var addtaskDialog: AlertDialog

    private lateinit var task : EditText
    private lateinit var date : TextView
    private lateinit var time : TextView


    override fun onResume() {
        super.onResume()
        setvalue()
    }

    private fun setvalue() {
        reminderList = ArrayList()
        reminderList.clear()
        reminderAdapter = ReminderAdapter(reminderList)
        newRecyclerView.layoutManager = LinearLayoutManager(context)
        newRecyclerView.adapter = reminderAdapter
        EventChangeListener()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val view = inflater.inflate(R.layout.fragment_reminder, container, false)
        db = FirebaseFirestore.getInstance()
        val addTaskBtn = view.findViewById<FloatingActionButton>(R.id.addReminderBtn)

        addTaskBtn.setOnClickListener {
            addtaskDialog.show()
        }
        newRecyclerView = view.findViewById(R.id.reminderRecycler)

        return view

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val swipeGesture = object : SwipeGesture(requireContext()) {
            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {

                when (direction) {

                    ItemTouchHelper.LEFT -> {
                        val archiveditem = reminderList[viewHolder.adapterPosition]
                        Toast.makeText(
                            context,
                            "Reminder deleted",
                            Toast.LENGTH_SHORT
                        ).show()
                        reminderAdapter.deleteitem(viewHolder.adapterPosition, archiveditem)
                    }
                    ItemTouchHelper.RIGHT -> {

                        val archiveditem = reminderList[viewHolder.adapterPosition]
                        reminderList.removeAt(viewHolder.adapterPosition)
                        reminderAdapter.addItem(viewHolder.adapterPosition, archiveditem)

                    }
                }
            }
        }
        val touchHelper = ItemTouchHelper(swipeGesture)
        touchHelper.attachToRecyclerView(newRecyclerView)
        val builder = AlertDialog.Builder(requireContext())
        val view2 = layoutInflater.inflate(R.layout.add_reminder_dialog, null)
        builder.setView(view2)
        addtaskDialog = builder.create()
        mAuth = FirebaseAuth.getInstance()
        val user = mAuth.currentUser
        uid = user?.uid

        val save = view2.findViewById<Button>(R.id.saveReminderBtn)
        task = view2.findViewById<EditText>(R.id.edtNewReminder)
        date = view2.findViewById<TextView>(R.id.txtAlarmDate)
        time = view2.findViewById<TextView>(R.id.txtAlarmTime)

        time.setOnClickListener {
            val cal = Calendar.getInstance()
            val timeSetListener = TimePickerDialog.OnTimeSetListener{ timePicker, hour, minute ->
                cal.set(Calendar.HOUR_OF_DAY,hour)
                cal.set(Calendar.MINUTE,minute)
                val sdf = SimpleDateFormat("HH:mm")
                time.setText( sdf.format(cal.time))
            }
            TimePickerDialog(context,timeSetListener,cal.get(Calendar.HOUR_OF_DAY),cal.get(Calendar.MINUTE),true).show()

        }

        date.setOnClickListener {
            val cal = Calendar.getInstance()
            val datePicker = DatePickerDialog.OnDateSetListener { datePicker, year, month, day ->
                cal.set(Calendar.YEAR,year)
                cal.set(Calendar.MONTH,month)
                cal.set(Calendar.DAY_OF_MONTH,day)
                val sdf = SimpleDateFormat("dd/MM/yyyy")
                date.setText( sdf.format(cal.time))
            }
            DatePickerDialog(requireContext(),datePicker,cal.get(Calendar.YEAR),cal.get(Calendar.MONTH),cal.get(
                Calendar.DAY_OF_MONTH)).show()

        }

        save.setOnClickListener {

            if (TextUtils.isEmpty(task!!.getText()?.trim().toString())) {

                task.error = "This cannot be empty"
                task.requestFocus()
            }
            else if(TextUtils.isEmpty(date!!.getText()?.trim().toString())){
                date.error = "Select a date"
                date.requestFocus()
            }
            else if(TextUtils.isEmpty(time.getText()?.trim().toString())){
                time.error = "Select a time"
                time.requestFocus()
            }
            else{

                addtaskDialog.dismiss()

                val data = hashMapOf(

                    "reminder" to task.text.trim().toString(),
                    "date" to date.text.trim().toString(),
                    "time" to time.text.trim().toString()

                )

                db.collection("Tasks").document("$uid").collection("Reminders").document("${task.text.trim()}")
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

                reminderAdapter.notifyDataSetChanged()
                task.text = null
                date.text = null
                time.text = null
            }
        }

    }

    private fun EventChangeListener() {

//        reminderList = ArrayList()

        db = FirebaseFirestore.getInstance()
        db.collection("Tasks").document("$uid").collection("Reminders").orderBy("reminder",Query.Direction.ASCENDING)
            .addSnapshotListener(object : EventListener<QuerySnapshot> {
                override fun onEvent(value: QuerySnapshot?, error: FirebaseFirestoreException?) {

                    if (error != null) {
                        Log.e("Firestore Error", error.message.toString())
                        return
                    }
                    for (dc: DocumentChange in value?.documentChanges!!) {
                        if (dc.type == DocumentChange.Type.ADDED) {

                            reminderList.add(dc.document.toObject(RemindersDC::class.java))
                        }
                    }
                    reminderAdapter.notifyDataSetChanged()
                }
            })
    }
}