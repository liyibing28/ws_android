package com.example.lyb.wsandorid.activity;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.amap.api.maps2d.AMap;
import com.example.lyb.wsandorid.R;

/**
 * Created by lyb on 16-11-18.
 */

public class MapActivity extends FragmentActivity implements View.OnClickListener {
    private LinearLayout tab_map;
    private LinearLayout tab_msg;

    private ImageView img_map;
    private ImageView img_msg;


    private TextView txt_map;
    private TextView txt_msg;

    private Fragment mapFragment;
    private Fragment msgFragment;
    //private Fragment setFragment;

    private AMap mMap;
    Toolbar toolbar = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("warmshowers");
        toolbar.setNavigationIcon(R.mipmap.ic_launcher);//设置导航蓝图标
        //初始化控件
        initView();

        //初始化底部事件,设置监听
        initEvent();

        // 初始化并设置当前Fragment
        initFragment(0);

    }

    private void initFragment(int index){
        //初始化管理器
        FragmentManager fragmentManager = getSupportFragmentManager();

        //开启事务
        FragmentTransaction transaction = fragmentManager.beginTransaction();

        //隐藏所有的Fragment
        hideFragment(transaction);

        switch (index){
            case 0:
                if (mapFragment == null){
                    mapFragment = new MapFragment();
                    //mMap = ((SupportMapFragment) fragmentManager.findFragmentById(R.id.amap)).getMap();
                    transaction.add(R.id.fl_content, mapFragment);
                }else {
                    transaction.show(mapFragment);
                }
                break;

            case 1:
                if (msgFragment == null){
                    msgFragment = new MsgFragment();
                    transaction.add(R.id.fl_content, msgFragment);
                }else {
                    transaction.show(msgFragment);
                }
                break;

            default:
                break;
        }

        transaction.commit();
    }

    //隐藏
    private void hideFragment(FragmentTransaction transaction){
        if (mapFragment != null){
            transaction.hide(mapFragment);
        }

        if (msgFragment != null){
            transaction.hide(msgFragment);
        }

    }

    private void initEvent(){
        //设置tab监听
        tab_map.setOnClickListener(this);
        tab_msg.setOnClickListener(this);
        //tab_set.setOnClickListener(this);
    }

    private void initView(){
        //Linerlayout
        this.tab_map = (LinearLayout)findViewById(R.id.tab_map);
        this.tab_msg = (LinearLayout)findViewById(R.id.tab_msg);
        //
        this.img_map = (ImageView) findViewById(R.id.img_map);
        this.img_msg = (ImageView) findViewById(R.id.img_msg);

        //
        this.txt_map = (TextView) findViewById(R.id.txt_map);
        this.txt_msg = (TextView) findViewById(R.id.txt_msg);

    }

    @Override
    public void onClick(View v) {

        // 在每次点击后将所有的底部按钮(ImageView,TextView)颜色改为灰色，然后根据点击着色
        restartBotton();
        // ImageView和TetxView置为绿色，页面随之跳转
        switch (v.getId()) {
            case R.id.tab_map:
                //tab_map.setImageResource(R.);
                txt_map.setTextColor(0xff1B940A);
                initFragment(0);
                //mMap = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.amap)).getMap();
                break;
            case R.id.tab_msg:
                //tab_msg.setImageResource(R.);
                txt_msg.setTextColor(0xff1B940A);
                initFragment(1);
                break;

            default:
                break;
        }

    }

    private void restartBotton() {
        // ImageView置为灰色
        //tab_map.setImageResource(R.drawable.tab_weixin_normal);
        //tab_msg.setImageResource(R.drawable.tab_address_normal);
        //tab_set.setImageResource(R.drawable.tab_find_frd_normal);
        // TextView置为白色
        txt_map.setTextColor(0xffffffff);
        txt_msg.setTextColor(0xffffffff);
        //txt_set.setTextColor(0xffffffff);
    }
}
