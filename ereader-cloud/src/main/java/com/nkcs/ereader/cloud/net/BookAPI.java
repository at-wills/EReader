package com.nkcs.ereader.cloud.net;

import com.nkcs.ereader.base.net.EndpointConfig;
import com.nkcs.ereader.base.net.ServiceFactory;
import com.nkcs.ereader.cloud.entity.CloudBook;

import java.io.File;
import java.util.List;

import io.reactivex.Observable;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Response;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Query;

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

        /**
         * 删除书籍
         *
         * @param bookId
         * @return
         */
        @POST("removeBook")
        Observable<Response<String>> removeBook(@Query("id") Long bookId);

        /**
         * 上传书籍
         *
         * @param title
         * @param file
         * @return
         */
        @Multipart
        @POST("uploadBook")
        Observable<Response<String>> uploadBook(@Query("title") String title,
                                                @Part MultipartBody.Part file);
    }

    public static Api getInstance() {
        return ServiceFactory.createService(EndpointConfig.BOOK_ENDPOINT, Api.class);
    }
}
