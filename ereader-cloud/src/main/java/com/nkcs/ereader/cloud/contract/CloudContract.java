package com.nkcs.ereader.cloud.contract;

import com.nkcs.ereader.base.contract.BasePresenter;
import com.nkcs.ereader.base.contract.BaseView;

/**
 * @author faunleaf
 * @date 2018/3/19
 */

public interface CloudContract {

    interface IView extends BaseView<IPresenter> {


        void showTips(String text);
    }

    interface IPresenter extends BasePresenter {
    }
}
