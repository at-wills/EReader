package com.nkcs.ereader.home.ui.utils;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.widget.ImageView;

import com.nkcs.ereader.base.entity.Book;
import com.nkcs.ereader.home.R;
import com.nkcs.ereader.home.ui.adaptor.BookAdapter;

import java.io.File;
import java.util.AbstractMap.SimpleEntry;
import java.util.ArrayList;
import java.util.List;

public class BookCoverTool {
    private static int currentCover = 0;
    private static final int ALL_COVER = 6;
    // 按照默认顺序绑定 book 和 cover，以在进行排序后保持封面顺序
    private List<SimpleEntry<Integer, Integer>> bindList = new ArrayList<>();

    public static void setNextCover(BookAdapter.BookViewHolder holder, Book book) {
        if ("".equals(book.getCover()) || book.getCover() == null) {
            setNextCover(holder);
        } else {
            File imgFile = new File(book.getCover());
            if (imgFile.exists()) {
                Bitmap bitmap = BitmapFactory.decodeFile(imgFile.getAbsolutePath());
                ((ImageView) holder.findViewById(R.id.book_image)).setImageBitmap(bitmap);
            } else {
                setNextCover(holder);
            }
        }
    }

    private static void setNextCover(BookAdapter.BookViewHolder holder) {
        setNextCover(holder, currentCover);
        currentCover++;
        if (currentCover == ALL_COVER) {
            currentCover = 0;
        }
    }

    // 可能没什么用？
    private static void setNextCover(BookAdapter.BookViewHolder holder, int coverId) {
        String cover = "cover" + String.valueOf(coverId);
        holder.findViewById(R.id.book_image).setBackgroundResource(
                holder.getResourceId(cover, "drawable"));
    }
}