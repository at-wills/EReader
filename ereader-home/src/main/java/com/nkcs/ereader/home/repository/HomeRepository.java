package com.nkcs.ereader.home.repository;

import io.reactivex.Observable;

import com.nkcs.ereader.base.db.BookDao;
import com.nkcs.ereader.base.db.ChapterDao;
import com.nkcs.ereader.base.db.DbHelper;
import com.nkcs.ereader.base.entity.Book;
import com.nkcs.ereader.base.repository.BaseRepository;
import com.nkcs.ereader.base.repository.RxLifecycleBinder;
import com.nkcs.ereader.base.utils.RxUtils;

import java.util.List;

public class HomeRepository extends BaseRepository {

    private BookDao bookDao;
    private ChapterDao chapterDao;

    public HomeRepository(RxLifecycleBinder binder) {
        super(binder);
        bookDao = mSession.getBookDao();
        chapterDao = mSession.getChapterDao();
    }

    public Observable<List<Book>> deleteBooks(List<Book> bookList) {
        return RxUtils
                .toObservable(() -> {
                    mSession.runInTx(() -> {
                        for (Book book : bookList) {
                            chapterDao.deleteInTx(book.getChapterList());
                            bookDao.delete(book);
                        }
                    });
                    return bookDao.loadAll();
                })
                .compose(defaultRxConfig());
    }

    public Observable<List<Book>> getBooks() {
        return RxUtils
                .toObservable(() -> bookDao.loadAll())
                .compose(defaultRxConfig());
    }
}
