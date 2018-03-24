package com.nkcs.ereader.base;

import android.app.Application;
import android.content.Context;

/**
 * @author faunleaf
 * @date 2018/3/19
 */

public abstract class BaseApplication extends Application {

    private static Context sInstance;

    @Override
    public void onCreate() {
        super.onCreate();
        sInstance = this;
    }

    public static Context getContext() {
        return sInstance;
    }
}
