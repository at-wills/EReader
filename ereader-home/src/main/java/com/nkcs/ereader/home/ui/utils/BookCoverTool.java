package com.nkcs.ereader.home.ui.utils;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.widget.ImageView;

import com.nkcs.ereader.base.entity.Book;
import com.nkcs.ereader.home.R;
import com.nkcs.ereader.home.ui.adaptor.BookAdapter;

import java.io.File;

public class BookCoverTool {
    public static void setCover(BookAdapter.BookViewHolder holder, Book book) {
        if ("".equals(book.getCover()) || book.getCover() == null) {
            setCover(holder);
        } else {
            File imgFile = new File(book.getCover());
            if (imgFile.exists()) {
                Bitmap bitmap = BitmapFactory.decodeFile(imgFile.getAbsolutePath());
                ((ImageView) holder.findViewById(R.id.book_image)).setImageBitmap(bitmap);
            } else {
                setCover(holder);
            }
        }
    }

    private static void setCover(BookAdapter.BookViewHolder holder) {
        String cover = "book_cover_vector";
        int resource = holder.getResourceId(cover, "drawable");
        holder.findViewById(R.id.book_image).setBackgroundResource(resource);
    }
}