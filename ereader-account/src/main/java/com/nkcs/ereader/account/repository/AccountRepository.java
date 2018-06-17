package com.nkcs.ereader.account.repository;

import com.nkcs.ereader.account.net.AccountAPI;
import com.nkcs.ereader.base.repository.BaseRepository;
import com.nkcs.ereader.base.repository.RxLifecycleBinder;

import io.reactivex.Observable;
import retrofit2.Response;

/**
 * @author faunleaf
 * @date 2018/3/19
 */

public class AccountRepository extends BaseRepository {

    public AccountRepository(RxLifecycleBinder binder) {
        super(binder);
    }

    public Observable<Response<String>> askSession(String openid) {
        return AccountAPI.getInstance().askSession(openid)
                .compose(defaultRxConfig());
    }
}
