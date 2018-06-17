package com.nkcs.ereader.account.ui.activity;

import com.alibaba.android.arouter.facade.annotation.Autowired;
import com.alibaba.android.arouter.facade.annotation.Route;
import com.nkcs.ereader.account.R;
import com.nkcs.ereader.account.presenter.LoginPresenter;
import com.nkcs.ereader.account.repository.AccountRepository;
import com.nkcs.ereader.account.ui.fragment.LoginFragment;
import com.nkcs.ereader.base.router.RouterConstant;
import com.nkcs.ereader.base.ui.activity.BaseActivity;

/**
 * @author faunleaf
 * @date 2018/6/11
 */

@Route(path = RouterConstant.LOGIN_PAGE)
public class LoginActivity extends BaseActivity {

    @Autowired(required = true)
    String target = "";

    LoginFragment mLoginFragment;

    @Override
    protected int getLayoutResource() {
        return R.layout.activity_login;
    }

    @Override
    protected int getFragmentContainerId() {
        return R.id.fl_login_activity;
    }

    @Override
    protected void onInitView() {
        mLoginFragment = LoginFragment.newInstance(target, getIntent().getExtras());
        new LoginPresenter(mLoginFragment, new AccountRepository(this));
        addFragment(mLoginFragment);
    }
}
