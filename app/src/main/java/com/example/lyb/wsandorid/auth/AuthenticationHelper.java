package com.example.lyb.wsandorid.auth;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.app.Application;

import com.example.lyb.wsandorid.WSAndroidApplication;

/**
 * Created by liyibing on 2016/12/4.
 */

public class AuthenticationHelper {
    public static final String KEY_SESSION_NAME = "session_name";
    public static final String KEY_SESSID = "sessid";
    public static final String KEY_USERID = "userid";

    public static Account createNewAccount(String username, String password) {
        AccountManager accountManager = AccountManager.get(WSAndroidApplication.getAppContext());
        Account account = new Account(username, AuthenticationService.ACCOUNT_TYPE);
    }
}
