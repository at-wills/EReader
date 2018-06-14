package com.nkcs.ereader.home.ui;

import android.support.v4.content.ContextCompat;
import android.view.KeyEvent;

import com.nkcs.ereader.base.ui.activity.BaseActivity;
import com.nkcs.ereader.home.R;
import com.nkcs.ereader.home.ui.fragment.HomeFragment;

import site.gemus.openingstartanimation.NormalDrawStrategy;
import site.gemus.openingstartanimation.OpeningStartAnimation;

public class HomeActivity extends BaseActivity {

    public HomeFragment homeFragment;

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
        OpeningStartAnimation openingStartAnimation = new OpeningStartAnimation.Builder(this)
                .setDrawStategy(new NormalDrawStrategy())
                .setAppName("一读")
                .setAppStatement("read my love")
                .setAnimationInterval(2000)
                .create();
        openingStartAnimation.show(this);
        homeFragment = getStoredFragment(HomeFragment.class);
        addFragment(homeFragment);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        switch (keyCode) {
            case KeyEvent.KEYCODE_MENU:
                homeFragment.showMenu();
                break;
        }
        return super.onKeyDown(keyCode, event);
    }
}
