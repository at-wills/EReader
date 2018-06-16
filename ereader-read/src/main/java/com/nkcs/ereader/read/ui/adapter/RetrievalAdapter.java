package com.nkcs.ereader.read.ui.adapter;

import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.nkcs.ereader.base.entity.Chapter;
import com.nkcs.ereader.base.ui.adapter.BaseRecyclerAdapter;
import com.nkcs.ereader.read.R;
import com.nkcs.ereader.read.entity.RetrievalResult;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author faunleaf
 * @date 2018/4/3
 */

public class RetrievalAdapter extends BaseRecyclerAdapter<RetrievalResult> {

    @Override
    protected int getItemLayoutResource() {
        return R.layout.item_retrieval;
    }

    @Override
    protected ViewHolder createViewHolder(ViewGroup parent, View view) {
        return new ViewHolder(parent, view);
    }

    @Override
    public void addItem(RetrievalResult value) {
        mList.add(value);
        notifyItemInserted(mList.size() - 1);
    }

    @Override
    public void addItem(int index, RetrievalResult value) {
        mList.add(index, value);
        notifyItemInserted(index);
    }

    @Override
    public void addItems(List<RetrievalResult> values) {
        mList.addAll(values);
        notifyItemRangeInserted(mList.size() - values.size(), values.size());
    }

    public class ViewHolder extends BaseRecyclerAdapter.ViewHolder<RetrievalResult> {

        private TextView mTvChapter;

        private TextView mTvContent;

        private TextView mTvPosition;

        public ViewHolder(ViewGroup parent, View view) {
            super(parent, view);
        }

        @Override
        protected void onInitView() {
            mTvChapter = (TextView) findViewById(R.id.retrieval_tv_chapter);
            mTvContent = (TextView) findViewById(R.id.retrieval_tv_content);
            mTvPosition = (TextView) findViewById(R.id.retrieval_tv_position);
        }

        @Override
        protected void onBind(RetrievalResult data, int position) {
            mTvChapter.setText(data.getChapter().getTitle());

            SpannableString spannableString = new SpannableString(data.getContent());
            int currentStart = 0;
            int currentIndex;
            while ((currentIndex = data.getContent().indexOf(data.getSearchKey(), currentStart)) >= 0) {
                spannableString.setSpan(new ForegroundColorSpan(Color.parseColor("red")),
                        currentIndex, currentIndex + data.getSearchKey().length(), Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
                currentStart += data.getSearchKey().length();
            }
            mTvContent.setText(spannableString);

            mTvPosition.setText("第 " + data.getPosition() + " 个段落");
        }
    }
}
