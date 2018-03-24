package com.nkcs.ereader;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.annotation.Nullable;

import com.alibaba.android.arouter.launcher.ARouter;
import com.nkcs.ereader.base.BaseApplication;
import com.nkcs.ereader.base.db.BookDao;
import com.nkcs.ereader.base.db.DbHelper;
import com.nkcs.ereader.base.entity.Book;

import java.util.Map;
import java.util.Set;

/**
 * @author faunleaf
 * @date 2018/3/16
 */

public class ReaderApplication extends BaseApplication {

    @Override
    public void onCreate() {
        super.onCreate();
        ARouter.init(this);

        SharedPreferences sp = getSharedPreferences("ereader_config", Context.MODE_PRIVATE);
        Boolean firstEnter = sp.getBoolean("first_enter", true);
        if (firstEnter) {
            testDb();
            sp.edit().putBoolean("first_enter", false).apply();
        }
    }

    private void testDb() {
        BookDao bookDao = DbHelper.getInstance().getSession().getBookDao();
        Book book = new Book();
        book.setTitle("test1");
        bookDao.insert(book);
    }
}
