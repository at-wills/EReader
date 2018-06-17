package com.nkcs.ereader.read.presenter;

import android.support.annotation.NonNull;

import com.nkcs.ereader.base.entity.Book;
import com.nkcs.ereader.base.entity.Chapter;
import com.nkcs.ereader.base.subscriber.CommonSubscriber;
import com.nkcs.ereader.read.contract.ReadContract;
import com.nkcs.ereader.read.entity.Config;
import com.nkcs.ereader.read.repository.BookRepository;
import com.nkcs.ereader.read.repository.ConfigRepository;
import com.nkcs.ereader.read.ui.widget.read.PageStyle;
import com.nkcs.ereader.read.ui.widget.read.PageView;

import java.util.List;

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
        mConfigRepository.saveBrigtnessConfig(brightness, systemBrightness).subscribe(new CommonSubscriber<Config>() {

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
        mConfigRepository.saveNightModeConfig(nightMode).subscribe(new CommonSubscriber<Config>() {

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
        mConfigRepository.saveTextSizeConfig(textSize).subscribe(new CommonSubscriber<Config>() {

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
        mConfigRepository.savePageModeConfig(pageMode).subscribe(new CommonSubscriber<Config>() {

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
        mConfigRepository.savePageStyleConfig(pageStyle).subscribe(new CommonSubscriber<Config>() {

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
        mConfigRepository.getAllConfig().subscribe(new CommonSubscriber<Config>() {

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
        mBookRepository.changeLastReadChapter(book, chapter).subscribe(new CommonSubscriber<Book>() {

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
        mBookRepository.formatChapters(book, chapterList).subscribe(new CommonSubscriber<Book>() {

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
        mBookRepository.getBook(bookId).subscribe(new CommonSubscriber<Book>() {

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
