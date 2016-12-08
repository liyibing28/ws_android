package com.example.lyb.wsandorid.auth;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.accounts.AuthenticatorException;
import android.accounts.OperationCanceledException;
import android.app.Application;

import com.example.lyb.wsandorid.R;
import com.example.lyb.wsandorid.WSAndroidApplication;
import com.example.lyb.wsandorid.util.GlobalInfo;

import java.io.IOException;

/**
 * Created by liyibing on 2016/12/4.
 */

public class AuthenticationHelper {
    public static final String KEY_SESSION_NAME = "session_name";
    public static final String KEY_SESSID = "sessid";
    public static final String KEY_USERID = "userid";


    //创建一个新账户
    public static Account createNewAccount(String username, String password) {
        AccountManager accountManager = AccountManager.get(WSAndroidApplication.getAppContext());
        Account account = new Account(username, AuthenticationService.ACCOUNT_TYPE);
        accountManager.addAccountExplicitly(account, null, null);
        accountManager.setAuthToken(account, AuthenticationService.ACCOUNT_TYPE, password);
        return account;
    }

    //在accountmanager中获取warmshowers账户
    public static Account getWarmshowersAccount() throws NoAccountException {
        AccountManager accountManager = AccountManager.get(WSAndroidApplication.getAppContext());
        Account[] accounts = accountManager.getAccountsByType(AuthenticationService.ACCOUNT_TYPE);

        if (accounts.length == 0) {
            throw new NoAccountException();
        }

        return accounts[0];
    }
    //获取账户cookie
    public static String getAccountCookie() {
        AccountManager accountManager = AccountManager.get(WSAndroidApplication.getAppContext());
        Account account = getWarmshowersAccount();
        String session_name = accountManager.getUserData(account, KEY_SESSION_NAME);
        String sessid = accountManager.getUserData(account, KEY_SESSID);
        String cookieString = session_name + "=" + sessid + "; domain=" + GlobalInfo.warmshowersCookieDomain;
        return cookieString;
    }
    //获取账户uid
    public static int getAccountUid() {
        AccountManager accountManager = AccountManager.get(WSAndroidApplication.getAppContext());
        Account account = getWarmshowersAccount();
        String sUid = accountManager.getUserData(account, KEY_USERID);
        int uid = -1;
        try {
            uid = Integer.parseInt(sUid);
        } catch (NumberFormatException e) {
            // Ignore, and we'll go with -1 for the uid. Doesn't work well, but cheater.
            // This probably should only be happening on initial 1.4.1 upgrade where account didn't have
            // the UID stashed in it.
        }
        return uid;
    }

    public static String getAccountUsername() throws NoAccountException {
        Account account = getWarmshowersAccount();
        String username = account.name;
        return username;
    }
    //获取token代替password进行验证
    public static String getAccountPassword() throws OperationCanceledException, IOException, AuthenticatorException, NoAccountException {
        AccountManager accountManager = AccountManager.get(WSAndroidApplication.getAppContext());
        Account account = getWarmshowersAccount();

        // authToken is here instead of password for legacy reasons, and doesn't make sense to
        // break all existing accounts by changing it.
        String password = accountManager.blockingGetAuthToken(account, AuthenticationService.ACCOUNT_TYPE, true);
        return password;
    }

    public static void addCookieInfo(String cookieSessionName, String cookieSessionId, int userId) throws NoAccountException {
        AccountManager accountManager = AccountManager.get(WSAndroidApplication.getAppContext());
        Account account = getWarmshowersAccount();

        accountManager.setUserData(account, KEY_SESSION_NAME, cookieSessionName);
        accountManager.setUserData(account, KEY_SESSID, cookieSessionId);
        accountManager.setUserData(account, KEY_USERID, String.valueOf(userId));
    }

    public static void removeOldAccount() {
        AccountManager accountManager = AccountManager.get(WSAndroidApplication.getAppContext());
        try {
            Account oldAccount = getWarmshowersAccount();
            accountManager.removeAccount(oldAccount, null, null);
        } catch (NoAccountException e) {
            // No action
        }
    }
}
