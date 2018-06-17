package com.nkcs.ereader.account.router;

import android.content.Context;
import android.content.DialogInterface;

import com.alibaba.android.arouter.facade.Postcard;
import com.alibaba.android.arouter.facade.annotation.Interceptor;
import com.alibaba.android.arouter.facade.callback.InterceptorCallback;
import com.alibaba.android.arouter.facade.template.IInterceptor;
import com.alibaba.android.arouter.launcher.ARouter;
import com.nkcs.ereader.base.net.session.Session;
import com.nkcs.ereader.base.router.RouterConstant;

/**
 * @author faunleaf
 * @date 2018/6/16
 */

@Interceptor(priority = 10)
public class LoginInterceptor implements IInterceptor {

    private String[] shouldLogin = { RouterConstant.CLOUD_PAGE };

    @Override
    public void process(Postcard postcard, InterceptorCallback callback) {
        for (String path : shouldLogin) {
            if (path.equals(postcard.getPath()) && !Session.getInstance().isLogin()) {
                callback.onInterrupt(null);
                ARouter.getInstance().build(RouterConstant.LOGIN_PAGE)
                        .with(postcard.getExtras())
                        .withString("target", postcard.getPath())
                        .navigation();
            }
        }
        callback.onContinue(postcard);
    }

    @Override
    public void init(Context context) {

    }
}
