package com.example.movieapp.Movies


import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import coil.load
import coil.size.Scale
import com.example.movieapp.HomeActivity.Companion.navController
import com.example.movieapp.R
import com.example.movieapp.databinding.MovieItemGridBinding
import com.example.movieapp.response.MovieListResponse
import com.example.movieapp.utils.Constants.POSTER_BASE_URL

class MoviesAdapterGrid : RecyclerView.Adapter<MoviesAdapterGrid.ViewHolder>() {

    private lateinit var binding: MovieItemGridBinding
    private lateinit var context: Context

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        binding = MovieItemGridBinding.inflate(inflater, parent, false)
        context = parent.context
        return ViewHolder()
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(MoviesAdapterList.sortByList[position])
    }

    override fun getItemCount(): Int {
        return MoviesAdapterList.sortByList.size
    }

    override fun getItemViewType(position: Int): Int {
        return position
    }

    inner class ViewHolder : RecyclerView.ViewHolder(binding.root) {

        @SuppressLint("SetTextI18n")
        fun bind(item: MovieListResponse.Result) {
            binding.apply {
                tvTittleGrid.text = item.title
                val moviePosterURL = POSTER_BASE_URL + item?.poster_path
                imgMovieGrid.load(moviePosterURL){
                    crossfade(true)
                    placeholder(R.drawable.poster_placeholder)
                    scale(Scale.FILL)
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
                    idMovieData.putBoolean("remindercheck", item.isReminder)
                    idMovieData.putBoolean("adultcheck", item.adult)
                    navController.navigate(R.id.detail, idMovieData)
                }
            }
        }
    }

    private val differCallback = object : DiffUtil.ItemCallback<MovieListResponse.Result>() {
        override fun areItemsTheSame(oldItem: MovieListResponse.Result, newItem: MovieListResponse.Result): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: MovieListResponse.Result, newItem: MovieListResponse.Result): Boolean {
            return oldItem == newItem
        }
    }

    val differ = AsyncListDiffer(this, differCallback)
}