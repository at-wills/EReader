package com.nkcs.ereader.home.ui.adaptor;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.nkcs.ereader.base.entity.Book;
import com.nkcs.ereader.home.R;
import com.nkcs.ereader.home.ui.fragment.HomeFragment;
import com.nkcs.ereader.home.ui.utils.BookCoverTool;

import java.util.ArrayList;
import java.util.List;

public class BookAdapter extends RecyclerView.Adapter<BookAdapter.ViewHolder> {
    private static final int TYPE_HEADER = 42;
    private static final int TYPE_BOOK = 43;
    public static final int GRID_COLUMNS = 3;
    private HomeFragment homeFragment;
    private List<Book> bookList = new ArrayList<>();

    public BookAdapter(HomeFragment homeFragment) {
        this.homeFragment = homeFragment;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View item;
        if (viewType == TYPE_HEADER) {
            item = LayoutInflater.from(parent.getContext())
                    .inflate(getItemLayoutResource(TYPE_HEADER), parent, false);
            return new HeaderViewHolder(parent, item);
        } else {
            item = LayoutInflater.from(parent.getContext())
                    .inflate(getItemLayoutResource(TYPE_BOOK), parent, false);
            return new BookViewHolder(parent, item);
        }
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        final RecyclerView.ViewHolder holder1 = holder;
        if (holder instanceof HeaderViewHolder) {
            holder.findViewById(R.id.book_shelf_import_btn)
                    .setOnClickListener(e -> homeFragment.goImportPage());
        } else {
            BookCoverTool.setCover((BookViewHolder) holder, bookList.get(position - 1));
            holder.findViewById(R.id.book_item).setOnLongClickListener(e -> {
                homeFragment.turnOnBookEditMode();
                
                return true;
            });
        }
    }

    public void addData() {
        for (int i = 0; i < 16; i++) {
            bookList.add(new Book());
        }
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return bookList.size() + 1;
    }

    @Override
    public int getItemViewType(int position) {
        if (position == 0) {
            return TYPE_HEADER;
        } else {
            return TYPE_BOOK;
        }
    }

    private int getItemLayoutResource(int viewType) {
        if (viewType == TYPE_HEADER) {
            return R.layout.recycler_header_import;
        } else {
            return R.layout.recycler_item_book;
        }
    }

    public abstract class ViewHolder extends RecyclerView.ViewHolder {
        Context context;
        View view;

        ViewHolder(ViewGroup parent, View view) {
            super(view);
            this.view = view;
            context = parent.getContext();
        }

        public View findViewById(int id) {
            return view.findViewById(id);
        }

        public int getResourceId(String var, String resourceName) {
            return context.getResources().getIdentifier(var, resourceName, context.getPackageName());
        }
    }

    class HeaderViewHolder extends ViewHolder {

        HeaderViewHolder(ViewGroup parent, View itemView) {
            super(parent, itemView);
        }
    }

    public class BookViewHolder extends ViewHolder {

        BookViewHolder(ViewGroup parent, View itemView) {
            super(parent, itemView);
        }

    }

}
