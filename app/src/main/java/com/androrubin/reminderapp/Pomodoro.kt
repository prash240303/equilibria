package com.androrubin.reminderapp

import android.app.AlarmManager
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.os.CountDownTimer
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import com.google.android.material.snackbar.Snackbar
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import com.androrubin.reminderapp.databinding.ActivityPomodoroBinding
import com.androrubin.reminderapp.util.NotificationUtil
import com.androrubin.reminderapp.util.PrefUtil
import kotlinx.android.synthetic.main.activity_pomodoro.*
import kotlinx.android.synthetic.main.content_pomodoro.*
import java.util.*

class Pomodoro : AppCompatActivity() {

    val CHANNEL_ID="channelID1"
    val CHANNEL_NAME= "channelName1"
    val NOTIFICATION_ID=0

    companion object {
        fun setAlarm(context: Context, nowSeconds: Long, secondsRemaining: Long): Long{
            val wakeUpTime = (nowSeconds + secondsRemaining) * 1000
            val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
            val intent = Intent(context, TimerExpiredReceiver::class.java)
            val pendingIntent = PendingIntent.getBroadcast(context,0,intent,0)
            alarmManager.setExact(AlarmManager.RTC_WAKEUP, wakeUpTime, pendingIntent)
            PrefUtil.setAlarmSetTime(nowSeconds,context)
            return wakeUpTime
        }

        fun removeAlarm(context: Context){
            val intent = Intent(context,TimerExpiredReceiver::class.java)
            val pendingIntent = PendingIntent.getBroadcast(context,0,intent,0)
            val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
            alarmManager.cancel(pendingIntent)
            PrefUtil.setAlarmSetTime(0,context)
        }
        val nowSeconds: Long
            get() = Calendar.getInstance().timeInMillis / 1000
    }

    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var binding: ActivityPomodoroBinding

    enum class TimerState{
        Stopped, Paused, Running
    }

    private lateinit var timer: CountDownTimer
    private var timerLengthSeconds = 0L
    private var timerState = TimerState.Stopped
    private var secondsRemaining = 0L

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityPomodoroBinding.inflate(layoutInflater)
        setContentView(binding.root)
        createNotificationChannel()
//        val actionbar = supportActionBar
//        //set back button
//        actionbar?.setDisplayHomeAsUpEnabled(true)
//        actionbar?.setDisplayHomeAsUpEnabled(true)
        setSupportActionBar(binding.toolbar)

        supportActionBar?.setIcon(R.drawable.ic_baseline_timer_24)
        supportActionBar?.title="  Pomodoro"
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        val notificationManager=NotificationManagerCompat.from(this)

        fab_play.setOnClickListener {

            val notification=NotificationCompat.Builder(this,CHANNEL_ID)
                .setContentTitle("FOCUS ON WORK")
                .setContentText("Started a session for ___ min")
                .setSmallIcon(R.drawable.ic_baseline_play_arrow_24)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .build()

            val notificationManager=NotificationManagerCompat.from(this)
            startTimer()
            timerState = TimerState.Running
            updateButtons()
            notificationManager.notify(NOTIFICATION_ID,notification)

        }

        fab_pause.setOnClickListener {
            timer.cancel()
            timerState = TimerState.Paused
            updateButtons()
            notificationManager.deleteNotificationChannel(CHANNEL_ID)
        }

        fab_stop.setOnClickListener {
            if(timerState==TimerState.Running){
                timer.cancel() }
            createNotificationChannel()
            onTimerFinished()
            timerState=TimerState.Stopped

        }


    }
    override fun onResume(){
        super.onResume()
        initTimer()
        removeAlarm(this)
        NotificationUtil.hideTimerNotification(this)
    }

    override fun onPause() {
        super.onPause()

        if(timerState == TimerState.Running){
            timer.cancel()
            val wakeUpTime = setAlarm(this, nowSeconds,secondsRemaining)
            NotificationUtil.showTimerRunning(this,wakeUpTime)
        }
        else if(timerState == TimerState.Paused){
            NotificationUtil.showTimerPaused(this)
        }

        PrefUtil.setPreviousTimerLengthSeconds(timerLengthSeconds,this)
        PrefUtil.setSecondsRemaining(secondsRemaining,this)
        PrefUtil.setTimerState(timerState,this)
    }

    private fun initTimer(){
        timerState = PrefUtil.getTimerState(this)
        // if the Length was changed in settings while it was backgrounded
        if(timerState == TimerState.Stopped)
            setNewTimerLength()
        else
            setPreviousTimerLength()

        secondsRemaining = if(timerState == TimerState.Running || timerState == TimerState.Paused)
            PrefUtil.getSecondsRemaining(this)
        else
            timerLengthSeconds

        val alarmSetTime = PrefUtil.getAlarmSetTime(this)
        if(alarmSetTime>0)
            secondsRemaining -= nowSeconds - alarmSetTime

        if(secondsRemaining <= 0)
            onTimerFinished()
        else if(timerState== TimerState.Running)
            startTimer()

        updateButtons()
        updateCountdownUI()
    }


    private fun onTimerFinished(){
        val notification=NotificationCompat.Builder(this,CHANNEL_ID)
            .setContentTitle("SESSION ENDED")
            .setContentText("Well Done !!\nYou deserve a break")
            .setSmallIcon(R.drawable.ic_baseline_stop_24)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .build()

        val notificationManager=NotificationManagerCompat.from(this)
        notificationManager.notify(NOTIFICATION_ID,notification)
        timerState = TimerState.Stopped

        setNewTimerLength()

        progress_countdown.progress = 0

        PrefUtil.setSecondsRemaining(timerLengthSeconds,this)
        secondsRemaining = timerLengthSeconds


        updateButtons()
        updateCountdownUI()

    }

    private fun startTimer(){
        timerState = TimerState.Running

        timer = object : CountDownTimer(secondsRemaining * 1000,1000){
            override fun onFinish() = onTimerFinished()

            override fun onTick(millisUntilFinished: Long) {
                secondsRemaining  = millisUntilFinished /1000
                updateCountdownUI()

            }
        }.start()
    }

    private  fun setNewTimerLength(){
        val lengthInMinutes = PrefUtil.getTimerLength(this)
        timerLengthSeconds = (lengthInMinutes * 60L)
        progress_countdown.max = timerLengthSeconds.toInt()
    }

    private fun setPreviousTimerLength(){
        timerLengthSeconds = PrefUtil.getPreviousTimerLengthSeconds(this)
        progress_countdown.max= timerLengthSeconds.toInt()
    }

    private fun updateCountdownUI(){
        val minutesUntilFinished = secondsRemaining / 60
        val secondsInMinutesUntilFinished = secondsRemaining - minutesUntilFinished * 60
        val secondsStr = secondsInMinutesUntilFinished.toString()
        time_counter.text = "$minutesUntilFinished:${
            if(secondsStr.length == 2) secondsStr
            else "0" + secondsStr
        }"
        progress_countdown.progress = (timerLengthSeconds - secondsRemaining).toInt()
    }

    private fun updateButtons(){
        when(timerState){
            TimerState.Running-> {
                fab_play.isEnabled = false
                fab_pause.isEnabled = true
                fab_stop.isEnabled = true
            }
            TimerState.Stopped->{
                fab_play.isEnabled = true
                fab_pause.isEnabled = false
                fab_stop.isEnabled = false
            }
            TimerState.Paused->{
                fab_play.isEnabled = true
                fab_pause.isEnabled = false
                fab_stop.isEnabled = true
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        return when (item.itemId) {
            R.id.action_settings -> {
                // val intent = Intent(this, SettingsActivity::class.java)
                //startActivity(intent)
                Toast.makeText(this,"Clicked Settings",Toast.LENGTH_SHORT).show()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

    fun createNotificationChannel(){
        val channel=NotificationChannel(CHANNEL_ID,CHANNEL_NAME,NotificationManager.IMPORTANCE_HIGH).apply {
            lightColor= Color.RED
            enableLights(true)
        }
        val manager=getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        manager.createNotificationChannel(channel)

    }

}


