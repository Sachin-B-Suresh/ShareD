package com.example.sharedcfc.ui.settings;

import android.annotation.SuppressLint;
import android.os.Bundle;

import com.example.sharedcfc.R;

import androidx.annotation.Nullable;
import androidx.preference.PreferenceFragmentCompat;


public class SettingsFragment extends PreferenceFragmentCompat {

    @SuppressWarnings("deprecation")
    @SuppressLint("ResourceType")
    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        addPreferencesFromResource(R.xml.preference_screen);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        // TODO: Use the ViewModel
    }

}
