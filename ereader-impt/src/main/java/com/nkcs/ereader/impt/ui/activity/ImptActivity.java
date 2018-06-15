package com.nkcs.ereader.impt.ui.activity;

import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;

import com.nkcs.ereader.base.ui.activity.BaseActivity;
import com.nkcs.ereader.impt.R;
import com.nkcs.ereader.base.ui.activity.RouterConstant;
import com.nkcs.ereader.impt.eventbus.Eventbuses;
import com.nkcs.ereader.impt.presenter.ImptPresenter;
import com.nkcs.ereader.impt.repository.FileRepository;
import com.nkcs.ereader.impt.ui.fragment.ImptFragment;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;



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
        new ImptPresenter(mImptFragment, new FileRepository(this));
        addFragment(mImptFragment);
    }
}
