package com.example.lyb.wsandorid.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.example.lyb.wsandorid.R;

/**
 * Created by lyb on 16-11-18.
 */

public class Map2Activity extends WSBaseActivity{
    Button button;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map2);
        initView();
        button = (Button)findViewById(R.id.button);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Map2Activity.this,MapActivity.class);
                startActivity(intent);
            }
        });

    }
}
