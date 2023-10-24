package com.example.movieapp

import android.annotation.SuppressLint
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Intent
import android.graphics.drawable.ColorDrawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.FragmentManager
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.movieapp.Detail.channelID
import com.example.movieapp.Profile.BitmapConverter
import com.example.movieapp.Profile.EditProfile
import com.example.movieapp.Profile.EditProfile.Companion.KEY_DATE
import com.example.movieapp.Profile.EditProfile.Companion.KEY_EMAIL
import com.example.movieapp.Profile.EditProfile.Companion.KEY_GENDER
import com.example.movieapp.Profile.EditProfile.Companion.KEY_NAME
import com.example.movieapp.Profile.EditProfile.Companion.SHARED_PREF_NAME
import com.example.movieapp.Profile.ReminderAdapterProfile
import com.example.movieapp.database.SQLiteHelper
import com.example.movieapp.database.SQLiteReminder
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.navigation.NavigationView
import de.hdodenhof.circleimageview.CircleImageView

class HomeActivity : AppCompatActivity() {
    private lateinit var btnedt : Button
    private lateinit var btnshow : Button
    private var type : Int = 0
    private lateinit var sqLiteReminder: SQLiteReminder
    private lateinit var sqLiteHelper : SQLiteHelper
    private val mConverterImg : BitmapConverter = BitmapConverter()

    companion object {
        @SuppressLint("StaticFieldLeak")
        var nameProfile : TextView? = null
        @SuppressLint("StaticFieldLeak")
        var emailProfile : TextView? = null
        @SuppressLint("StaticFieldLeak")
        var dobProfile : TextView? = null
        @SuppressLint("StaticFieldLeak")
        var genderProfile : TextView? = null
        var imageProfile : CircleImageView? = null
        @SuppressLint("StaticFieldLeak")
        lateinit var navController: NavController
        var page : Int = 1
        lateinit var fragmentManagerSetting : FragmentManager
        var adapterrem: ReminderAdapterProfile? = null
        @SuppressLint("StaticFieldLeak")
        lateinit var textfavnum : TextView
        lateinit var toggle : ActionBarDrawerToggle
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)
        textfavnum = findViewById(R.id.textViewFavNumver)
        sqLiteHelper = SQLiteHelper(this)
        var number = sqLiteHelper.getAllFavourite().size
        textfavnum.text = number.toString()
        getSupportActionBar()?.setBackgroundDrawable(ColorDrawable(getResources().getColor(R.color.blue)))
        val bottomNavigationView : BottomNavigationView = findViewById(R.id.bottomNV)
        val navView : NavigationView = findViewById(R.id.profiletab)
        val navHostFragment = supportFragmentManager
                .findFragmentById(R.id.fragmentContainerView) as NavHostFragment
        navController = navHostFragment.navController
        bottomNavigationView.setupWithNavController(navController)
        val appBarConfiguration = AppBarConfiguration(setOf(R.id.movies, R.id.favourite, R.id.settings, R.id.about))
        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setNavigationItemSelectedListener {
            when(it.itemId) {
                R.id.button_edt -> Toast.makeText(this, "Edit", Toast.LENGTH_SHORT).show()
            }
            true
        }
        val profileheader : View = navView.getHeaderView(0)
        btnedt = profileheader.findViewById<Button>(R.id.button_edt)
        nameProfile = profileheader.findViewById(R.id.name_profile)
        emailProfile = profileheader.findViewById(R.id.email_profile)
        dobProfile = profileheader.findViewById(R.id.dob_profile)
        genderProfile = profileheader.findViewById(R.id.gender_profile)
        imageProfile = profileheader.findViewById(R.id.image_profile)
        var reminderListProfile = profileheader.findViewById<RecyclerView>(R.id.reminder_list)
        btnshow = profileheader.findViewById<Button>(R.id.button_show)
        reminderListProfile.layoutManager = LinearLayoutManager(this)
        adapterrem = ReminderAdapterProfile()
        reminderListProfile.adapter = adapterrem
        sqLiteReminder = SQLiteReminder(profileheader.context)
        var remlist = sqLiteReminder.getAllReminder()
        adapterrem?.addItems(remlist)
        btnedt.setOnClickListener() {
            var intent = Intent(applicationContext, EditProfile::class.java)
            startActivity(intent)
        }
        btnshow.setOnClickListener() {
            navController.navigate(R.id.reminderAll)
        }
        updateProfile()
        fragmentManagerSetting = supportFragmentManager
        val channel = NotificationChannel(channelID, "Movie!!!", NotificationManager.IMPORTANCE_DEFAULT)
        channel.description = "Time to watch a movie <3"
        val notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.createNotificationChannel(channel)
    }
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.top_menu, menu)
        var viewType = menu!!.findItem(R.id.viewtype)
        if (type%2 !=0) {
            viewType.setIcon(R.drawable.ic_baseline_view_module_24)
        }
        else {
            viewType.setIcon(R.drawable.ic_baseline_format_list_bulleted_24)
        }
        drawToggle()
        return true
    }
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (toggle.onOptionsItemSelected(item)) {
            return true
        }
        return when(item.itemId) {
            R.id.viewtype -> {
                if (type%2 ==0) {
                    item.setIcon(R.drawable.ic_baseline_view_module_24)
                }
                else {
                    item.setIcon(R.drawable.ic_baseline_format_list_bulleted_24)
                }
                var viewType : Bundle = Bundle()
                viewType.putInt("list", type)
                viewType.putInt("page", page)
                navController.navigate(R.id.movies, viewType)
                type += 1
                drawToggle()
                return true
            }
            R.id.movies -> {
                navController.navigate(R.id.movies)
                drawToggle()
                return true
            }
            R.id.favourite -> {
                navController.navigate(R.id.favourite)
                drawToggle()
                return true
            }
            R.id.settings -> {
                navController.navigate(R.id.settings)
                drawToggle()
                return true
            }
            R.id.about -> {
                navController.navigate(R.id.about)
                drawToggle()
                return true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
    fun drawToggle() {
        val drawerLayout : DrawerLayout = findViewById(R.id.drawerlayout)
        toggle = ActionBarDrawerToggle(this, drawerLayout, R.string.open, R.string.close)
        drawerLayout.addDrawerListener(toggle)
        toggle.syncState()
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }
    open fun updateProfile() {
        var sharedPreferences = getSharedPreferences(SHARED_PREF_NAME, MODE_PRIVATE)
        var name : String? = sharedPreferences.getString(KEY_NAME, "Name")
        var email : String? = sharedPreferences.getString(KEY_EMAIL, "Email")
        var dob : String? = sharedPreferences.getString(KEY_DATE, "Date Of Birth")
        var gender : String? = sharedPreferences.getString(KEY_GENDER, "None")
        var image : String? = sharedPreferences.getString(EditProfile.KEY_IMAGE_GALLERY, "")
        nameProfile?.text = name
        emailProfile?.text = email
        dobProfile?.text = dob
        genderProfile?.text = gender
        imageProfile?.setImageBitmap(mConverterImg.decodeBase64(image))
    }
}