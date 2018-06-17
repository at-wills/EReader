package com.nkcs.ereader.home.ui.utils;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.View;
import android.widget.ImageView;

import com.nkcs.ereader.base.entity.Book;
import com.nkcs.ereader.home.R;
import com.nkcs.ereader.home.ui.adapter.BookAdapter;

import java.io.File;

public class BookCoverTool {
    public static void hideText(BookAdapter.BookViewHolder holder, Book book) {
        if (!"".equals(book.getCover()) && book.getCover() != null) {
            File imgFile = new File(book.getCover());
            if (imgFile.exists()) {
                Bitmap bitmap = BitmapFactory.decodeFile(imgFile.getAbsolutePath());
                ((ImageView) holder.findViewById(R.id.book_image)).setImageBitmap(bitmap);
                hideText(holder);
            }
        }
    }

    private static void hideText(BookAdapter.BookViewHolder holder) {
        holder.findViewById(R.id.book_name).setVisibility(View.INVISIBLE);
        holder.findViewById(R.id.book_format).setVisibility(View.INVISIBLE);
    }
}