package com.nkcs.ereader.base.repository;

import com.nkcs.ereader.base.R;
import com.nkcs.ereader.base.db.DaoSession;
import com.nkcs.ereader.base.db.DbHelper;

import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.ObservableTransformer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

/**
 * @author faunleaf
 * @date 2018/3/18
 */

public abstract class BaseRepository {

    protected RxLifecycleBinder mLifecycleBinder;

    protected DaoSession mSession;

    public BaseRepository(RxLifecycleBinder binder) {
        mLifecycleBinder = binder;
        mSession = DbHelper.getInstance().getSession();
    }

    /**
     * 默认rx配置
     * @return ObservableTransformer
     */
    public <T> ObservableTransformer<T, T> defaultRxConfig() {
        if (mLifecycleBinder == null) {
            return tObservable -> tObservable
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribeOn(Schedulers.io());
        }
        return tObservable -> tObservable
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .compose(mLifecycleBinder.bindLifecycle());
    }
}
