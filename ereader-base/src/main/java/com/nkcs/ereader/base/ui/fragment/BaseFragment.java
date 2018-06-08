package com.nkcs.ereader.base.ui.fragment;

import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.IdRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.nkcs.ereader.base.ui.activity.BaseActivity;
import com.nkcs.ereader.base.utils.LogUtils;
import com.nkcs.ereader.base.utils.StringUtils;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author faunleaf
 * @date 2018/3/19
 */

public abstract class BaseFragment extends Fragment {

    protected View mRootView;
    protected BaseActivity mActivity;

    public <T extends View> T findViewById(@IdRes int id){
        return mRootView.findViewById(id);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if (mRootView == null) {
            mRootView = inflater.inflate(getLayoutResource(), container, false);
            onInitView();
            onLoadData();
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
     * 返回值拦截
     */
    public boolean onBackPressed() {
        return false;
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
     * 加载数据，在初始视图之后
     */
    protected void onLoadData() {}

    /**
     * 获取父activity
     *
     * @return BaseActivity
     */
    protected BaseActivity getHoldingActivity() {
        return mActivity;
    }

    // region 请求权限相关

    private Map<Integer, Pair<Method, Object[]>> requests = new HashMap<>();

    /**
     * 请求权限之后，执行方法
     */
    protected void callWithPermissions(String methodName, Object... args) {
        Method method = getMethod(methodName, args);
        if (method == null) {
            return;
        }
        String[] permissions = method.getAnnotation(NeedsPermission.class).permissions();
        boolean hasPermission = true;
        for (String permission : permissions) {
            if (ActivityCompat.checkSelfPermission(getHoldingActivity(), permission)
                    != PackageManager.PERMISSION_GRANTED) {
                hasPermission = false;
            }
        }

        if (hasPermission) {
            try {
                method.invoke(this, args);
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            int requestCode = method.hashCode() & 0xFFFF;
            requests.put(requestCode, new Pair<>(method, args));
            requestPermissions(permissions, requestCode);
        }
    }

    private Method getMethod(String methodName, Object... args) {
        Method[] methods = getClass().getDeclaredMethods();
        for (Method method : methods) {
            if (method.getName().equals(methodName) && method.getAnnotation(NeedsPermission.class) != null
                    && isArgumentsMatch(method.getParameterTypes(), args)) {
                return method;
            }
        }
        return null;
    }

    private boolean isArgumentsMatch(Class[] clazzArray, Object[] args) {
        if (clazzArray.length != args.length) {
            return false;
        }
        for (int i = 0; i < clazzArray.length; ++i) {
            if (!clazzArray[i].isInstance(args[i])) {
                return false;
            }
        }
        return true;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        Pair<Method, Object[]> pair = requests.get(requestCode);
        String[] permissionsText = pair.first.getAnnotation(NeedsPermission.class).permissionsText();
        List<String> deniedPermissions = new ArrayList<>();
        for (int i = 0; i < grantResults.length; ++i) {
            if (grantResults[i] != PackageManager.PERMISSION_GRANTED) {
                deniedPermissions.add(permissionsText[i]);
            }
        }

        if (deniedPermissions.size() == 0) {
            try {
                pair.first.invoke(this, pair.second);
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            new AlertDialog.Builder(getHoldingActivity())
                    .setMessage("执行该操作需要权限：" + StringUtils.join("、", deniedPermissions))
                    .setNegativeButton("取消操作", null)
                    .setPositiveButton("去设置", (dialog, which) -> {})
                    .setCancelable(false)
                    .show();
        }
    }

    /**
     * 进行权限请求的方法不要重载
     */
    @Target(ElementType.METHOD)
    @Retention(RetentionPolicy.RUNTIME)
    public @interface NeedsPermission {
        String[] permissions();
        String[] permissionsText();
    }

    // endregion

    // region Fragment导航相关

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

    // endregion
}
