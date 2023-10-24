package com.example.movieapp.database

import android.annotation.SuppressLint
import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class SQLiteReminder(context: Context) : SQLiteOpenHelper(context, DATEBASE_NAME_REMINDER, null, DATEBASE_VERSION_REMINDER){

    companion object {
        private const val DATEBASE_VERSION_REMINDER = 2
        private const val DATEBASE_NAME_REMINDER = "reminder.list"
        private const val TBL_REMINDER = "tbl_reminder"
        private const val ID_REMINDER = "id"
        private const val TITLE_REMINDER = "title"
        private const val DATE_REMINDER = "date"
        private const val RATE_REMINDER = "rate"
        private const val POSTER_REMINDER = "poster"
        private const val OVERVIEW_REMINDER = "overview"
        private const val ADULT_REMINDER = "adult"
        private const val DAY_REMINDER = "day"
        private const val TIME_REMINDER = "time"
    }

    override fun onCreate(db: SQLiteDatabase?) {
            val createTbl = ("CREATE TABLE " + TBL_REMINDER + "(" + ID_REMINDER + " INTEGER," + TITLE_REMINDER + " TEXT," + DATE_REMINDER + " TEXT," + RATE_REMINDER + " TEXT," + POSTER_REMINDER + " TEXT," + OVERVIEW_REMINDER + " TEXT," + ADULT_REMINDER + " TEXT," + DAY_REMINDER + " TEXT," + TIME_REMINDER + " TEXT" + ")")
        db?.execSQL(createTbl)
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        db!!.execSQL("DROP TABLE IF EXISTS $TBL_REMINDER")
        onCreate(db)
    }

    fun insertReminder(data : ReminderData) : Long {
        val db = this.writableDatabase
        val contentValues = ContentValues()
        contentValues.put(ID_REMINDER, data.id)
        contentValues.put(TITLE_REMINDER, data.title)
        contentValues.put(DATE_REMINDER, data.date)
        contentValues.put(RATE_REMINDER, data.rate)
        contentValues.put(POSTER_REMINDER, data.poster)
        contentValues.put(OVERVIEW_REMINDER, data.overview)
        contentValues.put(ADULT_REMINDER, data.adult)
        contentValues.put(DAY_REMINDER, data.day)
        contentValues.put(TIME_REMINDER, data.time)
        val success = db.insert(TBL_REMINDER, null, contentValues)
        db.close()
        return success
    }

    @SuppressLint("Range")
    fun getAllReminder() : ArrayList<ReminderData> {
        val dataList : ArrayList<ReminderData> = ArrayList()
        val selectQuery = "SELECT * FROM $TBL_REMINDER"
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
        var day : String
        var time : String
        if (cursor.moveToFirst()) {
            do {
                id = cursor.getInt(cursor.getColumnIndex("id"))
                title = cursor.getString(cursor.getColumnIndex("title"))
                date = cursor.getString(cursor.getColumnIndex("date"))
                rate = cursor.getString(cursor.getColumnIndex("rate"))
                poster = cursor.getString(cursor.getColumnIndex("poster"))
                overview = cursor.getString(cursor.getColumnIndex("overview"))
                adult = cursor.getString(cursor.getColumnIndex("adult"))
                day = cursor.getString(cursor.getColumnIndex("day"))
                time = cursor.getString(cursor.getColumnIndex("time"))
                val data = ReminderData(id = id, title = title, date = date, rate = rate, poster = poster, overview = overview, adult = adult, day = day, time = time)
                dataList.add(data)
            } while (cursor.moveToNext())
        }
        return dataList
    }

    fun deleteReminder(id:Int) : Int {
        val db = this.writableDatabase
        val contentValues = ContentValues()
        contentValues.put(TITLE_REMINDER, id)
        val success = db.delete(TBL_REMINDER, "id=$id", null)
        db.close()
        return success
    }
}