package com.example.movieapp.Detail

import android.app.*
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import coil.load
import coil.size.Scale
import com.example.movieapp.*
import com.example.movieapp.API.ApiClient
import com.example.movieapp.API.ApiServices
import com.example.movieapp.HomeActivity.Companion.adapterrem
import com.example.movieapp.database.FavouriteData
import com.example.movieapp.database.ReminderData
import com.example.movieapp.database.SQLiteHelper
import com.example.movieapp.database.SQLiteReminder
import com.example.movieapp.databinding.MovieDetailesBinding
import com.example.movieapp.response.CastCrewResponse
import com.example.movieapp.response.DetailListResponse
import com.example.movieapp.utils.Constants
import kotlinx.android.synthetic.main.fragment_movies.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.*

class Detail : Fragment() {
    lateinit var binding: MovieDetailesBinding
    private val api: ApiServices by lazy {
        ApiClient().getClient().create(ApiServices::class.java)
    }
    private val castcrewAdapter by lazy { CastCrewAdapter() }
    private val crewAdapter by lazy { CrewAdapter() }
    private lateinit var sqLiteHelper: SQLiteHelper
    private lateinit var sqLiteReminder: SQLiteReminder
    private var movieId : Int = 0
    private lateinit var movieTitle : String
    private lateinit var movieDate : String
    private lateinit var movieRate : String
    private lateinit var moviePoster : String
    private lateinit var timeText : TextView
    private var mSaveDay = 0
    private var mSaveMonth = 0
    private var mSaveYear = 0
    private var mSaveHour = 0
    private var mSaveMinute = 0
    private var saveTime : String = ""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {

        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = MovieDetailesBinding.inflate(layoutInflater)
        val view = binding.getRoot()
        HomeActivity.toggle.syncState()
        (activity as HomeActivity?)?.getSupportActionBar()?.setDisplayHomeAsUpEnabled(true)
        movieId = arguments?.getInt("id", 1)!!
        var isfavourite : Boolean = arguments?.getBoolean("fav", false)!!
        movieTitle = arguments?.getString("title")!!
        movieDate = arguments?.getString("date")!!
        movieRate = arguments?.getString("rate")!!
        moviePoster = arguments?.getString("poster")!!
        timeText = view.findViewById(R.id.textTime)
        val movieOverview : String = arguments?.getString("overview")!!
        val movieAdult : Boolean = arguments?.getBoolean("adultcheck", false)!!
        var movieReminder : Boolean = arguments?.getBoolean("remindercheck", false)!!
        (activity as HomeActivity?)?.getSupportActionBar()?.setTitle("$movieTitle")
        sqLiteHelper = SQLiteHelper(view.context)
        sqLiteReminder = SQLiteReminder(view.context)
        binding.apply {
            //show loading
            prgBarMoviesDetail.visibility = View.VISIBLE
            //Call movies api
            val callMoviesApi = api.getMovieDetails(movieId)
            callMoviesApi.enqueue(object : Callback<DetailListResponse> {
                override fun onResponse(call: Call<DetailListResponse>, response: Response<DetailListResponse>) {
                    Log.e("onFailure", "Err : ${response.code()}")
                    prgBarMoviesDetail.visibility = View.GONE
                    when (response.code()) {
                        in 200..299 -> {

                            response.body()?.let { itBody ->
                                val moviePosterURL = Constants.POSTER_BASE_URL + itBody.poster_path
                                imgMovieDetail.load(moviePosterURL) {
                                    crossfade(true)
                                    placeholder(R.drawable.poster_placeholder)
                                    scale(Scale.FILL)

                                }
                                imgMovieBackDetail.load(moviePosterURL) {
                                    crossfade(true)
                                    placeholder(R.drawable.poster_placeholder)
                                    scale(Scale.FILL)

                                }

                                tvMovieTitleDetail.text = itBody.title
                                tvMovieTagLineDetail.text = itBody.tagline
                                tvMovieDateRelease.text = itBody.release_date
                                tvMovieRatingDetail.text = itBody.vote_average.toString()
                                tvMovieRuntimeDetail.text = itBody.runtime.toString()
                                tvMovieBudgetDetail.text = itBody.budget.toString()
                                tvMovieRevenueDetail.text = itBody.revenue.toString()
                                tvMovieOverviewDetail.text = itBody.overview
                                if (movieAdult) {
                                    imageViewAdultDetail.visibility = View.VISIBLE
                                }
                                else if (!movieAdult) { imageViewAdultDetail.visibility = View.GONE }
                                val favlist = sqLiteHelper.getAllFavourite()
                                if (favlist.size > 0) {
                                    for (i in 0..favlist.size-1) {
                                        if (movieId == favlist[i].id) {
                                            isfavourite = true
                                            imageViewFavDetail.setImageResource(R.drawable.ic_baseline_favorite_24)
                                        }
                                    }
                                }
                                if (isfavourite) {
                                    imageViewFavDetail.setImageResource(R.drawable.ic_baseline_favorite_24)
                                }
                                else {
                                    imageViewFavDetail.setImageResource(R.drawable.ic_baseline_favorite_border_24)
                                }
                                imageViewFavDetail.setOnClickListener {
                                    if (!isfavourite) {
                                        imageViewFavDetail.setImageResource(R.drawable.ic_baseline_favorite_24)
                                        val data = FavouriteData(id = movieId, title = movieTitle, date = movieDate, rate = movieRate, poster = moviePoster, overview = movieOverview, adult = movieAdult.toString())
                                        val status = sqLiteHelper.insertFavourite(data)
                                        var number = sqLiteHelper.getAllFavourite().size
                                        HomeActivity.textfavnum.text = number.toString()
                                        if (status > -1) {
                                            Toast.makeText(context, "Favourite Add", Toast.LENGTH_SHORT).show()
                                        }
                                        else Toast.makeText(context, "Error", Toast.LENGTH_SHORT).show()
                                        isfavourite = true
                                    }
                                    else {
                                        imageViewFavDetail.setImageResource(R.drawable.ic_baseline_favorite_border_24)
                                        sqLiteHelper.deleteFavourite(movieId)
                                        var number = sqLiteHelper.getAllFavourite().size
                                        HomeActivity.textfavnum.text = number.toString()
                                        Toast.makeText(context, "Favourite Delete", Toast.LENGTH_SHORT).show()
                                        isfavourite = false
                                    }
                                }
                                var reminderList = sqLiteReminder.getAllReminder()
                                if (reminderList.size > 0) {
                                    for (i in 0..reminderList.size-1) {
                                        if (movieId == reminderList[i].id) {
                                            imageReminder.setImageResource(R.drawable.ic_baseline_notifications_active_24)
                                            movieReminder = true
                                            textTime.text = reminderList[i].day
                                        }
                                    }
                                }
                                if (movieReminder) {
                                    imageReminder.setImageResource(R.drawable.ic_baseline_notifications_active_24)
                                }
                                else {
                                    imageReminder.setImageResource(R.drawable.ic_baseline_notifications_24)
                                }
                                imageReminder.setOnClickListener {
                                    if (!movieReminder) {
                                        imageReminder.setImageResource(R.drawable.ic_baseline_notifications_active_24)
                                        movieReminder = true
                                        createReminder()
                                    }
                                    else {
                                        val buider = AlertDialog.Builder(context)
                                        buider.setMessage("Do you want update or delete reminder?")
                                        buider.setCancelable(true)
                                        buider.setPositiveButton("Delete") { dialog, _ ->
                                            imageReminder.setImageResource(R.drawable.ic_baseline_notifications_24)
                                            timeText.text = "----/--/-- --:--"
                                            cancelNotification(movieId, context!!)
                                            sqLiteReminder.deleteReminder(movieId)
                                            var remlist = sqLiteReminder.getAllReminder()
                                            adapterrem?.addItems(remlist)
                                            Toast.makeText(context, "Reminder Delete", Toast.LENGTH_SHORT).show()
                                            movieReminder = false
                                            dialog.dismiss()
                                        }
                                        buider.setNegativeButton("Update") { dialog, _ ->
                                            sqLiteReminder.deleteReminder(movieId)
                                            imageReminder.setImageResource(R.drawable.ic_baseline_notifications_active_24)
                                            movieReminder = true
                                            createReminder()
                                            dialog.dismiss()
                                        }
                                        val alert = buider.create()
                                        alert.show()

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

                override fun onFailure(call: Call<DetailListResponse>, t: Throwable) {
                    prgBarMoviesDetail.visibility = View.GONE
                    Log.e("onFailure", "Err : ${t.message}")
                }
            })
            val callCastApi = api.getCastCrew(movieId)
            callCastApi.enqueue(object : Callback<CastCrewResponse> {
                override fun onResponse(
                    call: Call<CastCrewResponse>,
                    response: Response<CastCrewResponse>
                ) {
                    response.body()?.let { itBody ->
                        itBody.cast.let { itData ->
                            if (itData.isNotEmpty()) {
                                castcrewAdapter.differ.submitList(itData)
                                //Recycler
                                rlCastCrew.apply {
                                    layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
                                    adapter = castcrewAdapter
                                }
                            }
                        }
                    }
                }

                override fun onFailure(call: Call<CastCrewResponse>, t: Throwable) {

                }

            })
            val callCrewApi = api.getCastCrew(movieId)
            callCrewApi.enqueue(object : Callback<CastCrewResponse> {
                override fun onResponse(
                    call: Call<CastCrewResponse>,
                    response: Response<CastCrewResponse>
                ) {
                    response.body()?.let { itBody ->
                        itBody.crew.let { itData ->
                            if (itData.isNotEmpty()) {
                                crewAdapter.differ.submitList(itData)
                                //Recycler
                                rlCrew.apply {
                                    layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
                                    adapter = crewAdapter
                                }
                            }
                        }
                    }
                }

                override fun onFailure(call: Call<CastCrewResponse>, t: Throwable) {

                }

            })
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
                timeText.text = timeHour
                createNotification(time)
                var remlist = sqLiteReminder.getAllReminder()
                adapterrem?.addItems(remlist)
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
        val pendingIntent = PendingIntent.getBroadcast(context, movieId, intent, PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT)
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        alarmManager.cancel(pendingIntent)
    }
}