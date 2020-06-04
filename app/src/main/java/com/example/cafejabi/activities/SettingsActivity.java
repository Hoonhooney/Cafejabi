package com.example.cafejabi.activities;

import android.os.Bundle;
import android.preference.PreferenceActivity;

import com.example.cafejabi.R;

public class SettingsActivity extends PreferenceActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);

        addPreferencesFromResource(R.xml.settings_preferences);
    }
}
