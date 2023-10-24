package com.example.movieapp.Detail

import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.core.app.NotificationCompat
import com.example.movieapp.R
import com.example.movieapp.database.SQLiteReminder

const val channelID = "channel1"
const val titleExtra = "titleExtra"
const val rateExtra = "rateExtra"
const val dateExtra = "dateExtra"
const val idExtra = "idExtra"

class AlarmReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        val bundle = intent!!.extras
        var movieTitle = bundle?.getString(titleExtra)
        var release = bundle?.getString(dateExtra)
        var rate = bundle?.getString(rateExtra)
        var vote = "$rate/10"
        var id = bundle?.getInt("movieID")
        if (bundle != null) {
            val sqLiteReminder = SQLiteReminder(context!!)
            sqLiteReminder.deleteReminder(id!!)
            Log.d("AlarmReceiver", "Removed")
        }
        val notification = NotificationCompat.Builder(context!!, channelID)
            .setSmallIcon(R.drawable.ic_baseline_notifications_active_24)
            .setContentTitle(movieTitle)
            .setContentText("Release: " + release + " - " + "Rate: " + vote)
            .build()
        val manager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        manager.notify(id!!, notification)
    }
}