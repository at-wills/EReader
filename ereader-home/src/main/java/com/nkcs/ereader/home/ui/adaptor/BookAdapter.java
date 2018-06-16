package com.nkcs.ereader.home.ui.adaptor;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.nkcs.ereader.base.db.BookDao;
import com.nkcs.ereader.base.db.DbHelper;
import com.nkcs.ereader.base.entity.Book;
import com.nkcs.ereader.home.R;
import com.nkcs.ereader.home.ui.fragment.HomeFragment;
import com.nkcs.ereader.home.ui.utils.BookCoverTool;
import com.nkcs.ereader.home.ui.utils.SharedPreferenceManager;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

public class BookAdapter extends RecyclerView.Adapter<BookAdapter.ViewHolder> {
    private static final int TYPE_HEADER = 42;
    private static final int TYPE_BOOK = 43;
    public static final int GRID_COLUMNS = 3;
    private static final Long NO_BOOK = -1L;
    private HomeFragment homeFragment;
    private List<Book> bookList = new ArrayList<>();
    // 用于在搜索时储存书目
    private List<Book> backupBookList;
    private boolean isEditing = false;
    // 记录选取的书籍id
    private SparseBooleanArray selectedBooks = new SparseBooleanArray();


    public BookAdapter(HomeFragment homeFragment) {
        this.homeFragment = homeFragment;
    }

    public void switchEdit(boolean isEditing) {
        this.isEditing = isEditing;
        if (!isEditing) {
            selectedBooks.clear();
        }
        notifyDataSetChanged();
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
        // 关闭复用（恶心
        holder.setIsRecyclable(false);

        if (holder instanceof HeaderViewHolder) {
            if (isEditing) {
                holder.findViewById(R.id.import_image).setVisibility(View.INVISIBLE);
                ((TextView) holder.findViewById(R.id.import_text)).setText("哎呀");
            } else {
                holder.findViewById(R.id.book_shelf_import_btn)
                        .setOnClickListener(e -> homeFragment.goImportPage());
            }
        } else {
            Book book = bookList.get(position - 1);

            // 占位符隐藏书目
            if (book.getId() != null && book.getId().equals(NO_BOOK)) {
                holder.findViewById(R.id.book_image).setVisibility(View.INVISIBLE);
                holder.findViewById(R.id.book_name).setVisibility(View.INVISIBLE);
                holder.findViewById(R.id.book_image_shadow).setVisibility(View.INVISIBLE);
                holder.findViewById(R.id.book_item_shadow).setVisibility(View.INVISIBLE);
                holder.findViewById(R.id.book_format).setVisibility(View.INVISIBLE);
                // 最后一行的书架边缘不显示（占位符如果有两行的话，那么是下面的一行
                if (position > 8 && position > bookList.size() - 3) {
                    holder.findViewById(R.id.book_item).setBackground(homeFragment
                            .getResources().getDrawable(R.drawable.book_item_gradient_no_edge));
                }
                return;
            }

            // 显示置顶图标
            if (book.getPinTopDate() != null) {
                holder.findViewById(R.id.pin_top).setVisibility(View.VISIBLE);
            }

            // 设置CheckBox
            if (isEditing) {
                ImageView checkBox = (ImageView) holder.findViewById(R.id.check_box_image);
                holder.findViewById(R.id.check_box_image).setVisibility(View.VISIBLE);

                boolean checked = selectedBooks.get(position - 1, false);
                if (checked) {
                    checkBox.setImageDrawable(homeFragment
                            .getResources().getDrawable(R.drawable.selected));
                }
            }

            // 设置标题与封面、扩展名
            ((TextView) holder.findViewById(R.id.book_name)).setText(book.getTitle());
            BookCoverTool.hideText((BookViewHolder) holder, book);
            if (book.getFormat() != null) {
                ((TextView) holder.findViewById(R.id.book_format)).setText(homeFragment
                        .getString(R.string.book_format, book.getFormat().trim().toUpperCase()));
            } else {
                holder.findViewById(R.id.book_format).setVisibility(View.INVISIBLE);
            }

            // 设置点击事件
            View bookItem = holder.findViewById(R.id.book_item);
            bookItem.setOnLongClickListener(e -> {
                selectedBooks.put(position - 1, true);
                homeFragment.switchBookEditMode(true);
                return true;
            });
            bookItem.setOnClickListener(e -> {
                if (!isEditing) {
                    goReadPage();
                } else {
                    // 选中书籍或取消
                    selectedBooks.put(position - 1, !selectedBooks.get(position - 1, false));
                    notifyDataSetChanged();
                }
            });
        }
    }

    public void addData(List<Book> books) {
        bookList = books;
        addPlaceHolderBooks(bookList);
        backupBookList = new ArrayList<>(bookList);
        sort();
        notifyDataSetChanged();
    }

    // 只修改数据，没有通知数据更改
    public void sort() {
        SharedPreferenceManager sharedPreferenceManager = homeFragment.sharedPreferenceManager;
        if (sharedPreferenceManager.getSetting(SharedPreferenceManager.HOME_BOOK_SORT_BY_TIME)) {
            // 按时间进行排序
            Collections.sort(bookList, (book, book2) -> {
                if (book.getCreated() == null) {
                    return 1;
                } else if (book2.getCreated() == null) {
                    return -1;
                } else {
                    return book.getCreated().after(book2.getCreated()) ? 1 : -1;
                }
            });
        } else {
            // 按名称进行排序
            Collections.sort(bookList, (book, book2) -> {
                if (book.getTitle() == null) {
                    return 1;
                } else if (book2.getTitle() == null) {
                    return -1;
                } else {
                    return book.getTitle().compareTo(book2.getTitle());
                }
            });
        }
        // 最后按照置顶时间排序
        Collections.sort(bookList, (book, book2) -> {
            if (book.getPinTopDate() == null && book2.getPinTopDate() != null) {
                return 1;
            } else if (book.getPinTopDate() != null && book2.getPinTopDate() == null) {
                return -1;
            } else if (book.getPinTopDate() == null && book2.getPinTopDate() == null) {
                return 0;
            } else {
                return book.getPinTopDate().after(book2.getPinTopDate()) ? -1 : 1;
            }
        });
    }

    // 计算并添加多余的book进行占位
    private void addPlaceHolderBooks(List<Book> list) {
        int plus = (int) Math.ceil((double) (list.size() + 1) / 3.0) * 3 - list.size() + 2;
        if (list.size() == 0) {
            plus = 5;
        }
        for (int i = 0; i < plus; i++) {
            Book book = new Book();
            book.setId(NO_BOOK);
            list.add(book);
        }
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
            return R.layout.recycler_import_header;
        } else {
            return R.layout.recycler_book_item;
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

    private void goReadPage() {
        Toast.makeText(this.homeFragment.getContext(), "前往阅读页面", Toast.LENGTH_SHORT).show();
    }

    public void selectAllUnselectAll(boolean isSelect) {
        for (int i = 0; i < bookList.size(); i++) {
            selectedBooks.put(i, isSelect);
        }
        notifyDataSetChanged();
    }

    public void search(String string) {
        bookList.clear();
        for (int i = 0; i < backupBookList.size(); i++) {
            String title = backupBookList.get(i).getTitle();
            if (title != null && title.contains(string)) {
                bookList.add(backupBookList.get(i));
            }
        }
        addPlaceHolderBooks(bookList);
        notifyDataSetChanged();
    }

    public void endSearch() {
        bookList = new ArrayList<>(backupBookList);
        notifyDataSetChanged();
    }

    // 置顶书目
    public void pinTop() {
        BookDao bookDao = DbHelper.getInstance().getSession().getBookDao();
        List<Book> selectedBooks = getSelectedBooks();
        for (int i = selectedBooks.size() - 1; i >= 0; i--) {
            Book book = selectedBooks.get(i);
            book.setPinTopDate(new Date());
            bookDao.update(book);
        }
        selectAllUnselectAll(false);
        sort();
        notifyDataSetChanged();
    }

    // 删除书目
    public void delete() {

    }

    // 分享书目
    public void share() {

    }

    // 显示详情
    public void detail() {

    }

    // 检查是否显示“取消置顶”
    private void checkCancelPinTop() {

    }

    private List<Book> getSelectedBooks() {
        List<Book> books = new ArrayList<>();
        for (int i = 0; i < selectedBooks.size(); i++) {
            int key = selectedBooks.keyAt(i);
            if (selectedBooks.get(key)) {
                books.add(bookList.get(key));
            }
        }
        return books;
    }
}
