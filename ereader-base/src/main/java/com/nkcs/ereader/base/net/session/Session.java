package com.nkcs.ereader.base.net.session;

import android.content.Context;
import android.content.SharedPreferences;

import com.nkcs.ereader.base.BaseApplication;

/**
 * @author faunleaf
 * @date 2018/6/16
 */

public class Session {

    private final static Session INSTANCE = new Session();

    private static final String SYSTEM_CONFIG = "system_config";

    private static final String SESSION_USER_TOKEN = "user_token";

    private String mUserToken;

    private SharedPreferences mSharedPreferences;

    public static Session getInstance() {
        return INSTANCE;
    }

    public Session() {
        mSharedPreferences = BaseApplication.getContext().getSharedPreferences(SYSTEM_CONFIG, Context.MODE_PRIVATE);
        mUserToken = mSharedPreferences.getString(SESSION_USER_TOKEN, null);
    }

    public void setUserToken(String token) {
        mUserToken = token;
        mSharedPreferences.edit()
                .putString(SESSION_USER_TOKEN, mUserToken)
                .apply();
    }

    public String getUserToken() {
        return mUserToken;
    }

    public boolean isLogin() {
        return mUserToken != null;
    }

    public void logout() {
        mUserToken = null;
        mSharedPreferences.edit()
                .remove(SESSION_USER_TOKEN)
                .apply();
    }
}
