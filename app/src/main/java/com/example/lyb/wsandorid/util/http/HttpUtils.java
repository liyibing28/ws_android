package com.example.lyb.wsandorid.util.http;

import android.os.Build;

import com.example.lyb.wsandorid.BuildConfig;

import org.apache.http.client.HttpClient;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.params.HttpProtocolParams;

import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;

/**
 * Created by liyibing on 2016/12/7.
 */

public class HttpUtils {
    private static final int TIMEOUT_MS = 20000;
    //对url进行编码
    public static String encodeUrl(String urlString) throws MalformedURLException, URISyntaxException {
        URL url = new URL(urlString);
        URI uri = new URI(url.getProtocol(), url.getUserInfo(), url.getHost(), url.getPort(), url.getPath(), url.getQuery(), url.getRef());
        url = uri.toURL();
        return url.toString();
    }

    public static HttpClient getDefaultClient() {
        HttpParams httpParams = new BasicHttpParams();
        HttpConnectionParams.setConnectionTimeout(httpParams, TIMEOUT_MS);
        HttpConnectionParams.setSoTimeout(httpParams, TIMEOUT_MS);

        String userAgentString = new StringBuilder().
                append("WSAndroid ").append(BuildConfig.VERSION_NAME).append(" ")
                .append(Build.MANUFACTURER).append(" ")
                .append(Build.MODEL).append(" ")
                .append("Android v").append(Build.VERSION.RELEASE)
                .toString();

        httpParams.setParameter(HttpProtocolParams.USER_AGENT, userAgentString);

        return new DefaultHttpClient(httpParams);
    }
}
