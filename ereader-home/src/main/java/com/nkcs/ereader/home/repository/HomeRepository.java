package com.nkcs.ereader.home.repository;

import io.reactivex.Observable;

import com.nkcs.ereader.base.db.BookDao;
import com.nkcs.ereader.base.db.DbHelper;
import com.nkcs.ereader.base.entity.Book;
import com.nkcs.ereader.base.repository.BaseRepository;
import com.nkcs.ereader.base.repository.RxLifecycleBinder;
import com.nkcs.ereader.base.utils.RxUtils;

import java.util.List;

public class HomeRepository extends BaseRepository {

    private BookDao bookDao;

    public HomeRepository(RxLifecycleBinder binder) {
        super(binder);
        bookDao = DbHelper.getInstance().getSession().getBookDao();
    }

    public Observable<List<Book>> getBooks() {
        return RxUtils
                .toObservable(() -> bookDao.loadAll())
                .compose(defaultRxConfig());
    }
}
