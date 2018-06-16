package com.nkcs.ereader.read.contract;

import com.nkcs.ereader.base.contract.BasePresenter;
import com.nkcs.ereader.base.contract.BaseView;
import com.nkcs.ereader.base.entity.Book;
import com.nkcs.ereader.base.entity.Chapter;
import com.nkcs.ereader.read.entity.Config;
import com.nkcs.ereader.read.entity.RetrievalResult;
import com.nkcs.ereader.read.ui.widget.read.PageStyle;
import com.nkcs.ereader.read.ui.widget.read.PageView;

import java.util.List;

/**
 * @author faunleaf
 * @date 2018/3/19
 */

public interface ReadSearchContract {

    interface IView extends BaseView<IPresenter> {

        void onGetBook(Book book);

        void onRetrievalFullText(List<RetrievalResult> resultList);

        void showTips(String text);
    }

    interface IPresenter extends BasePresenter {

        void getBook(Long bookId);

        void retrievalFullText(Book book, String searchKey);
    }
}
