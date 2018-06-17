package com.nkcs.ereader.base.subscriber;

import org.json.JSONObject;

import java.io.IOException;

import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;
import okhttp3.ResponseBody;
import retrofit2.Response;

/**
 * @author faunleaf
 * @date 2018/3/24
 */

public abstract class ResponseSubscriber<T> implements Observer<Response<T>> {

    private final static int UNAUTHORIZED_CODE = 401;

    protected abstract void onSuccess(T t);

    protected abstract void onFailure(Throwable e);

    @Override
    public void onSubscribe(Disposable d) {}

    @Override
    public void onNext(Response<T> response) {
        if (response.isSuccessful()) {
            onSuccess(response.body());
        } else if (response.code() == UNAUTHORIZED_CODE){
            onFailure(new Throwable("您的登录已过时"));
        } else {
           String error = parseToNetworkErrorStr(response.errorBody());
           onFailure(new Throwable(error));
        }
    }

    @Override
    public void onError(Throwable e) {
        onFailure(e);
    }

    @Override
    public void onComplete() {}

    private String parseToNetworkErrorStr(ResponseBody errorBody) {
        if (errorBody == null) {
            return null;
        }
        try {
            JSONObject jsonObject = new JSONObject(errorBody.string());
            return jsonObject.getString("message");
        } catch (Exception e) {
            return null;
        }
    }
}
