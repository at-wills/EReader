package com.nkcs.ereader.read.ui.activity;

import com.alibaba.android.arouter.facade.annotation.Autowired;
import com.alibaba.android.arouter.facade.annotation.Route;
import com.nkcs.ereader.base.ui.activity.BaseActivity;
import com.nkcs.ereader.base.router.RouterConstant;
import com.nkcs.ereader.read.R;
import com.nkcs.ereader.read.presenter.ReadPresenter;
import com.nkcs.ereader.read.repository.BookRepository;
import com.nkcs.ereader.read.repository.ConfigRepository;
import com.nkcs.ereader.read.ui.fragment.ReadFragment;
import com.nkcs.ereader.read.ui.fragment.ReadSearchFragment;

/**
 * @author faunleaf
 * @date 2018/3/16
 */

@Route(path = RouterConstant.READ_PAGE)
public class ReadActivity extends BaseActivity {

    @Autowired(required = true)
    Long bookId = 0L;

    ReadFragment mReadFragment;

    @Override
    protected int getLayoutResource() {
        return R.layout.activity_read;
    }

    @Override
    protected int getFragmentContainerId() {
        return R.id.fl_read_activity;
    }

    @Override
    protected void onInitView() {
        mReadFragment = ReadFragment.newInstance(bookId);
        new ReadPresenter(mReadFragment, new BookRepository(this), new ConfigRepository(this));
        addFragment(mReadFragment);
    }
}
