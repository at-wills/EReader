package com.nkcs.ereader.read.presenter;

import android.support.annotation.NonNull;

import com.nkcs.ereader.base.entity.Book;
import com.nkcs.ereader.base.subscriber.BaseDbSubscriber;
import com.nkcs.ereader.read.contract.ReadContract;
import com.nkcs.ereader.read.repository.BookRepository;

import java.util.List;

/**
 * @author faunleaf
 * @date 2018/3/19
 */

public class ReadPresenter implements ReadContract.IPresenter {

    private ReadContract.IView mView;
    private BookRepository mRepository;

    public ReadPresenter(@NonNull ReadContract.IView view, BookRepository repository) {
        mView = view;
        mView.setPresenter(this);
        mRepository = repository;
    }

    @Override
    public void getBooks() {
        mRepository.getBooks().subscribe(new BookListSubscriber());
    }

    private class BookListSubscriber extends BaseDbSubscriber<List<Book>> {

        @Override
        protected void onSuccess(List<Book> books) {
            mView.onGetBooks(books);
        }

        @Override
        protected void onFailure(Throwable e) {
            mView.showTips(e.getMessage());
        }
    }
}
