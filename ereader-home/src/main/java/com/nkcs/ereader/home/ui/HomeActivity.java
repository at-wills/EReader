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
//        new ReadPresenter(mReadFragment, new BookRepository(this), new ConfigRepository(this));
        addFragment(homeFragment);
    }

//    @Override
//    public void onBackPressed() {
//        if (drawer.isDrawerOpen(GravityCompat.START)) {
//            drawer.closeDrawer(GravityCompat.START);
//        } else {
//            if (doubleBackToExitFirst) {
//                super.onBackPressed();
//                return;
//            }
//            doubleBackToExitFirst = true;
//            Toast.makeText(this, "再次按下返回键退出应用程序", Toast.LENGTH_SHORT).show();
//            new Handler().postDelayed(() -> doubleBackToExitFirst = false, 2000);
//        }
//    }

}
