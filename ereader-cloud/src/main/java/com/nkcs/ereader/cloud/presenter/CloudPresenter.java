package com.nkcs.ereader.cloud.presenter;

import android.support.annotation.NonNull;

import com.nkcs.ereader.base.entity.Book;
import com.nkcs.ereader.base.subscriber.CommonSubscriber;
import com.nkcs.ereader.base.subscriber.ResponseSubscriber;
import com.nkcs.ereader.cloud.contract.CloudContract;
import com.nkcs.ereader.cloud.entity.CloudBook;
import com.nkcs.ereader.cloud.net.BookAPI;
import com.nkcs.ereader.cloud.repository.BookRepository;

import java.util.List;

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

    @Override
    public void getBookList() {
        mBookRepository.getBookList().subscribe(new ResponseSubscriber<List<CloudBook>>() {

            @Override
            protected void onSuccess(List<CloudBook> bookList) {
                mView.onGetBookList(bookList);
            }

            @Override
            protected void onFailure(Throwable e) {
                mView.showTips(e.getMessage());
            }
        });
    }

    @Override
    public void getLocalBookList() {
        mBookRepository.getLocalBookList().subscribe(new CommonSubscriber<List<Book>>() {

            @Override
            protected void onSuccess(List<Book> bookList) {
                mView.onGetLocalBookList(bookList);
            }

            @Override
            protected void onFailure(Throwable e) {
                mView.showTips(e.getMessage());
            }
        });
    }

    @Override
    public void removeBook(Long bookId) {
        mView.startLoading();
        mBookRepository.removeBook(bookId).subscribe(new ResponseSubscriber<String>() {

            @Override
            protected void onSuccess(String message) {
                getBookList();
                mView.stopLoading();
            }

            @Override
            protected void onFailure(Throwable e) {
                mView.showTips(e.getMessage());
                mView.stopLoading();
            }
        });
    }

    @Override
    public void uploadBook(Book book) {
        mView.startLoading();
        mBookRepository.uploadBook(book).subscribe(new ResponseSubscriber<String>() {

            @Override
            protected void onSuccess(String message) {
                getBookList();
                mView.stopLoading();
            }

            @Override
            protected void onFailure(Throwable e) {
                mView.showTips(e.getMessage());
                mView.stopLoading();
            }
        });
    }
}
