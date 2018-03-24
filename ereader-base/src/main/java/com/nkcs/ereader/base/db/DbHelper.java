package com.nkcs.ereader.base.db;

import android.database.sqlite.SQLiteDatabase;

import com.nkcs.ereader.base.BaseApplication;

import org.greenrobot.greendao.database.Database;

/**
 * @author faunleaf
 * @date 2018/3/24
 */

public class DbHelper {

    private static final String DB_NAME = "ereader_db";

    private static volatile DbHelper sInstance;

    private SQLiteDatabase mDb;
    private DaoMaster mDaoMaster;
    private DaoSession mSession;

    private DbHelper() {
        DaoMaster.DevOpenHelper openHelper = new DaoMaster.DevOpenHelper(
                BaseApplication.getContext(), DB_NAME, null) {
            @Override
            public void onCreate(Database db) {
                super.onCreate(db);
            }

            @Override
            public void onUpgrade(Database db, int oldVersion, int newVersion) {
                // 数据库更新策略
                switch (oldVersion) {
                    default:
                        break;
                }
            }
        };

        mDb = openHelper.getWritableDatabase();
        mDaoMaster = new DaoMaster(mDb);
        mSession = mDaoMaster.newSession();
    }

    public static DbHelper getInstance(){
        if (sInstance == null){
            synchronized (DbHelper.class){
                if (sInstance == null){
                    sInstance = new DbHelper();
                }
            }
        }
        return sInstance;
    }

    public DaoSession getSession() {
        return mSession;
    }

    public SQLiteDatabase getDatabase() {
        return mDb;
    }

    public DaoSession getNewSession() {
        return mDaoMaster.newSession();
    }
}
