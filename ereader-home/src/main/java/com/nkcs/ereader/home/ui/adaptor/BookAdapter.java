package com.nkcs.ereader.home.ui.adaptor;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.drawable.Drawable;
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
import com.nkcs.ereader.base.utils.LogUtils;
import com.nkcs.ereader.home.R;
import com.nkcs.ereader.home.ui.fragment.HomeFragment;
import com.nkcs.ereader.home.ui.utils.BookCoverTool;
import com.nkcs.ereader.home.ui.utils.SharedPreferenceManager;

import java.io.BufferedOutputStream;
import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import io.reactivex.annotations.NonNull;

import static java.lang.Math.ceil;

public class BookAdapter extends RecyclerView.Adapter<BookAdapter.ViewHolder> {
    // 宇宙的终极答案：42
    private static final int TYPE_HEADER = 42;
    private static final int TYPE_BOOK = 43;
    public static final int GRID_COLUMNS_PER_ROW = 3;
    private static final Long NOT_BOOK_BUT_JUST_PLACE_HOLDERS_ID = -1L;
    private HomeFragment homeFragment;
    private List<Book> bookList = new ArrayList<>();
    // 用于储存原始书目
    private List<Book> backupBookList;
    private boolean isEditing = false;
    // 记录选取的书籍id
    private Selector selectedBooks = new Selector();


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
            if (book.getId() != null && book.getId().equals(NOT_BOOK_BUT_JUST_PLACE_HOLDERS_ID)) {
                holder.findViewById(R.id.book_image).setVisibility(View.INVISIBLE);
                holder.findViewById(R.id.book_name).setVisibility(View.INVISIBLE);
                holder.findViewById(R.id.book_image_shadow).setVisibility(View.INVISIBLE);
                holder.findViewById(R.id.book_item_shadow).setVisibility(View.INVISIBLE);
                holder.findViewById(R.id.book_format).setVisibility(View.INVISIBLE);
                // 最后一行的书架边缘不显示（占位符如果有两行的话，那么是下面的一行
                if (position > 8 && position > bookList.size() - 3) {
                    holder.findViewById(R.id.book_item).setBackground(getDrawable(
                            R.drawable.book_item_gradient_no_edge));
                }
                return;
            }

            // 显示置顶图标
            if (book.getPinTopDate() != null) {
                holder.findViewById(R.id.pin_top_img).setVisibility(View.VISIBLE);
            }

            // 设置CheckBox
            if (isEditing) {
                ImageView checkBox = (ImageView) holder.findViewById(R.id.check_box_image);
                holder.findViewById(R.id.check_box_image).setVisibility(View.VISIBLE);

                boolean checked = selectedBooks.get(bookList.get(position - 1).getId().intValue(), false);
                if (checked) {
                    checkBox.setImageDrawable(getDrawable(R.drawable.selected));
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
                selectedBooks.put(bookList.get(position - 1).getId().intValue(), true);
                homeFragment.switchBookEditMode(true);
                return true;
            });
            bookItem.setOnClickListener(e -> {
                if (!isEditing) {
                    goReadPage();
                } else {
                    // 选中书籍或取消
                    selectedBooks.put(bookList.get(position - 1).getId().intValue(), !selectedBooks.get(bookList.get(position - 1).getId().intValue(), false));
                    notifyDataSetChanged();
                }
            });
        }
    }

    public void addData(List<Book> books) {
        bookList = books;
        supplyPlaceHolderBooksToTail(bookList);
        backupBookList = new ArrayList<>(bookList);
        sort();
    }

    // 包含通知数据更改
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
        notifyDataSetChanged();
    }

    // 计算并添加（或减去）多余的book进行占位
    private void supplyPlaceHolderBooksToTail(List<Book> list) {
        int validCount = listSizeExceptPlaceHolder(list);
        int needed = 3;
        if ((validCount + 1) % 3 != 0) {
            needed += 3 - (validCount + 1) % 3;
        }
        for (int i = list.size() - 1; list.size() != 0 && list.get(i).getId().equals(NOT_BOOK_BUT_JUST_PLACE_HOLDERS_ID); i--) {
            list.remove(i);
        }
        for (int i = 0; i < needed; i++) {
            Book book = new Book();
            book.setId(NOT_BOOK_BUT_JUST_PLACE_HOLDERS_ID);
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

    public void selectAllOrUnselectAll(boolean isSelect) {
        TextView selectView = homeFragment.findViewById(R.id.select_all);
        if (isSelect) {
            selectView.setText("反选");
            selectView.setCompoundDrawablesWithIntrinsicBounds(null, getDrawable(R.drawable.unselect_all), null, null);
        } else {
            selectView.setText("全选");
            selectView.setCompoundDrawablesWithIntrinsicBounds(null, getDrawable(R.drawable.select_all), null, null);
        }
        // 惊天 bug：bookList 里面有占位符，不能计算全部的
        for (int i = 0; i < listSizeExceptPlaceHolder(bookList); i++) {
            selectedBooks.put(bookList.get(i).getId().intValue(), isSelect);
        }
        notifyDataSetChanged();
    }

    private int listSizeExceptPlaceHolder(List<Book> list) {
        int validCount = 0;
        for (int i = 0; i < list.size(); i++) {
            if (!list.get(i).getId().equals(NOT_BOOK_BUT_JUST_PLACE_HOLDERS_ID)) {
                validCount++;
            }
        }
        return validCount;
    }

    public void search(String string) {
        bookList.clear();
        for (int i = 0; i < backupBookList.size(); i++) {
            String title = backupBookList.get(i).getTitle();
            if (title != null && title.contains(string)) {
                bookList.add(backupBookList.get(i));
            }
        }
        supplyPlaceHolderBooksToTail(bookList);
        sort();
    }

    public void endSearch() {
        bookList = new ArrayList<>(backupBookList);
        sort();
    }

    // 置顶书目或取消
    public void pinBooksTopOrCancel() {
        BookDao bookDao = DbHelper.getInstance().getSession().getBookDao();
        List<Book> selectedBooks = getSelectedBooks();
        boolean isPinTop = selectedBooksAreAllPinnedToBeCancel();

        for (int i = selectedBooks.size() - 1; i >= 0; i--) {
            Book book = selectedBooks.get(i);
            if (!isPinTop) {
                book.setPinTopDate(new Date());
            } else {
                book.setPinTopDate(null);
            }
            bookDao.update(book);
            dealBackupList(book, false);
        }
        selectAllOrUnselectAll(false);
        sort();
    }

    // 删除书目
    public void delete() {
        final boolean[] deleteFile = {false};
        AlertDialog dialog = new AlertDialog.Builder(homeFragment.getContext())
                .setTitle("从书架里移除")//设置对话框的标题
                .setMultiChoiceItems(new String[]{"彻底删除本地文件"}, deleteFile,
                        new DialogInterface.OnMultiChoiceClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which, boolean isChecked) {
                                deleteFile[0] = isChecked;
                            }
                        })
                .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                })
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        BookDao bookDao = DbHelper.getInstance().getSession().getBookDao();
                        List<Book> books = getSelectedBooks();
                        if (deleteFile[0]) {
                            for (Book book : books) {
                                if (book.getPath() == null) {
                                    Log.e("delete modal sure",
                                            "错误，无路径文件 " + book.toString());
                                    deleteFromList(bookDao, book);
                                    continue;
                                }
                                File file = new File(book.getPath());
                                if (file.exists()) {
                                    if (!file.delete()) {
                                        Log.e("delete modal sure", "删除文件失败：" + file.getAbsolutePath());
                                    } else {
                                        deleteFromList(bookDao, book);
                                    }
                                }
                                Log.e("delete modal sure",
                                        "文件不存在 " + book.toString());
                            }
                        } else {
                            for (Book book : books) {
                                deleteFromList(bookDao, book);
                            }
                        }
                        supplyPlaceHolderBooksToTail(bookList);
                        selectAllOrUnselectAll(false);
                        notifyDataSetChanged();
                        dialog.dismiss();
                    }
                }).create();
        dialog.show();
    }

    // 从列表以及数据库中删除
    private void deleteFromList(BookDao bookDao, Book book) {
        bookDao.delete(book);
        dealBackupList(book, true);
        bookList.remove(book);
        // 我也是无奈了
        selectedBooks.put(book.getId().intValue(), false);
    }

    // 分享书目
    public void share() {

    }

    // 显示详情
    public void detail() {

    }

    // 设置“取消置顶”或“置顶”文字提示
    private void setPinTopButtonText() {
        TextView pinTopBtn = homeFragment.findViewById(R.id.pin_top_btn);
        if (selectedBooksAreAllPinnedToBeCancel()) {
            pinTopBtn.setText("取消置顶");
            pinTopBtn.setCompoundDrawablesWithIntrinsicBounds(null,
                    getDrawable(R.drawable.pin_top_cancel), null, null);
        } else {
            pinTopBtn.setText("置顶");
            pinTopBtn.setCompoundDrawablesWithIntrinsicBounds(null,
                    getDrawable(R.drawable.pin_top_btn), null, null);
        }
    }

    private List<Book> getSelectedBooks() {
        List<Book> books = new ArrayList<>();
        for (int i = 0; i < selectedBooks.size(); i++) {
            int key = selectedBooks.keyAt(i);
            if (selectedBooks.get(key, false)) {
                books.add(getBookById(key));
            }
        }
        return books;
    }

    private Book getBookById(int id) {
        for (Book b :
                bookList) {
            if (b.getId() == id) {
                return b;
            }
        }
        return null;
    }

    private boolean selectedBooksAreAllPinnedToBeCancel() {
        List<Book> books = getSelectedBooks();
        if (books.size() == 0) {
            return false;
        }
        for (Book book : books) {
            if (book.getPinTopDate() == null) {
                return false;
            }
        }
        return true;
    }

    class Selector {
        SparseBooleanArray selectedBooks = new SparseBooleanArray();

        void onSelectChange() {
            setPinTopButtonText();
        }

        void clear() {
            selectedBooks.clear();
            onSelectChange();
        }

        int keyAt(int i) {
            return selectedBooks.keyAt(i);
        }

        boolean get(int i) {
            return selectedBooks.get(i);
        }

        boolean get(int i, boolean defaultValue) {
            return selectedBooks.get(i, defaultValue);
        }

        int size() {
            return selectedBooks.size();
        }

        void put(int i, boolean b) {
            selectedBooks.put(i, b);
            onSelectChange();
        }

    }

    private Drawable getDrawable(int id) {
        return homeFragment.getResources().getDrawable(id);
    }

    // 处理备份数据，以应对在搜索结果中进行的数据操作
    private void dealBackupList(Book origin, boolean isDelete) {
        long id = origin.getId();
        Iterator<Book> iterator = backupBookList.iterator();
        while (iterator.hasNext()) {
            Book book = iterator.next();
            if (book.getId().equals(id)) {
                iterator.remove();
                break;
            }
        }
        if (!isDelete) {
            backupBookList.add(origin);
        }
    }

    // 已经全部选取的
    public boolean allSelected() {
        int selected = getSelectedBooks().size();
        return selected == bookList.size();
    }

    // todo 文件权限
    // todo 占位符计算问题，不管了
}
