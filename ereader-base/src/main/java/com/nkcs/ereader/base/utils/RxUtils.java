package com.nkcs.ereader.base.utils;

import com.nkcs.ereader.base.ui.activity.BaseActivity;
import com.trello.rxlifecycle2.LifecycleTransformer;

import java.util.concurrent.Callable;

import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.ObservableTransformer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;

/**
 * @author faunleaf
 * @date 2018/3/20
 */

public class RxUtils {

    /**
     * 默认net配置
     * @return ObservableTransformer
     */
    public static <T> ObservableTransformer<T, T> defaultNetConfig(BaseActivity activity) {
        final LifecycleTransformer<T> transformer = activity.bindLifecycle();
        return tObservable -> tObservable
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .compose(transformer);
    }

    /**
     * 默认net配置
     * @return ObservableTransformer
     */
    public static <T> ObservableTransformer<T, T> defaultNetConfig() {
        return tObservable -> tObservable
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io());
    }

    /**
     * 默认db配置
     * @return ObservableTransformer
     */
    public static <T> ObservableTransformer<T, T> defaultDbConfig() {
        return tObservable -> tObservable
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io());
    }

    /**
     * db转Observable
     * @param callable
     * @return Observable
     */
    public static <T> Observable<T> dbToObservable(Callable<T> callable) {
        return Observable.defer(() -> {
            try {
                return Observable.just(callable.call());
            } catch (Exception e) {
                return Observable.error(e);
            }
        });
    }
}
