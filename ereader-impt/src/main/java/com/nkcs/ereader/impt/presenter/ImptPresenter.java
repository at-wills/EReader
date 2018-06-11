package com.nkcs.ereader.impt.presenter;

import android.support.annotation.NonNull;

import com.nkcs.ereader.base.subscriber.BaseDbSubscriber;
import com.nkcs.ereader.impt.contract.ImptContract;
import com.nkcs.ereader.impt.repository.FileRepository;

import java.io.File;
import java.util.List;

import io.reactivex.functions.Consumer;

/**
 * @author faunleaf
 * @date 2018/6/11
 */

public class ImptPresenter implements ImptContract.IPresenter {

    private ImptContract.IView mView;
    private FileRepository mFileRepository;

    public ImptPresenter(@NonNull ImptContract.IView view, FileRepository fileRepository) {
        mView = view;
        mView.setPresenter(this);
        mFileRepository = fileRepository;
    }

    @Override
    public void getTargetFile(File file) {
        mFileRepository.getTargetFile(file).subscribe(new BaseDbSubscriber<List<File>>() {

            @Override
            protected void onSuccess(List<File> fileList) {
                mView.onGetTargetFile(fileList);
            }

            @Override
            protected void onFailure(Throwable e) {
                mView.showTips(e.getMessage());
            }
        });
    }
}
