package com.nkcs.ereader.cloud.ui.activity;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.nkcs.ereader.base.router.RouterConstant;
import com.nkcs.ereader.base.ui.activity.BaseActivity;
import com.nkcs.ereader.cloud.R;
import com.nkcs.ereader.cloud.presenter.CloudPresenter;
import com.nkcs.ereader.cloud.repository.BookRepository;
import com.nkcs.ereader.cloud.ui.fragment.CloudFragment;

/**
 * @author faunleaf
 * @date 2018/6/11
 */

@Route(path = RouterConstant.CLOUD_PAGE)
public class CloudActivity extends BaseActivity {

    CloudFragment mCloudFragment;

    @Override
    protected int getLayoutResource() {
        return R.layout.activity_cloud;
    }

    @Override
    protected int getFragmentContainerId() {
        return R.id.fl_cloud_activity;
    }

    @Override
    protected void onInitView() {
        mCloudFragment = getStoredFragment(CloudFragment.class);
        new CloudPresenter(mCloudFragment, new BookRepository(this));
        addFragment(mCloudFragment);
    }
}
