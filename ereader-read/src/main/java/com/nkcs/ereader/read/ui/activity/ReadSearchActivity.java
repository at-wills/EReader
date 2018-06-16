package com.nkcs.ereader.read.ui.activity;

import com.alibaba.android.arouter.facade.annotation.Autowired;
import com.alibaba.android.arouter.facade.annotation.Route;
import com.nkcs.ereader.base.entity.Book;
import com.nkcs.ereader.base.ui.activity.BaseActivity;
import com.nkcs.ereader.base.router.RouterConstant;
import com.nkcs.ereader.read.R;
import com.nkcs.ereader.read.presenter.ReadPresenter;
import com.nkcs.ereader.read.presenter.ReadSearchPresenter;
import com.nkcs.ereader.read.repository.BookRepository;
import com.nkcs.ereader.read.repository.ConfigRepository;
import com.nkcs.ereader.read.ui.fragment.ReadSearchFragment;

/**
 * @author faunleaf
 * @date 2018/6/11
 */

@Route(path = RouterConstant.READ_SEARCH_PAGE)
public class ReadSearchActivity extends BaseActivity {

    @Autowired(required = true)
    Long bookId = 1L;

    ReadSearchFragment mReadSearchFragment;

    @Override
    protected int getLayoutResource() {
        return R.layout.activity_read_search;
    }

    @Override
    protected int getFragmentContainerId() {
        return R.id.fl_read_search_activity;
    }

    @Override
    protected void onInitView() {
        mReadSearchFragment = ReadSearchFragment.newInstance(bookId);
        new ReadSearchPresenter(mReadSearchFragment, new BookRepository(this));
        addFragment(mReadSearchFragment);
    }
}
