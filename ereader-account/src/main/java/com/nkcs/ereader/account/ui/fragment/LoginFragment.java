package com.nkcs.ereader.account.ui.fragment;

import android.Manifest;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.alibaba.android.arouter.launcher.ARouter;
import com.nkcs.ereader.account.R;
import com.nkcs.ereader.account.contract.LoginContract;
import com.nkcs.ereader.base.net.session.Session;
import com.nkcs.ereader.base.ui.fragment.BaseFragment;
import com.nkcs.ereader.base.utils.LogUtils;
import com.nkcs.ereader.base.utils.PermissionUtils;
import com.nkcs.ereader.base.utils.StringUtils;
import com.nkcs.ereader.base.utils.ToastUtils;
import com.tencent.connect.common.Constants;
import com.tencent.tauth.IUiListener;
import com.tencent.tauth.Tencent;
import com.tencent.tauth.UiError;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * @author faunleaf
 * @date 2018/6/11
 */

public class LoginFragment extends BaseFragment implements LoginContract.IView {

    private Bundle arguments;
    private LoginContract.IPresenter mPresenter;

    private ImageView mIvBack;

    private Button mBtnLogin;

    public static LoginFragment newInstance(Bundle bundle) {
        LoginFragment loginFragment = new LoginFragment();
        loginFragment.setArguments(new Bundle(bundle));
        return loginFragment;
    }

    @Override
    protected int getLayoutResource() {
        return R.layout.fragment_login;
    }

    @Override
    protected void onInitView() {
        initDataFromIntent();

        // 后退
        mIvBack = findViewById(R.id.login_iv_back);
        mIvBack.setOnClickListener(v -> {
            removeFragment();
        });

        // 登录
        mBtnLogin = findViewById(R.id.login_btn_login);
        mBtnLogin.setOnClickListener((v) -> {
            Tencent tencent = Tencent.createInstance("1106978042", getHoldingActivity().getApplicationContext());
            tencent.login(this, "get_simple_userinfo", new BaseUiListener());
        });
    }

    private void initDataFromIntent() {
        if (getArguments() != null) {
            arguments = getArguments();
        }
    }

    @Override
    public void setPresenter(LoginContract.IPresenter presenter) {
        mPresenter = presenter;
    }

    @Override
    public void onLogin(String token) {
        Session.getInstance().setUserToken(token);
        String target = arguments.getString("target");
        if (target != null && !"".equals(target)) {
            ARouter.getInstance().build(arguments.getString("target"))
                    .with(arguments)
                    .navigation();
        }
        removeFragment();
    }

    @Override
    public void startLoading() {
        showLoadingDialog();
    }

    @Override
    public void stopLoading() {
        hideLoadingDialog();
    }

    @Override
    public void showTips(String text) {
        ToastUtils.showText(text);
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        Tencent.onActivityResultData(requestCode, resultCode, data, new BaseUiListener());

        if (requestCode == Constants.REQUEST_API && resultCode == Constants.REQUEST_LOGIN) {
            Tencent.handleResultData(data, new BaseUiListener());
        }
    }

    private class BaseUiListener implements IUiListener {

        @Override
        public void onComplete(Object response) {
            try {
                String openid = ((JSONObject) response).getString("openid");
                mPresenter.login(openid);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void onError(UiError uiError) {
            ToastUtils.showText(uiError.errorMessage);
        }

        @Override
        public void onCancel() {
        }
    }
}
