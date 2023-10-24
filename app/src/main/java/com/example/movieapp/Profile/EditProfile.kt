package com.example.movieapp.Profile

import android.app.Activity
import android.app.DatePickerDialog
import android.content.DialogInterface
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.icu.text.SimpleDateFormat
import android.icu.util.Calendar
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.widget.*
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import com.example.movieapp.HomeActivity.Companion.dobProfile
import com.example.movieapp.HomeActivity.Companion.emailProfile
import com.example.movieapp.HomeActivity.Companion.genderProfile
import com.example.movieapp.HomeActivity.Companion.imageProfile
import com.example.movieapp.HomeActivity.Companion.nameProfile
import com.example.movieapp.R
import de.hdodenhof.circleimageview.CircleImageView
import java.io.InputStream
import java.util.*

class EditProfile : AppCompatActivity() {
    lateinit var edtName : EditText
    lateinit var edtEmail : EditText
    lateinit var edtDOB : EditText
    lateinit var radioGroup : RadioGroup
    lateinit var edtImage : CircleImageView
    lateinit var datePickerDialog : DatePickerDialog
    companion object {
        val SHARED_PREF_NAME : String = "mypref"
        val KEY_NAME : String = "name"
        val KEY_EMAIL : String = "email"
        val KEY_DATE : String = "dob"
        val KEY_GENDER : String = "gender"
        val KEY_IMAGE_GALLERY : String = "image"
        var edtImageUrl : String = ""
    }
    var dateshow = SimpleDateFormat("dd MMMM YYYY", Locale.US)
    private val mCameraResultLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result: ActivityResult ->
            if (result.resultCode == Activity.RESULT_OK) {
                val intent = result.data
                val imageBitmap: Bitmap = intent?.extras?.get("data") as Bitmap
                mProfileBitmap = imageBitmap
                edtImage.setImageBitmap(imageBitmap)
            }
        }
    private val mGalleryResultLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result: ActivityResult ->
            if (result.resultCode == Activity.RESULT_OK) {
                val intent = result.data
                val inputStream : InputStream = getContentResolver().openInputStream(result.data!!.data!!)!!
                val imageBitmap : Bitmap = BitmapFactory.decodeStream(inputStream)
                mProfileBitmap = imageBitmap
                edtImage.setImageBitmap(mProfileBitmap)
            }
        }
    var mProfileBitmap : Bitmap? = null
    private val mConverterImg : BitmapConverter = BitmapConverter()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_profile)
        supportActionBar?.hide()
        val btn_done: Button = findViewById(R.id.done)
        val btn_cancel: Button = findViewById(R.id.cancel)
        var edtGender: String = ""
        edtName = findViewById(R.id.edtname)
        edtEmail = findViewById(R.id.edtemail)
        edtDOB = findViewById(R.id.edtDOB)
        edtImage = findViewById(R.id.editimage)
        radioGroup = findViewById(R.id.radioGroup)
        val calendar: Calendar = Calendar.getInstance()
        val year: Int = calendar.get(Calendar.YEAR)
        val month: Int = calendar.get(Calendar.MONTH)
        val day: Int = calendar.get(Calendar.DAY_OF_MONTH)

        edtDOB.setOnClickListener() {
            datePickerDialog = DatePickerDialog(
                this,
                android.R.style.Theme_Holo_Light_Dialog_MinWidth,
                DatePickerDialog.OnDateSetListener { datePickerDialog, i, i2, i3 ->
                    val selectDOB: Calendar = Calendar.getInstance()
                    selectDOB.set(Calendar.YEAR, i)
                    selectDOB.set(Calendar.MONTH, i2)
                    selectDOB.set(Calendar.DAY_OF_MONTH, i3)
                    val date: String = dateshow.format(selectDOB.time)
                    edtDOB.setText("$date")
                },
                year,
                month,
                day
            )
            datePickerDialog.getWindow()!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            datePickerDialog.show()
        }
        edtImage.setOnClickListener() {
            val builder = androidx.appcompat.app.AlertDialog.Builder(this)
            builder.setTitle("Select Image")
            builder.setMessage("Choose your option?")
            builder.setPositiveButton("Gallery") {dialog : DialogInterface, which : Int ->
                galleryAvatar()
                dialog.dismiss()

            }
            builder.setNegativeButton("Camera") {dialog : DialogInterface, which : Int ->
                pickAvatar()
                dialog.dismiss()

            }
            val alert = builder.create()
            alert.show()
        }
        var sharedPreference = getSharedPreferences(SHARED_PREF_NAME, MODE_PRIVATE)
        btn_done.setOnClickListener() {
            edtImageUrl = mConverterImg.encodeBase64(mProfileBitmap!!)!!
            val checkedgenderId = radioGroup.checkedRadioButtonId
            val genderbtn = findViewById<RadioButton>(checkedgenderId)
            edtGender = genderbtn.text.toString()
            val editProfile: SharedPreferences.Editor = sharedPreference.edit()
            editProfile.clear()
            editProfile.commit()
            editProfile.putString(KEY_NAME, edtName.getText().toString())
            editProfile.putString(KEY_EMAIL, edtEmail.getText().toString())
            editProfile.putString(KEY_DATE, edtDOB.getText().toString())
            editProfile.putString(KEY_GENDER, edtGender)
            editProfile.putString(KEY_IMAGE_GALLERY, edtImageUrl)
            editProfile.apply()
            editProfile.commit()
            val shared = getSharedPreferences(SHARED_PREF_NAME, MODE_PRIVATE)
            val name: String? = shared.getString(KEY_NAME, "Name")
            val email: String? = shared.getString(KEY_EMAIL, "Email")
            val dob: String? = shared.getString(KEY_DATE, "Date Of Birth")
            val gender : String? = shared.getString(KEY_GENDER, "Male")
            val image: String? = shared.getString(KEY_IMAGE_GALLERY, "")
            nameProfile?.text = name
            emailProfile?.text = email
            dobProfile?.text = dob
            genderProfile?.text = gender
            imageProfile?.setImageBitmap(mConverterImg.decodeBase64(image))
            finish()
        }
        btn_cancel.setOnClickListener() {
            finish()
        }
    }
    private fun pickAvatar() {
        val cameraIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        mCameraResultLauncher.launch(cameraIntent)
    }
    private fun galleryAvatar() {
        val galleryIntent = Intent(Intent.ACTION_PICK)
        galleryIntent.type = "image/*"
        mGalleryResultLauncher.launch(galleryIntent)
    }
}

