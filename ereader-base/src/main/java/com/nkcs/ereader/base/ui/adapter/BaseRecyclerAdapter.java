package com.nkcs.ereader.base.ui.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * @author faunleaf
 * @date 2018/4/3
 */

public abstract class BaseRecyclerAdapter<T> extends RecyclerView.Adapter<BaseRecyclerAdapter.ViewHolder> {

    protected final List<T> mList = new ArrayList<>();

    protected abstract int getItemLayoutResource();

    protected abstract ViewHolder createViewHolder(View view);

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(getItemLayoutResource(), parent, false);
        ViewHolder holder = createViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.onBind(getItem(position), position);
        holder.itemView.setOnClickListener(v -> onItemClick(v, position));
        holder.itemView.setOnLongClickListener(v -> onItemLongClick(v, position));
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    protected void onItemClick(View view, int position) {
    }

    protected boolean onItemLongClick(View view, int position) {
        return false;
    }

    public void addItem(T value) {
        mList.add(value);
        notifyDataSetChanged();
    }

    public void addItem(int index, T value) {
        mList.add(index, value);
        notifyDataSetChanged();
    }

    public void addItems(List<T> values) {
        mList.addAll(values);
        notifyDataSetChanged();
    }

    public void removeItem(T value) {
        mList.remove(value);
        notifyDataSetChanged();
    }

    public void removeItems(List<T> value) {
        mList.removeAll(value);
        notifyDataSetChanged();
    }

    public T getItem(int position) {
        return mList.get(position);
    }

    public List<T> getItems() {
        return Collections.unmodifiableList(mList);
    }

    public int getItemSize() {
        return mList.size();
    }

    public void refreshItems(List<T> list) {
        mList.clear();
        mList.addAll(list);
        notifyDataSetChanged();
    }

    public void clear() {
        mList.clear();
    }

    public abstract class ViewHolder extends RecyclerView.ViewHolder {

        public ViewHolder(View view) {
            super(view);
            initView(view);
        }

        protected abstract void initView(View view);

        protected abstract void onBind(T data, int position);
    }
}
