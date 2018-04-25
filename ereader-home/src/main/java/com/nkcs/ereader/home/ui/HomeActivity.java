package com.nkcs.ereader.home.ui;

import com.nkcs.ereader.base.ui.activity.BaseActivity;
import com.nkcs.ereader.home.R;
import com.nkcs.ereader.home.ui.fragment.HomeFragment;

public class HomeActivity extends BaseActivity {

    HomeFragment homeFragment;

    @Override
    protected int getLayoutResource() {
        return R.layout.activity_home;
    }

    @Override
    protected int getFragmentContainerId() {
        return R.id.fl_home_activity;
    }

    @Override
    protected void onInitView() {
        homeFragment = getStoredFragment(HomeFragment.class);
        addFragment(homeFragment);
    }

}
