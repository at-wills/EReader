package com.nkcs.ereader.read.contract;

import com.nkcs.ereader.base.contract.BasePresenter;
import com.nkcs.ereader.base.contract.BaseView;
import com.nkcs.ereader.base.entity.Book;
import com.nkcs.ereader.base.entity.Chapter;
import com.nkcs.ereader.read.entity.Config;
import com.nkcs.ereader.read.ui.widget.read.PageStyle;
import com.nkcs.ereader.read.ui.widget.read.PageView;

import java.io.BufferedReader;
import java.util.List;
import java.util.concurrent.Callable;

/**
 * @author faunleaf
 * @date 2018/3/19
 */

public interface ReadContract {

    interface IView extends BaseView<IPresenter> {

        void onGetBook(Book book);

        void onChangeBrightness(Config config);

        void onChangeNightMode(Config config);

        void onChangeTextSize(Config config);

        void onChangePageMode(Config config);

        void onChangePageStyle(Config config);

        void onGetReadConfig(Config config);

        void showTips(String text);
    }

    interface IPresenter extends BasePresenter {

        void changeLastReadChapter(Book book, Chapter chapter);

        void formatChapters(Book book, List<Chapter> chapterList);

        void getBook(Long bookId);

        void changeBrightness(int brightness, boolean systemBrightness);

        void changeNightMode(boolean nightMode);

        void changeTextSize(int textSize);

        void changePageMode(PageView.PageMode pageMode);

        void changePageStyle(PageStyle pageStyle);

        void getReadConfig();
    }
}
