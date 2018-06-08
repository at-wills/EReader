package com.nkcs.ereader.read.presenter;

import android.support.annotation.NonNull;
import android.util.Pair;

import com.nkcs.ereader.base.entity.Book;
import com.nkcs.ereader.base.entity.Chapter;
import com.nkcs.ereader.base.subscriber.BaseDbSubscriber;
import com.nkcs.ereader.read.contract.ReadContract;
import com.nkcs.ereader.read.entity.Config;
import com.nkcs.ereader.read.repository.BookRepository;
import com.nkcs.ereader.read.repository.ConfigRepository;
import com.nkcs.ereader.read.ui.widget.read.PageStyle;
import com.nkcs.ereader.read.ui.widget.read.PageView;

import java.io.BufferedReader;
import java.util.List;
import java.util.concurrent.Callable;

/**
 * @author faunleaf
 * @date 2018/3/19
 */

public class ReadPresenter implements ReadContract.IPresenter {

    private ReadContract.IView mView;
    private BookRepository mBookRepository;
    private ConfigRepository mConfigRepository;

    public ReadPresenter(@NonNull ReadContract.IView view, BookRepository bookRepository,
            ConfigRepository configRepository) {
        mView = view;
        mView.setPresenter(this);
        mBookRepository = bookRepository;
        mConfigRepository = configRepository;
    }

    @Override
    public void changeBrightness(int brightness, boolean systemBrightness) {
        mConfigRepository.saveBrigtnessConfig(brightness, systemBrightness).subscribe(new BaseDbSubscriber<Config>() {

            @Override
            protected void onSuccess(Config config) {
                mView.onChangeBrightness(config);
            }

            @Override
            protected void onFailure(Throwable e) {
                mView.showTips(e.getMessage());
            }
        });
    }

    @Override
    public void changeNightMode(boolean nightMode) {
        mConfigRepository.saveNightModeConfig(nightMode).subscribe(new BaseDbSubscriber<Config>() {

            @Override
            protected void onSuccess(Config config) {
                mView.onChangeNightMode(config);
            }

            @Override
            protected void onFailure(Throwable e) {
                mView.showTips(e.getMessage());
            }
        });
    }

    @Override
    public void changeTextSize(int textSize) {
        mConfigRepository.saveTextSizeConfig(textSize).subscribe(new BaseDbSubscriber<Config>() {

            @Override
            protected void onSuccess(Config config) {
                mView.onChangeTextSize(config);
            }

            @Override
            protected void onFailure(Throwable e) {
                mView.showTips(e.getMessage());
            }
        });
    }

    @Override
    public void changePageMode(PageView.PageMode pageMode) {
        mConfigRepository.savePageModeConfig(pageMode).subscribe(new BaseDbSubscriber<Config>() {

            @Override
            protected void onSuccess(Config config) {
                mView.onChangePageMode(config);
            }

            @Override
            protected void onFailure(Throwable e) {
                mView.showTips(e.getMessage());
            }
        });
    }

    @Override
    public void changePageStyle(PageStyle pageStyle) {
        mConfigRepository.savePageStyleConfig(pageStyle).subscribe(new BaseDbSubscriber<Config>() {

            @Override
            protected void onSuccess(Config config) {
                mView.onChangePageStyle(config);
            }

            @Override
            protected void onFailure(Throwable e) {
                mView.showTips(e.getMessage());
            }
        });
    }

    @Override
    public void getReadConfig() {
        mConfigRepository.getAllConfig().subscribe(new BaseDbSubscriber<Config>() {

            @Override
            protected void onSuccess(Config config) {
                mView.onGetReadConfig(config);
            }

            @Override
            protected void onFailure(Throwable e) {
                mView.showTips(e.getMessage());
            }
        });
    }

    @Override
    public void changeLastReadChapter(Book book, Chapter chapter) {
        mBookRepository.changeLastReadChapter(book, chapter).subscribe(new BaseDbSubscriber<Book>() {

            @Override
            protected void onSuccess(Book book) {}

            @Override
            protected void onFailure(Throwable e) {
                mView.showTips(e.getMessage());
            }
        });
    }

    @Override
    public void formatChapters(Book book, List<Chapter> chapterList) {
        mBookRepository.formatChapters(book, chapterList).subscribe(new BaseDbSubscriber<Book>() {

            @Override
            protected void onSuccess(Book book) {
                mView.onGetBook(book);
            }

            @Override
            protected void onFailure(Throwable e) {
                mView.showTips(e.getMessage());
            }
        });
    }

    @Override
    public void getBook(Long bookId) {
        mBookRepository.getBook(bookId).subscribe(new BaseDbSubscriber<Book>() {

            @Override
            protected void onSuccess(Book book) {
                mView.onGetBook(book);
            }

            @Override
            protected void onFailure(Throwable e) {
                mView.showTips(e.getMessage());
            }
        });
    }
}
