package com.nkcs.ereader.base.ui.widget;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.Gravity;
import android.view.Window;
import android.view.WindowManager;

/**
 * @author faunleaf
 * @date 2018/4/14
 */

public abstract class BaseDialog extends Dialog {

    public BaseDialog(@NonNull Context context) {
        super(context);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(getLayoutResource());
        onInitWindow();
        onInitView();
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
     * 初始化窗口
     */
    protected void onInitWindow() {
        Window window = getWindow();
        WindowManager.LayoutParams lp = window.getAttributes();
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
        lp.gravity = Gravity.BOTTOM;
        window.setAttributes(lp);
    }
}
