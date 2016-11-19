package com.example.lyb.wsandorid.activity;

import android.app.Activity;
import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.TypedArray;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.example.lyb.wsandorid.R;
import com.example.lyb.wsandorid.model.NavRow;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by lyb on 16-11-16.
 */

public class WSBaseActivity extends AppCompatActivity implements android.widget.AdapterView.OnItemClickListener {
    public static final String TAG = "WSBaseActivity";
    protected String mActivityName = this.getClass().getSimpleName();

    protected Toolbar mToolbar;
    protected DrawerLayout mDrawerLayout;
    private ActionBarDrawerToggle mDrawerToggle;
    private ListView mLeftDrawerList;
    private NavDrawerListAdapter mNavDrawerListAdapter;
    private int mCurrentActivity;

    protected ArrayList<NavRow> mNavRowList = new ArrayList<NavRow>();
    String mActivityFriendly;//toolbar标题

    protected boolean mHasBackIntent = false;
    protected boolean mDisableNavigation = false;


    /**
     * 实现侧滑栏 图标 文字 activity之间的同步
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        String[] navMenuOptions = getResources().getStringArray(R.array.nav_menu_options);
        String[] navMenuActivities = getResources().getStringArray(R.array.nav_menu_activities);
        HashMap<String, String> mActivityClassToFriendly = new HashMap<String, String>();

        TypedArray icons = getResources().obtainTypedArray(R.array.nav_menu_icons);
        for (int i = 0; i < navMenuOptions.length; i++) {
            mActivityClassToFriendly.put(navMenuActivities[i], navMenuOptions[i]);

            int icon = icons.getResourceId(i, R.drawable.ic_action_email);
            NavRow row = new NavRow(icon, navMenuOptions[i], navMenuActivities[i]);
            mNavRowList.add(row);

            if (navMenuActivities[i].equals(mActivityName)) {
                mCurrentActivity = i;
            }
        }
        mActivityFriendly = mActivityClassToFriendly.get(mActivityName);
    }

    protected boolean initView() {
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mLeftDrawerList = (ListView) mDrawerLayout.findViewById(R.id.left_drawer);
        mLeftDrawerList.setChoiceMode(ListView.CHOICE_MODE_SINGLE);

        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        mNavDrawerListAdapter = new NavDrawerListAdapter(this, mNavRowList, mCurrentActivity);
        mLeftDrawerList.setAdapter(mNavDrawerListAdapter);
        mLeftDrawerList.setOnItemClickListener(this);

        if (mToolbar != null) {
            mToolbar.setTitle(mActivityFriendly);
            setSupportActionBar(mToolbar);
        }

        mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout, mToolbar, R.string.drawer_open, R.string.drawer_close) {

            @Override
            public void onDrawerClosed(View drawerView) {
                super.onDrawerClosed(drawerView);
            }

            @Override
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
            }
        };
        mDrawerLayout.setDrawerListener(mDrawerToggle);

        //用于实现登录验证，后面一大坑
        /**
         *
        // Make sure we have an active account, or go to authentication screen
        if (!setupCredentials()) {
            return(false);
        }
         */
        initDrawer();

        return true;
    }

    /**
     * 初始化侧滑栏
     */
    protected void initDrawer() {

        final TextView lblUsername = (TextView) mDrawerLayout.findViewById(R.id.lblUsername);
        final TextView lblNotLoggedIn = (TextView) mDrawerLayout.findViewById(R.id.lblNotLoggedIn);
        final TextView lblFullname = (TextView) mDrawerLayout.findViewById(R.id.lblFullname);
        final ImageView memberPhoto = (ImageView) mDrawerLayout.findViewById(R.id.imgUserPhoto);

        //实现侧滑栏显示用户，暂时注释掉，后面一大坑
        /**
         *

        Host memberInfo = MemberInfo.getMemberInfo();
        if (memberInfo != null) {
            lblUsername.setText(memberInfo.getName());
            lblFullname.setText(memberInfo.getFullname());
            String photoPath = MemberInfo.getMemberPhotoFilePath();
            if (photoPath != null && memberPhoto != null) {
                memberPhoto.setImageBitmap(BitmapFactory.decodeFile(photoPath));
            } else {
                memberPhoto.setImageResource(R.drawable.default_hostinfo_profile);
            }
        } else {
            memberPhoto.setImageResource(R.drawable.default_hostinfo_profile);
            lblNotLoggedIn.setVisibility(View.VISIBLE);
            lblUsername.setVisibility(View.GONE);
            lblFullname.setVisibility(View.GONE);
        }
         */

        if (mDisableNavigation)
        {
            mDrawerToggle.setDrawerIndicatorEnabled(false);
        }
    }

    //activity完全执行之后回调
    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);

        mDrawerToggle.syncState();

        if (mHasBackIntent) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onBackPressed();
                }
            });
        }
    }

    /**
     * 你可能希望当一种或者多种配置改变时避免重新启动你的activity。当这些配置改变时不会重新启动activity，而会调用activity的
     onConfigurationChanged(Resources.Configuration)方法。
     * @param newConfig
     */
    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        mDrawerToggle.onConfigurationChanged(newConfig);
    }


    /**
     * android中的后退键
     */
    @Override
    public void onBackPressed() {
        if (mDrawerLayout.isDrawerOpen(Gravity.START)) {
            mDrawerLayout.closeDrawers();
            return;
        }

        if (mDisableNavigation) {
            return;
        }
        super.onBackPressed();
    }

    /*
    /**
    * Activity 从 Pause 状态转换到 Active 状态时被调用。
    @Override
    protected void onResume() {
        super.onResume();
        if (!setupCredentials()) {
            return;
        }

        initDrawer();
    }
    */

    /**
     * 如果你想在Activity中得到新打开Activity关闭后返回的数据，你需要使用系统提供的startActivityForResult(Intent intent,int requestCode)
     * 方法打开新的Activity，新的Activity关闭后会向前面的Activity传回数据，为了得到传回的数据，你必须在前面的Activity中重写onActivityResult
     * (int requestCode, int resultCode,Intent data)方法
     * @param requestCode
     * @param resultCode
     * @param data
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // Update the drawer if we're returning from another activity
        initDrawer();
    }

    /**
     * Handle click from ListView in NavigationDrawer
     *
     * @param parent
     * @param view
     * @param position
     * @param id
     */
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        String[] activities = getResources().getStringArray(R.array.nav_menu_activities);
        if (mActivityName.equals(activities[position])) return;

        try {
            Class activityClass = Class.forName(this.getPackageName() + ".activity." + activities[position]);
            Intent i = new Intent(this, activityClass);
            startActivity(i);
        } catch (ClassNotFoundException e) {
            Log.i(TAG, "Class not found: " + activities[position]);
        }

        mDrawerLayout.closeDrawers();
    }

    /**
     * @return true if we already have an account set up in the AccountManager
     * false if we have to wait for the auth screen to process
     */
    /*
    public boolean setupCredentials() {
        try {
            AuthenticationHelper.getWarmshowersAccount();
            if (MemberInfo.getInstance() == null) {
                MemberInfo.initInstance(null); // Try to get persisted information
            }
            return true;
        } catch (NoAccountException e) {

            if (this.getClass() != AuthenticatorActivity.class) {  // Would be circular, so don't do it.
                Intent i = new Intent(this, AuthenticatorActivity.class);
                i.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
                startActivity(i);
            }
            return false;
        }
    }
       */
}