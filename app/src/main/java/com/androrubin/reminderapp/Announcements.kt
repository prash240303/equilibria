package com.androrubin.reminderapp

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.MenuItem
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.androrubin.reminderapp.NotificationData.NotificationData
import com.androrubin.reminderapp.NotificationData.PushNotification
import com.androrubin.reminderapp.NotificationData.RetrofitInstance
import com.androrubin.reminderapp.adapters.*
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.*
import com.google.firebase.firestore.EventListener
import com.google.firebase.messaging.FirebaseMessaging
import com.google.gson.Gson
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList
const val TOPIC="/topics/myTopic"

class Announcements:AppCompatActivity() {

    val TAG="Announcements"
    private lateinit var announcementRecyclerView: RecyclerView
    private lateinit var mAuth: FirebaseAuth
    private lateinit var announcementList: ArrayList<AnnouncementDC>
    private lateinit var db: FirebaseFirestore
    private lateinit var announcementAdapter: AnnouncementAdapter
    private lateinit var reminderAdapter: ReminderAdapter
    private var uid: String? = null
    private lateinit var addAnnouncementDialog: AlertDialog
    private lateinit var reminderList: ArrayList<RemindersDC>
    private lateinit var chats: String



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_announcements)

        supportActionBar?.title = "Announcements"
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        val bundle: Bundle? = intent.extras
        chats = bundle?.getString("chat").toString()
        announcementList = ArrayList()
        reminderList = ArrayList()
        reminderAdapter = ReminderAdapter(reminderList)
        announcementRecyclerView=findViewById(R.id.announceRecycler)
        announcementAdapter = AnnouncementAdapter(announcementList)

        announcementRecyclerView.layoutManager = LinearLayoutManager(this)
        announcementRecyclerView.adapter = announcementAdapter
        EventChangeListener()



        val announceBtn=findViewById<FloatingActionButton>(R.id.btnAnnouncement)
        announceBtn.setOnClickListener{
            addAnnouncementDialog.show()
        }

        val builder = AlertDialog.Builder(this)
        val view2 = layoutInflater.inflate(R.layout.announcement_dialog, null)
        builder.setView(view2)
        addAnnouncementDialog = builder.create()
        mAuth = FirebaseAuth.getInstance()
        val user = mAuth.currentUser
        val name=user?.displayName
        uid = user?.uid

        val save = view2.findViewById<Button>(R.id.saveAnnouncementBtn)
        val title = view2.findViewById<EditText>(R.id.edtTitle)
        val desc=view2.findViewById<EditText>(R.id.edtDescription)
        val date = view2.findViewById<TextView>(R.id.txtAnnounceDate)
        val time = view2.findViewById<TextView>(R.id.txtAnnounceTime)

        val swipeGesture = object : SwipeGesture(this) {
            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {

                when (direction) {

                    ItemTouchHelper.LEFT -> {
                        val archiveditem = announcementList[viewHolder.adapterPosition]
                        announcementList.removeAt(viewHolder.adapterPosition)
                        announcementAdapter.addItem(viewHolder.adapterPosition, archiveditem)
                    }
                    ItemTouchHelper.RIGHT -> {

                        val archiveditem = announcementList[viewHolder.adapterPosition]
                        announcementList.removeAt(viewHolder.adapterPosition)

                        Toast.makeText(this@Announcements, "Reminder added", Toast.LENGTH_SHORT).show()
                        announcementAdapter.addItem(viewHolder.adapterPosition, archiveditem)

                    }
                }
            }
        }
        val touchHelper = ItemTouchHelper(swipeGesture)
        touchHelper.attachToRecyclerView(announcementRecyclerView)

        time.setOnClickListener {
            val cal = Calendar.getInstance()
            val timeSetListener = TimePickerDialog.OnTimeSetListener{ timePicker, hour, minute ->
                cal.set(Calendar.HOUR_OF_DAY,hour)
                cal.set(Calendar.MINUTE,minute)
                val sdf = SimpleDateFormat("HH:mm")
                time.setText( sdf.format(cal.time))
            }
            TimePickerDialog(this,timeSetListener,cal.get(Calendar.HOUR_OF_DAY),cal.get(Calendar.MINUTE),true).show()
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
            DatePickerDialog(this,datePicker,cal.get(Calendar.YEAR),cal.get(Calendar.MONTH),cal.get(
                Calendar.DAY_OF_MONTH)).show()

        }
        FirebaseMessaging.getInstance().subscribeToTopic(TOPIC)
        save.setOnClickListener {
            mAuth = FirebaseAuth.getInstance()
            db = FirebaseFirestore.getInstance()


            if (TextUtils.isEmpty(title!!.getText()?.trim().toString())) {

                title.error = "This cannot be empty"
                title.requestFocus()
            }
            if (TextUtils.isEmpty(desc!!.getText()?.trim().toString())) {

                desc.error = "This cannot be empty"
                desc.requestFocus()
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

                addAnnouncementDialog.dismiss()

                val data = hashMapOf(

                    "title" to title.text.trim().toString(),
                    "desc" to desc.text.trim().toString(),
                    "date" to date.text.trim().toString(),
                    "time" to time.text.trim().toString()

                )

                db.collection("Communities").document("$chats").collection("Announcements")
                    .add(data)
                    .addOnSuccessListener { docref ->
                        Log.d(
                            "Chat Data Addition",
                            "DocumentSnapshot written with ID: ${docref}.id"
                        )
                        val toast=Toast.makeText(this,"Announcement Published",Toast.LENGTH_SHORT).show()
                    }
                    .addOnFailureListener { e ->
                        Toast.makeText(
                            this,
                            "Chat Data Addition Error adding document",
                            Toast.LENGTH_SHORT).show()
                    }



                val main=title.text.toString()
                val message=desc.text.toString()
                if((main.isNotEmpty()) && (message.isNotEmpty())){
                    PushNotification(
                        NotificationData(main,message),
                        TOPIC
                    ).also {
                        sendNotification(it)
                    }
                    }
                }
                title.text = null
                desc.text=null
                date.text = null
                time.text = null
            }
    }
    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

    private fun EventChangeListener() {

        db = FirebaseFirestore.getInstance()
        db.collection("Communities").document("$chats").collection("Announcements").orderBy("time", Query.Direction.ASCENDING)
            .addSnapshotListener(object : EventListener<QuerySnapshot> {
                override fun onEvent(value: QuerySnapshot?, error: FirebaseFirestoreException?){
                    if (error != null) {
                        Log.e("Firestore Error", error.message.toString())
                        return
                    }
                    for (dc: DocumentChange in value?.documentChanges!!) {
                        if (dc.type == DocumentChange.Type.ADDED) {
                            announcementList.add(dc.document.toObject(AnnouncementDC::class.java))
                            reminderList.add(dc.document.toObject(RemindersDC::class.java))
                        }
                    }
                    announcementAdapter.notifyDataSetChanged()
                    reminderAdapter.notifyDataSetChanged()
                }
            })
    }
private fun sendNotification(notification: PushNotification)= CoroutineScope(Dispatchers.IO).launch {
    try{
        val response= RetrofitInstance.api.postNotification(notification)
        if(response.isSuccessful){
            Log.d(TAG,"Response: ${Gson(). toJson(response)}")
        }else{
            Log.e(TAG,response.errorBody().toString())
        }
    }catch(e:Exception){
        Log.e(TAG,e.toString())
    }
}

    }

