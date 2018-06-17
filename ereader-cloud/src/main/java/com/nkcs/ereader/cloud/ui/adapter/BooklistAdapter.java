package com.nkcs.ereader.cloud.ui.adapter;

import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.nkcs.ereader.base.entity.Book;
import com.nkcs.ereader.base.ui.adapter.BaseRecyclerAdapter;
import com.nkcs.ereader.cloud.R;
import com.nkcs.ereader.cloud.entity.CloudBook;

/**
 * @author faunleaf
 * @date 2018/4/3
 */

public class BooklistAdapter extends BaseRecyclerAdapter<Book> {

    @Override
    protected int getItemLayoutResource() {
        return R.layout.item_booklist;
    }

    @Override
    protected ViewHolder createViewHolder(ViewGroup parent, View view) {
        return new ViewHolder(parent, view);
    }

    public class ViewHolder extends BaseRecyclerAdapter.ViewHolder<Book> {

        private ImageView mIvIcon;

        private TextView mTvName;

        public ViewHolder(ViewGroup parent, View view) {
            super(parent, view);
        }

        @Override
        protected void onInitView() {
            mIvIcon = (ImageView) findViewById(R.id.booklist_icon);
            mTvName = (TextView) findViewById(R.id.booklist_name);
        }

        @Override
        protected void onBind(Book data, int position) {
            mTvName.setText(data.getTitle());
        }
    }
}
