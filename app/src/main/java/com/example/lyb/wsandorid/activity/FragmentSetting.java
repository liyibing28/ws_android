package com.example.lyb.wsandorid.activity;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceFragment;
import android.support.compat.BuildConfig;

import com.example.lyb.wsandorid.R;

/**
 * Created by lyb on 16-11-19.
 */

public class FragmentSetting extends PreferenceFragment {


    @Override
    public void onCreate(final Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.preference);
        /*
        if (BuildConfig.DEBUG) {
            addPreferencesFromResource(R.xml.developer_options);
        }
        */
        //setSummary();
    }
    /*

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        //setSummary();
    }

    //void setSummary() {
    //    MaterialListPreference pref = (MaterialListPreference) findPreference("distance_unit");
    //    CharSequence title = pref.getEntry();
    //    pref.setSummary(title);
    //}

    @Override
    public void onPause() {
        getPreferenceManager().getSharedPreferences().unregisterOnSharedPreferenceChangeListener(this);
        super.onPause();
    }

    @Override
    public void onResume() {
        super.onResume();
        getPreferenceManager().getSharedPreferences().registerOnSharedPreferenceChangeListener(this);

    }
    */
}