package com.nkcs.ereader.read.presenter;

import android.support.annotation.NonNull;

import com.nkcs.ereader.base.entity.Book;
import com.nkcs.ereader.base.subscriber.CommonSubscriber;
import com.nkcs.ereader.read.contract.ReadSearchContract;
import com.nkcs.ereader.read.entity.RetrievalResult;
import com.nkcs.ereader.read.repository.BookRepository;

import java.util.List;

import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;

/**
 * @author faunleaf
 * @date 2018/6/15
 */

public class ReadSearchPresenter implements ReadSearchContract.IPresenter {

    private ReadSearchContract.IView mView;
    private BookRepository mBookRepository;

    private Disposable mFullTextDisp;

    public ReadSearchPresenter(@NonNull ReadSearchContract.IView view, BookRepository bookRepository) {
        mView = view;
        mView.setPresenter(this);
        mBookRepository = bookRepository;
    }

    @Override
    public void getBook(Long bookId) {
        mBookRepository.getBook(bookId).subscribe(new CommonSubscriber<Book>() {

            @Override
            protected void onSuccess(Book book) {
                mView.onGetBook(book);
            }

            @Override
            protected void onFailure(Throwable e) {
                mView.showTips(e.getMessage());
            }
        });
    }

    @Override
    public void retrievalFullText(Book book, String searchKey) {
        if (mFullTextDisp != null) {
            mFullTextDisp.dispose();
            mFullTextDisp = null;
        }

        mBookRepository.retrievalFullText(book, searchKey).subscribe(new CommonSubscriber<List<RetrievalResult>>() {

            @Override
            public void onSubscribe(Disposable d) {
                mFullTextDisp = d;
                mView.showTips("正在搜索“" + searchKey + "”，请稍后...");
            }

            @Override
            public void onSuccess(List<RetrievalResult> resultList) {
                mView.onRetrievalFullText(resultList);
            }

            @Override
            public void onFailure(Throwable e) {
                mView.showTips(e.getMessage());
            }

            @Override
            public void onComplete() {
                mFullTextDisp = null;
                mView.showTips("");
            }
        });
    }
}
