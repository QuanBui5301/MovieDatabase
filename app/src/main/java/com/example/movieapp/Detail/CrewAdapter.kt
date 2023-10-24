package com.example.movieapp.Detail

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import coil.load
import coil.size.Scale
import com.example.movieapp.R
import com.example.movieapp.databinding.CastcrewItemBinding
import com.example.movieapp.response.CastCrewResponse
import com.example.movieapp.utils.Constants

class CrewAdapter : RecyclerView.Adapter<CrewAdapter.ViewHolder>() {
    private lateinit var binding: CastcrewItemBinding
    private lateinit var context: Context
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        binding = CastcrewItemBinding.inflate(inflater, parent, false)
        context = parent.context
        return ViewHolder()
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(differ.currentList[position])
    }

    override fun getItemCount(): Int {
        return differ.currentList.size
    }

    override fun getItemViewType(position: Int): Int {
        return position
    }
    inner class ViewHolder : RecyclerView.ViewHolder(binding.root) {
        @SuppressLint("SetTextI18n")
        fun bind(item: CastCrewResponse.Crew) {
            binding.apply {
                val moviePosterURL = Constants.POSTER_BASE_URL + item?.profile_path
                imgCast.load(moviePosterURL){
                    crossfade(true)
                    placeholder(R.drawable.ic_baseline_person_25)
                    scale(Scale.FILL)
                }
                tvNameCast.text = item.name
                tvCastCharacter.text = item.job
            }
        }
    }
    private val differCallback = object : DiffUtil.ItemCallback<CastCrewResponse.Crew>() {
        override fun areItemsTheSame(oldItem: CastCrewResponse.Crew, newItem: CastCrewResponse.Crew): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: CastCrewResponse.Crew, newItem: CastCrewResponse.Crew): Boolean {
            return oldItem == newItem
        }
    }

    val differ = AsyncListDiffer(this, differCallback)
}