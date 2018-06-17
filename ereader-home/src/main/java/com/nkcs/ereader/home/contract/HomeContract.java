package com.nkcs.ereader.home.contract;

import com.nkcs.ereader.base.contract.BasePresenter;
import com.nkcs.ereader.base.contract.BaseView;
import com.nkcs.ereader.base.entity.Book;

import java.util.List;

public interface HomeContract {
    interface IPresenter extends BasePresenter {
        void deleteBooks(List<Book> bookList);

        void getBooks();
    }

    interface IView extends BaseView<IPresenter> {
        void onGetBooks(List<Book> books);
    }
}
