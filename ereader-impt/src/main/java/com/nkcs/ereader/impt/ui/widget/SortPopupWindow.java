package com.nkcs.ereader.impt.ui.widget;

import android.content.Context;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.nkcs.ereader.impt.R;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by futanwei on 2018/6/10.
 */

public class SortPopupWindow extends PopupWindow {

    private final static List<String> list = new ArrayList<>(Arrays.asList("按时间倒叙排序", "按文件名称排序", "按文件大小排序"));
    private OnItemClickListener mListener;

    public SortPopupWindow(Context context){
        super(LayoutInflater.from(context).inflate(R.layout.popup_sort_list, null, false),
                LinearLayout.LayoutParams.WRAP_CONTENT, 330, true);
        init(context);
    }

    private void init(Context context){
        View contentView = this.getContentView();
        RecyclerView sortList = contentView.findViewById(R.id.popup_sort_list);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(context);
        sortList.setLayoutManager(linearLayoutManager);
        sortList.setAdapter(new SortAdapter(list));
    }

    public void setOnItemClickListener(OnItemClickListener mListener){
        this.mListener = mListener;
    }

    private class SortAdapter extends RecyclerView.Adapter<SortAdapter.ViewHolder>{

        private List<String> mList;

        public SortAdapter(List<String> list){
            mList = list;
        }

        class ViewHolder extends RecyclerView.ViewHolder{

            TextView sortText;

            public ViewHolder(View itemView) {
                super(itemView);
                sortText = itemView.findViewById(R.id.popup_sort_list_item_text);
            }
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {
            holder.sortText.setText(mList.get(position));
            if(mListener != null){
                holder.sortText.setOnClickListener(view -> {
                    mListener.onItemClick(position);
                });
            }
        }

        @Override
        public int getItemCount() {
            return mList.size();
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.popup_sort_list_item,parent,false);
            ViewHolder holder = new ViewHolder(view);
            return holder;
        }
    }

    public interface OnItemClickListener{
        void onItemClick(int position);
    }
}
