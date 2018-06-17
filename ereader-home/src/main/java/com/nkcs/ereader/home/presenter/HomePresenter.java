package com.nkcs.ereader.home.presenter;

import android.util.Log;

import com.nkcs.ereader.base.entity.Book;
import com.nkcs.ereader.base.subscriber.CommonSubscriber;
import com.nkcs.ereader.base.utils.ToastUtils;
import com.nkcs.ereader.home.contract.HomeContract;
import com.nkcs.ereader.home.repository.HomeRepository;

import java.util.List;

import io.reactivex.annotations.NonNull;

public class HomePresenter implements HomeContract.IPresenter {
    private HomeContract.IView view;
    private HomeRepository repository;

    public HomePresenter(@NonNull HomeContract.IView view, HomeRepository repository) {
        this.view = view;
        view.setPresenter(this);
        this.repository = repository;
    }


    @Override
    public void deleteBooks(List<Book> bookList) {
        repository.deleteBooks(bookList).subscribe(new BooksSubscriber());
    }

    @Override
    public void getBooks() {
        repository.getBooks().subscribe(new BooksSubscriber());
    }

    class BooksSubscriber extends CommonSubscriber<List<Book>> {

        @Override
        protected void onSuccess(List<Book> books) {
            view.onGetBooks(books);
        }

        @Override
        protected void onFailure(Throwable e) {
            ToastUtils.showText(e.getMessage());
        }
    }
}
