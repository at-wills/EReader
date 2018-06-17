package com.nkcs.ereader.cloud.net;

import com.nkcs.ereader.base.net.EndpointConfig;
import com.nkcs.ereader.base.net.ServiceFactory;
import com.nkcs.ereader.cloud.entity.CloudBook;

import java.util.List;

import io.reactivex.Observable;
import retrofit2.Response;
import retrofit2.http.POST;

/**
 * @author faunleaf
 * @date 2018/6/16
 */

public class BookAPI {

    public interface Api {

        /**
         * 获取云书架列表
         *
         * @return
         */
        @POST("getBookList")
        Observable<Response<List<CloudBook>>> getBookList();
    }

    public static Api getInstance() {
        return ServiceFactory.createService(EndpointConfig.BOOK_ENDPOINT, Api.class);
    }
}
