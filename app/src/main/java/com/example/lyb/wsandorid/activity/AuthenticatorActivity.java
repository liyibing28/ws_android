package com.example.lyb.wsandorid.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import com.example.lyb.wsandorid.R;
/**
 * Created by liyibing on 2016/12/3.
 */

public class AuthenticatorActivity extends WSSupportAccountAuthenticatorActivity {
    public static final String PARAM_AUTHTOKEN_TYPE = "authtokenType";
    public static final int REQUEST_TYPE_AUTHENTICATE = 201;

    View loggedInLayout;
    View notLoggedInLayout;
    EditText editUsername;
    EditText editPassword;
    TextView txtLoggedInStatus;

    //private DialogHandler mDialogHandler;

    @Override
    protected void onCreate(Bundle icicle) {
        super.onCreate(icicle);
        setContentView(R.layout.activity_authentication);
        initView();

        loggedInLayout = findViewById(R.id.loggedInLayout);
        notLoggedInLayout = findViewById(R.id.notLoggedInLayout);
        txtLoggedInStatus = (TextView)findViewById(R.id.txtLoggedInStatus);
        editUsername = (EditText) findViewById(R.id.editUsername);
        editPassword = (EditText) findViewById(R.id.editPassword);

        //mDialogHandler = new DialogHandler(this);


    }


}
