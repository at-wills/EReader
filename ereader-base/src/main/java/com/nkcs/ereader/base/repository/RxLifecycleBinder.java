package com.nkcs.ereader.base.repository;

import com.trello.rxlifecycle2.LifecycleTransformer;

/**
 * @author faunleaf
 * @date 2018/3/19
 */

public interface RxLifecycleBinder {

    /**
     * 绑定lifecycle
     * @return LifecycleTransformer
     */
    <T> LifecycleTransformer<T> bindLifecycle();
}
