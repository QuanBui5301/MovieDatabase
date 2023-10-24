package com.example.movieapp.Favourite

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.SearchView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.movieapp.HomeActivity
import com.example.movieapp.R
import com.example.movieapp.database.FavouriteData
import com.example.movieapp.database.SQLiteHelper
import java.util.*
import kotlin.collections.ArrayList

class Favourite : Fragment() {
    private lateinit var sqLiteHelper: SQLiteHelper
    private lateinit var recyclerView: RecyclerView
    private lateinit var searchView: SearchView
    private lateinit var tempArrayList: ArrayList<FavouriteData>
    private var adapterfav: FavouriteAdapter? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {

        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_favourite, container, false)
        recyclerView = view.findViewById(R.id.rlFavourite)
        searchView = view.findViewById(R.id.searchView)
        tempArrayList = arrayListOf<FavouriteData>()
        recyclerView.layoutManager = LinearLayoutManager(context)
        adapterfav = FavouriteAdapter()
        recyclerView.adapter = adapterfav
        sqLiteHelper = SQLiteHelper(view.context)
        var favlist = sqLiteHelper.getAllFavourite()
        HomeActivity.toggle.syncState()
        (activity as HomeActivity?)?.getSupportActionBar()?.setDisplayHomeAsUpEnabled(true)
        adapterfav?.addItems(favlist)
        adapterfav?.setOnClickDeleteItem {
            unFavourite(it.id)
        }
        searchView.clearFocus()
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                TODO("Not yet implement")
            }

            @SuppressLint("NotifyDataSetChanged")
            override fun onQueryTextChange(newText: String?): Boolean {
                tempArrayList.clear()
                val searchText = newText!!.toLowerCase(Locale.getDefault())
                if (searchText.isNotEmpty()) {
                    favlist.forEach {
                        if (it.title.toLowerCase(Locale.getDefault()).contains(searchText)) {
                            tempArrayList.add(it)
                        }
                    }
                    adapterfav?.addItems(tempArrayList)
                    recyclerView.adapter!!.notifyDataSetChanged()
                    adapterfav?.addItems(tempArrayList)
                } else {
                    tempArrayList.clear()
                    favlist = sqLiteHelper.getAllFavourite()
                    adapterfav?.addItems(favlist)
                }
                return false
            }
        })
        return view
    }
    @SuppressLint("NotifyDataSetChanged")
    private fun unFavourite(id: Int) {
        val buider = AlertDialog.Builder(context)
        buider.setMessage("Are you sure you want to unfavourite this movie?")
        buider.setCancelable(true)
        buider.setPositiveButton("Yes") { dialog, _ ->
            sqLiteHelper.deleteFavourite(id)
            val favUpdate = sqLiteHelper.getAllFavourite()
            adapterfav?.addItems(favUpdate)
            var number = sqLiteHelper.getAllFavourite().size
            HomeActivity.textfavnum.text = number.toString()
            recyclerView.adapter!!.notifyDataSetChanged()
            dialog.dismiss()
        }
        buider.setNegativeButton("No") { dialog, _ ->
            dialog.dismiss()
        }
        val alert = buider.create()
        alert.show()
    }
}