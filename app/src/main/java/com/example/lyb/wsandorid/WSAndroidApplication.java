package com.example.lyb.wsandorid;

import android.app.Application;
import android.content.Context;
import android.preference.PreferenceManager;

/**
 * Created by liyibing on 2016/12/4.
 */

public class WSAndroidApplication extends Application {
    public static final String TAG = "WSAndroidApplication";
    private static Context mContext;

    public void onCreate() {
        super.onCreate();

        mContext = getApplicationContext();

        // Set automatic activity reports, per http://stackoverflow.com/a/24983778/215713
        //GoogleAnalytics analytics = GoogleAnalytics.getInstance(this);
        //analytics.enableAutoActivityReports(this);

        //boolean gaOptOut = !PreferenceManager.getDefaultSharedPreferences(this)
         //      .getBoolean("ga_collect_stats", true);
        //analytics.setAppOptOut(gaOptOut);

    }

    public static Context getAppContext() {
        return WSAndroidApplication.mContext;
    }

}
