package com.nkcs.ereader.base.net;

import com.nkcs.ereader.base.net.session.Session;

import org.greenrobot.eventbus.EventBus;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

/**
 * @author faunleaf
 * @date 2018/6/16
 */

public class TokenCheckInterceptor implements Interceptor {

    private final static String ACCESS_TOKEN_HEADER = "Authorization";
    private final static String ACCEPT_HEADER = "Accept";

    private final static int UNAUTHORIZED_CODE = 401;

    @Override
    public Response intercept(Chain chain) throws IOException {
        Request request = chain.request();
        Request.Builder builder = request.newBuilder();
        builder.header(ACCEPT_HEADER, "application/json");
        Request modifyRequest = builder.build();

        Response response = chain.proceed(modifyRequest);
        if (response.code() == UNAUTHORIZED_CODE) {
            Session.getInstance().logout();
        }
        return response;
    }
}
