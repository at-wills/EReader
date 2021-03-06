package com.nkcs.ereader.read.ui.adapter;

import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.nkcs.ereader.base.entity.Chapter;
import com.nkcs.ereader.base.ui.adapter.BaseRecyclerAdapter;
import com.nkcs.ereader.read.R;

/**
 * @author faunleaf
 * @date 2018/4/3
 */

public class CatalogueAdapter extends BaseRecyclerAdapter<Chapter> {

    private int currentSelected = 0;

    @Override
    protected int getItemLayoutResource() {
        return R.layout.item_catalogue;
    }

    @Override
    protected ViewHolder createViewHolder(ViewGroup parent, View view) {
        return new ViewHolder(parent, view);
    }

    public void setCurChapter(int position) {
        currentSelected = position;
        notifyDataSetChanged();
    }

    public int getCurChapter() {
        return currentSelected;
    }

    public void scrollToSelected() {
        if (mView != null) {
            LinearLayoutManager layoutManager = (LinearLayoutManager) mView.getLayoutManager();
            layoutManager.scrollToPositionWithOffset(currentSelected, 0);
        }
    }

    public class ViewHolder extends BaseRecyclerAdapter.ViewHolder<Chapter> {

        private TextView mTvChapter;

        public ViewHolder(ViewGroup parent, View view) {
            super(parent, view);
        }

        @Override
        protected void onInitView() {
            mTvChapter = (TextView) findViewById(R.id.catalogue_tv_chapter);
        }

        @Override
        protected void onBind(Chapter data, int position) {
            Drawable drawable = null;
            if (data.getHasRead()) {
                drawable = ContextCompat.getDrawable(getContext(), R.drawable.selector_catalogue_read);
            } else {
                drawable = ContextCompat.getDrawable(getContext(), R.drawable.selector_catalogue_unread);
            }

            mTvChapter.setSelected(currentSelected == position);
            mTvChapter.setCompoundDrawablesWithIntrinsicBounds(drawable, null, null, null);
            mTvChapter.setText(data.getTitle());
        }
    }
}
