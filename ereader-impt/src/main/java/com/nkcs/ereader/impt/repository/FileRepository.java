package com.nkcs.ereader.impt.repository;

import com.nkcs.ereader.base.db.BookDao;
import com.nkcs.ereader.base.repository.BaseRepository;
import com.nkcs.ereader.base.repository.RxLifecycleBinder;
import com.nkcs.ereader.base.utils.LogUtils;
import com.nkcs.ereader.base.utils.RxUtils;
import com.nkcs.ereader.impt.entity.FileType;
import com.nkcs.ereader.impt.utils.FileUtils;

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
 * @date 2018/6/11
 */

public class FileRepository extends BaseRepository {

    private BookDao mBookDao;

    public FileRepository(RxLifecycleBinder binder) {
        super(binder);
        mBookDao = mSession.getBookDao();
    }

    public Observable<List<File>> getTargetFile(File file) {
        return RxUtils
                .toObservable((ObservableEmitter<List<File>> emitter) -> {
                    Queue<File> queue = new LinkedList<>();
                    queue.offer(file);
                    List<File> txtList = new ArrayList<>();

                    while (queue.size() > 0) {
                        File[] filesArray = queue.poll().listFiles();
                        if (filesArray != null) {
                            List<File> fileList = new ArrayList<>();
                            Collections.addAll(fileList, filesArray);  //把数组转化成list
                            Collections.sort(fileList, FileUtils.comparator);  //按照名字排序
                            for (File f : fileList) {
                                if (f.isHidden()) {
                                    continue;
                                }
                                FileType fileType = FileUtils.getFileType(f);
                                if (fileType == FileType.directory) {
                                    queue.offer(f);
                                }
                                if (fileType == FileType.txt) {
                                    txtList.add(f);
                                }

                                if (txtList.size() >= 20) {
                                    emitter.onNext(new ArrayList<>(txtList));
                                    txtList.clear();
                                }
                            }
                        }
                    }
                    emitter.onNext(txtList);
                    emitter.onComplete();
                }).compose(computationRxConfig());
    }
}
