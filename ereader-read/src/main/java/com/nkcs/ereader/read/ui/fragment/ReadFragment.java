package com.nkcs.ereader.read.ui.fragment;

import android.widget.TextView;

import com.nkcs.ereader.base.entity.Book;
import com.nkcs.ereader.base.ui.fragment.BaseFragment;
import com.nkcs.ereader.base.utils.ToastUtils;
import com.nkcs.ereader.read.R;
import com.nkcs.ereader.read.contract.ReadContract;

import java.util.List;

/**
 * @author faunleaf
 * @date 2018/3/19
 */

public class ReadFragment extends BaseFragment implements ReadContract.IView {

    private ReadContract.IPresenter mPresenter;

    private TextView mTextView;

    @Override
    protected int getLayoutResource() {
        return R.layout.fragment_read;
    }

    @Override
    protected void onInitView() {
        mTextView = (TextView) findViewById(R.id.read_content);

        mPresenter.getBooks();
    }

    @Override
    public void setPresenter(ReadContract.IPresenter presenter) {
        mPresenter = presenter;
    }

    @Override
    public void showBooks(List<Book> books) {
        mTextView.setText(books.toString());
    }

    @Override
    public void showTips(String text) {
        ToastUtils.showText(text);
    }
}
