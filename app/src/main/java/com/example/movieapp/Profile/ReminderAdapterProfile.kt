package com.example.movieapp.Profile

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.movieapp.R
import com.example.movieapp.database.ReminderData
import com.example.movieapp.utils.Constants

class ReminderAdapterProfile : RecyclerView.Adapter<ReminderAdapterProfile.ReminderViewHolder>() {
    private var reminderList : ArrayList<ReminderData> = ArrayList()
    private var onClickDeleteItem : ((ReminderData) -> Unit)? = null
    @SuppressLint("NotifyDataSetChanged")
    fun addItems(items: ArrayList<ReminderData>) {
        this.reminderList = items
        this.notifyDataSetChanged()
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = ReminderViewHolder (
        LayoutInflater.from(parent.context).inflate(R.layout.item_reminder_profile, parent, false))

    override fun onBindViewHolder(holder: ReminderViewHolder, position: Int) {
        val data = reminderList[position]
        holder.bindView(data)
    }

    override fun getItemCount(): Int {
        return reminderList.size
    }
    class ReminderViewHolder(var view: View) : RecyclerView.ViewHolder(view) {
        private var title = view.findViewById<TextView>(R.id.tvRemName)
        private var date = view.findViewById<TextView>(R.id.tvRemDateRelease)
        private var rate = view.findViewById<TextView>(R.id.tvFavRate)
        private var dayreminder = view.findViewById<TextView>(R.id.textReminder)

        fun bindView(data: ReminderData) {
            title.text = data.title
            date.text = data.date
            rate.text = data.rate
            dayreminder.text = data.day
            val moviePosterURL = Constants.POSTER_BASE_URL + data.poster
        }
    }
}