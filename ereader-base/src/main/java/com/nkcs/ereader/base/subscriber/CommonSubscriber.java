package com.nkcs.ereader.base.subscriber;

import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;

/**
 * @author faunleaf
 * @date 2018/3/24
 */

public abstract class CommonSubscriber<T> implements Observer<T> {

    protected abstract void onSuccess(T t);

    protected abstract void onFailure(Throwable e);

    @Override
    public void onSubscribe(Disposable d) {}

    @Override
    public void onNext(T t) {
        onSuccess(t);
    }

    @Override
    public void onError(Throwable e) {
        onFailure(e);
    }

    @Override
    public void onComplete() {}
}
