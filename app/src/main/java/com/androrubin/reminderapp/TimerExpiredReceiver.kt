package com.androrubin.reminderapp

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.androrubin.reminderapp.util.NotificationUtil
import com.androrubin.reminderapp.util.PrefUtil

class TimerExpiredReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        NotificationUtil.showTimerExpired(context)
        PrefUtil.setTimerState(Pomodoro.TimerState.Stopped,context)
        PrefUtil.setAlarmSetTime(0,context)
    }
}