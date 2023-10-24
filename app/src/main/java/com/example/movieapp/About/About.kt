package com.example.movieapp.About

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import com.example.movieapp.HomeActivity
import com.example.movieapp.R

class About : Fragment() {
    companion object {
        @SuppressLint("StaticFieldLeak")
        lateinit var webView: WebView
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {

        }
    }

    @SuppressLint("SetJavaScriptEnabled")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_about, container, false)
        webView = view.findViewById(R.id.aboutView)
        webView.loadUrl("https://www.themoviedb.org/about")
        val webSetting : WebSettings = webView.settings
        webSetting.javaScriptEnabled = true
        webView.webViewClient = WebViewClient()
        HomeActivity.toggle.syncState()
        (activity as HomeActivity?)?.getSupportActionBar()?.setDisplayHomeAsUpEnabled(true)
        return view
    }

}