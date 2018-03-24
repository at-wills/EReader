package com.nkcs.ereader.base.entity;

import com.google.gson.Gson;

/**
 * @author faunleaf
 * @date 2018/3/18
 */

public class BaseEntity {

    @Override
    public String toString() {
        return new Gson().toJson(this);
    }
}
