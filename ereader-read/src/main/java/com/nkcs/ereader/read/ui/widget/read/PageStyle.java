package com.nkcs.ereader.read.ui.widget.read;

import android.support.annotation.ColorRes;

import com.nkcs.ereader.read.R;

/**
 * @author faunleaf
 * @date 2018/4/6
 */

public enum PageStyle {

    BG_1(R.color.read_font_1, R.color.read_bg_1),
    BG_2(R.color.read_font_2, R.color.read_bg_2),
    BG_3(R.color.read_font_3, R.color.read_bg_3),
    BG_4(R.color.read_font_4, R.color.read_bg_4),
    BG_5(R.color.read_font_5, R.color.read_bg_5),
    NIGHT(R.color.read_font_night, R.color.read_bg_night);

    private int mFontColor;
    private int mBgColor;

    PageStyle(@ColorRes int fontColor, @ColorRes int bgColor) {
        mFontColor = fontColor;
        mBgColor = bgColor;
    }

    public int getFontColor() {
        return mFontColor;
    }

    public int getBgColor() {
        return mBgColor;
    }
}
