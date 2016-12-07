package com.example.lyb.wsandorid.auth.http;

import android.accounts.AuthenticatorException;
import android.accounts.OperationCanceledException;
import android.util.Log;

import com.example.lyb.wsandorid.auth.AuthenticationHelper;
import com.example.lyb.wsandorid.auth.NoAccountException;
import com.example.lyb.wsandorid.util.GlobalInfo;

import org.apache.http.HttpException;
import org.apache.http.NameValuePair;
import org.apache.http.client.CircularRedirectException;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by liyibing on 2016/12/6.
 */

public class HttpAuthenticator  {
    private final String wsUserAuthUrl = GlobalInfo.warmshowersBaseUrl + "/services/rest/user/login";//登陆网址
    private final String wsUserLogoutUrl = GlobalInfo.warmshowersBaseUrl + "/services/rest/user/logout";//登出网址

    private static final String TAG = "HttpAuthenticator";
    //从保存的account中获取账户以及密码
    private List<NameValuePair> getCredentialsFromAccount() throws OperationCanceledException, AuthenticatorException, IOException, NoAccountException {
        List<NameValuePair> credentials = new ArrayList<NameValuePair>();

        String username = AuthenticationHelper.getAccountUsername();
        String password = AuthenticationHelper.getAccountPassword();

        credentials.add(new BasicNameValuePair("username", username));
        credentials.add(new BasicNameValuePair("password", password));
        return credentials;
    }

    /**
     * Hits the logout service and then the login.
     * 用于验证登陆
     * Returns
     * - userid
     * - 0 if already logged in
     */
    public int authenticate() throws HttpAuthenticationFailedException, IOException, JSONException, NoAccountException {
        RestClient authClient = new RestClient();
        int userId = 0;
        try {
            // Log.i(TAG, "Routine logout attempt");
            authClient.authpost(wsUserLogoutUrl);
        } catch (Exception e) {
            Log.i(TAG, "Exception on logout - not to worry: " + e.toString());
            // We don't care a lot about this, as we were just trying to ensure clean login.
        }

        try {
            List<NameValuePair> credentials= getCredentialsFromAccount();
            JSONObject authResult = authClient.authpost(wsUserAuthUrl, credentials);
            userId = authResult.getJSONObject("user").getInt("uid");

            Host profileInfo = Host.CREATOR.parse(authResult.getJSONObject("user"));后面来看
            MemberInfo.initInstance(profileInfo);//同上

            String cookieSessionName = authResult.getString("session_name");
            String cookieSessionId = authResult.getString("sessid");

            AuthenticationHelper.addCookieInfo(cookieSessionName, cookieSessionId, userId);

            String filePath = MemberInfo.getMemberPhotoFilePath();//设置存储地址
            // Get the member photo if it doesn't exist already
            File profileImageFile = new File(filePath);
            // If the file doesn't exist or is tiny, download it, otherwise use the one we have
            if (!profileImageFile.exists() || profileImageFile.length() < 1000) {
                // Download it
                downloadMemberPhoto(profileInfo, filePath);
            }

        }catch (ClientProtocolException e) {
            if (e.getCause() instanceof CircularRedirectException) {
                // If we get this authentication has still been successful, so ignore it
            } else {
                throw new HttpAuthenticationFailedException(e);
            }
        } catch (IOException e) {
            // Rethrow, prevent the catch below from getting to it. we want to know this was IO exception
            throw e;
        } catch (HttpAuthenticationFailedException e) {  // Attempting to do auth with wrong credentials
            throw e;
        } catch (NoAccountException e) {
            throw e;
        } catch (HttpException e) {
            if (e.getMessage().equals("406")) {
                Log.i(TAG, "Got error 406 attempting to log in, so ignoring");
                // This is the case where we hit auth and were already authenticated... but shouldn't have happened.
            } else {
                throw e;
            }
        } catch (Exception e) {
            // We might have had a json parsing or access exception - for example, if the "user" was not there,
            // Could also have AuthenticatorException or OperationCancelledException here
            // or if there was something wrong with what the server returned
            throw new HttpAuthenticationFailedException(e);
        }

        return userId;
    }
    //后面来看
    private File downloadMemberPhoto(Host profileInfo, String targetFilePath) {
        File targetFile = new File(targetFilePath);
        if (targetFile.exists()) {
            targetFile.delete();
        }
        HttpURLConnection c = null;
        FileOutputStream fos = null;
        BufferedOutputStream out = null;
        try {
            c = (HttpURLConnection) new URL(profileInfo.getProfilePictureLarge()).openConnection();
            fos = new FileOutputStream(targetFilePath);
            out = new BufferedOutputStream(fos);

            InputStream in = c.getInputStream();
            byte[] buffer = new byte[16384];
            int len = 0;
            while (( len = in.read( buffer)) > 0) {
                fos.write(buffer, 0, len);
            }
            fos.flush();

        } catch (Exception e) {
            Log.i(TAG, "Error: " + e);
        }
        finally {
            try {
                fos.getFD().sync();
                out.close();
                c.disconnect();
            } catch (Exception e) {
                // Don't worry about these?
            }
        }
        return targetFile;
    }
}
