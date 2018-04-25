package com.nkcs.ereader.home.ui.utils;

import android.os.Build;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;

import com.nkcs.ereader.base.ui.fragment.BaseFragment;
import com.nkcs.ereader.base.utils.LogUtils;
import com.nkcs.ereader.home.R;

/**
 * Created by leto on 2018/4/22.
 */

public class WindowTool {
    private BaseFragment fragment;

    public WindowTool(BaseFragment fragment) {
        this.fragment = fragment;
    }

    private void setViewHeight(int viewId, int height) {
        View view = fragment.findViewById(viewId);
        ViewGroup.LayoutParams params = view.getLayoutParams();
        params.height = height;
        LogUtils.e(height);
        view.setLayoutParams(params);
    }

    public void setFullWindow() {
        fragment.getActivity().getWindow().clearFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        fragment.getActivity().getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setViewHeight(R.id.placeholder, 0);
        setViewHeight(R.id.title, (int) fragment.getActivity().getResources().getDimension(R.dimen.homepage_title_height_full_screen));
    }

    public void setNoLimitsWindow() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            fragment.getActivity().getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
            fragment.getActivity().getWindow().addFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        }
        setViewHeight(R.id.placeholder, getStatusBarHeight());
        setViewHeight(R.id.title, (int) fragment.getActivity().getResources().getDimension(R.dimen.homepage_title_height));
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
