package com.nkcs.ereader.base.router;

import android.content.Context;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.alibaba.android.arouter.facade.service.SerializationService;
import com.google.gson.Gson;

import java.lang.reflect.Type;

/**
 * @author faunleaf
 * @date 2018/6/12
 */

@Route(path = "/service/json")
public class JsonServiceImpl implements SerializationService {

    @Override
    public void init(Context context) {

    }

    @Override
    public <T> T json2Object(String input, Class<T> clazz) {
        return new Gson().fromJson(input, clazz);
    }

    @Override
    public String object2Json(Object instance) {
        return new Gson().toJson(instance);
    }

    @Override
    public <T> T parseObject(String input, Type clazz) {
        return new Gson().fromJson(input, clazz);
    }
}
