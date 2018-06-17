package com.nkcs.ereader.cloud.ui.fragment;

import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;

import com.alibaba.android.arouter.launcher.ARouter;
import com.nkcs.ereader.base.entity.Book;
import com.nkcs.ereader.base.event.LogoutEvent;
import com.nkcs.ereader.base.router.RouterConstant;
import com.nkcs.ereader.base.ui.fragment.BaseFragment;
import com.nkcs.ereader.base.utils.LogUtils;
import com.nkcs.ereader.base.utils.PermissionUtils;
import com.nkcs.ereader.base.utils.StringUtils;
import com.nkcs.ereader.base.utils.ToastUtils;
import com.nkcs.ereader.cloud.R;
import com.nkcs.ereader.cloud.contract.CloudContract;
import com.nkcs.ereader.cloud.entity.CloudBook;
import com.nkcs.ereader.cloud.ui.adapter.BooklistAdapter;
import com.nkcs.ereader.cloud.ui.adapter.BookshelfAdapter;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.List;

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

    private List<Book> mLocalBookList;

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
        // 上传
        mIvUpload = findViewById(R.id.cloud_iv_search);
        mIvUpload.setOnClickListener(v -> {
            showPopupWindow();
        });

        // 列表
        mRvBookshelf = findViewById(R.id.cloud_rv_bookshelf);
        mRvBookshelf.setLayoutManager(new GridLayoutManager(getHoldingActivity(), 3));
        mBookshelfAdapter = new BookshelfAdapter();
        mRvBookshelf.setAdapter(mBookshelfAdapter);
        mBookshelfAdapter.setOnItemClickListener((v, cloudBook, position) -> {
            // TODO: 下载书籍
        });
        mBookshelfAdapter.setOnItemLongClickListener((v, cloudBook, position) -> {
            if (cloudBook.getId() < 0) {
                return false;
            }
            new AlertDialog.Builder(getHoldingActivity())
                    .setMessage("确定要删除“" + cloudBook.getTitle() + "”吗？")
                    .setNegativeButton("取消", null)
                    .setPositiveButton("确定", (dialog, which) -> {
                        mPresenter.removeBook(cloudBook.getId());
                    })
                    .setCancelable(false)
                    .show();
            return true;
        });
    }


    @Override
    protected void onLoadData() {
        mPresenter.getLocalBookList();
    }

    @Override
    public void setPresenter(CloudContract.IPresenter presenter) {
        mPresenter = presenter;
    }

    @Override
    public void onGetBookList(List<CloudBook> bookList) {
        mBookshelfAdapter.clear();
        mBookshelfAdapter.addItems(bookList);
    }

    @Override
    public void onGetLocalBookList(List<Book> bookList) {
        mLocalBookList = bookList;
    }

    @Override
    public void startLoading() {
        showLoadingDialog();
    }

    @Override
    public void stopLoading() {
        hideLoadingDialog();
    }

    @Override
    public void showTips(String text) {
        ToastUtils.showText(text);
    }

    private void showPopupWindow() {
        View popupView = getHoldingActivity().getLayoutInflater().inflate(R.layout.popup_cloud_books, null);
        PopupWindow window = new PopupWindow(popupView, 460, 500);
        window.setBackgroundDrawable(ContextCompat.getDrawable(getHoldingActivity(), android.R.color.white));
        window.setFocusable(true);
        window.setOutsideTouchable(true);
        window.update();
        window.showAtLocation(getHoldingActivity().getWindow().getDecorView(), Gravity.CENTER, 0, 0);

        RecyclerView rvBookList = popupView.findViewById(R.id.cloud_rv_booklist);
        rvBookList.setLayoutManager(new LinearLayoutManager(getHoldingActivity()));
        BooklistAdapter booklistAdapter = new BooklistAdapter();
        booklistAdapter.addItems(mLocalBookList);
        rvBookList.setAdapter(booklistAdapter);
        booklistAdapter.setOnItemClickListener((v, book, position) -> {
            window.dismiss();
            mPresenter.uploadBook(book);
        });
    }

    // region EventBus 相关

    @Override
    public void onStart() {
        super.onStart();
        mPresenter.getBookList();
        EventBus.getDefault().register(this);
    }

    @Override
    public void onStop() {
        EventBus.getDefault().unregister(this);
        super.onStop();
    }

    @Subscribe(threadMode = ThreadMode.MAIN, sticky = true)
    public void onMessageEvent(LogoutEvent event) {
        ARouter.getInstance().build(RouterConstant.LOGIN_PAGE).navigation();
    }

    // endregion
}
