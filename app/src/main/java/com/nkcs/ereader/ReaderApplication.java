package com.nkcs.ereader;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.annotation.Nullable;

import com.alibaba.android.arouter.launcher.ARouter;
import com.nkcs.ereader.base.BaseApplication;
import com.nkcs.ereader.base.db.BookDao;
import com.nkcs.ereader.base.db.ChapterDao;
import com.nkcs.ereader.base.db.DbHelper;
import com.nkcs.ereader.base.entity.Book;
import com.nkcs.ereader.base.entity.Chapter;

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
        ARouter.openDebug();
        ARouter.openLog();
        ARouter.init(this);

        SharedPreferences sp = getSharedPreferences("system_config", Context.MODE_PRIVATE);
        Boolean firstEnter = sp.getBoolean("first_enter", true);
        if (firstEnter) {
            testDb();
            sp.edit().putBoolean("first_enter", false).apply();
        }
    }

    private void testDb() {
        BookDao bookDao = DbHelper.getInstance().getSession().getBookDao();
        Book book = new Book();
        book.setId(1L);
        book.setTitle("斗罗大陆");
        book.setHash("f04746e48eb51ecaa398e8fb096f4d6c");
        book.setPath("/storage/emulated/0/斗罗大陆.txt");
        book.setFormat("txt");
        book.setHasFormat(false);
        bookDao.insert(book);
    }
}
