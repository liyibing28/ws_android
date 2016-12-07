package com.example.lyb.wsandorid.activity;

import android.accounts.Account;
import android.app.Dialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.lyb.wsandorid.R;
import com.example.lyb.wsandorid.auth.AuthenticationHelper;
import com.example.lyb.wsandorid.auth.NoAccountException;
import com.example.lyb.wsandorid.auth.http.HttpAuthenticator;

import org.json.JSONException;

import java.io.IOException;

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

        updateView();
    }

    public void updateView() {
        try {
            String username = AuthenticationHelper.getAccountUsername();
            updateLoggedInView(username);
        } catch (NoAccountException e) {
            updateLoggedOutView();
        }
    }
    //显示登陆账户
    public void updateLoggedInView(String username) {
        notLoggedInLayout.setVisibility(View.GONE);
        loggedInLayout.setVisibility(View.VISIBLE);
        txtLoggedInStatus.setText(getString(R.string.current_login_status, username));
        initDrawer();
    }

    //显示登陆界面
    public void updateLoggedOutView() {
        notLoggedInLayout.setVisibility(View.VISIBLE);
        loggedInLayout.setVisibility(View.GONE);
        findViewById(android.R.id.content).invalidate();
        mDisableNavigation = true;
        initDrawer();
    }
    //登出
    public void logout(View unusedArg) {
        AuthenticationHelper.removeOldAccount();
        MemberInfo.doLogout();
        // TODO: Actually perform a logout operation
        updateLoggedOutView();
    }

    public void cancel(View view) {
        Intent resultIntent = new Intent();
        setResult(RESULT_CANCELED, resultIntent);
        finish();
    }

    public void applyCredentials(View view) {

        String username = editUsername.getText().toString();
        String password = editPassword.getText().toString();
        if (!username.isEmpty() && !password.isEmpty()) {
            //mDialogHandler.showDialog(DialogHandler.AUTHENTICATE);
            Account account = AuthenticationHelper.createNewAccount(username, password);
            AuthenticationTask authTask = new AuthenticationTask();
            authTask.execute();
        }
    }

    //验证账号是否正确
    public class AuthenticationTask extends AsyncTask<Void, Void, Void> {
        int mUID = 0;
        boolean mNetworkError = false;

        protected Void doInBackground(Void... params) {

            HttpAuthenticator authenticator = new HttpAuthenticator();
            try {
                mUID = authenticator.authenticate();
            } catch (IOException e) {
                mNetworkError = true;
            } catch (JSONException e) {
                mNetworkError = true;
            } catch (Exception e) {
                // mUid value will capture anything else.
            }
            return null;
        }

        protected void onPostExecute(Void v) {
            //mDialogHandler.dismiss();

            if (mUID < 1) {
                if (mNetworkError) {
                    Toast.makeText(AuthenticatorActivity.this, R.string.http_server_access_failure, Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(AuthenticatorActivity.this, R.string.authentication_failed, Toast.LENGTH_LONG).show();
                }
                AuthenticationHelper.removeOldAccount();
                // And just stay on the page auth screen.
            }
            // Otherwise launch the maps activity, with no history
            else {
                Intent i = new Intent(AuthenticatorActivity.this, Map2Activity.class);
                i.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
                startActivity(i);
            }
        }
    }

    //@Override
    //protected Dialog onCreateDialog(int id, Bundle args) {
    //    return mDialogHandler.createDialog(id, getResources().getString(R.string.authenticating));
    //}
    /*
    @Override
    protected void onStop() {
        GoogleAnalytics.getInstance(this).reportActivityStop(this);
        super.onStop();
    }

    @Override
    protected void onStart() {
        super.onStart();
        GoogleAnalytics.getInstance(this).reportActivityStart(this);
    }
    */
}
