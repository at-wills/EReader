package com.nkcs.ereader.read.contract;

import com.nkcs.ereader.base.contract.BasePresenter;
import com.nkcs.ereader.base.contract.BaseView;
import com.nkcs.ereader.base.entity.Book;
import com.nkcs.ereader.base.entity.Chapter;
import com.nkcs.ereader.read.entity.Config;

import java.util.List;

/**
 * @author faunleaf
 * @date 2018/3/19
 */

public interface ReadContract {

    interface IView extends BaseView<IPresenter> {

        void onGetBook(Book book);

        void onChangeBrightness(Config config);

        void onChangeNightMode(Config config);

        void onGetReadConfig(Config config);

        void showTips(String text);
    }

    interface IPresenter extends BasePresenter {

        void getBook(Long bookId);

        void changeBrightness(int brightness, boolean systemBrightness);

        void changeNightMode(boolean nightMode);

        void getReadConfig();
    }
}
