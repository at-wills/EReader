package com.nkcs.ereader.cloud.contract;

import com.nkcs.ereader.base.contract.BasePresenter;
import com.nkcs.ereader.base.contract.BaseView;
import com.nkcs.ereader.base.entity.Book;
import com.nkcs.ereader.cloud.entity.CloudBook;

import java.util.List;

/**
 * @author faunleaf
 * @date 2018/3/19
 */

public interface CloudContract {

    interface IView extends BaseView<IPresenter> {

        void onGetBookList(List<CloudBook> bookList);

        void onGetLocalBookList(List<Book> bookList);

        void startLoading();

        void stopLoading();

        void showTips(String text);
    }

    interface IPresenter extends BasePresenter {

        void getBookList();

        void getLocalBookList();

        void removeBook(Long bookId);

        void uploadBook(Book book);
    }
}
