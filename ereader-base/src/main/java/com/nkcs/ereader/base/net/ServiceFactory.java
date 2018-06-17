package com.nkcs.ereader.base.net;

import android.support.annotation.NonNull;

import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * @author faunleaf
 * @date 2018/6/16
 */

public class ServiceFactory {

    private final static GsonConverterFactory gsonConverterFactory = GsonConverterFactory.create(new GsonBuilder()
            .setLenient()
            .setDateFormat("yyyy-MM-dd HH:mm:ss")
            .setFieldNamingPolicy(FieldNamingPolicy.IDENTITY)
            .create());

    private final static long CONNECTION_TIMEOUT = 30L;

    public static <T> T createService(String endpoint, Class<T> serviceClass) {
        return getRetrofitBuilder(endpoint, 0).build().create(serviceClass);
    }

    public static <T> T createService(String endpoint, Class<T> serviceClass, int timeout) {
        return getRetrofitBuilder(endpoint, timeout).build().create(serviceClass);
    }

    @NonNull
    private static Retrofit.Builder getRetrofitBuilder(String endpoint, int timeout) {
        return new Retrofit.Builder()
                .baseUrl(endpoint)
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .addConverterFactory(gsonConverterFactory)
                .client(getOkHttpClient(new TokenCheckInterceptor(), timeout));
    }

    private static OkHttpClient getOkHttpClient(TokenCheckInterceptor interceptor, int timeout) {
        OkHttpClient.Builder okClientBuilder = new OkHttpClient.Builder();
        okClientBuilder.addInterceptor(interceptor);
        setTimeout(timeout, okClientBuilder);
        return okClientBuilder.build();
    }

    private static void setTimeout(int timeout, OkHttpClient.Builder okClientBuilder) {
        if (timeout == 0) {
            okClientBuilder.connectTimeout(CONNECTION_TIMEOUT, TimeUnit.SECONDS)
                    .readTimeout(CONNECTION_TIMEOUT, TimeUnit.SECONDS)
                    .writeTimeout(CONNECTION_TIMEOUT, TimeUnit.SECONDS);
        } else {
            okClientBuilder.connectTimeout(timeout, TimeUnit.SECONDS)
                    .readTimeout(timeout, TimeUnit.SECONDS)
                    .writeTimeout(timeout, TimeUnit.SECONDS);
        }
    }
}
