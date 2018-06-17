package com.nkcs.ereader.cloud.presenter;

import android.support.annotation.NonNull;

import com.nkcs.ereader.cloud.contract.CloudContract;
import com.nkcs.ereader.cloud.repository.BookRepository;

/**
 * @author faunleaf
 * @date 2018/6/15
 */

public class CloudPresenter implements CloudContract.IPresenter {

    private CloudContract.IView mView;
    private BookRepository mBookRepository;

    public CloudPresenter(@NonNull CloudContract.IView view, BookRepository bookRepository) {
        mView = view;
        mView.setPresenter(this);
        mBookRepository = bookRepository;
    }

}
