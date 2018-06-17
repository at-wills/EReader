package com.nkcs.ereader.account.net;

import com.nkcs.ereader.base.net.EndpointConfig;
import com.nkcs.ereader.base.net.ServiceFactory;

import java.util.List;

import io.reactivex.Observable;
import retrofit2.Response;
import retrofit2.http.POST;
import retrofit2.http.Query;

/**
 * @author faunleaf
 * @date 2018/6/16
 */

public class AccountAPI {

    public interface Api {

        /**
         * 获取token
         *
         * @param openid
         * @return
         */
        @POST("askSession")
        Observable<Response<String>> askSession(@Query("openid") String openid);
    }

    public static Api getInstance() {
        return ServiceFactory.createService(EndpointConfig.ACCOUNT_ENDPOINT, Api.class);
    }
}
