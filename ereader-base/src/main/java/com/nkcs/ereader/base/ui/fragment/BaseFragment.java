package com.nkcs.ereader.base.ui.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.IdRes;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.nkcs.ereader.base.ui.activity.BaseActivity;

/**
 * @author faunleaf
 * @date 2018/3/19
 */

public abstract class BaseFragment extends Fragment {

    protected View mRootView;
    protected BaseActivity mActivity;

    public View findViewById(@IdRes int id){
        return mRootView.findViewById(id);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if (mRootView == null) {
            mRootView = inflater.inflate(getLayoutResource(), container, false);
            onInitView();
        }

        ViewGroup parentView = (ViewGroup) mRootView.getParent();
        if (parentView != null) {
            parentView.removeView(mRootView);
        }

        return mRootView;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mActivity = (BaseActivity) context;
    }

    /**
     * 获取layout
     *
     * @return int
     */
    protected abstract int getLayoutResource();

    /**
     * 初始化视图
     */
    protected abstract void onInitView();

    /**
     * 获取父activity
     *
     * @return BaseActivity
     */
    protected BaseActivity getHoldingActivity() {
        return mActivity;
    }

    /**
     * @param fragment
     */
    protected void addFragment(BaseFragment fragment) {
        if (fragment != null) {
            getHoldingActivity().addFragment(fragment);
        }
    }

    /**
     * @param fragment
     */
    protected void replaceFragment(BaseFragment fragment) {
        if (fragment != null){
            getHoldingActivity().replaceFragment(fragment);
        }
    }

    /**
     *
     */
    protected void removeFragment() {
        getHoldingActivity().removeFragment();
    }
}
