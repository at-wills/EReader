package com.nkcs.ereader.home.ui;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.NavigationView;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.widget.SwitchCompat;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.CompoundButton;
import android.widget.Switch;

import com.nkcs.ereader.base.ui.activity.BaseActivity;
import com.nkcs.ereader.home.R;
import com.nkcs.ereader.home.presenter.HomePresenter;
import com.nkcs.ereader.home.repository.HomeRepository;
import com.nkcs.ereader.home.ui.fragment.HomeFragment;
import com.nkcs.ereader.home.ui.utils.SharedPreferenceManager;

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
                .setAnimationInterval(3000)
                .create();
        openingStartAnimation.show(this);
        homeFragment = getStoredFragment(HomeFragment.class);
        new HomePresenter(homeFragment, new HomeRepository(this));
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
