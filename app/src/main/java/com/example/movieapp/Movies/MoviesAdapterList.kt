package com.example.movieapp.Movies


import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import coil.load
import coil.size.Scale
import com.example.movieapp.HomeActivity
import com.example.movieapp.HomeActivity.Companion.navController
import com.example.movieapp.HomeActivity.Companion.textfavnum
import com.example.movieapp.R
import com.example.movieapp.database.FavouriteData
import com.example.movieapp.database.SQLiteHelper
import com.example.movieapp.databinding.ItemMoviesBinding
import com.example.movieapp.response.MovieListResponse
import com.example.movieapp.utils.Constants.POSTER_BASE_URL

class MoviesAdapterList : RecyclerView.Adapter<MoviesAdapterList.ViewHolder>() {
    companion object {
        var sortByList : ArrayList<MovieListResponse.Result> = ArrayList()
    }
    private lateinit var binding: ItemMoviesBinding
    private lateinit var context: Context
    private lateinit var sqLiteHelper: SQLiteHelper

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        binding = ItemMoviesBinding.inflate(inflater, parent, false)
        context = parent.context
        sqLiteHelper = SQLiteHelper(parent.context)
        return ViewHolder()
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(sortByList[position])
    }

    override fun getItemCount(): Int {
        return sortByList.size
    }

    override fun getItemViewType(position: Int): Int {
        return position
    }

    inner class ViewHolder : RecyclerView.ViewHolder(binding.root) {

        @SuppressLint("SetTextI18n")
        fun bind(item: MovieListResponse.Result) {
            binding.apply {
                tvMovieName.text = item.title
                tvMovieDateRelease.text = item.release_date
                tvRate.text=item.vote_average.toString()
                val moviePosterURL = POSTER_BASE_URL + item.poster_path
                ImgMovie.load(moviePosterURL){
                    crossfade(true)
                    placeholder(R.drawable.poster_placeholder)
                    scale(Scale.FILL)
                }
                tvLang.text=item.original_language
                if (item.adult) {
                    imageMovieAdult.visibility = View.VISIBLE
                } else {
                    imageMovieAdult.visibility = View.GONE
                }
                textMovieOverview.text = item.overview
                val favlist = sqLiteHelper.getAllFavourite()
                if (favlist.size > 0) {
                    for (i in 0..favlist.size-1) {
                        if (item.id == favlist[i].id) {
                            item.isFavourite = true
                            Moviefavourite.setImageResource(R.drawable.ic_baseline_favorite_24)
                        }
                    }
                }
                if (item.isFavourite) {
                    Moviefavourite.setImageResource(R.drawable.ic_baseline_favorite_24)
                }
                else {
                    Moviefavourite.setImageResource(R.drawable.ic_baseline_favorite_border_24)
                }
                Moviefavourite.setOnClickListener {
                    if (!item.isFavourite) {
                        Moviefavourite.setImageResource(R.drawable.ic_baseline_favorite_24)
                        val data = FavouriteData(id = item.id, title = item.title, date = item.release_date, rate = item.vote_average.toString(), poster = item.poster_path, overview = item.overview, adult = item.adult.toString())
                        val status = sqLiteHelper.insertFavourite(data)
                        var number = sqLiteHelper.getAllFavourite().size
                        textfavnum.text = number.toString()
                        if (status > -1) {
                            Toast.makeText(context, "Favourite Add", Toast.LENGTH_SHORT).show()
                        }
                        else Toast.makeText(context, "Error", Toast.LENGTH_SHORT).show()
                        item.isFavourite = true
                    }
                    else {
                        Moviefavourite.setImageResource(R.drawable.ic_baseline_favorite_border_24)
                        sqLiteHelper.deleteFavourite(item.id)
                        var number = sqLiteHelper.getAllFavourite().size
                        HomeActivity.textfavnum.text = number.toString()
                        Toast.makeText(context, "Favourite Delete", Toast.LENGTH_SHORT).show()
                        item.isFavourite = false
                    }
                }

                root.setOnClickListener {
                    var idMovieData : Bundle = Bundle()
                    idMovieData.putInt("id", item.id)
                    idMovieData.putString("title", item.title)
                    idMovieData.putString("date", item.release_date)
                    idMovieData.putString("rate", item.vote_average.toString())
                    idMovieData.putString("poster", item.poster_path)
                    idMovieData.putString("overview", item.overview)
                    idMovieData.putBoolean("fav", item.isFavourite)
                    idMovieData.putBoolean("adultcheck", item.adult)
                    idMovieData.putBoolean("remindercheck", item.isReminder)
                    navController.navigate(R.id.detail, idMovieData)
                }
            }
        }
    }
}