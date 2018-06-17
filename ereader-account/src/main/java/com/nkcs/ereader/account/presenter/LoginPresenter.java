package com.nkcs.ereader.account.presenter;

import android.support.annotation.NonNull;

import com.nkcs.ereader.account.contract.LoginContract;
import com.nkcs.ereader.account.net.AccountAPI;
import com.nkcs.ereader.account.repository.AccountRepository;
import com.nkcs.ereader.base.net.session.Session;
import com.nkcs.ereader.base.subscriber.ResponseSubscriber;

/**
 * @author faunleaf
 * @date 2018/6/15
 */

public class LoginPresenter implements LoginContract.IPresenter {

    private LoginContract.IView mView;
    private AccountRepository mAccountRepository;

    public LoginPresenter(@NonNull LoginContract.IView view, AccountRepository accountRepository) {
        mView = view;
        mView.setPresenter(this);
        mAccountRepository = accountRepository;
    }

    @Override
    public void login(String openid) {
        mView.startLoading();
        mAccountRepository.askSession(openid).subscribe(new ResponseSubscriber<String>() {

            @Override
            protected void onSuccess(String token) {
                mView.onLogin(token);
            }

            @Override
            protected void onFailure(Throwable e) {
                mView.showTips(e.getMessage());
            }

            @Override
            public void onComplete() {
                mView.stopLoading();
            }
        });
    }
}
