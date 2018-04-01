package com.nkcs.ereader.base.ui.activity;

import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.annotation.Nullable;

import com.nkcs.ereader.base.BaseApplication;
import com.nkcs.ereader.base.repository.RxLifecycleBinder;
import com.nkcs.ereader.base.ui.fragment.BaseFragment;
import com.trello.rxlifecycle2.LifecycleTransformer;

/**
 * @author faunleaf
 * @date 2018/3/17
 */

public abstract class BaseActivity extends RxAppCompatActivity implements RxLifecycleBinder {

    public BaseApplication application;

    // region 初始化相关

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initConfigBeforeSetContentView();
        if (getLayoutResource() != 0) {
            setContentView(getLayoutResource());
        }
        onInitData();
        onInitView();
    }

    /**
     * 获取layout
     *
     * @return int
     */
    protected abstract int getLayoutResource();

    /**
     * 获取fragment
     *
     * @return int
     */
    protected abstract int getFragmentContainerId();

    /**
     * 初始化视图
     */
    protected abstract void onInitView();

    /**
     * 初始化数据，在初始化视图前调用
     */
    protected void onInitData() {
        application = (BaseApplication) getApplication();
    }

    /**
     * 设置布局前调用该方法
     */
    protected void initConfigBeforeSetContentView() {
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
    }

    // endregion

    // region RxLifecycleBinder方法重写

    @Override
    public <T> LifecycleTransformer<T> bindLifecycle() {
        return bindToLifecycle();
    }

    // endregion

    // region Fragment导航相关

    /**
     * @param fragment
     */
    public void addFragment(BaseFragment fragment) {
        if (fragment != null) {
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(getFragmentContainerId(), fragment, fragment.getClass().getName())
                    .addToBackStack(fragment.getClass().getName())
                    .commitAllowingStateLoss();
        }
    }

    /**
     * @param fragment
     */
    public void replaceFragment(BaseFragment fragment) {
        if (fragment != null) {
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(getFragmentContainerId(), fragment, fragment.getClass().getName())
                    .commitAllowingStateLoss();
        }
    }

    /**
     * @param clazz
     * @return BaseFragment
     */
    public <T extends BaseFragment> T getStoredFragment(Class clazz) {
        T fragment = (T) getSupportFragmentManager().findFragmentByTag(clazz.getName());
        if (fragment == null) {
            try {
                fragment = (T) Class.forName(clazz.getName()).newInstance();
            } catch (Exception e) {
                throw new RuntimeException("Cannot create " + clazz.getName());
            }
        }
        return fragment;
    }

    /**
     *
     */
    public void removeFragment() {
        if (getSupportFragmentManager().getBackStackEntryCount() > 1) {
            getSupportFragmentManager().popBackStack();
        } else {
            finish();
        }
    }

    @Override
    public void onBackPressed() {
        removeFragment();
    }

    // endregion
}
