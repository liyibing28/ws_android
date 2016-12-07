package com.example.lyb.wsandorid.auth.http;

import org.apache.http.HttpException;

/**
 * Created by liyibing on 2016/12/7.
 */

public class HttpAuthenticationFailedException extends HttpException {
    private static final long serialVersionUID = 1L;

    public HttpAuthenticationFailedException(Exception e) {
        super(e);
    }

    public HttpAuthenticationFailedException(String msg) {
        super(msg);
    }
}
