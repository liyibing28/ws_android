package com.example.lyb.wsandorid.activity;

import android.os.Bundle;
import android.support.compat.BuildConfig;
import android.widget.TextView;

import com.example.lyb.wsandorid.R;

/**
 * Created by lyb on 16-11-17.
 */

public class AboutActivity extends WSBaseActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);
        initView();

        TextView versionTextView = (TextView) findViewById(R.id.app_version);
        versionTextView.setText(getString(R.string.app_version, BuildConfig.VERSION_NAME));

    }
}
