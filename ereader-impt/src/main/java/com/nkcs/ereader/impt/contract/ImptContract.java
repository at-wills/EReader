package com.nkcs.ereader.impt.contract;

import com.nkcs.ereader.base.contract.BasePresenter;
import com.nkcs.ereader.base.contract.BaseView;

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

        void getTargetFile(File file);
    }
}
