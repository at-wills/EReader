package com.nkcs.ereader.cloud.ui.adapter;

import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.nkcs.ereader.base.ui.adapter.BaseRecyclerAdapter;
import com.nkcs.ereader.cloud.R;
import com.nkcs.ereader.cloud.entity.CloudBook;

/**
 * @author faunleaf
 * @date 2018/4/3
 */

public class BookshelfAdapter extends BaseRecyclerAdapter<CloudBook> {

    @Override
    protected int getItemLayoutResource() {
        return R.layout.item_bookshelf;
    }

    @Override
    protected ViewHolder createViewHolder(ViewGroup parent, View view) {
        return new ViewHolder(parent, view);
    }

    public class ViewHolder extends BaseRecyclerAdapter.ViewHolder<CloudBook> {

        private ImageView mIvCover;

        private TextView mTvTitle;

        public ViewHolder(ViewGroup parent, View view) {
            super(parent, view);
        }

        @Override
        protected void onInitView() {
            mIvCover = (ImageView) findViewById(R.id.bookshelf_iv_cover);
            mTvTitle = (TextView) findViewById(R.id.bookshelf_tv_title);
        }

        @Override
        protected void onBind(CloudBook data, int position) {


            mTvTitle.setText("");
        }
    }
}
