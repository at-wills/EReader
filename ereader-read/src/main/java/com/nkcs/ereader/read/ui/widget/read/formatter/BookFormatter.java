package com.nkcs.ereader.read.ui.widget.read.formatter;

import com.nkcs.ereader.base.entity.Book;
import com.nkcs.ereader.base.entity.Chapter;

import java.io.BufferedReader;
import java.util.List;

import io.reactivex.Observable;

/**
 * @author faunleaf
 * @date 2018/4/20
 */

public abstract class BookFormatter {

    protected final static String ERROR_LOAD_BOOK = "书籍或文档加载失败";

    protected Book mBook;

    public BookFormatter(Book book) {
        mBook = book;
    }

    public static BookFormatter getFormatter(Book book) {
        if (book == null) {
            return null;
        }

        BookFormatter reader = null;
        switch (book.getFormat()) {
            case "txt":
                reader = new TxtFormatter(book);
            default:
                break;
        }
        return reader;
    }

    public abstract List<Chapter> getChapterList() throws Exception;

    public abstract BufferedReader loadChapter(Chapter chapter) throws Exception;
}
