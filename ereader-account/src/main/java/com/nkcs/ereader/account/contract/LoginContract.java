package com.nkcs.ereader.account.contract;

import com.nkcs.ereader.base.contract.BasePresenter;
import com.nkcs.ereader.base.contract.BaseView;

/**
 * @author faunleaf
 * @date 2018/3/19
 */

public interface LoginContract {

    interface IView extends BaseView<IPresenter> {

        void onLogin(String token);

        void startLoading();

        void stopLoading();

        void showTips(String text);
    }

    interface IPresenter extends BasePresenter {

        void login(String openid);
    }
}
