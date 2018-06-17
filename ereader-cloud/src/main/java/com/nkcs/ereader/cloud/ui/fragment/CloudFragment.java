package com.nkcs.ereader.cloud.ui.fragment;

import android.Manifest;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.nkcs.ereader.base.entity.Book;
import com.nkcs.ereader.base.ui.fragment.BaseFragment;
import com.nkcs.ereader.base.utils.ToastUtils;
import com.nkcs.ereader.cloud.R;
import com.nkcs.ereader.cloud.contract.CloudContract;
import com.nkcs.ereader.cloud.ui.adapter.BookshelfAdapter;

import org.greenrobot.eventbus.EventBus;

/**
 * @author faunleaf
 * @date 2018/6/11
 */

public class CloudFragment extends BaseFragment implements CloudContract.IView {

    private CloudContract.IPresenter mPresenter;

    private ImageView mIvBack;
    private ImageView mIvUpload;

    private RecyclerView mRvBookshelf;
    private BookshelfAdapter mBookshelfAdapter;
    private LinearLayout mLlEmpty;

    @Override
    protected int getLayoutResource() {
        return R.layout.fragment_cloud;
    }

    @Override
    protected void onInitView() {
        // 后退
        mIvBack = findViewById(R.id.cloud_iv_back);
        mIvBack.setOnClickListener(v -> {
            removeFragment();
        });
        // 搜索
        mIvUpload = findViewById(R.id.cloud_iv_search);
        mIvUpload.setOnClickListener(v -> {
        });

        // 列表
        mRvBookshelf = findViewById(R.id.cloud_rv_bookshelf);
        mRvBookshelf.setLayoutManager(new GridLayoutManager(getHoldingActivity(), 3));
        mBookshelfAdapter = new BookshelfAdapter();
        mRvBookshelf.setAdapter(mBookshelfAdapter);
        mBookshelfAdapter.setOnItemClickListener((v, cloudBook, position) -> {

        });
        mBookshelfAdapter.setOnItemLongClickListener((v, cloudBook, position) -> {
            return true;
        });
        // 没有文件
        mLlEmpty = findViewById(R.id.cloud_ll_empty);
    }


    @Override
    protected void onLoadData() {
    }

    @NeedsPermission(permissions = { Manifest.permission.WRITE_EXTERNAL_STORAGE },
            permissionsText = { "读写手机存储" })
    public void getBook() {
    }

    @Override
    public void setPresenter(CloudContract.IPresenter presenter) {
        mPresenter = presenter;
    }

    @Override
    public void showTips(String text) {
        ToastUtils.showText(text);
    }
}
