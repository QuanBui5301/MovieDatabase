package com.example.movieapp.Favourite

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import coil.load
import coil.size.Scale
import com.example.movieapp.R
import com.example.movieapp.database.FavouriteData
import com.example.movieapp.utils.Constants

class FavouriteAdapter : RecyclerView.Adapter<FavouriteAdapter.FavouriteViewHolder>() {
    private var favList : ArrayList<FavouriteData> = ArrayList()
    private var onClickDeleteItem : ((FavouriteData) -> Unit)? = null
    @SuppressLint("NotifyDataSetChanged")
    fun addItems(items: ArrayList<FavouriteData>) {
        this.favList = items
        this.notifyDataSetChanged()
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = FavouriteViewHolder (
        LayoutInflater.from(parent.context).inflate(R.layout.item_favourite, parent, false))
    fun setOnClickDeleteItem(callback:(FavouriteData) -> Unit) {
        this.onClickDeleteItem = callback
    }
    override fun onBindViewHolder(holder: FavouriteViewHolder, position: Int) {
        val data = favList[position]
        holder.bindView(data)
        holder.btnUnFav.setOnClickListener { onClickDeleteItem?.invoke(data) }
        holder.view.setOnLongClickListener() {
            onClickDeleteItem?.invoke(data)
            return@setOnLongClickListener true
        }
    }

    override fun getItemCount(): Int {
        return favList.size
    }
    class FavouriteViewHolder(var view: View) : RecyclerView.ViewHolder(view) {
        private var id = view.findViewById<TextView>(R.id.FavLang)
        private var title = view.findViewById<TextView>(R.id.tvRemName)
        private var date = view.findViewById<TextView>(R.id.tvFavDateRelease)
        private var rate = view.findViewById<TextView>(R.id.tvFavRate)
        private var poster = view.findViewById<ImageView>(R.id.ImgMovieFav)
        private var overview = view.findViewById<TextView>(R.id.textFavOverview)
        private var adult = view.findViewById<ImageView>(R.id.imageViewAdultFav)
        var btnUnFav = view.findViewById<ImageButton>(R.id.MovieUnfavourite)

        fun bindView(data: FavouriteData) {
            id.text = data.id.toString()
            title.text = data.title
            date.text = data.date
            rate.text = data.rate
            if (data.adult == "true") {
                adult.visibility = View.VISIBLE
            }
            else if (data.adult == "false") {
                adult.visibility = View.GONE
            }
            val moviePosterURL = Constants.POSTER_BASE_URL + data.poster
            poster.load(moviePosterURL){
                crossfade(true)
                placeholder(R.drawable.poster_placeholder)
                scale(Scale.FILL)
            }
            overview.text = data.overview
        }
    }
}