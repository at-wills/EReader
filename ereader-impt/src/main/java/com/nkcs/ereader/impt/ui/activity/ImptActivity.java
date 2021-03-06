package com.nkcs.ereader.impt.ui.activity;

import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.nkcs.ereader.base.router.RouterConstant;
import com.nkcs.ereader.base.ui.activity.BaseActivity;
import com.nkcs.ereader.impt.R;
import com.nkcs.ereader.impt.presenter.ImptPresenter;
import com.nkcs.ereader.impt.repository.BookRepository;
import com.nkcs.ereader.impt.repository.FileRepository;
import com.nkcs.ereader.impt.ui.fragment.ImptFragment;


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
        new ImptPresenter(mImptFragment, new BookRepository(this), new FileRepository(this));
        addFragment(mImptFragment);
    }
}
