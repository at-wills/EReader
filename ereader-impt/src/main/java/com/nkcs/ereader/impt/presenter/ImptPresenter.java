package com.nkcs.ereader.impt.presenter;

import android.database.sqlite.SQLiteConstraintException;
import android.support.annotation.NonNull;

import com.nkcs.ereader.base.entity.Book;
import com.nkcs.ereader.base.subscriber.CommonSubscriber;
import com.nkcs.ereader.impt.contract.ImptContract;
import com.nkcs.ereader.impt.repository.BookRepository;
import com.nkcs.ereader.impt.repository.FileRepository;

import java.io.File;
import java.util.List;

import io.reactivex.disposables.Disposable;

/**
 * @author faunleaf
 * @date 2018/6/11
 */

public class ImptPresenter implements ImptContract.IPresenter {

    private ImptContract.IView mView;
    private BookRepository mBookRepository;
    private FileRepository mFileRepository;

    private Disposable mGetFileDisp;

    public ImptPresenter(@NonNull ImptContract.IView view, BookRepository bookRepository, FileRepository fileRepository) {
        mView = view;
        mView.setPresenter(this);
        mBookRepository = bookRepository;
        mFileRepository = fileRepository;
    }

    @Override
    public void cancelGetTargetFile() {
        if (mGetFileDisp != null) {
            mGetFileDisp.dispose();
            mGetFileDisp = null;
        }
    }

    @Override
    public void getTargetFile(File file) {
        if (mGetFileDisp != null) {
            mGetFileDisp.dispose();
            mGetFileDisp = null;
        }

        mFileRepository.getTargetFile(file).subscribe(new CommonSubscriber<List<File>>() {

            @Override
            public void onSubscribe(Disposable d) {
                mGetFileDisp = d;
            }

            @Override
            protected void onSuccess(List<File> fileList) {
                mView.onGetTargetFile(fileList);
            }

            @Override
            protected void onFailure(Throwable e) {
                mView.showTips(e.getMessage());
            }

            @Override
            public void onComplete() {
                mGetFileDisp = null;
            }
        });
    }

    @Override
    public void importBook(List<Book> bookList) {
        mBookRepository.importBook(bookList).subscribe(new CommonSubscriber<String>() {

            @Override
            protected void onSuccess(String message) {
                mView.showTips("导入成功");
            }

            @Override
            protected void onFailure(Throwable e) {
                if (e instanceof SQLiteConstraintException) {
                    mView.showTips("请勿重复导入书籍");
                } else {
                    mView.showTips(e.getMessage());
                }
            }
        });
    }
}
