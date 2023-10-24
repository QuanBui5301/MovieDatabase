package com.example.movieapp.Profile

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import coil.load
import coil.size.Scale
import com.example.movieapp.HomeActivity
import com.example.movieapp.R
import com.example.movieapp.database.ReminderData
import com.example.movieapp.utils.Constants

class ReminderAllAdapter : RecyclerView.Adapter<ReminderAllAdapter.ReminderViewHolder>() {
    private var reminderList : ArrayList<ReminderData> = ArrayList()
    private var onClickDeleteItem : ((ReminderData) -> Unit)? = null
    @SuppressLint("NotifyDataSetChanged")
    fun addItems(items: ArrayList<ReminderData>) {
        this.reminderList = items
        this.notifyDataSetChanged()
    }
    fun setOnClickDeleteItem(callback:(ReminderData) -> Unit) {
        this.onClickDeleteItem = callback
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = ReminderViewHolder (
        LayoutInflater.from(parent.context).inflate(R.layout.item_reminder, parent, false))

    override fun onBindViewHolder(holder: ReminderViewHolder, position: Int) {
        val data = reminderList[position]
        holder.bindView(data)
        holder.view.setOnLongClickListener() {
            onClickDeleteItem?.invoke(data)
            return@setOnLongClickListener true
        }
    }

    override fun getItemCount(): Int {
        return reminderList.size
    }
    class ReminderViewHolder(var view: View) : RecyclerView.ViewHolder(view) {
        private var title = view.findViewById<TextView>(R.id.tvRemName)
        private var date = view.findViewById<TextView>(R.id.tvRemDateRelease)
        private var rate = view.findViewById<TextView>(R.id.tvFavRate)
        private var poster = view.findViewById<ImageView>(R.id.ImgMovieFav)
        private var dayreminder = view.findViewById<TextView>(R.id.textReminder)

        fun bindView(data: ReminderData) {
            title.text = data.title
            date.text = data.date
            rate.text = data.rate
            dayreminder.text = data.day
            val moviePosterURL = Constants.POSTER_BASE_URL + data.poster
            poster.load(moviePosterURL){
                crossfade(true)
                placeholder(R.drawable.poster_placeholder)
                scale(Scale.FILL)
            }
            view.setOnClickListener() {
                var idMovieData : Bundle = Bundle()
                idMovieData.putInt("id", data.id)
                idMovieData.putString("title", data.title)
                idMovieData.putString("date", data.date)
                idMovieData.putString("rate", data.rate)
                idMovieData.putString("poster", data.poster)
                idMovieData.putString("overview", data.overview)
                idMovieData.putBoolean("fav", false)
                idMovieData.putBoolean("adultcheck", data.adult.toBoolean())
                idMovieData.putBoolean("remindercheck", true)
                HomeActivity.navController.navigate(R.id.detail, idMovieData)
            }
        }
    }
}