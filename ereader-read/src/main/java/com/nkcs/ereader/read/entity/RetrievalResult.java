package com.nkcs.ereader.read.entity;

import com.nkcs.ereader.base.entity.Chapter;

/**
 * @author faunleaf
 * @date 2018/6/12
 */

public class RetrievalResult {

    private Chapter chapter;

    private String Content;

    private String searchKey;

    private int position;

    public Chapter getChapter() {
        return chapter;
    }

    public void setChapter(Chapter chapter) {
        this.chapter = chapter;
    }

    public String getContent() {
        return Content;
    }

    public void setContent(String content) {
        Content = content;
    }

    public String getSearchKey() {
        return searchKey;
    }

    public void setSearchKey(String searchKey) {
        this.searchKey = searchKey;
    }

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }
}
