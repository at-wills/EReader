package com.nkcs.ereader.read.contract;

import com.nkcs.ereader.base.contract.BasePresenter;
import com.nkcs.ereader.base.contract.BaseView;
import com.nkcs.ereader.base.entity.Book;

import java.util.List;

/**
 * @author faunleaf
 * @date 2018/3/19
 */

public interface ReadContract {

    interface IView extends BaseView<IPresenter> {

        void showBooks(List<Book> books);

        void showTips(String text);
    }

    interface IPresenter extends BasePresenter {

        void getBooks();
    }
}
