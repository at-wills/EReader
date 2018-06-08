package com.nkcs.ereader.read.repository;

import android.util.Pair;

import com.nkcs.ereader.base.db.BookDao;
import com.nkcs.ereader.base.db.ChapterDao;
import com.nkcs.ereader.base.entity.Book;
import com.nkcs.ereader.base.entity.Chapter;
import com.nkcs.ereader.base.repository.BaseRepository;
import com.nkcs.ereader.base.repository.RxLifecycleBinder;
import com.nkcs.ereader.base.utils.RxUtils;
import com.nkcs.ereader.read.ui.widget.read.formatter.BookFormatter;

import java.io.BufferedReader;
import java.util.List;

import io.reactivex.Observable;

/**
 * @author faunleaf
 * @date 2018/3/19
 */

public class BookRepository extends BaseRepository {

    private BookDao mBookDao;

    private ChapterDao mChapterDao;

    public BookRepository(RxLifecycleBinder binder) {
        super(binder);
        mBookDao = mSession.getBookDao();
        mChapterDao = mSession.getChapterDao();
    }

    public Observable<Book> changeLastReadChapter(Book book, Chapter chapter) {
        return RxUtils
                .toObservable(() -> {
                    mSession.runInTx(() -> {
                        chapter.setHasRead(true);
                        mChapterDao.update(chapter);
                        book.setLastReadChapter(chapter.getSequence());
                        book.update();
                    });
                    return getBookWithChapters(book.getId());
                }).compose(defaultRxConfig());
    }

    public Observable<Book> formatChapters(Book book, List<Chapter> chapterList) {
        return RxUtils
                .toObservable(() -> {
                    mSession.runInTx(() -> {
                        mChapterDao.deleteInTx(book.getChapterList());
                        mChapterDao.insertInTx(chapterList);
                        book.setHasFormat(true);
                        book.update();
                    });
                    return getBookWithChapters(book.getId());
                }).compose(defaultRxConfig());
    }

    public Observable<Book> getBook(Long bookId) {
        return RxUtils
                .toObservable(() -> getBookWithChapters(bookId))
                .compose(defaultRxConfig());
    }

    private Book getBookWithChapters(Long bookId) {
        Book book = mBookDao.load(bookId);
        book.getChapterList();
        return book;
    }
}
