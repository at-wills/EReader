package com.nkcs.ereader.read.repository;

import com.nkcs.ereader.base.db.BookDao;
import com.nkcs.ereader.base.db.DbHelper;
import com.nkcs.ereader.base.entity.Book;
import com.nkcs.ereader.base.repository.BaseRepository;
import com.nkcs.ereader.base.repository.RxLifecycleBinder;
import com.nkcs.ereader.base.utils.RxUtils;

import java.util.List;

import io.reactivex.Observable;

/**
 * @author faunleaf
 * @date 2018/3/19
 */

public class BookRepository extends BaseRepository {

    private BookDao mBookDao;

    public BookRepository(RxLifecycleBinder binder) {
        super(binder);
        mBookDao = DbHelper.getInstance().getSession().getBookDao();
    }

    public Observable<List<Book>> getBooks() {
        return RxUtils
                .toObservable(() -> mBookDao.loadAll())
                .compose(defaultRxConfig());
    }
}
