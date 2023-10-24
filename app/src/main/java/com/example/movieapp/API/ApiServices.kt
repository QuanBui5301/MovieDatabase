package com.example.movieapp.API

import com.example.movieapp.response.CastCrewResponse
import com.example.movieapp.response.DetailListResponse
import com.example.movieapp.response.MovieListResponse
import retrofit2.Call
import retrofit2.http.*

interface ApiServices {

    @GET("movie/{typeMovie}")
    fun getPopularMovie(@Path("typeMovie") typeListMovie:String, @Query("page") page: Int): Call<MovieListResponse>

    @GET("movie/{movie_id}")
    fun getMovieDetails(@Path("movie_id") id: Int): Call<DetailListResponse>

    @GET("movie/{movie_id}/credits")
    fun getCastCrew(@Path("movie_id") id: Int): Call<CastCrewResponse>

    @GET("search/movie")
    fun searchMovie(@Query("query") queryString: String): Call<MovieListResponse>
}