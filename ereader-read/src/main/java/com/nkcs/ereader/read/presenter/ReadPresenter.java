package com.nkcs.ereader.read.presenter;

import android.support.annotation.NonNull;

import com.nkcs.ereader.base.entity.Book;
import com.nkcs.ereader.base.subscriber.BaseDbSubscriber;
import com.nkcs.ereader.base.utils.BrightnessUtils;
import com.nkcs.ereader.read.contract.ReadContract;
import com.nkcs.ereader.read.entity.Config;
import com.nkcs.ereader.read.repository.BookRepository;
import com.nkcs.ereader.read.repository.ConfigRepository;

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
    public void getBook(Long bookId) {
        mBookRepository.getBookById(bookId).subscribe(new BaseDbSubscriber<Book>() {

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


}
