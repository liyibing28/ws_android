package com.example.lyb.wsandorid.api;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.example.lyb.wsandorid.R;
import com.example.lyb.wsandorid.auth.NoAccountException;
import com.example.lyb.wsandorid.auth.http.HttpAuthenticationFailedException;
import com.example.lyb.wsandorid.auth.http.HttpAuthenticator;
import com.example.lyb.wsandorid.auth.http.HttpSessionContainer;
import com.example.lyb.wsandorid.util.GlobalInfo;
import com.example.lyb.wsandorid.util.http.HttpUtils;

import org.apache.http.HttpEntity;
import org.apache.http.HttpException;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.protocol.HttpContext;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by liyibing on 2016/12/7.
 */

public class RestClient {
    public static final String TAG = "RestClient";
    private static String csrfToken = "";

    private void getToken() throws IOException {
        getToken(false);
    }
    //获取authtoken？？？
    private void getToken(boolean reset) throws IOException {
        if (csrfToken.isEmpty() || reset) {
            try {
                csrfToken = stringGet(GlobalInfo.warmshowersBaseUrl + "/services/session/token");
            } catch (URISyntaxException e) {
                // Ignore it...
            }
        }
    }

    public JSONObject get(String simpleUrl) throws HttpException, JSONException, URISyntaxException, IOException {
        HttpClient client = HttpUtils.getDefaultClient();
        JSONObject jsonObj;
        String json;
        int responseCode;

        getToken();

        String url = HttpUtils.encodeUrl(simpleUrl);
        HttpGet get = new HttpGet(url);
        get.addHeader("X-CSRF-Token", csrfToken);

        HttpContext context = HttpSessionContainer.INSTANCE.getSessionContext();

        HttpResponse response = client.execute(get, context);
        HttpEntity entity = response.getEntity();
        responseCode = response.getStatusLine().getStatusCode();

        json = EntityUtils.toString(entity, "UTF-8");

        if (responseCode != HttpStatus.SC_OK) {
            Log.i(TAG, "Non-200 HTTP response(" + Integer.toString(responseCode) + " for URL " + url);
            switch (responseCode) {
                case HttpStatus.SC_UNAUTHORIZED:    // 401, typically when not logged in, or no CSRF token
                    doRequiredAuth();
                    return get(url);
                case HttpStatus.SC_FORBIDDEN:
                case HttpStatus.SC_NOT_ACCEPTABLE:  // 406, Typically trying to log out when not logged in
                default:
                    throw new HttpException(Integer.toString(responseCode));
            }
        }

        jsonObj = new JSONObject(json);
        client.getConnectionManager().shutdown();

        return jsonObj;
    }

    // Bare post with no param干什么用的？？？
    public JSONObject post(String url) throws HttpException, IOException, JSONException {
        List<NameValuePair> params = new ArrayList<NameValuePair>();
        return post(url, params);
    }

    //用于首次登陆？？？
    public JSONObject post(String url, List<NameValuePair> params) throws HttpException, IOException, JSONException, HttpAuthenticationFailedException {
        HttpClient client = HttpUtils.getDefaultClient();
        String jsonString = "";
        JSONObject jsonObj;
        getToken();

        HttpPost httpPost = new HttpPost(url);
        httpPost.addHeader("X-CSRF-Token", csrfToken);
        httpPost.setEntity(new UrlEncodedFormEntity(params, "utf-8"));
        HttpContext httpContext = HttpSessionContainer.INSTANCE.getSessionContext();//这是干什么用的？？
        HttpResponse response = client.execute(httpPost, httpContext);
        HttpEntity entity = response.getEntity();

        int responseCode = response.getStatusLine().getStatusCode();

        if (responseCode != HttpStatus.SC_OK) {
            Log.i(TAG, "Non-200 HTTP response(" + Integer.toString(responseCode) + " for URL " + url);
            switch (responseCode) {
                case HttpStatus.SC_UNAUTHORIZED:    // 401, typically when not logged in
                case HttpStatus.SC_FORBIDDEN:       // 403 denied
                    // If it is *not* an unauth for user 0/anonymous, it's for a properly auth user, so we have to bail.
                    // Drupal 6 says "user 0", Drupal 7 says "user anonymous".
                    // 401 for not logged in, Drupal7 gives 403 for no CSRF token or bad csrf token
                    String statusLine = response.getStatusLine().getReasonPhrase();
                    Pattern pattern = Pattern.compile("(denied for user (0|anonymous)|CSRF validation failed)");
                    Matcher matcher = pattern.matcher(statusLine);
                    if (!matcher.find()) {
                        throw new HttpException(Integer.toString(responseCode) + " error " + response.getStatusLine().getReasonPhrase());
                    }
                    doRequiredAuth();
                    return post(url, params);
                case HttpStatus.SC_NOT_ACCEPTABLE:  // 406, Typically trying to log out when not logged in
                default:
                    throw new HttpException(Integer.toString(responseCode) + " " + response.getStatusLine().getReasonPhrase());
            }
        }

        jsonString = EntityUtils.toString(entity, "UTF-8");

        jsonString = EntityUtils.toString(entity, "UTF-8");

        try {
            jsonObj = new JSONObject(jsonString);
        } catch (JSONException e) {  // Assume it might have been an array [true]
            JSONArray jsonArray = new JSONArray(jsonString);
            jsonObj = new JSONObject();
            jsonObj.put("arrayresult", jsonArray);
        }

        client.getConnectionManager().shutdown();


        return jsonObj;
    }
    //用于验证？？
    public JSONObject authpost(String url, List<NameValuePair> params) throws HttpException, IOException, JSONException, HttpAuthenticationFailedException {
        HttpClient client = HttpUtils.getDefaultClient();
        String jsonString = "";
        JSONObject jsonObj;

        getToken();

        HttpPost httpPost = new HttpPost(url);
        httpPost.addHeader("X-CSRF-Token", csrfToken);

        httpPost.setEntity(new UrlEncodedFormEntity(params, "utf-8"));
        HttpContext httpContext = HttpSessionContainer.INSTANCE.getSessionContext();
        HttpResponse response = client.execute(httpPost, httpContext);
        HttpEntity entity = response.getEntity();

        int responseCode = response.getStatusLine().getStatusCode();

        if (responseCode != HttpStatus.SC_OK) {
            Log.i(TAG, "authpost() Non-200 HTTP response(" + Integer.toString(responseCode) + " for URL " + url);
            switch (responseCode) {
                case HttpStatus.SC_UNAUTHORIZED:    // 401, typically when not logged in
                case HttpStatus.SC_FORBIDDEN:       // 403 forbidden
                case HttpStatus.SC_NOT_ACCEPTABLE:  // 406, Typically trying to log out when not logged in
                default:
                    throw new HttpException(Integer.toString(responseCode));
            }
        }

        jsonString = EntityUtils.toString(entity, "UTF-8");

        try {
            jsonObj = new JSONObject(jsonString);
        } catch (JSONException e) {  // Assume it might have been an array [true]
            JSONArray jsonArray = new JSONArray(jsonString);
            jsonObj = new JSONObject();
            jsonObj.put("arrayresult", jsonArray);
        }

        client.getConnectionManager().shutdown();


        return jsonObj;
    }


    //暂时不知道用于哪里
    public static void reportError(Context context, Object obj) {
        if (obj instanceof Exception) {
            int rId = 0;
            if (obj instanceof NoAccountException) {
                rId = R.string.no_account;
            } else if (obj instanceof HttpAuthenticationFailedException) {
                rId = R.string.authentication_failed;
            } else if (obj instanceof HttpException) {
                rId = R.string.http_server_access_failure;
            } else if (obj instanceof IOException) {
                rId = R.string.io_error;
            } else if (obj instanceof JSONException) {
                rId = R.string.json_error;
            } else {
                // Unexpected error
                rId = R.string.http_unexpected_failure;
            }
            Exception e = (Exception)obj;
            String exceptionDescription = e.toString();
            if (e.getMessage() != null) exceptionDescription += " Message:" + e.getMessage();
            if (e.getCause() != null) exceptionDescription += " Cause: " + e.getCause().toString();
            Tools.gaReportException(context, "RestClient Exception: ", exceptionDescription);

            Toast.makeText(context, rId, Toast.LENGTH_LONG).show();
        }
        return;
    }

    protected void doRequiredAuth() {
        getToken(true);
        HttpAuthenticator authenticator = new HttpAuthenticator();
        authenticator.authenticate();
        getToken(true);  // New token required with new session establishment
    }

    public String stringGet(String simpleUrl) throws HttpException, IOException, URISyntaxException {
        HttpClient client = HttpUtils.getDefaultClient();
        int responseCode;

        String url;
        url = HttpUtils.encodeUrl(simpleUrl);
        HttpGet get = new HttpGet(url);
        HttpContext context = HttpSessionContainer.INSTANCE.getSessionContext();

        HttpResponse response = client.execute(get, context);
        HttpEntity entity = response.getEntity();
        responseCode = response.getStatusLine().getStatusCode();

        String result = EntityUtils.toString(entity, "UTF-8");

        if (responseCode != HttpStatus.SC_OK) {
            Log.i(TAG, "Non-200 HTTP response(" + Integer.toString(responseCode) + " for URL " + url);
            throw new HttpException(Integer.toString(responseCode));
        }
        return result;
    }
}
