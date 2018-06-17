package com.nkcs.ereader.base.subscriber;

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

    protected abstract void onSuccess(T t);

    protected abstract void onFailure(Throwable e);

    @Override
    public void onSubscribe(Disposable d) {}

    @Override
    public void onNext(Response<T> response) {
        if (response.isSuccessful()) {
            onSuccess(response.body());
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
            return errorBody.string();
        } catch (IOException e) {
            return null;
        }
    }
}
