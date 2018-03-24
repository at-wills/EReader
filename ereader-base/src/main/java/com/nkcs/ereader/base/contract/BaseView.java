package com.nkcs.ereader.base.contract;

/**
 * @author faunleaf
 * @date 2018/3/19
 */

public interface BaseView<T> {


    /**
     * 关联presenter
     *
     * @param presenter
     */
    void setPresenter(T presenter);
}
