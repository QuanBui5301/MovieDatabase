package com.example.movieapp.database

import java.util.*

data class FavouriteData (
    var id : Int = 0,
    var title : String = "",
    var date : String = "",
    var rate : String = "",
    var poster : String = "",
    var overview : String = "",
    var adult : String = ""
        ) {
    companion object {
        fun getRandomId() : Int {
            val random = Random()
            return  random.nextInt(100)
        }
    }
}