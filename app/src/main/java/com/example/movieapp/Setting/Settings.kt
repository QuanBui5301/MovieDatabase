package com.example.movieapp.Setting

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.FragmentTransaction
import com.example.movieapp.HomeActivity
import com.example.movieapp.HomeActivity.Companion.fragmentManagerSetting
import com.example.movieapp.R


class Settings : Fragment() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {

        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_settings, container, false)
        val fragment = SettingPreference()
        val fragmentTransaction : FragmentTransaction = fragmentManagerSetting.beginTransaction()
        fragmentTransaction.add(R.id.settingsFragPre, fragment)
        fragmentTransaction.commit()
        HomeActivity.toggle.syncState()
        (activity as HomeActivity?)?.getSupportActionBar()?.setDisplayHomeAsUpEnabled(true)
        return view
    }

}