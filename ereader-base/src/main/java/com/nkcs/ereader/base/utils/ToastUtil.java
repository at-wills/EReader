package com.nkcs.ereader.base.utils;

import android.widget.Toast;

import com.nkcs.ereader.base.BaseApplication;

/**
 * @author faunleaf
 * @date 2018/3/24
 */

public class ToastUtil {

    public static void showText(String text) {
        Toast.makeText(BaseApplication.getContext(), text, Toast.LENGTH_SHORT).show();
    }
}
