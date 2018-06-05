package com.nkcs.ereader.impt.ui.activity;

import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.nkcs.ereader.base.ui.activity.BaseActivity;
import com.nkcs.ereader.impt.R;
import com.nkcs.ereader.base.ui.activity.RouterConstant;
import com.nkcs.ereader.impt.eventbus.Eventbuses;
import com.nkcs.ereader.impt.ui.fragment.ImptFragment;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;


@Route(path = RouterConstant.IMPORT_PAGE)
public class ImptActivity extends BaseActivity {

    ImptFragment mImptFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    protected int getLayoutResource() {
        return R.layout.activity_impt;
    }

    @Override
    protected int getFragmentContainerId() {
        return R.id.fl_impt_activity;
    }

    @Override
    protected void onInitView() {
        mImptFragment = getStoredFragment(ImptFragment.class);
        addFragment(mImptFragment);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_DOWN) {
            mImptFragment.cancleSelect();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    }
