package com.nkcs.ereader.read.ui.widget.read;

import com.nkcs.ereader.base.entity.Chapter;

import java.util.List;

/**
 * @author faunleaf
 * @date 2018/4/24
 */

public class ContentPage {

    private int position;

    private Chapter chapter;

    private List<String> lines;

    private int titleLinesCount;

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }

    public Chapter getChapter() {
        return chapter;
    }

    public void setChapter(Chapter chapter) {
        this.chapter = chapter;
    }

    public List<String> getLines() {
        return lines;
    }

    public void setLines(List<String> lines) {
        this.lines = lines;
    }

    public int getTitleLinesCount() {
        return titleLinesCount;
    }

    public void setTitleLinesCount(int titleLinesCount) {
        this.titleLinesCount = titleLinesCount;
    }
}
