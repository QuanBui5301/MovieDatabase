package com.example.movieapp.Movies

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.preference.PreferenceManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.movieapp.API.ApiClient
import com.example.movieapp.API.ApiServices
import com.example.movieapp.HomeActivity
import com.example.movieapp.databinding.FragmentSearchBinding
import com.example.movieapp.response.MovieListResponse
import kotlinx.android.synthetic.main.fragment_search.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class Search : Fragment() {
    private lateinit var binding: FragmentSearchBinding
    private val moviesAdapter by lazy { MoviesAdapterList() }
    private val api: ApiServices by lazy {
        ApiClient().getClient().create(ApiServices::class.java)
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentSearchBinding.inflate(layoutInflater)
        val view : View = binding.getRoot()
        (activity as HomeActivity?)?.getSupportActionBar()?.setTitle("Search")
        Movies.prefs = PreferenceManager.getDefaultSharedPreferences(view.context)
        Movies.rateValue = Movies.prefs.getString("rate", "5")!!
        Movies.releaseValue = Movies.prefs.getString("release", "2015")!!
        Movies.sortByValue = Movies.prefs.getString("sort", "release_date")!!
        HomeActivity.toggle.syncState()
        (activity as HomeActivity?)?.getSupportActionBar()?.setDisplayHomeAsUpEnabled(true)
        binding.apply {
            prgBarMoviesSearch.visibility = View.GONE
            searchIcon.setOnClickListener() {
                if (searchTitle.text.toString() != "") {
                    prgBarMoviesSearch.visibility = View.VISIBLE
                    ListViewType(searchTitle.text.toString())
                    searchTitle.setText("")
                }
            }
        }
        return view
    }
    private fun ListViewType(title : String) {
        //Call movies api
        val callMoviesApi = api.searchMovie(title)
        callMoviesApi.enqueue(object : Callback<MovieListResponse> {
            override fun onResponse(call: Call<MovieListResponse>, response: Response<MovieListResponse>) {
                Log.e("onFailure", "Err : ${response.code()}")

                prgBarMoviesSearch.visibility = View.GONE
                when (response.code()) {
                    in 200..299 -> {
                        response.body()?.let { itBody ->
                            itBody.results.let { itData ->
                                if (itData.isNotEmpty()) {
                                    MoviesAdapterList.sortByList = ArrayList()
                                    for (i in 0..itData.size-1) {
                                        if (Movies.sortByValue == "rating") {
                                            val converRate: Int? = try {
                                                Movies.rateValue.toInt()
                                            } catch(e:Exception) {
                                                null
                                            }
                                            if (converRate != null) {
                                                if (itData[i].vote_average >= converRate) {
                                                    MoviesAdapterList.sortByList.add(itData[i])
                                                }
                                            }
                                        }
                                        if (Movies.sortByValue == "release_date") {
                                            val convertYear: Int? = if (Movies.releaseValue.length > 3) {
                                                Movies.releaseValue.substring(0, 4).trim().toIntOrNull()
                                            } else {
                                                null
                                            }
                                            if (convertYear != null) {
                                                if (itData[i].release_date.substring(0, 4).trim() == Movies.releaseValue) {
                                                    MoviesAdapterList.sortByList.add(itData[i])
                                                }
                                            }
                                        }
                                        if (Movies.sortByValue == "default") {
                                            MoviesAdapterList.sortByList.add(itData[i])
                                        }
                                    }
                                    //Recycler
                                    rlMoviesSearch.apply {
                                        layoutManager = LinearLayoutManager(context)
                                        adapter = moviesAdapter
                                    }
                                }
                            }
                        }
                    }
                    in 300..399 -> {
                        Log.d("Response Code", " Redirection messages : ${response.code()}")
                    }
                    in 400..499 -> {
                        Log.d("Response Code", " Client error responses : ${response.code()}")
                    }
                    in 500..599 -> {
                        Log.d("Response Code", " Server error responses : ${response.code()}")
                    }
                }
            }

            override fun onFailure(call: Call<MovieListResponse>, t: Throwable) {
                prgBarMoviesSearch.visibility = View.GONE
                Log.e("onFailure", "Err : ${t.message}")
            }
        })
    }
}