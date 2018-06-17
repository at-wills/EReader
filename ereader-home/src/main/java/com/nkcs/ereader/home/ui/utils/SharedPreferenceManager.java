package com.nkcs.ereader.home.ui.utils;

import android.content.Context;
import android.content.SharedPreferences;

public class SharedPreferenceManager {
    // 书籍按照传统书架展示
    public static final String HOME_BOOK_SHOW_TRADITION = "HOME_BOOK_SHOW_TRADITION";
    // 书籍按照导入顺序排列
    public static final String HOME_BOOK_SORT_BY_TIME = "HOME_BOOK_SORT_BY_TIME";
    // window 是 nolimit （默认）还是全屏
    public static final String WINDOW_NO_LIMIT = "WINDOW_NO_LIMIT";

    private SharedPreferences settings;

    public SharedPreferenceManager(Context context) {
        String prefName = "EReaderHomePrefsFile";
        this.settings = context.getSharedPreferences(prefName, 0);
    }

    public Boolean getSetting(String key) {
        return settings.getBoolean(key, true);
    }

    public void setSetting(String key, Boolean var) {
        SharedPreferences.Editor editor = settings.edit();
        editor.putBoolean(key, var);
        editor.apply();
    }
}