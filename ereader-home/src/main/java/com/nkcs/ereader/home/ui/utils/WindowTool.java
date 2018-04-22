package com.nkcs.ereader.home.ui.utils;

import android.app.Activity;
import android.app.Fragment;
import android.os.Build;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;

import com.nkcs.ereader.base.ui.fragment.BaseFragment;
import com.nkcs.ereader.home.R;

/**
 * Created by 王利通 on 2018/4/22.
 */

public class WindowTool {
    private BaseFragment fragment;

    public WindowTool( BaseFragment fragment) {
        this.fragment = fragment;
    }

    public void setFullWindow() {
        fragment.getActivity().getWindow().clearFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        fragment.getActivity().getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setPlaceHolderHeight(0);
    }

    private void setPlaceHolderHeight(int height) {
        View placeholder = fragment.findViewById(R.id.placeholder);
        ViewGroup.LayoutParams params = placeholder.getLayoutParams();
        params.height = height;
        placeholder.setLayoutParams(params);
    }

    public void setNoLimitsWindow() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            fragment.getActivity().getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
            fragment.getActivity().getWindow().addFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        }
        setPlaceHolderHeight(getStatusBarHeight());
    }

    private int getStatusBarHeight() {
        int result = 0;
        int resourceId = fragment.getResources().getIdentifier("status_bar_height",
                "dimen", "android");
        if (resourceId > 0) {
            result = fragment.getResources().getDimensionPixelSize(resourceId);
        }
        return result;
    }
}
