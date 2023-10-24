package com.example.movieapp.Movies

import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.view.*
import androidx.fragment.app.Fragment
import androidx.preference.PreferenceManager
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.movieapp.API.ApiClient
import com.example.movieapp.API.ApiServices
import com.example.movieapp.HomeActivity
import com.example.movieapp.HomeActivity.Companion.navController
import com.example.movieapp.HomeActivity.Companion.page
import com.example.movieapp.R
import com.example.movieapp.Movies.MoviesAdapterList.Companion.sortByList
import com.example.movieapp.databinding.FragmentMoviesBinding
import com.example.movieapp.response.MovieListResponse
import kotlinx.android.synthetic.main.fragment_movies.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class Movies() : Fragment(), View.OnClickListener {
    private lateinit var binding: FragmentMoviesBinding
    private val moviesAdapter by lazy { MoviesAdapterList() }
    private val moviesAdapterGrid by lazy { MoviesAdapterGrid() }
    private lateinit var movieSort : MutableList<MovieListResponse.Result>
    companion object {
        lateinit var prefs : SharedPreferences
        lateinit var rateValue : String
        lateinit var releaseValue : String
        lateinit var sortByValue : String
        lateinit var categoryValue : String
    }

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
    ): View {
        binding = FragmentMoviesBinding.inflate(layoutInflater)
        val view : View = binding.getRoot()
        prefs = PreferenceManager.getDefaultSharedPreferences(view.context)
        rateValue = prefs.getString("rate", "5")!!
        releaseValue = prefs.getString("release", "2015")!!
        sortByValue = prefs.getString("sort", "release_date")!!
        categoryValue = prefs.getString("category", "popular")!!
        val CategoryTitle = when(categoryValue) {
            "popular" -> "Popular"
            "top_rated" -> "Top Rated"
            "upcoming" -> "Upcoming"
            "now_playing" -> "Now Playing"
            else -> "Movie"
        }
        (activity as HomeActivity?)?.getSupportActionBar()?.setTitle("$CategoryTitle")
        var typeScreen = arguments?.getInt("list", 0)
        var numberPage = arguments?.getInt("page", 0)
        if (numberPage == null) {
            page = 1
        }
        else {
            page = numberPage
        }
        //InitViews
        if (typeScreen == null) {
            binding.apply {
                prgBarMovies.visibility = View.VISIBLE
                searchMovie.setOnClickListener() {
                    navController.navigate(R.id.search)
                }
                ListViewType(page)
                swipeLayout.setOnRefreshListener {
                    var viewType : Bundle = Bundle()
                    if (arguments?.getInt("list", 0) != null) {
                        viewType.putInt("page", page)
                        viewType.putInt("list", arguments?.getInt("list", 0)!!)
                        navController.navigate(R.id.movies, viewType)
                    }
                    else {
                        viewType.putInt("list", 1)
                        viewType.putInt("page", page)
                        navController.navigate(R.id.movies, viewType)
                    }
                    HomeActivity.toggle.syncState()
                    (activity as HomeActivity?)?.getSupportActionBar()?.setDisplayHomeAsUpEnabled(true)
                }
                nextbtn.setOnClickListener() {
                    var pageNumber : Bundle = Bundle()
                    pageNumber.putInt("page", (page+1))
                    pageNumber.putInt("list", 1)
                    navController.navigate(R.id.movies, pageNumber)
                    HomeActivity.toggle.syncState()
                    (activity as HomeActivity?)?.getSupportActionBar()?.setDisplayHomeAsUpEnabled(true)
                }
                if (page == 1) {
                    backbtn.setImageResource(R.drawable.ic_baseline_arrow_circle_left_25)
                }
                else {
                    backbtn.setImageResource(R.drawable.ic_baseline_arrow_circle_left_24)
                    backbtn.setOnClickListener() {
                        var pageNumber: Bundle = Bundle()
                        pageNumber.putInt("page", (page - 1))
                        pageNumber.putInt("list", arguments?.getInt("list", 0)!!)
                        navController.navigate(R.id.movies, pageNumber)
                        HomeActivity.toggle.syncState()
                        (activity as HomeActivity?)?.getSupportActionBar()?.setDisplayHomeAsUpEnabled(true)
                    }
                }
            }
        }
        else {
            if (typeScreen % 2 == 0) {
                binding.apply {
                    prgBarMovies.visibility = View.VISIBLE
                    searchMovie.setOnClickListener() {
                        navController.navigate(R.id.search)
                    }
                    GridViewType(page)
                    swipeLayout.setOnRefreshListener {
                        var viewType : Bundle = Bundle()
                        viewType.putInt("page", page)
                        viewType.putInt("list", arguments?.getInt("list", 0)!!)
                        navController.navigate(R.id.movies, viewType)
                        HomeActivity.toggle.syncState()
                        (activity as HomeActivity?)?.getSupportActionBar()?.setDisplayHomeAsUpEnabled(true)
                    }
                    nextbtn.setOnClickListener() {
                        var pageNumber : Bundle = Bundle()
                        pageNumber.putInt("page", (page+1))
                        pageNumber.putInt("list", arguments?.getInt("list", 0)!!)
                        navController.navigate(R.id.movies, pageNumber)
                        HomeActivity.toggle.syncState()
                        (activity as HomeActivity?)?.getSupportActionBar()?.setDisplayHomeAsUpEnabled(true)
                    }
                    if (page == 1) {
                        backbtn.setImageResource(R.drawable.ic_baseline_arrow_circle_left_25)
                    }
                    else {
                        backbtn.setImageResource(R.drawable.ic_baseline_arrow_circle_left_24)
                        backbtn.setOnClickListener() {
                            var pageNumber: Bundle = Bundle()
                            pageNumber.putInt("page", (page - 1))
                            pageNumber.putInt("list", arguments?.getInt("list", 0)!!)
                            navController.navigate(R.id.movies, pageNumber)
                            HomeActivity.toggle.syncState()
                            (activity as HomeActivity?)?.getSupportActionBar()?.setDisplayHomeAsUpEnabled(true)
                        }
                    }
                }
            }
            if (typeScreen % 2 != 0) {
                binding.apply {
                    prgBarMovies.visibility = View.VISIBLE
                    searchMovie.setOnClickListener() {
                        navController.navigate(R.id.search)
                    }
                    ListViewType(page)
                    swipeLayout.setOnRefreshListener {
                        var viewType : Bundle = Bundle()
                        viewType.putInt("page", page)
                        viewType.putInt("list", arguments?.getInt("list", 0)!!)
                        navController.navigate(R.id.movies, viewType)
                        HomeActivity.toggle.syncState()
                        (activity as HomeActivity?)?.getSupportActionBar()?.setDisplayHomeAsUpEnabled(true)
                    }
                    nextbtn.setOnClickListener() {
                        var pageNumber : Bundle = Bundle()
                        pageNumber.putInt("page", (page+1))
                        pageNumber.putInt("list", arguments?.getInt("list", 0)!!)
                        navController.navigate(R.id.movies, pageNumber)
                        HomeActivity.toggle.syncState()
                        (activity as HomeActivity?)?.getSupportActionBar()?.setDisplayHomeAsUpEnabled(true)
                    }
                    if (page == 1) {
                        backbtn.setImageResource(R.drawable.ic_baseline_arrow_circle_left_25)
                    }
                    else {
                        backbtn.setImageResource(R.drawable.ic_baseline_arrow_circle_left_24)
                        backbtn.setOnClickListener() {
                            var pageNumber: Bundle = Bundle()
                            pageNumber.putInt("page", (page - 1))
                            pageNumber.putInt("list", arguments?.getInt("list", 0)!!)
                            navController.navigate(R.id.movies, pageNumber)
                            HomeActivity.toggle.syncState()
                            (activity as HomeActivity?)?.getSupportActionBar()?.setDisplayHomeAsUpEnabled(true)
                        }
                    }
                }
            }
        }
        return view
    }
    private fun GridViewType(page : Int) {
        //Call movies api
        val callMoviesApi = api.getPopularMovie(categoryValue, page)
        callMoviesApi.enqueue(object : Callback<MovieListResponse> {
            override fun onResponse(call: Call<MovieListResponse>, response: Response<MovieListResponse>) {
                Log.e("onFailure", "Err : ${response.code()}")

                prgBarMovies.visibility = View.GONE
                when (response.code()) {
                    in 200..299 -> {
                        response.body()?.let { itBody ->
                            itBody.results.let { itData ->
                                if (itData.isNotEmpty()) {
                                    sortByList = ArrayList()
                                    for (i in 0..itData.size-1) {
                                        if (sortByValue == "rating") {
                                            val converRate: Int? = try {
                                                rateValue.toInt()
                                            } catch(e:Exception) {
                                                null
                                            }
                                            if (converRate != null) {
                                                if (itData[i].vote_average >= converRate) {
                                                    sortByList.add(itData[i])
                                                }
                                            }
                                        }
                                        if (sortByValue == "release_date") {
                                            val convertYear: Int? = if (releaseValue.length > 3) {
                                                releaseValue.substring(0, 4).trim().toIntOrNull()
                                            } else {
                                                null
                                            }
                                            if (convertYear != null) {
                                                if (itData[i].release_date.substring(0, 4).trim() == releaseValue) {
                                                    sortByList.add(itData[i])
                                                }
                                            }
                                        }
                                        if (sortByValue == "default") {
                                            sortByList.add(itData[i])
                                        }
                                    }
                                    //Recycler
                                    rlMovies.apply {
                                        layoutManager = GridLayoutManager(context, 2)
                                        adapter = moviesAdapterGrid
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
                prgBarMovies.visibility = View.GONE
                Log.e("onFailure", "Err : ${t.message}")
            }
        })
    }
    private fun ListViewType(page : Int) {
        //Call movies api
        val callMoviesApi = api.getPopularMovie(categoryValue, page)
        callMoviesApi.enqueue(object : Callback<MovieListResponse> {
            override fun onResponse(call: Call<MovieListResponse>, response: Response<MovieListResponse>) {
                Log.e("onFailure", "Err : ${response.code()}")

                prgBarMovies.visibility = View.GONE
                when (response.code()) {
                    in 200..299 -> {
                        response.body()?.let { itBody ->
                            itBody.results.let { itData ->
                                if (itData.isNotEmpty()) {
                                    sortByList = ArrayList()
                                    for (i in 0..itData.size-1) {
                                        if (sortByValue == "rating") {
                                            val converRate: Int? = try {
                                                rateValue.toInt()
                                            } catch(e:Exception) {
                                                null
                                            }
                                            if (converRate != null) {
                                                if (itData[i].vote_average >= converRate) {
                                                    sortByList.add(itData[i])
                                                }
                                            }
                                        }
                                        if (sortByValue == "release_date") {
                                            val convertYear: Int? = if (releaseValue.length > 3) {
                                                releaseValue.substring(0, 4).trim().toIntOrNull()
                                            } else {
                                                null
                                            }
                                            if (convertYear != null) {
                                                if (itData[i].release_date.substring(0, 4).trim() == releaseValue) {
                                                    sortByList.add(itData[i])
                                                }
                                            }
                                        }
                                        if (sortByValue == "default") {
                                            sortByList.add(itData[i])
                                        }
                                    }
                                    //Recycler
                                    rlMovies.apply {
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
                prgBarMovies.visibility = View.GONE
                Log.e("onFailure", "Err : ${t.message}")
            }
        })
    }
    override fun onClick(v: View?) {

    }
}