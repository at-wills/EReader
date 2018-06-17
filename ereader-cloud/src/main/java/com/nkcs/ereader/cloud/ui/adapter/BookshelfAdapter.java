package com.nkcs.ereader.cloud.ui.adapter;

import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.nkcs.ereader.base.BaseApplication;
import com.nkcs.ereader.base.entity.Book;
import com.nkcs.ereader.base.ui.adapter.BaseRecyclerAdapter;
import com.nkcs.ereader.cloud.R;
import com.nkcs.ereader.cloud.entity.CloudBook;

import java.util.List;

/**
 * @author faunleaf
 * @date 2018/4/3
 */

public class BookshelfAdapter extends BaseRecyclerAdapter<CloudBook> {

    private static final Long NOT_BOOK_BUT_JUST_PLACE_HOLDERS_ID = -1L;

    @Override
    protected int getItemLayoutResource() {
        return R.layout.item_bookshelf;
    }

    @Override
    protected ViewHolder createViewHolder(ViewGroup parent, View view) {
        return new ViewHolder(parent, view);
    }

    @Override
    public void addItems(List<CloudBook> values) {
        super.addItems(values);
        supplyPlaceHolderBooksToTail(mList);
    }

    private void supplyPlaceHolderBooksToTail(List<CloudBook> list) {
        int validCount = listSizeExceptPlaceHolder(list);
        int needed = 3;
        if (validCount % 3 != 0) {
            needed += 3 - validCount % 3;
        }
        for (int i = list.size() - 1; list.size() != 0 && list.get(i).getId().equals(NOT_BOOK_BUT_JUST_PLACE_HOLDERS_ID); i--) {
            list.remove(i);
        }
        for (int i = 0; i < needed; i++) {
            CloudBook book = new CloudBook();
            book.setId(NOT_BOOK_BUT_JUST_PLACE_HOLDERS_ID);
            list.add(book);
        }
    }

    private int listSizeExceptPlaceHolder(List<CloudBook> list) {
        int validCount = 0;
        for (int i = 0; i < list.size(); i++) {
            if (!list.get(i).getId().equals(NOT_BOOK_BUT_JUST_PLACE_HOLDERS_ID)) {
                validCount++;
            }
        }
        return validCount;
    }

    public class ViewHolder extends BaseRecyclerAdapter.ViewHolder<CloudBook> {

        private View mVItemShadow;
        private ImageView mIvImage;
        private TextView mTvName;
        private TextView mTvFormat;
        private View mVImageShadow;

        public ViewHolder(ViewGroup parent, View view) {
            super(parent, view);
        }

        @Override
        protected void onInitView() {
            mVItemShadow = findViewById(R.id.book_item_shadow);
            mIvImage = (ImageView) findViewById(R.id.book_image);
            mTvName = (TextView) findViewById(R.id.book_name);
            mTvFormat = (TextView) findViewById(R.id.book_format);
            mVImageShadow = findViewById(R.id.book_image_shadow);
        }

        @Override
        protected void onBind(CloudBook data, int position) {
            if (data.getId() != null && !data.getId().equals(NOT_BOOK_BUT_JUST_PLACE_HOLDERS_ID)) {
                mVItemShadow.setVisibility(View.VISIBLE);
                mIvImage.setVisibility(View.VISIBLE);
                mTvName.setVisibility(View.VISIBLE);
                mTvFormat.setVisibility(View.VISIBLE);
                mVImageShadow.setVisibility(View.VISIBLE);
                mTvName.setText(data.getTitle());
                String format = data.getPath().substring(data.getPath().lastIndexOf('.') + 1).trim().toUpperCase();
                mTvFormat.setText(BaseApplication.getContext().getString(R.string.book_format, format));
            } else {
                mVItemShadow.setVisibility(View.GONE);
                mIvImage.setVisibility(View.GONE);
                mTvName.setVisibility(View.GONE);
                mTvFormat.setVisibility(View.GONE);
                mVImageShadow.setVisibility(View.GONE);
            }
        }
    }
}
