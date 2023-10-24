package com.example.movieapp.Setting

import android.os.Bundle
import android.util.Log
import androidx.preference.EditTextPreference
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import com.example.movieapp.R

class SettingPreference : PreferenceFragmentCompat() {
    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        addPreferencesFromResource(R.xml.setting_preference)
        val mTextPreference = preferenceScreen.findPreference<Preference>("text") as EditTextPreference?
        val preferenceChangeListener: Preference.OnPreferenceChangeListener = Preference.OnPreferenceChangeListener { _, newValue ->
            Log.d("EditTextPreference", "$newValue")
            false
        }
        mTextPreference?.onPreferenceChangeListener = preferenceChangeListener
    }
}