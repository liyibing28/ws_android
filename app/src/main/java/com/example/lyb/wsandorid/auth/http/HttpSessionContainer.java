package com.example.lyb.wsandorid.auth.http;

import org.apache.http.client.protocol.ClientContext;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.protocol.BasicHttpContext;

/**
 * Created by liyibing on 2016/12/7.
 */

public enum HttpSessionContainer {

    INSTANCE;

    private final BasicCookieStore cookieStore;
    private final BasicHttpContext httpContext;

    HttpSessionContainer() {
        cookieStore = new BasicCookieStore();
        httpContext = new BasicHttpContext();
        httpContext.setAttribute(ClientContext.COOKIE_STORE, cookieStore);
    }

    public BasicHttpContext getSessionContext() {
        return httpContext;
    }
}

