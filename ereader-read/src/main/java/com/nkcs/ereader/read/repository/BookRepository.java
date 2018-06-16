package com.nkcs.ereader.read.repository;

import android.util.Pair;

import com.nkcs.ereader.base.db.BookDao;
import com.nkcs.ereader.base.db.ChapterDao;
import com.nkcs.ereader.base.entity.Book;
import com.nkcs.ereader.base.entity.Chapter;
import com.nkcs.ereader.base.repository.BaseRepository;
import com.nkcs.ereader.base.repository.RxLifecycleBinder;
import com.nkcs.ereader.base.utils.LogUtils;
import com.nkcs.ereader.base.utils.RxUtils;
import com.nkcs.ereader.read.entity.RetrievalResult;
import com.nkcs.ereader.read.ui.widget.read.formatter.BookFormatter;

import java.io.BufferedReader;
import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;

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

    public Observable<List<RetrievalResult>> retrievalFullText(Book book, String searchKey) {
        return RxUtils
                .toObservable((ObservableEmitter<List<RetrievalResult>> emitter) -> {
                    BookFormatter formatter = BookFormatter.getFormatter(book);
                    for (Chapter chapter : book.getChapterList()) {
                        List<RetrievalResult> resultList = new ArrayList<>();
                        BufferedReader reader = formatter.loadChapter(chapter);
                        String content = null;
                        int position = 1;
                        while ((content = reader.readLine()) != null) {
                            content = content.trim();
                            if ("".equals(content)) {
                                continue;
                            }
                            if (content.contains(searchKey)) {
                                RetrievalResult retrievalResult = new RetrievalResult();
                                retrievalResult.setChapter(chapter);
                                retrievalResult.setContent(content);
                                retrievalResult.setSearchKey(searchKey);
                                retrievalResult.setPosition(position);
                                resultList.add(retrievalResult);
                            }
                            ++position;
                        }
                        if (resultList.size() > 0) {
                            emitter.onNext(resultList);
                        }
                    }
                    emitter.onComplete();
                }).compose(computationRxConfig());
    }

    private Book getBookWithChapters(Long bookId) {
        Book book = mBookDao.load(bookId);
        book.getChapterList();
        return book;
    }
}
