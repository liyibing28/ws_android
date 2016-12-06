package com.example.lyb.wsandorid.auth;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

/**
 * Created by liyibing on 2016/12/4.
 */

public class AuthenticationService extends Service {
    public static final String ACCOUNT_TYPE = "org.warmshowers";

    private Authenticator mAuthenticator;

    @Override
    public void onCreate() {
        mAuthenticator = new Authenticator(this);
    }

    @Override
    public void onDestroy() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        return mAuthenticator.getIBinder();
    }

}
