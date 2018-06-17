package com.nkcs.ereader.cloud.repository;

import com.nkcs.ereader.base.db.BookDao;
import com.nkcs.ereader.base.entity.Book;
import com.nkcs.ereader.base.repository.BaseRepository;
import com.nkcs.ereader.base.repository.RxLifecycleBinder;
import com.nkcs.ereader.base.utils.RxUtils;
import com.nkcs.ereader.cloud.entity.CloudBook;
import com.nkcs.ereader.cloud.net.BookAPI;

import java.io.File;
import java.util.List;

import io.reactivex.Observable;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Response;

/**
 * @author faunleaf
 * @date 2018/3/19
 */

public class BookRepository extends BaseRepository {

    private BookDao mBookDao;

    public BookRepository(RxLifecycleBinder binder) {
        super(binder);
        mBookDao = mSession.getBookDao();
    }

    public Observable<Response<List<CloudBook>>> getBookList() {
        return BookAPI.getInstance().getBookList()
                .compose(defaultRxConfig());
    }

    public Observable<List<Book>> getLocalBookList() {
        return RxUtils.toObservable(() -> mBookDao.loadAll());
    }

    public Observable<Response<String>> removeBook(Long bookId) {
        return BookAPI.getInstance().removeBook(bookId)
                .compose(defaultRxConfig());
    }

    public Observable<Response<String>> uploadBook(Book book) {
        RequestBody title = RequestBody.create(null, book.getTitle());
        RequestBody requestFile = RequestBody.create(MediaType.parse("multipart/form-data"), new File(book.getPath()));
        MultipartBody.Part file = MultipartBody.Part.createFormData("file",
                book.getPath().substring(book.getPath().lastIndexOf('/') + 1), requestFile);
        return BookAPI.getInstance().uploadBook(book.getTitle(), file)
                .compose(defaultRxConfig());
    }
}
