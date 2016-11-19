package com.example.lyb.wsandorid.activity;

import android.os.Bundle;

import com.example.lyb.wsandorid.R;

/**
 * Created by lyb on 16-11-19.
 */

public class SettingActivity extends WSBaseActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
        initView();

        // Fragment approach thanks to http://stackoverflow.com/a/26564401/215713
        //getFragmentManager().beginTransaction().replace(R.id.content_frame, new FragmentSetting()).commit();
    }


}
