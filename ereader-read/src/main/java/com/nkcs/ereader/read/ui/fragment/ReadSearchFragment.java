package com.nkcs.ereader.read.ui.fragment;

import android.Manifest;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.nkcs.ereader.base.entity.Book;
import com.nkcs.ereader.base.ui.fragment.BaseFragment;
import com.nkcs.ereader.base.utils.LogUtils;
import com.nkcs.ereader.read.R;
import com.nkcs.ereader.read.contract.ReadSearchContract;
import com.nkcs.ereader.read.entity.RetrievalResult;
import com.nkcs.ereader.read.ui.adapter.RetrievalAdapter;

import org.greenrobot.eventbus.EventBus;

import java.util.List;

/**
 * @author faunleaf
 * @date 2018/6/11
 */

public class ReadSearchFragment extends BaseFragment implements ReadSearchContract.IView {

    private Long bookId;
    private Book mBook;
    private ReadSearchContract.IPresenter mPresenter;

    private ImageView mIvBack;
    private EditText mEtContent;
    private ImageView mIvSearch;

    private RecyclerView mRvRetrieval;
    private RetrievalAdapter mRetrievalAdapter;
    private TextView mTvTip;

    public static ReadSearchFragment newInstance(Long bookId) {
        ReadSearchFragment readSearchFragment = new ReadSearchFragment();
        Bundle bundle = new Bundle();
        bundle.putLong("bookId", bookId);
        readSearchFragment.setArguments(bundle);
        return readSearchFragment;
    }

    @Override
    protected int getLayoutResource() {
        return R.layout.fragment_read_search;
    }

    @Override
    protected void onInitView() {
        initDataFromIntent();

        // 后退
        mIvBack = findViewById(R.id.read_search_iv_back);
        mIvBack.setOnClickListener(v -> {
            removeFragment();
        });
        // 文本框
        mEtContent = findViewById(R.id.read_search_et_content);
        mEtContent.setOnEditorActionListener((view, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                startSearch(mEtContent.getText().toString());
                return true;
            }
            return false;
        });
        // 搜索
        mIvSearch = findViewById(R.id.read_search_iv_search);
        mIvSearch.setOnClickListener(v -> {
            startSearch(mEtContent.getText().toString());
        });

        // 列表
        mRvRetrieval = findViewById(R.id.read_search_rv_retrieval);
        mRvRetrieval.setLayoutManager(new LinearLayoutManager(getHoldingActivity()));
        mRetrievalAdapter = new RetrievalAdapter();
        mRvRetrieval.setAdapter(mRetrievalAdapter);
        mRetrievalAdapter.setOnItemClickListener((v, retrievalResult, position) -> {
            EventBus.getDefault().postSticky(retrievalResult);
            removeFragment();
        });
        // 提示信息
        mTvTip = findViewById(R.id.read_search_tv_tip);
    }

    private void initDataFromIntent() {
        if (getArguments() != null) {
            bookId = getArguments().getLong("bookId");
        }
    }

    @Override
    protected void onLoadData() {
        callWithPermissions("getBook");
    }

    @NeedsPermission(permissions = { Manifest.permission.WRITE_EXTERNAL_STORAGE },
            permissionsText = { "读写手机存储" })
    public void getBook() {
        mPresenter.getBook(bookId);
    }

    @Override
    public void setPresenter(ReadSearchContract.IPresenter presenter) {
        mPresenter = presenter;
    }

    @Override
    public void onGetBook(Book book) {
        mBook = book;
    }

    @Override
    public void onRetrievalFullText(List<RetrievalResult> resultList) {
        mRetrievalAdapter.addItems(resultList);
    }

    @Override
    public void showTips(String text) {
        mTvTip.setText(text);
    }

    private void startSearch(String searchKey) {
        if (mBook == null || "".equals(searchKey)) {
            return;
        }
        mRetrievalAdapter.clear();
        mPresenter.retrievalFullText(mBook, searchKey);
    }
}
