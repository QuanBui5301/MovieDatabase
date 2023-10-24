package com.example.movieapp.database

import android.annotation.SuppressLint
import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class SQLiteHelper(context: Context) : SQLiteOpenHelper(context, DATEBASE_NAME, null, DATEBASE_VERSION){

    companion object {
        private const val DATEBASE_VERSION = 1
        private const val DATEBASE_NAME = "favourite.list"
        private const val TBL_FAVOURITE = "tbl_favourite"
        private const val ID = "id"
        private const val TITLE = "title"
        private const val DATE = "date"
        private const val RATE = "rate"
        private const val POSTER = "poster"
        private const val OVERVIEW = "overview"
        private const val ADULT = "adult"
    }

    override fun onCreate(db: SQLiteDatabase?) {
            val createTbl = ("CREATE TABLE " + TBL_FAVOURITE + "(" + ID + " INTEGER PRIMARY KEY," + TITLE + " TEXT," + DATE + " TEXT," + RATE + " TEXT," + POSTER + " TEXT," + OVERVIEW + " TEXT," + ADULT + " TEXT" + ")")
        db?.execSQL(createTbl)
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        db!!.execSQL("DROP TABLE IF EXISTS $TBL_FAVOURITE")
        onCreate(db)
    }

    fun insertFavourite(data : FavouriteData) : Long {
        val db = this.writableDatabase
        val contentValues = ContentValues()
        contentValues.put(ID, data.id)
        contentValues.put(TITLE, data.title)
        contentValues.put(DATE, data.date)
        contentValues.put(RATE, data.rate)
        contentValues.put(POSTER, data.poster)
        contentValues.put(OVERVIEW, data.overview)
        contentValues.put(ADULT, data.adult)
        val success = db.insert(TBL_FAVOURITE, null, contentValues)
        db.close()
        return success
    }

    @SuppressLint("Range")
    fun getAllFavourite() : ArrayList<FavouriteData> {
        val dataList : ArrayList<FavouriteData> = ArrayList()
        val selectQuery = "SELECT * FROM $TBL_FAVOURITE"
        val db = this.readableDatabase
        val cursor: Cursor?

        try {
            cursor = db.rawQuery(selectQuery, null)
        } catch (e : Exception) {
            e.printStackTrace()
            db.execSQL(selectQuery)
            return ArrayList()
        }
        var id: Int
        var title: String
        var date : String
        var rate : String
        var poster : String
        var overview : String
        var adult : String
        if (cursor.moveToFirst()) {
            do {
                id = cursor.getInt(cursor.getColumnIndex("id"))
                title = cursor.getString(cursor.getColumnIndex("title"))
                date = cursor.getString(cursor.getColumnIndex("date"))
                rate = cursor.getString(cursor.getColumnIndex("rate"))
                poster = cursor.getString(cursor.getColumnIndex("poster"))
                overview = cursor.getString(cursor.getColumnIndex("overview"))
                adult = cursor.getString(cursor.getColumnIndex("adult"))
                val data = FavouriteData(id = id, title = title, date = date, rate = rate, poster = poster, overview = overview, adult = adult)
                dataList.add(data)
            } while (cursor.moveToNext())
        }
        return dataList
    }

    fun deleteFavourite(id:Int) : Int {
        val db = this.writableDatabase
        val contentValues = ContentValues()
        contentValues.put(TITLE, id)
        val success = db.delete(TBL_FAVOURITE, "id=$id", null)
        db.close()
        return success
    }
}