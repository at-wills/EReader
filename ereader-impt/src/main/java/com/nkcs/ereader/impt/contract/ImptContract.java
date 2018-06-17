package com.nkcs.ereader.impt.contract;

import com.nkcs.ereader.base.contract.BasePresenter;
import com.nkcs.ereader.base.contract.BaseView;
import com.nkcs.ereader.base.entity.Book;

import java.io.File;
import java.util.List;

/**
 * @author faunleaf
 * @date 2018/3/19
 */

public interface ImptContract {

    interface IView extends BaseView<IPresenter> {

        void onGetTargetFile(List<File> fileList);

        void showTips(String text);
    }

    interface IPresenter extends BasePresenter {

        void cancelGetTargetFile();

        void getTargetFile(File file);

        void importBook(List<Book> bookList);
    }
}
