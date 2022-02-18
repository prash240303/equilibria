package com.androrubin.reminderapp

import com.google.firebase.Timestamp

data class Message(var sendername : String?=null , var message : String?=null , var time : Timestamp?=null)