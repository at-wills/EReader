package com.nkcs.ereader.home.ui.utils;

import android.app.Activity;
import android.os.Build;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;

import com.nkcs.ereader.home.R;

/**
 * Created by 王利通 on 2018/4/22.
 */

public class WindowTool {
    private Activity activity;

    public WindowTool(Activity activity) {
        this.activity = activity;
    }

    public void setFullWindow() {
        activity.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        activity.getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setPlaceHolderHeight(0);
    }

    private void setPlaceHolderHeight(int height) {
        View placeholder = activity.findViewById(R.id.placeholder);
        ViewGroup.LayoutParams params = placeholder.getLayoutParams();
        params.height = height;
        placeholder.setLayoutParams(params);
    }

    public void setNoLimitsWindow() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            activity.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
            activity.getWindow().addFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        }
        setPlaceHolderHeight(getStatusBarHeight());
    }

    private int getStatusBarHeight() {
        int result = 0;
        int resourceId = activity.getResources().getIdentifier("status_bar_height",
                "dimen", "android");
        if (resourceId > 0) {
            result = activity.getResources().getDimensionPixelSize(resourceId);
        }
        return result;
    }
}
