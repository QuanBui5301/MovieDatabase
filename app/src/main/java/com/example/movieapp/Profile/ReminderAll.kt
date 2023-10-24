package com.example.movieapp.Profile

import android.app.*
import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.movieapp.*
import com.example.movieapp.Detail.*
import com.example.movieapp.database.ReminderData
import com.example.movieapp.database.SQLiteReminder
import java.util.*

class ReminderAll : Fragment() {
    private lateinit var recyclerView: RecyclerView
    private lateinit var sqLiteReminder: SQLiteReminder
    private var movieId : Int = 0
    private lateinit var movieTitle : String
    private lateinit var movieDate : String
    private lateinit var movieRate : String
    private lateinit var moviePoster : String
    private var mSaveDay = 0
    private var mSaveMonth = 0
    private var mSaveYear = 0
    private var mSaveHour = 0
    private var mSaveMinute = 0
    private var saveTime : String = ""
    private var adapterrem: ReminderAllAdapter? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {

        }
    }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_reminder_all, container, false)
        HomeActivity.toggle.syncState()
        (activity as HomeActivity?)?.getSupportActionBar()?.setDisplayHomeAsUpEnabled(true)
        recyclerView = view.findViewById(R.id.rlReminderAll)
        recyclerView.layoutManager = LinearLayoutManager(context)
        adapterrem = ReminderAllAdapter()
        recyclerView.adapter = adapterrem
        sqLiteReminder = SQLiteReminder(view.context)
        var remlist = sqLiteReminder.getAllReminder()
        adapterrem?.addItems(remlist)
        adapterrem?.setOnClickDeleteItem {
            val buider = AlertDialog.Builder(context)
            buider.setMessage("Are you sure you want to unremind this movie?")
            buider.setCancelable(true)
            buider.setPositiveButton("Delete") { dialog, _ ->
                cancelNotification(it.id, requireContext())
                sqLiteReminder.deleteReminder(it.id)
                val remUpdate = sqLiteReminder.getAllReminder()
                adapterrem?.addItems(remUpdate)
                dialog.dismiss()
            }
            buider.setNegativeButton("Update") { dialog, _ ->
                movieId = it.id
                movieRate = it.rate
                movieDate = it.date
                moviePoster = it.poster
                movieTitle = it.title
                sqLiteReminder.deleteReminder(movieId)
                createReminder()
                dialog.dismiss()
            }
            val alert = buider.create()
            alert.show()
        }
        return view
    }
    fun createReminder() {
        val currentDateTime = Calendar.getInstance()
        val startYear = currentDateTime.get(Calendar.YEAR)
        val startMonth = currentDateTime.get(Calendar.MONTH)
        val startDay = currentDateTime.get(Calendar.DAY_OF_MONTH)
        val startHour = currentDateTime.get(Calendar.HOUR_OF_DAY)
        val startMinute = currentDateTime.get(Calendar.MINUTE)

        DatePickerDialog(requireContext(), { _, year, month, day ->
            TimePickerDialog(requireContext(), { _, hour, minute ->
                val pickedDateTime = Calendar.getInstance()
                pickedDateTime.set(year, month, day, hour, minute)
                mSaveYear = year
                mSaveMonth = month
                mSaveDay = day
                mSaveHour = hour
                mSaveMinute = minute
                currentDateTime.set(mSaveYear, mSaveMonth, mSaveDay, mSaveHour, mSaveMinute)
                val time: Long = currentDateTime.timeInMillis
                val timeHour = "$mSaveYear/${mSaveMonth+1}/$mSaveDay $mSaveHour:$mSaveMinute"
                saveTime = timeHour
                val data = ReminderData(id = movieId, title = movieTitle, rate = movieRate, date = movieDate, poster = moviePoster, overview = "", adult = "", day = timeHour, time = time.toString())
                val status = sqLiteReminder.insertReminder(data)
                val remUpdate = sqLiteReminder.getAllReminder()
                adapterrem?.addItems(remUpdate)
                recyclerView.adapter!!.notifyDataSetChanged()
                createNotification(time)
                var remlist = sqLiteReminder.getAllReminder()
                HomeActivity.adapterrem?.addItems(remlist)
                if (status > -1) {
                    Toast.makeText(context, "Reminder Add", Toast.LENGTH_SHORT).show()
                }
                else Toast.makeText(context, "Error", Toast.LENGTH_SHORT).show()
            }, startHour, startMinute, true).show()
        }, startYear, startMonth, startDay).show()
    }
    private fun createNotification (time: Long) {
        val intent = Intent(requireContext().applicationContext, AlarmReceiver::class.java)
        val bundle = Bundle()
        bundle.putString(titleExtra, movieTitle)
        bundle.putString(rateExtra, movieRate)
        bundle.putString(dateExtra, movieDate)
        bundle.putInt(idExtra, movieId)
        intent.putExtras(bundle)
        val pendingIntent = PendingIntent.getBroadcast(requireActivity().applicationContext, movieId, intent, PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT)
        val alarmManager = requireActivity().getSystemService(Context.ALARM_SERVICE) as AlarmManager
        alarmManager.setExact(AlarmManager.RTC_WAKEUP, time, pendingIntent)
    }
    private fun cancelNotification(movieId: Int, context: Context) {
        val intent = Intent(context, AlarmReceiver::class.java)
        val pendingIntent = PendingIntent.getBroadcast(
            context,
            movieId,
            intent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        alarmManager.cancel(pendingIntent)
    }
}